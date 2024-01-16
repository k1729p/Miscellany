package kp.reactive.streams.impl;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import kp.utils.Printer;

/**
 * The implementation of the {@link Subscriber} for the {@link ByteBuffer} list.
 * 
 */
public class SubscriberImplForByteBufferList implements Subscriber<List<ByteBuffer>> {

	private final AtomicInteger atomic = new AtomicInteger();
	private Subscription subscription;

	/**
	 * The constructor.
	 * 
	 */
	public SubscriberImplForByteBufferList() {
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
		Printer.printf("SubscriberImplForByteBufferList.onSubscribe(): entry number[%s]", entryNumber);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void onNext(List<ByteBuffer> byteBufferList) {

		final int entryNumber = atomic.incrementAndGet();
		subscription.request(1);
		final Consumer<ByteBuffer> action = byteBuffer -> {
			final StringBuilder strBld = new StringBuilder();
			strBld.append("SubscriberImplForByteBufferList.onNext(): byteBuffer[");
			while (byteBuffer.hasRemaining()) {
				strBld.append(String.format("%c", byteBuffer.get()));
			}
			strBld.append("], entry number[").append(entryNumber).append("]");
			Printer.printObject(strBld);
		};
		byteBufferList.forEach(action);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void onError(Throwable throwable) {

		Printer.printException("SubscriberImplForByteBufferList.onError():", throwable);
		System.exit(1);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void onComplete() {

		final int entryNumber = atomic.incrementAndGet();
		Printer.printf("SubscriberImplForByteBufferList.onComplete(): entry number[%s]", entryNumber);
	}
}
