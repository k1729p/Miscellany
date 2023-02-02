package kp.web.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import kp.utils.Printer;

/**
 * The {@link HttpClient} launcher.
 *
 */
public class ApplicationForWebClient {

	private static final URI EXAMPLE_URI_STATUS_CODE_200 = URI.create("https://example.org/");
	private static final URI EXAMPLE_URI_STATUS_CODE_404 = URI.create("https://www.google.com/404");
	private static final int SKIP = 3;
	private static final int LIMIT = 2;

	/**
	 * The hidden constructor.
	 */
	private ApplicationForWebClient() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * The entry point for the application.
	 * 
	 * @param args the command-line arguments
	 */
	public static void main(String[] args) {

		Printer.printHor();
		sentRequestSynchronously(EXAMPLE_URI_STATUS_CODE_200);
		sentRequestSynchronously(EXAMPLE_URI_STATUS_CODE_404);
		sentRequestAsynchronously(EXAMPLE_URI_STATUS_CODE_200);
		sentRequestAsynchronously(EXAMPLE_URI_STATUS_CODE_404);
	}

	/**
	 * Sends {@link HttpRequest} and reads the {@link HttpResponse}
	 * <b>synchronously</b>.
	 * 
	 * @param uri the {@link URI} (Uniform Resource Identifier reference)
	 */
	private static void sentRequestSynchronously(URI uri) {

		final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
		final HttpRequest httpRequest = HttpRequest.newBuilder(uri).build();
		HttpResponse<String> httpResponse = null;
		try {
			httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
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
	 * Sends {@link HttpRequest} and reads the {@link HttpResponse}
	 * <b>asynchronously</b> using {@link CompletableFuture}.
	 * 
	 * @param uri the {@link URI} (Uniform Resource Identifier reference)
	 */
	private static void sentRequestAsynchronously(URI uri) {

		final HttpClient httpClient = HttpClient.newBuilder().build();
		final HttpRequest httpRequest = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(10)).build();
		final Function<Throwable, Void> exceptionFunction = thr -> {
			Printer.printException(thr);
			return null;
		};
		final CompletableFuture<Void> completableFuture = httpClient//
				.sendAsync(httpRequest, BodyHandlers.ofString())//
				.thenAcceptAsync(ApplicationForWebClient::reportResponse)//
				.exceptionallyAsync(exceptionFunction);

		Printer.print("Requests sent asynchronously:");
		CompletableFuture.allOf(completableFuture).join();
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
