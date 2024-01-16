package kp.reactive.streams.impl;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicInteger;

import kp.utils.Printer;

/**
 * The implementation of the {@link Subscriber}.
 *
 * @param <T> the item type of the {@link Subscriber}
 */
public class SubscriberImpl<T> implements Subscriber<T> {

	private final AtomicInteger atomic = new AtomicInteger();
	private Subscription subscription;

	/**
	 * The constructor.
	 * 
	 */
	public SubscriberImpl() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void onSubscribe(Subscription subscriptionParam) {

		final int entryNumber = atomic.incrementAndGet();
		subscription = subscriptionParam;
		subscription.request(1);
		Printer.printf("SubscriberImpl.onSubscribe(): entry number[%s]", entryNumber);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void onNext(T item) {

		final int entryNumber = atomic.incrementAndGet();
		subscription.request(1);
		Printer.printf("SubscriberImpl.onNext(): item[%s], entry number[%s]", item.toString(), entryNumber);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void onError(Throwable throwable) {

		Printer.printException("SubscriberImpl.onError():", throwable);
		System.exit(1);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void onComplete() {

		final int entryNumber = atomic.incrementAndGet();
		Printer.printf("SubscriberImpl.onComplete(): entry number[%s]", entryNumber);
	}
}
