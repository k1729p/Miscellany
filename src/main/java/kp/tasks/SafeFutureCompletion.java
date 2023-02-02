package kp.tasks;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import kp.utils.Printer;
import kp.utils.Utils;

/**
 * Completes the {@link CompletableFuture} in a safe way.
 *
 */
public class SafeFutureCompletion {

	/**
	 * The constructor.
	 * 
	 */
	public SafeFutureCompletion() {
		super();
	}

	/**
	 * Processes the safe future completion.
	 * 
	 */
	void process() {

		try {
			process(true, true);
			Printer.printSeparatorLine();

			process(true, false);
			Printer.printSeparatorLine();

			process(false, false);
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
	 * Processes the safe future completion.
	 * 
	 * @param setDivisorToZero         the flag for setting divisor to zero
	 * @param withoutGetFromFutureCall the flag for switching off get call
	 * @throws InterruptedException the interrupted exception
	 * @throws ExecutionException   the concurrent exception
	 */
	private void process(boolean setDivisorToZero, boolean withoutGetFromFutureCall)
			throws InterruptedException, ExecutionException {

		final int[] divisor = { 1 };

		final CompletableFuture<Integer> futureThrowingException = CompletableFuture
				.supplyAsync(() -> 100 / divisor[0]);
		if (setDivisorToZero) {
			// Here the value is zeroed and in the next assignment it is reset to normal.
			// That is enough to cause the ArithmeticException!
			divisor[0] = 0;
		}
		Printer.printf("divisor[%d] (before reset)", divisor[0]);
		divisor[0] = 1;
		Printer.printf("divisor[%d] (after  reset)", divisor[0]);

		final Function<Throwable, Integer> exceptionHandler = ex -> {
			Printer.printf("Handled exception: message [%s], returning '-1'", ex.getMessage());
			return -1;
		};
		final CompletableFuture<Integer> safeFutureWithExceptionally = futureThrowingException
				.exceptionally(exceptionHandler);
		final CompletableFuture<Integer> safeFutureWithHandle = futureThrowingException.handle(//
				(ok, exc) -> Objects.nonNull(ok) ? ok : exceptionHandler.apply(exc));

		if (withoutGetFromFutureCall) {
			Printer.print("Run without any 'get()' call.");
		} else {
			Printer.printf("Safe future results: with 'exceptionally()' [%d], with 'handle()' [%d]",
					safeFutureWithExceptionally.get(), safeFutureWithHandle.get());
		}
		Utils.sleepMillis(50);
	}

}
