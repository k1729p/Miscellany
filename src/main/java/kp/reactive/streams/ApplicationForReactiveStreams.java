package kp.reactive.streams;

import kp.utils.Printer;

/**
 * The main launcher for the reactive programming model research.
 * <p>
 * The Flow APIs correspond to the Reactive Streams Specification.
 * <ul>
 * <li>{@link java.util.concurrent.Flow}
 * <li>{@link java.util.concurrent.SubmissionPublisher}
 * <li>{@link java.net.http.HttpResponse.BodySubscribers}
 * <li>{@link java.net.http.HttpRequest.BodyPublishers}
 * </ul>
 * Asynchronous streams of data with non-blocking back pressure.
 * <ol>
 * <li><a href="https://community.oracle.com/docs/DOC-1006738">Reactive
 * Programming with JDK 9 Flow API</a>
 * <li><a href="http://www.reactive-streams.org/">Reactive Streams</a>
 * </ol>
 */
public class ApplicationForReactiveStreams {

    /**
     * Private constructor to prevent instantiation.
     */
    private ApplicationForReactiveStreams() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The primary entry point for launching the application.
     *
     */
    public static void main() {

        Printer.printHor();
        FlowLauncher.launchSubmissionPublisherAndSubscriber();
        FlowLauncher.launchPeriodicPublisherAndSubscriber();
        FlowLauncher.launchSubmissionPublisherAndProcessor();
        FlowLauncher.launchPublisherImplAndSubscriber();

        WebFlowLauncher.receiveResponseUsingLineSubscriber();
        WebFlowLauncher.receiveResponseUsingSubscriberForByteBufferList();
        WebFlowLauncher.receiveResponseUsingPublisher();
    }
}
