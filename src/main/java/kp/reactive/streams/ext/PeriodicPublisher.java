package kp.reactive.streams.ext;

import kp.utils.Printer;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * The periodic publisher. It extends the {@link SubmissionPublisher}.
 * <p>
 * Based on the 'PeriodicPublisher' example in the {@link SubmissionPublisher}.
 *
 * @param <T> the published item type
 */
public class PeriodicPublisher<T> extends SubmissionPublisher<T> {

    private final ScheduledFuture<?> periodicTask;
    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    /**
     * The constructor.
     *
     * @param supplier the {@link Supplier}
     * @param period   the period
     * @param unit     the {@link TimeUnit}
     */
    public PeriodicPublisher(Supplier<? extends T> supplier, long period, TimeUnit unit) {

        this.periodicTask = scheduler.scheduleAtFixedRate(() -> submit(supplier.get()), 0, period, unit);
        Printer.printf("PeriodicPublisher(): period[%d], unit[%s]", period, unit.name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {

        periodicTask.cancel(false);
        scheduler.shutdown();
        super.close();
        Printer.print("PeriodicPublisher.close():");
    }
}