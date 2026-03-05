package kp.web.httpclient;

import kp.utils.Printer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * The {@link HttpClient} launcher.
 */
public class ApplicationForWebClient {

    private static final URI EXAMPLE_URI_STATUS_CODE_200 = URI.create("https://example.org/");
    private static final URI EXAMPLE_URI_STATUS_CODE_404 = URI.create("https://www.google.com/404");
    private static final int SKIP = 3;
    private static final int LIMIT = 2;

    /**
     * Private constructor to prevent instantiation.
     */
    private ApplicationForWebClient() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The primary entry point for launching the application.
     *
     */
    public static void main() {

        Printer.printHor();
        sendRequestSynchronously(EXAMPLE_URI_STATUS_CODE_200);
        sendRequestSynchronously(EXAMPLE_URI_STATUS_CODE_404);
        sendRequestAsynchronously(EXAMPLE_URI_STATUS_CODE_200);
        sendRequestAsynchronously(EXAMPLE_URI_STATUS_CODE_404);
    }

    /**
     * Sends an {@link HttpRequest} and reads the {@link HttpResponse} synchronously.
     *
     * @param uri the {@link URI} (Uniform Resource Identifier reference)
     */
    private static void sendRequestSynchronously(URI uri) {

        final HttpRequest httpRequest = HttpRequest.newBuilder(uri).build();
        HttpResponse<String> httpResponse = null;
        try (HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()) {
            httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserve interrupt status
            Printer.printInterruptedException(e);
            System.exit(1);
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        Printer.print("Requests sent synchronously:");
        reportResponse(httpResponse);
        Printer.printHor();
    }

    /**
     * Sends an {@link HttpRequest} and reads the {@link HttpResponse} asynchronously
     * using {@link CompletableFuture}.
     *
     * @param uri the {@link URI} (Uniform Resource Identifier reference)
     */
    private static void sendRequestAsynchronously(URI uri) {

        final HttpRequest httpRequest = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(10)).build();
        final Function<Throwable, Void> exceptionHandler = throwable -> {
            Printer.printException(throwable);
            return null;
        };
        try (HttpClient httpClient = HttpClient.newBuilder().build()) {
            final CompletableFuture<Void> completableFuture = httpClient
                    .sendAsync(httpRequest, BodyHandlers.ofString())
                    .thenAcceptAsync(ApplicationForWebClient::reportResponse)
                    .exceptionallyAsync(exceptionHandler);
            Printer.print("Requests sent asynchronously:");
            completableFuture.join();
        }
        Printer.printHor();
    }

    /**
     * Reports the {@link HttpResponse} status code and content.
     *
     * @param httpResponse the {@link HttpResponse}
     */
    private static void reportResponse(HttpResponse<String> httpResponse) {

        Printer.printf("Response status code[%s]", httpResponse.statusCode());
        Printer.printf("Response body from line number[%d] to line number[%d]:", SKIP, SKIP + LIMIT);
        Printer.printTopBorderLine();
        httpResponse.body().lines().skip(SKIP).limit(LIMIT).forEach(Printer::print);
        Printer.printBottomBorderLine();
    }

}
