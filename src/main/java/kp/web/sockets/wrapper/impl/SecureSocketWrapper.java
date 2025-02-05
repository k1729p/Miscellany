package kp.web.sockets.wrapper.impl;

import kp.utils.Printer;
import kp.utils.Utils;
import kp.web.sockets.wrapper.SocketWrapper;

import javax.net.ssl.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.Phaser;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * The secure implementation of the {@link SocketWrapper}.
 */
public class SecureSocketWrapper extends SocketWrapper {
    /**
     * Flag to indicate whether to configure SSL properties programmatically.
     * If true, SSL properties will be configured programmatically within the code.
     * If false, SSL properties will be set using system properties as an alternative method.
     */
    public static final boolean CONFIGURE_SSL_PROPERTIES_PROGRAMMATICALLY = true;

    private static final String KEY_STORE_FILE = "src/main/resources/security/key_store";
    private static final String TRUST_STORE_FILE = "src/main/resources/security/trust_store";
    private static final String PASSWORD = "passphrase";
    private static final Supplier<char[]> PASSWORD_ARR_SUP = () -> {
        char[] resultArray = new char[PASSWORD.length()];
        IntStream.range(0, PASSWORD.getBytes(StandardCharsets.UTF_8).length)
                .forEach(i -> resultArray[i] = (char) PASSWORD.getBytes(StandardCharsets.UTF_8)[i]);
        return resultArray;
    };

    /**
     * The {@link SSLSocketFactory} instance.
     */
    private SSLSocketFactory sslClientSocketFactory = null;

    /**
     * Constructs a SecureSocketWrapper.
     * Initializes the secure socket wrapper and logs the creation time.
     */
    public SecureSocketWrapper() {

        final Instant start = Instant.now();
        initialize();
        final Instant finish = Instant.now();
        Printer.printf("SecureSocketWrapper(): server socket created, host[%s], port[%d], %s", HOST, PORT,
                Utils.formatElapsed(start, finish));
    }

    /**
     * Initializes the secure socket wrapper.
     */
    private void initialize() {

        try {
            final SSLServerSocketFactory sslServerSocketFactory;
            if (CONFIGURE_SSL_PROPERTIES_PROGRAMMATICALLY) {
                sslServerSocketFactory = createSSLServerSocketFactory();
            } else {
                sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                sslClientSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            }
            serverSocket = sslServerSocketFactory.createServerSocket(PORT);
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
    }

    /**
     * Creates customized SSL server socket factory.
     *
     * @return the {@link SSLContext}
     */
    private SSLServerSocketFactory createSSLServerSocketFactory() {

        SSLServerSocketFactory sslServerSocketFactory = null;
        try {
            /*-
             * Loads the KeyStore.
             */
            final KeyManagerFactory keyManagerFactory =
                    KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(KEY_STORE_FILE), PASSWORD_ARR_SUP.get());
            keyManagerFactory.init(keyStore, PASSWORD_ARR_SUP.get());
            /*-
             * Loads the TrustStore.
             */
            final TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            final KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream(TRUST_STORE_FILE), PASSWORD_ARR_SUP.get());
            trustManagerFactory.init(trustStore);
            /*-
             * Uses the SSLContext to create an SSLServerSocketFactory and SSLSocketFactory.
             */
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            sslServerSocketFactory = sslContext.getServerSocketFactory();
            sslClientSocketFactory = sslContext.getSocketFactory();
        } catch (IOException | GeneralSecurityException e) {
            if (e instanceof GeneralSecurityException) {
                Printer.printException(e);
            } else {
                Printer.printIOException((IOException) e);
            }
            System.exit(1);
        }
        return sslServerSocketFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runServer(Phaser phaser, String content, int number) {

        Printer.printf("runServer(): number[%d], start", number);
        final ArrayList<Instant> instants = new ArrayList<>();
        instants.add(Instant.now());// ◄ place '0'
        try (SSLSocket sslSocket = (SSLSocket) serverSocket.accept();
             PrintWriter printWriter =
                     new PrintWriter(new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream())));
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()))) {
            instants.add(Instant.now());// ◄ place '1'
            showSessionAttributes(number, sslSocket, "server");
            instants.add(Instant.now());// ◄ place '2'
            String line;
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                Printer.printf("runServer(): number[%d], content...[%s], server received from client", number,
                        line.substring(line.length() - 15));
            }
            instants.add(Instant.now());// ◄ place '3'
            printWriter.println(content);
            printWriter.flush();
        } catch (IOException ioException) {
            Printer.printIOException(ioException);
            System.exit(1);
        }
        instants.add(Instant.now());// ◄ place '4'
        Printer.printf("runServer(): number[%d], %s", number, Utils.calculateElapsedTimes(instants));
        phaser.arriveAndDeregister();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runClient(Phaser phaser, String content, int number) {

        Printer.printf("runClient(): number[%d], start", number);
        final ArrayList<Instant> instants = new ArrayList<>();
        instants.add(Instant.now());// ◄ place '0'
        try (SSLSocket sslSocket = (SSLSocket) sslClientSocketFactory.createSocket(HOST, PORT);
             PrintWriter printWriter =
                     new PrintWriter(new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream())));
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()))) {
            showSessionAttributes(number, sslSocket, "client");
            instants.add(Instant.now());// ◄ place '1'
            sslSocket.startHandshake();
            instants.add(Instant.now());// ◄ place '2'
            printWriter.printf("%s%n%n", content);
            printWriter.flush();
            if (printWriter.checkError()) {
                Printer.print("runClient(): java.io.PrintWriter error");
                System.exit(1);
            }
            instants.add(Instant.now());// ◄ place '3'
            String line;
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                Printer.printf("runClient(): number[%d], content...[%s], client received from server", number,
                        line.substring(line.length() - 15));
            }
        } catch (IOException ioException) {
            Printer.printIOException(ioException);
            System.exit(1);
        }
        instants.add(Instant.now());// ◄ place '4'
        Printer.printf("runClient(): number[%d], %s", number, Utils.calculateElapsedTimes(instants));
        phaser.arriveAndDeregister();
    }

    /**
     * Shows the SSL session attributes.<br>
     * It is active only for run with number 4.
     *
     * @param number the number
     * @param socket the SSL socket
     * @param label  the label
     */
    private void showSessionAttributes(int number, SSLSocket socket, String label) {

        if (number == 4) {
            socket.addHandshakeCompletedListener(event -> Printer.printf(
                    "showSessionAttributes(): protocol[%s], cipher suite[%s], %s",
                    event.getSession().getProtocol(), event.getCipherSuite(), label));
        }
    }

    /**
     * Sets the required SSL properties using system properties.
     * <p>
     * This method is an alternative way of setting SSL properties and is only used
     * when {@code CONFIGURE_SSL_PROPERTIES_PROGRAMMATICALLY} is set to {@code false}.
     * It is provided for comparison purposes to demonstrate how SSL properties
     * can be configured using system properties.
     */
    public static void setRequiredSslProperties() {

        System.setProperty("javax.net.ssl.keyStore", KEY_STORE_FILE);
        System.setProperty("javax.net.ssl.keyStorePassword", PASSWORD);
        System.setProperty("javax.net.ssl.trustStore", TRUST_STORE_FILE);
        System.setProperty("javax.net.ssl.trustStorePassword", PASSWORD);
        /*-
         * For secure sockets enable the JSSE system debugging with code line:
         * 'System.setProperty("javax.net.debug", "all");'
         */
        Printer.printf("setRequiredSslProperties(): keyStore[%s], trustStore[%s]", KEY_STORE_FILE, TRUST_STORE_FILE);
    }

}