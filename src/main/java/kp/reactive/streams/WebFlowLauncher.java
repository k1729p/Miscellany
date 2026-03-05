package kp.reactive.streams;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import kp.reactive.streams.impl.SubscriberImpl;
import kp.reactive.streams.impl.SubscriberImplForByteBufferList;
import kp.utils.Printer;
import kp.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Flow.Publisher;
import java.util.function.Supplier;

/**
 * Reads the content from the web server with subscribers.
 */
public class WebFlowLauncher {

    private static final int PORT = 8080;
    private static final URI LOCAL_URI = URI.create(String.format("http://localhost:%d/", PORT));
    private static final Supplier<HttpRequest> REQUEST_SUP = () -> HttpRequest.newBuilder(LOCAL_URI)
            .header("Content-Type", "text/plain; charset=UTF-8").POST(BodyPublishers.ofString("ABC")).build();

    /**
     * Private constructor to prevent instantiation.
     */
    private WebFlowLauncher() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Launches the {@link HttpServer} and the {@link HttpClient}.
     * <p>
     * Receives the response using the {@link SubscriberImpl}.
     */
    public static void receiveResponseUsingLineSubscriber() {

        final BodyHandler<Void> bodyHandler = BodyHandlers.fromLineSubscriber(new SubscriberImpl<>());
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            final HttpServer httpServer = startServer();
            /*-
             * The type is 'Void' because all response body is forwarded to the given subscriber.
             */
            final HttpResponse<Void> httpResponse = httpClient.send(REQUEST_SUP.get(), bodyHandler);
            Utils.sleepMillis(101);
            httpServer.stop(0);
            Printer.printf("receiveResponseUsingLineSubscriber(): response status code[%s]", httpResponse.statusCode());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();// Preserve interrupt status
            Printer.printInterruptedException(e);
            System.exit(1);
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        Printer.printHor();
    }

    /**
     * Launches the {@link HttpServer} and the {@link HttpClient}.
     * <p>
     * Receives the response using the {@link SubscriberImplForByteBufferList}.
     */
    public static void receiveResponseUsingSubscriberForByteBufferList() {

        final BodyHandler<Void> bodyHandler = BodyHandlers.fromSubscriber(new SubscriberImplForByteBufferList());
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            final HttpServer httpServer = startServer();
            /*-
             * The type is 'Void' because all response body is forwarded to the given subscriber.
             */
            final HttpResponse<Void> httpResponse = httpClient.send(REQUEST_SUP.get(), bodyHandler);
            httpServer.stop(0);
            Printer.printf("receiveResponseUsingSubscriberForByteBufferList(): response status code[%s]",
                    httpResponse.statusCode());
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();// Preserve interrupt status
            Printer.printInterruptedException(e);
            System.exit(1);
        }
        Printer.printHor();
    }

    /**
     * Launches the {@link HttpServer} and the {@link HttpClient}.
     * <p>
     * Receives the response using the {@link Publisher}.
     */
    public static void receiveResponseUsingPublisher() {

        final BodyHandler<Publisher<List<ByteBuffer>>> bodyHandler = BodyHandlers.ofPublisher();
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            final HttpServer httpServer = startServer();
            final HttpResponse<Publisher<List<ByteBuffer>>> httpResponse = httpClient.send(REQUEST_SUP.get(),
                    bodyHandler);
            httpServer.stop(0);
            Printer.printf("receiveResponseUsingPublisher(): response status code[%s]%n", httpResponse.statusCode());

            httpResponse.body().subscribe(new SubscriberImplForByteBufferList());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();// Preserve interrupt status
            Printer.printInterruptedException(e);
            System.exit(1);
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        Printer.printHor();
    }

    /**
     * Starts the {@link HttpServer}.
     *
     * @return the {@link HttpServer}.
     * @throws IOException if an I/O error occurs
     */
    private static HttpServer startServer() throws IOException {

        final HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/", WebFlowLauncher::handle);
        httpServer.start();
        Printer.printf("startServer(): uri[%s]", LOCAL_URI);
        return httpServer;
    }

    /**
     * Handles the response on the {@link HttpServer}.
     *
     * @param httpExchange the {@link HttpExchange}
     * @throws IOException if an I/O error occurs
     */
    private static void handle(HttpExchange httpExchange) throws IOException {

        String requestText;
        String responseText;
        try (httpExchange) {
            try (InputStream inputStream = httpExchange.getRequestBody()) {
                requestText = new String(inputStream.readAllBytes());
            }
            responseText = new StringBuilder(requestText).reverse().toString();
            final byte[] bytes = responseText.getBytes(StandardCharsets.UTF_8);

            httpExchange.getRequestHeaders().forEach(
                    (key, value) -> httpExchange.getResponseHeaders().put(key, value));
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);

            try (OutputStream outputStream = httpExchange.getResponseBody()) {
                outputStream.write(bytes);
            }
        }
        Printer.printf("handle(): received request[%s], sent response[%s]", requestText, responseText);
    }
}
