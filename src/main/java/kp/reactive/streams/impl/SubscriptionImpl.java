package kp.reactive.streams.impl;

import kp.utils.Printer;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link Subscription} implementation with a limited number of items in the sequence.
 */
public class SubscriptionImpl implements Subscription {

    private static final int LIMIT = 2;

    private final Subscriber<? super String> subscriber;
    private final AtomicInteger sequenceAtomic = new AtomicInteger(0);
    private final ExecutorService executor = ForkJoinPool.commonPool();
    private Future<?> future;

    /**
     * The constructor.
     *
     * @param subscriber the {@link Subscriber}
     */
    public SubscriptionImpl(Subscriber<? super String> subscriber) {
        this.subscriber = subscriber;
        Printer.printf("SubscriptionImpl(): limit[%d]", LIMIT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void request(long numberOfItems) {

        final int item = sequenceAtomic.incrementAndGet();
        if (item > LIMIT) {
            subscriber.onComplete();
            Printer.printf("SubscriptionImpl.request(): over limit, item[%d], number of items[%d]", item,
                    numberOfItems);
            return;
        }
        future = executor.submit(() -> subscriber.onNext(String.valueOf(item)));
        Printer.printf("SubscriptionImpl.request(): item[%d], number of items[%d]", item, numberOfItems);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void cancel() {

        Optional.ofNullable(future).ifPresent(f -> f.cancel(false));
        Printer.print("SubscriptionImpl.cancel():");
    }
}
