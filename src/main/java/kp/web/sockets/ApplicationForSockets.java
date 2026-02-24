package kp.web.sockets;

import kp.utils.Printer;
import kp.utils.Utils;
import kp.web.sockets.generator.RandomTextsGenerator;
import kp.web.sockets.wrapper.SocketWrapper;
import kp.web.sockets.wrapper.impl.InsecureSocketWrapper;
import kp.web.sockets.wrapper.impl.SecureSocketWrapper;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.LongSummaryStatistics;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;

/*-
 * The Java Secure Socket Extension (JSSE) Reference Guide (for Java SE 23):
 * https://docs.oracle.com/en/java/javase/23/security/java-secure-socket-extension-jsse-reference-guide.html
 *
 * Secure channels:
 *    - SSLSocket (based on a blocking I/O model)
 *    - SSLServerSocket
 *    - SSLEngine (based on a nonblocking I/O model)
 *
 * The TLS protocol (Transport Layer Security) version 1.3.
 *    TLS 1 is the successor of the SSL (Secure Socket Layer) 3.0 protocol.
 *    SSL 3.0 protocol has been deactivated in Java because it has security flaw.
 * The SSLEngine is an advanced API and beginners should use SSLSocket.
 *
 * A KeyManager determines which authentication credentials to send to the remote host.
 *
 * A TrustManager determines whether the remote authentication credentials
 *    (and thus the connection) should be trusted.
 */

/**
 * The launcher for the socket wrappers: insecure and secure.
 * <p>
 * Researched classes:
 * <ul>
 * <li>Socket
 * <li>ServerSocket
 * <li>SSLSocket
 * <li>SSLServerSocket
 * </ul>
 * <small>The 'SSLEngine' is not researched here.</small>
 */
public class ApplicationForSockets {

    private static final int ITERATIONS = 4;

    private final RandomTextsGenerator randomTextsGenerator = new RandomTextsGenerator(ITERATIONS);

    /**
     * The primary entry point for launching the application.
     *
     */
    public static void main() {

        Printer.printHor();
        if (!SecureSocketWrapper.CONFIGURE_SSL_PROPERTIES_PROGRAMMATICALLY) {
            SecureSocketWrapper.setRequiredSslProperties();
        }
        final ApplicationForSockets application = new ApplicationForSockets();
        application.processLoop(new InsecureSocketWrapper());
        application.processLoop(new SecureSocketWrapper());
    }

    /**
     * Processes the loop.
     *
     * @param socketWrapper the {@link SocketWrapper}
     */
    private void processLoop(SocketWrapper socketWrapper) {

        final long[] elapsedArray = new long[ITERATIONS];
        final AtomicInteger atomic = new AtomicInteger();
        Printer.printSeparatorLine();
        final Instant startAll = Instant.now();
        try (final ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            for (int index = 0; index < ITERATIONS; index++) {
                process(socketWrapper, atomic, executorService, elapsedArray);
                Printer.printSeparatorLine();
            }
            if (Objects.nonNull(socketWrapper)) {
                socketWrapper.closeServerSocket();
            }
        }
        final Instant endAll = Instant.now();
        final String implementation = Optional.ofNullable(socketWrapper)
                .map(Object::getClass).map(Class::getSimpleName).orElse("");
        final LongSummaryStatistics stats = Arrays.stream(elapsedArray).summaryStatistics();
        Printer.printf("processLoop(): implementation[%s], min[%03dms], avg[%3.1fms], max[%03dms], %s", implementation,
                stats.getMin(), stats.getAverage(), stats.getMax(), Utils.formatElapsed(startAll, endAll));
        Printer.printHor();
    }

    /**
     * Processes single loop item.
     *
     * @param socketWrapper   the {@link SocketWrapper}
     * @param atomic          the {@link AtomicInteger}
     * @param executorService the {@link ExecutorService}
     * @param elapsedArray    the array of elapsed times
     */
    private void process(SocketWrapper socketWrapper, AtomicInteger atomic, ExecutorService executorService,
                         long[] elapsedArray) {

        final Phaser phaser = new Phaser(3);
        final int number = atomic.incrementAndGet();
        final Runnable serverTask = () -> socketWrapper.runServer(phaser, randomTextsGenerator.getTextForServer(number),
                number);
        final Runnable clientTask = () -> socketWrapper.runClient(phaser, randomTextsGenerator.getTextForClient(number),
                number);
        final Instant start = Instant.now();
        executorService.execute(serverTask);
        executorService.execute(clientTask);
        Printer.printf("process():   number[%d], both tasks accepted for execution", number);
        phaser.arriveAndAwaitAdvance();
        elapsedArray[number - 1] = Duration.between(start, Instant.now()).toMillis();
    }
}
