package kp.processes;

import kp.Constants;
import kp.processes.programs.SingleFileSourceCodeProgram;
import kp.utils.Printer;
import kp.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * The main class for processes research.
 */
public class ApplicationForProcesses {

    private static final Path JAVA_LAUNCHER = Paths.get(System.getProperty("java.home"), "bin", "java.exe");
    private static final String JAVA_PROGRAM = "kp/processes/programs/SingleFileSourceCodeProgram.java";
    private static final File WORKING_DIRECTORY = Paths.get("src", "main", "java").toFile();

    /**
     * Private constructor to prevent instantiation.
     */
    private ApplicationForProcesses() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The primary entry point for launching the application.
     *
     */
    public static void main() {

        Printer.printHor();
        Reporter.showElapsed();
        Reporter.listSystemProperties();
        Reporter.showEnvironment();
        Reporter.showClassloaders();
        Reporter.showDirectories();
        Reporter.showFileStores();

        startProcess();
    }

    /**
     * Starts the new process.
     * <p>
     * It runs the java launcher in a <b>source-file mode</b>.<br>
     * That java command launches given Java application
     * {@link SingleFileSourceCodeProgram}.
     */
    private static void startProcess() {

        showProcessInfo(ProcessHandle.current());
        final ProcessBuilder processBuilder = new ProcessBuilder(JAVA_LAUNCHER.toString(), JAVA_PROGRAM);
        processBuilder.redirectErrorStream(true);
        processBuilder.directory(WORKING_DIRECTORY);
        try {
            final Process process = processBuilder.start();
            final ProcessHandle processHandle = process.toHandle();
            Printer.printf("Child process with ID[%d] has started", process.pid());
            showProcessInfo(processHandle);
            showProcessOutput(process);
            process.waitFor();
            final CompletableFuture<ProcessHandle> futureOnExit = processHandle.onExit();
            futureOnExit.thenAccept(
                    handle -> Printer.printf("Child process with ID[%d] has terminated", handle.pid()));
            showProcessInfo(processHandle);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();// Preserve interrupt status
            Printer.printInterruptedException(e);
            System.exit(1);
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        Printer.printHor();
    }

    /**
     * Shows the process info.
     *
     * @param processHandle the process handle
     */
    private static void showProcessInfo(ProcessHandle processHandle) {

        /*-
         * The method 'ProcessHandle.Info::arguments' return empty results.
         * It is the JDK bug "ProcessHandle arguments is always null".
         */
        final ProcessHandle.Info info = processHandle.info();
        Printer.printf("Process info: process ID[%d], user[%s], command[%s]", processHandle.pid(),
                info.user().orElse(""), info.command().orElse(""));
        Printer.printf("\t\t is alive[%5s], start time[%s], %s", processHandle.isAlive(),
                Constants.INSTANT_AS_TIME_FUN.apply(info.startInstant().orElse(Instant.now())),
                Utils.formatElapsed("total CPU time", info.totalCpuDuration().orElse(Duration.ofMillis(0))));
    }

    /**
     * Shows the process output.
     *
     * @param process the process
     * @throws IOException if an I/O error occurs
     */
    private static void showProcessOutput(Process process) throws IOException {

        Printer.printTopBorderLine();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while (Objects.nonNull(line = reader.readLine())) {
                Printer.print(line);
            }
        }
        Printer.printBottomBorderLine();
    }
}