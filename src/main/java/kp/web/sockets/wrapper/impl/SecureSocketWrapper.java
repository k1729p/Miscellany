package kp.web.sockets.wrapper.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.Phaser;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import kp.utils.Printer;
import kp.utils.Utils;
import kp.web.sockets.wrapper.SocketWrapper;

/**
 * The secure implementation of the {@link SocketWrapper}.
 * 
 */
public class SecureSocketWrapper extends SocketWrapper {

	private static final SSLServerSocketFactory SSL_SERVER_SOCKET_FACTORY = //
			(SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

	private static final SSLSocketFactory SSL_SOCKET_FACTORY = (SSLSocketFactory) SSLSocketFactory.getDefault();

	/**
	 * Constructor.
	 * 
	 */
	public SecureSocketWrapper() {

		super();
		final Instant start = Instant.now();
		initializeSecureSocketWrapper();
		final Instant finish = Instant.now();
		Printer.printf("SecureSocketWrapper(): server socket created, host[%s], port[%d], %s", HOST, PORT,
				Utils.formatElapsed(start, finish));
	}

	/**
	 * Initializes secure socket wrapper
	 * 
	 */
	private void initializeSecureSocketWrapper() {

		try {
			serverSocket = SSL_SERVER_SOCKET_FACTORY.createServerSocket(PORT);
		} catch (IOException e) {
			Printer.printIOException(e);
			System.exit(1);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Void runServer(Phaser phaser, String content, int number) {

		Printer.printf("runServer(): number[%d], start", number);
		final ArrayList<Instant> instants = new ArrayList<>();
		instants.add(Instant.now());// ◄ place '0'
		try {
			final SSLSocket sslSocket = (SSLSocket) ((SSLServerSocket) serverSocket).accept();
			showSessionAttributes(number, sslSocket, "server");
			instants.add(Instant.now());// ◄ place '1'
			final PrintWriter printWriter = new PrintWriter(
					new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream())));
			final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
			try (sslSocket; bufferedReader; printWriter) {
				instants.add(Instant.now());// ◄ place '2'
				String line = null;
				while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
					Printer.printf("runServer(): number[%d], content...[%s], server received from client", number,
							line.substring(line.length() - 15));
				}
				instants.add(Instant.now());// ◄ place '3'
				printWriter.println(content);
				printWriter.flush();
			}
		} catch (IOException ioException) {
			Printer.printIOException(ioException);
			System.exit(1);
		}
		instants.add(Instant.now());// ◄ place '4'
		Printer.printf("runServer(): number[%d], %s", number, Utils.calculateElapsedTimes(instants));
		phaser.arriveAndDeregister();
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Void runClient(Phaser phaser, String content, int number) {

		Printer.printf("runClient(): number[%d], start", number);
		final ArrayList<Instant> instants = new ArrayList<>();
		instants.add(Instant.now());// ◄ place '0'
		try {
			final SSLSocket sslSocket = (SSLSocket) SSL_SOCKET_FACTORY.createSocket(HOST, PORT);
			showSessionAttributes(number, sslSocket, "client");
			instants.add(Instant.now());// ◄ place '1'
			final PrintWriter printWriter = new PrintWriter(
					new BufferedWriter(new OutputStreamWriter(sslSocket.getOutputStream())));
			final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
			try (sslSocket; bufferedReader; printWriter) {
				instants.add(Instant.now());// ◄ place '2'
				sslSocket.startHandshake();
				instants.add(Instant.now());// ◄ place '3'
				printWriter.printf("%s%n%n", content);
				printWriter.flush();
				if (printWriter.checkError()) {
					Printer.print("runClient(): java.io.PrintWriter error");
					System.exit(1);
				}
				String line;
				while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
					Printer.printf("runClient(): number[%d], content...[%s], client received from server", number,
							line.substring(line.length() - 15));
				}
			}
		} catch (IOException ioException) {
			Printer.printIOException(ioException);
			System.exit(1);
		}
		instants.add(Instant.now());// ◄ place '4'
		Printer.printf("runClient(): number[%d], %s", number, Utils.calculateElapsedTimes(instants));
		phaser.arriveAndDeregister();
		return null;
	}

	/**
	 * Shows the SSL session attributes.<br>
	 * It is active only for run with number 5.
	 * 
	 * @param number the number
	 * @param socket the SSL socket
	 * @param label  the label
	 */
	private void showSessionAttributes(int number, SSLSocket socket, String label) {

		if (number != 5) {
			return;
		}
		socket.addHandshakeCompletedListener(
				event -> Printer.printf("showSessionAttributes(): protocol[%s], cipher suite[%s], %s",
						event.getSession().getProtocol(), event.getCipherSuite(), label));
	}

}