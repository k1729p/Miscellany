package kp.reactive.streams.impl;

import kp.utils.Printer;

import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

/**
 * The implementation of the {@link Publisher}.
 */
public class PublisherImpl implements Publisher<String>, AutoCloseable {

    private Subscription subscription;

    /**
     * {@inheritDoc}
     */
    @Override
    public void subscribe(Subscriber<? super String> subscriber) {

        subscription = new SubscriptionImpl(subscriber);
        subscriber.onSubscribe(subscription);
        Printer.print("PublisherImpl.subscribe():");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {

        subscription.cancel();
        Printer.print("PublisherImpl.close():");
    }
}
