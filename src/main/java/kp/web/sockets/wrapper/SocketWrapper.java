package kp.web.sockets.wrapper;

import kp.utils.Printer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;
import java.util.concurrent.Phaser;

/**
 * Wrapper class for managing a {@link ServerSocket}.
 */
public abstract class SocketWrapper {

    /**
     * Host address.
     */
    protected static final String HOST = "127.0.0.1";

    /**
     * Port number.
     */
    protected static final int PORT = 12345;

    /**
     * The {@link ServerSocket} instance.
     */
    protected ServerSocket serverSocket = null;

    /**
     * Constructs a {@link SocketWrapper} instance.
     */
    protected SocketWrapper() {
        // No specific implementation needed
    }

    /**
     * Runs the server.
     *
     * @param phaser  the {@link Phaser} to signal the end of the operation
     * @param content the content to be processed by the server
     * @param number  an identifier number
     */
    public abstract void runServer(Phaser phaser, String content, int number);

    /**
     * Runs the client.
     *
     * @param phaser  the {@link Phaser} to signal the end of the operation
     * @param content the content to be processed by the client
     * @param number  an identifier number
     */
    public abstract void runClient(Phaser phaser, String content, int number);

    /**
     * Closes the {@link ServerSocket}.
     */
    public void closeServerSocket() {
        if (Objects.isNull(serverSocket)) {
            return;
        }
        try {
            serverSocket.close();
            serverSocket = null;
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
    }
}