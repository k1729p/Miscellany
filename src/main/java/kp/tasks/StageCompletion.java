package kp.tasks;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import kp.utils.Printer;
import kp.utils.Utils;

/**
 * The task stage completion research
 */
public class StageCompletion {

	/**
	 * The constructor.
	 * 
	 */
	public StageCompletion() {
		super();
	}

	/**
	 * Completes the single completion stage with all its dependencies.
	 * 
	 */
	void completeSingleStageWithDependencies() {

		final boolean withSleep = false;
		Printer.print("The state reported at points ▐A▌, ▐B▌, ▐C▌, ▐D▌, ▐E▌, ▐F▌. All these points are in Java code.");
		final StringBuilder report = new StringBuilder();
		final CompletableFuture<String> future1 = new CompletableFuture<>();
		final Supplier<String> supplier = () -> {
			if (withSleep) {
				Utils.sleepSeconds(1);
			}
			report.append("«Completed»");
			return "X";
		};
		final CompletableFuture<String> future2 = future1.completeAsync(supplier);
		Printer.printf("At point ▐A▌: %s", report.toString());
		final UnaryOperator<String> functionAppl = str -> {
			report.append(" → «Then-Applied-Function»");
			return str.concat("-Y");
		};
		final CompletableFuture<String> future3 = future2.thenApply(functionAppl);
		Printer.printf("At point ▐B▌: %s", report.toString());
		final CompletableFuture<String> alternativeFuture = new CompletableFuture<>();
		final Function<String, CompletableFuture<String>> functionComp = str -> {
			report.append(" → «Then-Composed-Function»");
			alternativeFuture.completeAsync(() -> str.concat("-Z"));
			return alternativeFuture;
		};
		final CompletableFuture<String> future4 = future3.thenCompose(functionComp);
		Printer.printf("At point ▐C▌: %s", report.toString());
		final CompletableFuture<Void> future5 = future4.thenAccept(str -> report.append(" → «Then-Accepted-Consumer»"));
		Printer.printf("At point ▐D▌: %s", report.toString());
		final CompletableFuture<Void> future6 = future5.thenRun(() -> report.append(" → «Then-Run-Runnable»"));
		Printer.printf("At point ▐E▌: %s", report.toString());
		try {
			Printer.printf("future 1 result[%s]", future1.get());
			Printer.printf("future 2 result[%s]", future2.get());
			Printer.printf("future 3 result[%s]", future3.get());
			Printer.printf("future 4 result[%s]", future4.get());
			Printer.printf("future 5 result[%s]", future5.get());
			Printer.printf("future 6 result[%s]", future6.get());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			Printer.printInterruptedException(e);
			System.exit(1);
		} catch (ExecutionException e) {
			Printer.printExecutionException(e);
			System.exit(1);
		}
		Printer.printf("At point ▐F▌: %s", report.toString());
		Printer.printHor();
	}

	/**
	 * Completes either of two stages with the method
	 * {@link CompletableFuture#acceptEitherAsync}.
	 * <p>
	 * It is the <i>slow future</i> versus the <i>fast future</i>.
	 */
	void completeEitherOfTwoStages() {

		final AtomicInteger atomic = new AtomicInteger();
		final CompletableFuture<String> future100 = CompletableFuture.supplyAsync(() -> {
			Utils.sleepMillis(100);
			atomic.getAndIncrement();
			return "100MS-ACTION";
		});
		final CompletableFuture<String> future10 = CompletableFuture.supplyAsync(() -> {
			Utils.sleepMillis(10);
			atomic.getAndIncrement();
			return "10MS-ACTION";
		});
		final CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
			Utils.sleepMillis(1);
			atomic.getAndIncrement();
			return "1MS-ACTION";
		});
		future10.acceptEitherAsync(future1,
				arg -> Printer.printf("Accepted either  '10MS-ACTION' or   '1MS-ACTION': [%12s]", arg));
		future10.acceptEitherAsync(future100,
				arg -> Printer.printf("Accepted either  '10MS-ACTION' or '100MS-ACTION': [%12s]", arg));

		final Function<String, Void> function1 = arg -> {
			Printer.printf("Applied  either   '1MS-ACTION' or ' 10MS-ACTION': [%12s]", arg);
			return null;
		};
		future1.applyToEitherAsync(future10, function1);
		final Function<String, Void> function2 = arg -> {
			Printer.printf("Applied  either '100MS-ACTION' or ' 10MS-ACTION': [%12s]", arg);
			return null;
		};
		future100.applyToEitherAsync(future10, function2);
		do {
			Utils.sleepMillis(10);
		} while (atomic.get() < 3);
		Printer.printHor();
	}

	/**
	 * Completes both of two stages with the methods
	 * {@link CompletableFuture#thenAcceptBothAsync},
	 * {@link CompletableFuture#runAfterBothAsync},
	 * {@link CompletableFuture#thenCombineAsync}.
	 * 
	 */
	void completeBothOfTwoStages() {

		final CompletableFuture<String> futureOne = CompletableFuture.supplyAsync(() -> "One");
		final CompletableFuture<String> futureTwo = CompletableFuture.supplyAsync(() -> "Two");
		final CompletableFuture<String> futureOneApplied = futureOne.thenApply(arg -> arg.concat("+applied"));
		final CompletableFuture<String> futureTwoApplied = futureTwo.thenApply(arg -> arg.concat("+applied"));

		final BiConsumer<String, String> consumer = (arg1, arg2) -> Printer.printf(//
				"Accepted both (output from BiConsumer): [%s] AND [%s]", arg1, arg2);
		futureOneApplied.thenAcceptBothAsync(futureTwoApplied, consumer);
		futureOne.thenAcceptBothAsync(futureTwo, consumer);

		Utils.sleepMillis(10);
		Printer.printSeparatorLine();

		final Runnable runnable = () -> {
			try {
				Printer.printf("Run      both (output from Runnable)  : [%s] AND [%s]", futureOne.get(),
						futureTwo.get());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				Printer.printInterruptedException(e);
				System.exit(1);
			} catch (ExecutionException e) {
				Printer.printExecutionException(e);
				System.exit(1);
			}
		};
		futureOne.runAfterBothAsync(futureTwo, runnable);

		final BinaryOperator<String> function = (str1, str2) -> str1.concat(" & ").concat(str2);
		final CompletableFuture<String> futureCombined = futureOne.thenCombineAsync(futureTwo, function);
		try {
			Printer.printf("Combined both (combined future result): [%s]", futureCombined.get());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			Printer.printInterruptedException(e);
			System.exit(1);
		} catch (ExecutionException e) {
			Printer.printExecutionException(e);
			System.exit(1);
		}
		Utils.sleepMillis(10);
		Printer.printHor();
	}

}
