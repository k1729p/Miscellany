package kp.web.sockets.wrapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Objects;
import java.util.concurrent.Phaser;

import kp.utils.Printer;

/**
 * The wrapper for the {@link ServerSocket}.
 *
 */
public abstract class SocketWrapper {

	/**
	 * The host.
	 */
	protected static final String HOST = "127.0.0.1";

	/**
	 * The port.
	 */
	protected static final int PORT = 12345;

	/**
	 * The {@link ServerSocket}
	 */
	protected ServerSocket serverSocket = null;

	/**
	 * The constructor.
	 * 
	 */
	protected SocketWrapper() {
		super();
	}

	/**
	 * Runs the server.
	 * 
	 * @param phaser the end {@link Phaser}
	 * @param content   the content
	 * @param number    the number
	 * @return the {@link Void}
	 */
	public abstract Void runServer(Phaser phaser, String content, int number);

	/**
	 * Runs the client.
	 * 
	 * @param phaser the end {@link Phaser}
	 * @param content   the content
	 * @param number    the number
	 * @return the {@link Void}
	 */
	public abstract Void runClient(Phaser phaser, String content, int number);

	/**
	 * Closes the {@link ServerSocket}.
	 * 
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
