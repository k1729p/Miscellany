package kp.processes;

import kp.Constants;
import kp.utils.Printer;
import kp.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Reports about the processes, the system, and the environment.
 */
public class Reporter {

    /**
     * Private constructor to prevent instantiation.
     */
    private Reporter() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * One gigabyte in bytes
     */
    private static final int ONE_GIGABYTE_IN_BYTES = 1_073_741_824;

    /**
     * Shows elapsed time.
     */
    public static void showElapsed() {

        final Instant startMethod = Instant.now();
        final Optional<Instant> startProcessOpt = ProcessHandle.current().info().startInstant();
        if (startProcessOpt.isEmpty()) {
            Printer.print("The start time of the process is empty!");
            return;
        }
        Printer.printf("Start time of the process[%s]", Constants.INSTANT_AS_TIME_FUN.apply(startProcessOpt.get()));
        Printer.printf("Start time of the  method[%s]", Constants.INSTANT_AS_TIME_FUN.apply(startMethod));
        Printer.print(Utils.formatElapsed(startProcessOpt.get(), startMethod));
        Printer.printHor();
    }

    /**
     * Lists system properties.
     */
    public static void listSystemProperties() {

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PrintWriter printWriter = new PrintWriter(outputStream)) {
            System.getProperties().list(printWriter);
        }
        Printer.print("System properties listing fragment:");
        final List<String> systemPropertiesList = outputStream.toString().lines().toList();
        if (systemPropertiesList.size() > 4) {
            Printer.printTopBorderLine();
            Printer.print(systemPropertiesList.getFirst());
            Printer.print(systemPropertiesList.get(1));
            Printer.print(systemPropertiesList.get(2));
            Printer.print("[ ... ]");
            Printer.print(systemPropertiesList.getLast());
            Printer.printBottomBorderLine();
        }
        Printer.printHor();
    }

    /**
     * Shows environment.
     */
    public static void showEnvironment() {

        Printer.printf("Java version[%s], native process ID[%d]", System.getProperty("java.version"),
                ProcessHandle.current().pid());
        Printer.printf("Name representing the running JVM  [%s]", ManagementFactory.getRuntimeMXBean().getName());
        final ProcessHandle.Info processInfo = ProcessHandle.current().info();
        /*-
         * JDK bug "ProcessHandle arguments is always null" comment:
         * On Windows the command line arguments are not available
         * via a non-privileged API for other processes and so the Optional is always empty.
         */
        Printer.printf("Process command[%s], process arguments%s, process total CPU time[%dms]",
                processInfo.command().orElse(""), processInfo.arguments().map(Arrays::asList).orElseGet(List::of),
                processInfo.totalCpuDuration().orElse(Duration.ZERO).toMillis());

        final String args = ManagementFactory.getRuntimeMXBean().getInputArguments().toString();
        Printer.printf("Input arguments passed to the JVM:%n%s", Utils.breakLine(args, 100));

        Printer.printf("Maximum amount of memory that the JVM will attempt to use[%s]",
                Utils.formatNumber(Runtime.getRuntime().maxMemory()));
        Printer.printf("Total amount of memory in JVM[%s], amount of free memory in JVM[%s]",
                Utils.formatNumber(Runtime.getRuntime().totalMemory()),
                Utils.formatNumber(Runtime.getRuntime().freeMemory()));

        final OperatingSystemMXBean mxBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        Printer.printf("Operating system: architecture[%s], name[%s], version[%s]", mxBean.getArch(), mxBean.getName(),
                mxBean.getVersion());
        Printer.printf("Available processors[%s], object name[%s]",
                mxBean.getAvailableProcessors(), mxBean.getObjectName());
        Printer.printHor();
    }

    /**
     * Shows classloaders.
     */
    public static void showClassloaders() {

        Printer.print("Classloaders:");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        while (true) {
            if (Objects.isNull(classLoader)) {
                Printer.print("   BOOTSTRAP CLASSLOADER");
                break;
            } else {
                Printer.printf("   %s%n", classLoader.getClass().getName());
            }
            classLoader = classLoader.getParent();
        }
        Printer.printf("This class[%s] is loaded from location[%s]", Reporter.class.getSimpleName(),
                Reporter.class.getProtectionDomain().getCodeSource().getLocation());
        Printer.printHor();
    }

    /**
     * Shows directories.
     */
    public static void showDirectories() {

        Printer.printf("Current Working Directory[%s]", System.getProperty("user.dir"));
        Printer.printf("Current relative path ( )[%s]", Paths.get("").toAbsolutePath().normalize());
        Printer.printf("Current relative path (.)[%s]", Paths.get(".").toAbsolutePath().normalize());
        Printer.printHor();
    }

    /**
     * Shows file stores.
     */
    public static void showFileStores() {

        FileSystems.getDefault().getFileStores().forEach(Reporter::printFileStore);
        Printer.printHor();
    }

    /**
     * Prints the file store.
     *
     * @param store the file store
     */
    private static void printFileStore(FileStore store) {

        try {
            final long total = store.getTotalSpace() / ONE_GIGABYTE_IN_BYTES;
            final long used = (store.getTotalSpace() - store.getUnallocatedSpace()) / ONE_GIGABYTE_IN_BYTES;
            final long available = store.getUsableSpace() / ONE_GIGABYTE_IN_BYTES;
            Printer.printf("Filesystem[%20s], total size[%6s]GB, used size[%4s]GB, available size[%6s]GB", store,
                    Utils.formatNumber(total), Utils.formatNumber(used), Utils.formatNumber(available));
        } catch (IOException e) {
            Printer.printIOException(e);
        }
    }
}
