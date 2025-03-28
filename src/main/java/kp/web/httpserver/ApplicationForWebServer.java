package kp.web.httpserver;

import kp.utils.Printer;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * The HTTP server starter and the default browser launcher.
 */
public interface ApplicationForWebServer {

    /**
     * The primary entry point for launching the application.
     *
     * @param args the command-line arguments
     */
    static void main(String[] args) {

        if (!Desktop.isDesktopSupported()) {
            Printer.print("Desktop not supported!");
            System.exit(1);
        }
        final String uriString = Optional.ofNullable(args.length > 0 ? args[0] : null)
                .filter(String::isBlank).orElse("http://localhost:8080");
        /*-
         * Web server is started in a new background thread.
         */
        WebServerLauncher.startServer();
        try {
            Desktop.getDesktop().browse(new URI(uriString));
        } catch (URISyntaxException | IOException e) {
            if (e instanceof URISyntaxException) {
                Printer.printException(e);
            } else {
                Printer.printIOException((IOException) e);
            }
            WebServerLauncher.stopServer();
            System.exit(1);
        }
    }

}