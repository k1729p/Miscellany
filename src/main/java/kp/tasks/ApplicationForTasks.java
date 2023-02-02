package kp.tasks;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import kp.utils.Printer;
import kp.utils.Utils;

/**
 * The main class for tasks research.
 * <p>
 * Researched classes:
 * <ul>
 * <li>{@link Runnable}
 * <li>{@link Callable}
 * <li>{@link Future}
 * <li>{@link CompletableFuture}
 * <li>{@link FutureTask}
 * <li>{@link ExecutorService}
 * <li>{@link ForkJoinPool}
 * <li>{@link ThreadFactory}
 * </ul>
 */
public class ApplicationForTasks {

	/**
	 * The constructor.
	 */
	public ApplicationForTasks() {
		super();
	}

	/**
	 * The entry point for the application.
	 * 
	 * @param args the command-line arguments
	 */
	public static void main(String[] args) {

		final ApplicationForTasks application = new ApplicationForTasks();
		Printer.printHor();
		application.compareExecutors();
		application.completeCompletableFutures();
		application.executeFutureTasks();
		application.executeRunnablesAndCallables();
		application.invokeCallableTasksCollection();

		final SafeFutureCompletion safeFutureCompletion = new SafeFutureCompletion();
		safeFutureCompletion.process();

		final StageCompletion stageCompletion = new StageCompletion();
		stageCompletion.completeSingleStageWithDependencies();
		stageCompletion.completeEitherOfTwoStages();
		stageCompletion.completeBothOfTwoStages();

	}

