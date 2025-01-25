package kp.reactive.streams.impl;

import kp.utils.Printer;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * The implementation of the {@link Subscriber} for the {@link ByteBuffer} list.
 */
public class SubscriberImplForByteBufferList implements Subscriber<List<ByteBuffer>> {

    private final AtomicInteger atomic = new AtomicInteger();
    private Subscription subscription;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSubscribe(Subscription subscriptionParam) {

        subscription = subscriptionParam;
        subscription.request(1);
        Printer.printf("SubscriberImplForByteBufferList.onSubscribe(): entry number[%s]", atomic.incrementAndGet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNext(List<ByteBuffer> byteBufferList) {

        subscription.request(1);
        final Consumer<ByteBuffer> action = byteBuffer -> {
            final StringBuilder strBld = new StringBuilder("SubscriberImplForByteBufferList.onNext(): byteBuffer[");
            while (byteBuffer.hasRemaining()) {
                strBld.append((char) byteBuffer.get());
            }
            strBld.append("], entry number[").append(atomic.incrementAndGet()).append("]");
            Printer.printObject(strBld);
        };
        byteBufferList.forEach(action);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError(Throwable throwable) {

        Printer.printException("SubscriberImplForByteBufferList.onError():", throwable);
        System.exit(1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onComplete() {

        Printer.printf("SubscriberImplForByteBufferList.onComplete(): entry number[%s]", atomic.incrementAndGet());
    }
}
