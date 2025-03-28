package kp.reactive.streams.impl;

import kp.utils.Printer;

import java.util.concurrent.Flow.Processor;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Function;

/**
 * The implementation of the {@link Processor}.
 * <p>
 * Based on the 'TransformProcessor' example in the {@link SubmissionPublisher}.
 * <p>
 * The {@link Processor} is a component that acts as both a Subscriber and a
 * Publisher.
 *
 * @param <S> the subscribed item type
 * @param <T> the published item type
 */
public class ProcessorImpl<S, T> extends SubmissionPublisher<T> implements Processor<S, T> {

    private final Function<? super S, ? extends T> function;
    private Subscription subscription;

    /**
     * The constructor.
     *
     * @param function the {@link Function}
     */
    public ProcessorImpl(Function<? super S, ? extends T> function) {
        this.function = function;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSubscribe(Subscription subscription) {

        this.subscription = subscription;
        this.subscription.request(1);
        Printer.print("ProcessorImpl.onSubscribe():");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNext(S item) {

        subscription.request(1);
        final T result = function.apply(item);
        submit(result);
        Printer.printf("ProcessorImpl.onNext(): received item[%s], sending item[%s]", item.toString(),
                result.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError(Throwable throwable) {

        closeExceptionally(throwable);
        Printer.printException("ProcessorImpl.onError():", throwable);
        System.exit(1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onComplete() {

        close();
        Printer.print("ProcessorImpl.onComplete():");
    }
}