	/**
	 * Compares the {@link ExecutorService}s.
	 * 
	 */
	private void compareExecutors() {

		final Map<String, ExecutorService> poolMap = new TreeMap<>();
		/*-
		 * For fixed size thread pool it sets up a thread pool with one thread per core
		 * which is appropriate for CPU bound tasks
		 */
		poolMap.put("A", Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
		/*-
		 * unbounded thread pool, with automatic thread reclamation
		 */
		poolMap.put("B", Executors.newCachedThreadPool());
		/*-
		 * constructor taken from 'newSingleThreadExecutor()' code
		 */
		poolMap.put("C", new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()));
		/*-
		 *single background thread
		 */
		poolMap.put("D", Executors.newSingleThreadExecutor());
		poolMap.put("E", Executors.newWorkStealingPool());
		poolMap.put("F", new ForkJoinPool());

		final Consumer<Entry<String, ExecutorService>> keyConsumer = entry -> {
			final ExecutorService pool = entry.getValue();
			final CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "supplied value", pool);
			final String result;
			try {
				result = future.get();
			} catch (InterruptedException e) {
				pool.shutdownNow();
				Thread.currentThread().interrupt();
				Printer.printInterruptedException(e);
				return;
			} catch (ExecutionException e) {
				pool.shutdownNow();
				Printer.printExecutionException(e);
				throw new IllegalStateException(e);
			}
			shutdownAndAwaitTermination(pool);
			Printer.printf("Pool %s: future result[%s], pool class[%s]", entry.getKey(), result,
					pool.getClass().getSimpleName());
		};
		poolMap.entrySet().stream().forEach(keyConsumer);
		Printer.printHor();
	}

	/**
	 * Shutdowns pool and awaits termination.<br>
	 * The way of pool shutdown recommended by Oracle.
	 * 
	 * @param pool the pool
	 */
	private void shutdownAndAwaitTermination(ExecutorService pool) {

		pool.shutdown(); // Disable new tasks from being submitted
		try {
			if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
				pool.shutdownNow(); // Cancel currently executing tasks
				if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
					Printer.print("Pool did not terminate");
				}
			}
		} catch (InterruptedException e) {
			pool.shutdownNow();
			Thread.currentThread().interrupt();// Preserve interrupt status
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Completes the {@link CompletableFuture}s.
	 * 
	 */
	private void completeCompletableFutures() {

		final CompletableFuture<String> completableFuture1 = CompletableFuture//
				.supplyAsync(() -> "supplied value 1");
		final CompletableFuture<String> completableFuture2 = new CompletableFuture<String>()//
				.completeAsync(() -> "supplied value 2");
		final CompletableFuture<String> completableFuture3 = new CompletableFuture<String>().newIncompleteFuture();
		completableFuture3.complete("completing value 3");
		// If you already know the result of a computation
		final Future<String> completableFuture4 = CompletableFuture.completedFuture("completing value 4");
		Printer.printf("Completable futures are completed: 1[%B], 2[%B], 3[%B], 4[%B]", completableFuture1.isDone(),
				completableFuture2.isDone(), completableFuture3.isDone(), completableFuture4.isDone());
		try {
			Printer.printf("Completable futures results: 1[%s], 2[%s], 3[%s], 4[%s]", completableFuture1.get(),
					completableFuture2.get(), completableFuture3.get(), completableFuture4.get());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			Printer.printInterruptedException(e);
			System.exit(1);
		} catch (ExecutionException e) {
			Printer.printExecutionException(e);
			System.exit(1);
		}
		Printer.printHor();
	}

	/**
	 * Executes the {@link FutureTask}s with a {@link Runnable} or a
	 * {@link Callable}.
	 * 
	 */
	private void executeFutureTasks() {

		// The FutureTask wraps a Callable object.
		final FutureTask<String> futureTaskCallable = new FutureTask<>(() -> "callable result");
		// The FutureTask wraps a Runnable object.
		final FutureTask<String> futureTaskRunnable = new FutureTask<>(() -> Printer.print("«runnable action»"),
				"successful completion result");

		try (final ExecutorService pool = Executors.newFixedThreadPool(10)) {
			pool.execute(futureTaskCallable);
			pool.execute(futureTaskRunnable);
			Printer.printf("Before sleep - future task is completed: R[%5S], C[%5S]", futureTaskRunnable.isDone(),
					futureTaskCallable.isDone());
			Utils.sleepMillis(10);
			Printer.printf("After  sleep - future task is completed: R[%5S], C[%5S]", futureTaskRunnable.isDone(),
					futureTaskCallable.isDone());
			try {
				Printer.printf("Future task result: C[%s]", futureTaskCallable.get());
				Printer.printf("Future task result: R[%s]", futureTaskRunnable.get());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				Printer.printInterruptedException(e);
				System.exit(1);
			} catch (ExecutionException e) {
				Printer.printExecutionException(e);
				System.exit(1);
			}
		}
		Printer.printHor();
	}

	/**
	 * Executes various {@link Runnable}s and {@link Callable}s.
	 * 
	 */
	private void executeRunnablesAndCallables() {

		final StringBuilder strBld = new StringBuilder();
		final Thread thread = new Thread(() -> strBld.append("«action 1»"));
		final ThreadFactory threadFactory = Executors.defaultThreadFactory();
		final Thread threadFromFactory = threadFactory.newThread(() -> strBld.append("«action 2»"));
		Printer.printf("Thread names: thread[%s], threadFromFactory[%s]", thread.getName(),
				threadFromFactory.getName());

		Printer.print("Runnable actions:");
		Printer.print("(with '▌▐' are printed Java code line markers)");
		strBld.append("\n▐A▌");
		thread.start();
		Utils.sleepMillis(10);
		strBld.append("▐B▌");
		threadFromFactory.start();
		try (final ExecutorService pool = Executors.newFixedThreadPool(10)) {
			pool.execute(() -> strBld.append("«action 3»"));
			strBld.append("▐C▌");
			CompletableFuture.runAsync(() -> strBld.append("«action 4»"), pool);
			strBld.append("▐D▌");

			/*- 3 alternatives of using the 'pool.submit(...)' */
			final Future<?> taskFutureRun = pool.submit(() -> {
				strBld.append("«action 5»");
				return "runnable task result (R)";
			});
			strBld.append("▐E▌");
			final Runnable runnable = () -> strBld.append("«action 6»");
			strBld.append("▐F▌");
			final Future<?> taskFutureCalFromRun = pool
					.submit(Executors.callable(runnable, "callable task result (R►C)"));
			final Future<?> taskFutureCal = pool.submit(() -> "callable task result (C)");
			strBld.append("▐G▌\n");
			Printer.printObject(strBld);

			Printer.printf("Future task is completed: 'runnable'[%5S], 'runnable►callable'[%5S], 'callable'[%5S]",
					taskFutureRun.isDone(), taskFutureCalFromRun.isDone(), taskFutureCal.isDone());
			try {
				// Future from runnable returns null if the task has finished correctly.
				Printer.print("Future results:");
				Printer.printf("\t'runnable'[%s]", taskFutureRun.get());
				Printer.printf("\t'runnable►callable'[%s]", taskFutureCalFromRun.get());
				Printer.printf("\t'callable'[%s]", taskFutureCal.get());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				Printer.printInterruptedException(e);
				System.exit(1);
			} catch (ExecutionException e) {
				Printer.printExecutionException(e);
				System.exit(1);
			}
		}
		Printer.printHor();
	}

	/**
	 * Invokes the {@link Callable} tasks collection using the method
	 * {@link ExecutorService#invokeAll}.
	 * 
	 */
	private void invokeCallableTasksCollection() {

		final List<Callable<String>> callableList = List.of(//
				() -> "from callable task A", () -> "from callable task B");
		final Consumer<Future<String>> futureConsumer = future -> {
			try {
				Printer.printf("Consumed future result[%s]", future.get());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				Printer.printInterruptedException(e);
				System.exit(1);
			} catch (ExecutionException e) {
				Printer.printExecutionException(e);
				System.exit(1);
			}
		};
		try (final ExecutorService pool = Executors.newFixedThreadPool(10)) {
			List<Future<String>> futureList = null;
			try {
				futureList = pool.invokeAll(callableList);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				Printer.printInterruptedException(e);
				System.exit(1);
			}
			futureList.stream().forEach(futureConsumer);
		}
		Printer.printHor();
	}

}
