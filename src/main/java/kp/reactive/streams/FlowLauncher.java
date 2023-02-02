package kp.reactive.streams;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import kp.reactive.streams.ext.PeriodicPublisher;
import kp.reactive.streams.impl.ProcessorImpl;
import kp.reactive.streams.impl.PublisherImpl;
import kp.reactive.streams.impl.SubscriberImpl;
import kp.utils.Printer;
import kp.utils.Utils;

/**
 * 
 * The launcher for subscribers and publishers.
 *
 */
public class FlowLauncher {

	// run the processor with an error because it is closed too soon
	private static final boolean RUN_WITH_ERROR = false;
	private static final Consumer<String> FUTURE_STR_CONSUMER = item -> Printer.printf("→ Consumed item[%s]", item);
	private static final Consumer<Integer> FUTURE_INT_CONSUMER = item -> Printer.printf("→ Consumed item[%s]", item);
	private static final List<String> EXAMPLE_DATA_LIST = List.of("A", "B", "C");

	/**
	 * The hidden constructor.
	 */
	private FlowLauncher() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Launches the {@link SubmissionPublisher} and the {@link SubscriberImpl}.
	 * 
	 */
	static void launchSubmissionPublisherAndSubscriber() {

		final SubmissionPublisher<String> submissionPublisher = new SubmissionPublisher<>();
		final CompletableFuture<Void> future = submissionPublisher.consume(FUTURE_STR_CONSUMER);
		final Subscriber<String> subscriber = new SubscriberImpl<>();

		try (submissionPublisher) {
			submissionPublisher.subscribe(subscriber);
			EXAMPLE_DATA_LIST.forEach(submissionPublisher::submit);
		}
		CompletableFuture.allOf(future).join();
		Printer.printHor();
	}

	/**
	 * Launches the {@link PeriodicPublisher} and the {@link SubscriberImpl}.
	 * 
	 */
	static void launchPeriodicPublisherAndSubscriber() {

		final AtomicInteger sequenceNumber = new AtomicInteger(0);
		final Supplier<Integer> supplier = () -> {
			final Integer item = sequenceNumber.getAndIncrement();
			Printer.printf("→ Supplied item[%s]", item);
			return item;

		};
		// with period 10ms the results are unrepeatable
		final int period = 100;
		final int expectedNumberOfItems = 3;
		final PeriodicPublisher<Integer> periodicPublisher = new PeriodicPublisher<>(supplier, period,
				TimeUnit.MILLISECONDS);
		final CompletableFuture<Void> future = periodicPublisher.consume(FUTURE_INT_CONSUMER);
		final Subscriber<Integer> subscriber = new SubscriberImpl<>();

		try (periodicPublisher) {
			periodicPublisher.subscribe(subscriber);
			Utils.sleepMillis(expectedNumberOfItems * period);
		}
		CompletableFuture.allOf(future).join();
		Printer.printHor();
	}

	/**
	 * Launches the {@link SubmissionPublisher} and the {@link ProcessorImpl}.
	 * 
	 */
	static void launchSubmissionPublisherAndProcessor() {

		final SubmissionPublisher<String> submissionPublisher = new SubmissionPublisher<>();
		final UnaryOperator<String> operator = str -> Character.toString((char) (str.codePointAt(0) + 10));
		final ProcessorImpl<String, String> processorImpl = new ProcessorImpl<>(operator);
		final CompletableFuture<Void> future = processorImpl.consume(FUTURE_STR_CONSUMER);
		final Subscriber<String> subscriber = new SubscriberImpl<>();

		try (submissionPublisher; processorImpl) {
			submissionPublisher.subscribe(processorImpl);
			processorImpl.subscribe(subscriber);
			EXAMPLE_DATA_LIST.forEach(submissionPublisher::submit);
			Utils.sleepMillis(RUN_WITH_ERROR ? 0 : 100);
		}
		CompletableFuture.allOf(future).join();
		Utils.sleepMillis(101);
		Printer.printHor();
	}

	/**
	 * Launches the {@link PublisherImpl} and the {@link SubscriberImpl}.
	 * 
	 */
	static void launchPublisherImplAndSubscriber() {

		final PublisherImpl publisherImpl = new PublisherImpl();
		final Subscriber<String> subscriber = new SubscriberImpl<>();
		try (publisherImpl) {
			publisherImpl.subscribe(subscriber);
		}
		Utils.sleepMillis(102);
		Printer.printHor();
	}
}
