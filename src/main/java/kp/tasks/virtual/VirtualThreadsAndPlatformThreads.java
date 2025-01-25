package kp.tasks.virtual;

import kp.utils.Printer;
import kp.utils.Utils;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * Demonstrates the usage of platform threads and virtual threads.
 * <p>
 * The virtual threads are compared against the 'Reactive' programming style.
 * </p>
 * <p>
 * The 'Reactive' programming style is at odds with the Java Platform because
 * the application's unit of concurrency, the asynchronous pipeline, is no longer
 * the platform's unit of concurrency.
 * </p>
 * <a href="https://openjdk.org/jeps/444">JEP 444: Virtual Threads</a>
 */
public class VirtualThreadsAndPlatformThreads {

    private static final int THREADS_NUMBER = 5_000_000;
    private static final String THREAD_MSG_FORMAT = "thread id[%s], name[%s]";

    /**
     * Private constructor to prevent instantiation.
     */
    private VirtualThreadsAndPlatformThreads() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Starts platform threads and virtual threads.
     * <p>
     * Virtual threads are always daemon threads.
     * </p>
     */
    public static void startPlatformThreadsAndVirtualThreads() {

        final List<Thread.Builder> platformThreadBuilders = List.of(
                Thread.ofPlatform().daemon(),
                Thread.ofPlatform().daemon(),
                Thread.ofPlatform().daemon()
        );
        startThreads(platformThreadBuilders, "platform");

        Printer.printSeparatorLine();

        final List<Thread.Builder> virtualThreadBuilders = List.of(
                Thread.ofVirtual(),
                Thread.ofVirtual(),
                Thread.ofVirtual()
        );
        startThreads(virtualThreadBuilders, "virtual_");
        Printer.printHor();
    }

    /**
     * Executes tasks using different types of executor services.
     */
    public static void executeTasksWithExecutorService() {
        try (ExecutorService platformExecutor = Executors.newCachedThreadPool()) {
            executeTasks(platformExecutor, "platform");
        }
        try (ExecutorService virtualExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            executeTasks(virtualExecutor, "virtual");
        }
        Printer.printHor();
    }

    /**
     * Starts the threads.
     *
     * @param threadBuilders the list of {@link Thread.Builder}
     * @param label          the description label
     */
    private static void startThreads(List<Thread.Builder> threadBuilders, String label) {

        final Thread.Builder threadBuilder1 = threadBuilders.getFirst().name(label + "-from-builder");
        final CountDownLatch countDownLatch1 = new CountDownLatch(1);
        final Runnable runnable1 = () -> {
            Printer.printf(THREAD_MSG_FORMAT, Thread.currentThread().threadId(),
                    Thread.currentThread().getName());
            countDownLatch1.countDown();
        };
        final Thread.Builder threadBuilder2 = threadBuilders.get(1).name(label + "-from-builder-unstarted");
        final CountDownLatch countDownLatch2 = new CountDownLatch(1);
        final Runnable runnable2 = () -> {
            Printer.printf(THREAD_MSG_FORMAT, Thread.currentThread().threadId(),
                    Thread.currentThread().getName());
            countDownLatch2.countDown();
        };
        final Thread.Builder threadBuilder3 = threadBuilders.get(2).name(label + "-from-factory-", 1);
        final CountDownLatch countDownLatch3 = new CountDownLatch(2);
        final Runnable runnable3 = () -> {
            Printer.printf(THREAD_MSG_FORMAT, Thread.currentThread().threadId(),
                    Thread.currentThread().getName());
            countDownLatch3.countDown();
        };
        try {
            threadBuilder1.start(runnable1);
            countDownLatch1.await();

            final Thread thread = threadBuilder2.unstarted(runnable2);
            thread.start();
            countDownLatch2.await();

            final ThreadFactory threadFactory = threadBuilder3.factory();
            IntStream.rangeClosed(1, 2).mapToObj(_ -> threadFactory.newThread(runnable3)).forEach(Thread::start);
            countDownLatch3.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserve interrupt status
        }
    }

    /**
     * Executes the runnable tasks using the provided {@link ExecutorService}.
     *
     * @param executorService the {@link ExecutorService}
     * @param label           the description label
     */
    private static void executeTasks(ExecutorService executorService, String label) {

        final AtomicLong atomicLong = new AtomicLong();
        final CountDownLatch countDownLatch = new CountDownLatch(THREADS_NUMBER);
        final long start = Instant.now().toEpochMilli();
        IntStream.range(0, THREADS_NUMBER).forEach(_ -> executorService.execute(() -> {
            atomicLong.incrementAndGet();
            countDownLatch.countDown();
        }));
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserve interrupt status
        }
        final long elapsed = Instant.now().toEpochMilli() - start;
        Printer.printf("%-8s : threads number[%s], atomic result[%s], elapsed time[%6s]milliseconds", label,
                Utils.formatNumber(THREADS_NUMBER), Utils.formatNumber(atomicLong.get()), Utils.formatNumber(elapsed));
    }
}
