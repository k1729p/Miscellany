package kp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * The wrapper on 'Java Util Logging' with simplified format.<br>
 * It prints only the message without any other items (like date, level).
 * <p>
 * The code which uses 'System.out.println' is noncompliant in 'SonarQube'.
 */
public class Printer {
	/**
	 * The logger
	 */
	private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	static {
		final InputStream inputStream = Printer.class.getClassLoader().getResourceAsStream("logging.properties");
		try (inputStream) {
			readLogConfiguration(inputStream);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * The constructor.
	 */
	private Printer() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Prints the message to the console with simplified logger.
	 * 
	 * @param message the message
	 */
	public static void print(String message) {

		if (logger.isLoggable(Level.INFO)) {
			logger.info(message);
		}
	}

	/**
	 * Formats the message and prints it to the console with simplified logger.
	 * 
	 * @param format the message format
	 * @param args   the message items
	 */
	public static void printf(String format, Object... args) {

		if (logger.isLoggable(Level.INFO)) {
			logger.info(String.format(format, args));
		}
	}

	/**
	 * Prints a string representation of the object.
	 * 
	 * @param object the {@link Object}
	 */
	public static void printObject(Object object) {
		print(object.toString());
	}

	/**
	 * Prints the horizontal rule.
	 * 
	 */
	public static void printHor() {
		print("- ".repeat(50));
	}

	/**
	 * Prints the end line from the up-pointing triangles.
	 * 
	 */
	public static void printEndLineOfTriangles() {
		print("▲".repeat(40));
	}

	/**
	 * Prints the top borderline.
	 * 
	 */
	public static void printTopBorderLine() {
		print("▄ ".repeat(25));
	}

	/**
	 * Prints the bottom borderline.
	 * 
	 */
	public static void printBottomBorderLine() {
		print("▀ ".repeat(25));
	}

	/**
	 * Prints the low line.
	 * 
	 */
	public static void printLowLine() {
		print("_".repeat(100));
	}

	/**
	 * Prints the overline.
	 * 
	 */
	public static void printOverline() {
		print("‾".repeat(100));
	}

	/**
	 * Prints the separator line.
	 * 
	 */
	public static void printSeparatorLine() {
		print("▀ ▄ ▀ ▄ ▀ ▄");
	}

	/**
	 * Prints the exception message.
	 * 
	 * @param exception the exception
	 */
	public static void printException(Throwable exception) {
		logger.log(Level.SEVERE, exception.getMessage(), exception);
	}

	/**
	 * Prints the exception message.
	 * 
	 * @param message   the message
	 * @param exception the exception
	 */
	public static void printException(String message, Throwable exception) {
		logger.log(Level.SEVERE, String.format("%s Exception[%s]", message, exception.getMessage()), exception);
	}

	/**
	 * Prints the {@link InterruptedException} message.
	 * 
	 * @param exception the {@link InterruptedException}
	 */
	public static void printInterruptedException(InterruptedException exception) {
		logger.log(Level.SEVERE, String.format("InterruptedException[%s]", exception.getMessage()), exception);
	}

	/**
	 * Prints the {@link ExecutionException} message.
	 * 
	 * @param exception the {@link ExecutionException}
	 */
	public static void printExecutionException(ExecutionException exception) {
		logger.log(Level.SEVERE, String.format("ExecutionException[%s]", exception.getMessage()), exception);
	}

	/**
	 * Prints the {@link IOException} message.
	 * 
	 * @param exception the {@link IOException}
	 */
	public static void printIOException(IOException exception) {
		logger.log(Level.SEVERE, String.format("IOException[%s]", exception.getMessage()), exception);
	}

	/**
	 * Reads the log configuration.
	 * 
	 * 
	 * @param inputStream the {@link InputStream}
	 * @throws IOException the {@link IOException}
	 */
	@SuppressWarnings("java:S4792") // SonarQube: configuring loggers is security-sensitive
	private static void readLogConfiguration(InputStream inputStream) throws IOException {
		LogManager.getLogManager().readConfiguration(inputStream);
	}

}
