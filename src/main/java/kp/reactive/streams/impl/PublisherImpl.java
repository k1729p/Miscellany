package kp.reactive.streams.impl;

import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import kp.utils.Printer;

/**
 * The implementation of the {@link Publisher}.
 *
 */
public class PublisherImpl implements Publisher<String>, AutoCloseable {

	private Subscription subscription;

	/**
	 * The constructor.
	 * 
	 */
	public PublisherImpl() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void subscribe(Subscriber<? super String> subscriber) {

		subscription = new SubscriptionImpl(subscriber);
		subscriber.onSubscribe(subscription);
		Printer.print("PublisherImpl.subscribe():");
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void close() {

		subscription.cancel();
		Printer.print("PublisherImpl.close():");
	}
}
