package kp.web.httpserver;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import kp.utils.Printer;

/**
 * The HTTP server starter and the default browser launcher.
 *
 */
public interface ApplicationForWebServer {

	/**
	 * The entry point for the application.
	 * 
	 * @param args the command-line arguments
	 */
	static void main(String[] args) {

		if (!Desktop.isDesktopSupported()) {
			Printer.print("Desktop not supported!" + args);
			System.exit(1);
		}
		WebServerLauncher.startServer();
		try {
			Desktop.getDesktop().browse(getUri());
		} catch (IOException e) {
			Printer.printIOException(e);
			System.exit(1);
		}
	}

	/**
	 * Initializes the {@link URI} for browser.
	 * 
	 * @return the {@link URI}
	 */
	private static URI getUri() {
		
		URI uri = null;
		try {
			final Properties properties = new Properties();
			final String uriString = (String) properties.getOrDefault("localhost.uri", "http://localhost:8080");
			uri = new URI(uriString);
		} catch (URISyntaxException e) {
			Printer.printException(e);
			System.exit(1);
		}
		return uri;
	}

}