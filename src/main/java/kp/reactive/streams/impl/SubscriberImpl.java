package kp.reactive.streams.impl;

import kp.utils.Printer;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The implementation of the {@link Subscriber}.
 *
 * @param <T> the item type of the {@link Subscriber}
 */
public class SubscriberImpl<T> implements Subscriber<T> {

    private final AtomicInteger atomic = new AtomicInteger();
    private Subscription subscription;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSubscribe(Subscription subscriptionParam) {

        subscription = subscriptionParam;
        subscription.request(1);
        Printer.printf("SubscriberImpl.onSubscribe(): entry number[%s]", atomic.incrementAndGet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNext(T item) {

        subscription.request(1);
        Printer.printf("SubscriberImpl.onNext(): item[%s], entry number[%s]",
                item.toString(), atomic.incrementAndGet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError(Throwable throwable) {

        Printer.printException("SubscriberImpl.onError():", throwable);
        System.exit(1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onComplete() {

        Printer.printf("SubscriberImpl.onComplete(): entry number[%s]", atomic.incrementAndGet());
    }
}
