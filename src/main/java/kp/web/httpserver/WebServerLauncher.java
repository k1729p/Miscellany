package kp.web.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;// NOSONAR don't use classes from "sun.*" packages

import kp.utils.Printer;

/**
 * The launcher for the {@link HttpServer}.
 * 
 */
public class WebServerLauncher {
	/**
	 * The {@link HttpServer}.
	 */
	private static HttpServer httpServer;
	/**
	 * The {@link HttpServer} server port.
	 */
	private static final int PORT = 8080;

	/**
	 * The hidden constructor.
	 */
	private WebServerLauncher() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Starts the {@link HttpServer}.
	 * 
	 */
	public static void startServer() {

		try {
			httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
		} catch (IOException e) {
			Printer.printIOException(e);
			System.exit(1);
		}
		httpServer.createContext("/", WebHandlers::handleHome);
		httpServer.createContext("/input_tags", WebHandlers::handleInputTags);
		httpServer.createContext("/emojis", WebHandlers::handleEmojis);
		httpServer.createContext("/combining", WebHandlers::handleCombining);
		httpServer.start();
		Printer.print("Web server started");
		Printer.printHor();
	}

	/**
	 * Stops the {@link HttpServer}.
	 * 
	 */
	public static void stopServer() {

		httpServer.stop(0);
		Printer.print("Web server stopped");
	}

}