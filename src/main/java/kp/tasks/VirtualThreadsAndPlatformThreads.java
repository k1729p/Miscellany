package kp.tasks;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import kp.utils.Printer;
import kp.utils.Utils;

/**
 * <p>
 * The threads:
 * </p>
 * <ul>
 * <li>platform threads</li>
 * <li>virtual threads</li>
 * </ul>
 * <p>
 * The virtual threads versus the 'Reactive' programming style:<br>
 * The 'Reactive' programming style is at odds with that Java Platform
 * because<br>
 * the application's unit of concurrency "the asynchronous pipeline" is no
 * longer<br>
 * the platform's unit of concurrency.
 * </p>
 * <a href="https://openjdk.org/jeps/444">JEP 444: Virtual Threads</a>
 */
public class VirtualThreadsAndPlatformThreads {

	private static final int THREADS_NUMBER = 5_000_000;
	private static final String THREAD_MSG_FORMAT = "thread id[%s], name[%s]";

	/**
	 * The hidden constructor.
	 */
	private VirtualThreadsAndPlatformThreads() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Starts platform threads and virtual threads.
	 * <p>
	 * The virtual threads are always daemon threads.
	 * </p>
	 */
	static void startPlatformThreadsAndVirtualThreads() {

		final List<Thread.Builder> threadBuilderOfPlatformList = List.of(Thread.ofPlatform().daemon(),
				Thread.ofPlatform().daemon(), Thread.ofPlatform().daemon());
		startThreads(threadBuilderOfPlatformList, "platform");
		Printer.printSeparatorLine();
		final List<Thread.Builder> threadBuilderOfVirtualList = List.of(Thread.ofVirtual(), Thread.ofVirtual(),
				Thread.ofVirtual());
		startThreads(threadBuilderOfVirtualList, "virtual_");
		Printer.printHor();
	}

	/**
	 * Executes tasks with executor service.
	 *
	 */
	static void executeTasksWithExecutorService() {
		try (ExecutorService executorService = Executors.newCachedThreadPool()) {
			executeTasks(executorService, "platform");
		}
		try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
			executeTasks(executorService, "virtual");
		}
		Printer.printHor();
	}

	/**
	 * Starts the threads
	 */
	private static void startThreads(List<Thread.Builder> threadBuilderList, String label) {

		final Thread.Builder threadBuilder1 = threadBuilderList.getFirst().name(label + "-from-builder");
		final CountDownLatch countDownLatch1 = new CountDownLatch(1);
		final Runnable runnable1 = () -> {
			Printer.printf(THREAD_MSG_FORMAT, Thread.currentThread().threadId(),
					Thread.currentThread().getName());
			countDownLatch1.countDown();
		};
		final Thread.Builder threadBuilder2 = threadBuilderList.get(1).name(label + "-from-builder-unstarted");
		final CountDownLatch countDownLatch2 = new CountDownLatch(1);
		final Runnable runnable2 = () -> {
			Printer.printf(THREAD_MSG_FORMAT, Thread.currentThread().threadId(),
					Thread.currentThread().getName());
			countDownLatch2.countDown();
		};
		final Thread.Builder threadBuilder3 = threadBuilderList.get(2).name(label + "-from-factory-", 1);
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
			IntStream.rangeClosed(1, 2).mapToObj(i -> threadFactory.newThread(runnable3)).forEach(Thread::start);
			countDownLatch3.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Execute the runnable tasks with the {@link ExecutorService}.
	 *
	 * @param executorService the {@link ExecutorService}
	 * @param label           the description label
	 */
	private static void executeTasks(ExecutorService executorService, String label) {

		final AtomicLong atomicLong = new AtomicLong();
		final CountDownLatch countDownLatch = new CountDownLatch(THREADS_NUMBER);
		final long start = Instant.now().toEpochMilli();
		IntStream.range(0, THREADS_NUMBER).forEach(number -> executorService.execute(() -> {
			atomicLong.incrementAndGet();
			countDownLatch.countDown();
		}));
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		final long elapsed = (Instant.now().toEpochMilli() - start);
		Printer.printf("%-8s : threads number[%s], atomic result[%s], elapsed time[%6s]milliseconds", label,
				Utils.formatNumber(THREADS_NUMBER), Utils.formatNumber(atomicLong.get()), Utils.formatNumber(elapsed));
	}
}
