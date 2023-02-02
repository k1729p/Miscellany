package kp.reactive.streams.ext;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import kp.utils.Printer;

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

		super();
		this.periodicTask = scheduler.scheduleAtFixedRate(() -> submit(supplier.get()), 0, period, unit);
		Printer.printf("PeriodicPublisher(): period[%d], unit[%s]", period, unit.name());
	}

	/**
	 * 
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