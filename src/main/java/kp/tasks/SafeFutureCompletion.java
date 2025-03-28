package kp.tasks;

import kp.utils.Printer;
import kp.utils.Utils;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * Completes the {@link CompletableFuture} in a safe way.
 */
public class SafeFutureCompletion {

    /**
     * Processes the safe future completion.
     */
    public void process() {

        try {
            process(true, true);
            Printer.printSeparatorLine();

            process(true, false);
            Printer.printSeparatorLine();

            process(false, false);
        } catch (InterruptedException | ExecutionException | RuntimeException e) {
            Thread.currentThread().interrupt(); // Preserve interrupt status
            switch (e) {
                case InterruptedException interruptedException ->
                        Printer.printInterruptedException(interruptedException);
                case ExecutionException executionException -> Printer.printExecutionException(executionException);
                default -> Printer.printException(e);
            }
            System.exit(1);
        }
        Printer.printHor();
    }

    /**
     * Processes the safe future completion.
     *
     * @param setDivisorToZero         the flag for setting divisor to zero
     * @param withoutGetFromFutureCall the flag for switching off get call
     * @throws InterruptedException if the thread is interrupted
     * @throws ExecutionException   if the computation threw an exception
     */
    private void process(boolean setDivisorToZero, boolean withoutGetFromFutureCall)
            throws InterruptedException, ExecutionException {

        final int[] divisor = {1};
        // Define the future before setting divisor to zero to capture its initial state
        final CompletableFuture<Integer> futureThrowingException = CompletableFuture
                .supplyAsync(() -> 100 / divisor[0]);
        if (setDivisorToZero) {
            /*-
             * The value of 'divisor[0]' is set to zero here and then reset to one a few lines later.
             * Despite this reset, an ArithmeticException occurs because the CompletableFuture is defined
             * before 'divisor[0]' is set to zero.
             */
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
        final CompletableFuture<Integer> safeFutureWithHandle = futureThrowingException.handle(
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
