package kp.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * A wrapper for 'Java Util Logging' with a simplified format.<br>
 * It prints only the message without any other items (like date, level).
 * <p>
 * Because code using 'System.out.println' is noncompliant with SonarQube warnings,
 * this utility class was created for the 'Miscellany' project.
 */
public class Printer {
    /**
     * The logger
     */
    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    static {
        final InputStream inputStream = Printer.class.getClassLoader().getResourceAsStream("logging.properties");
        if (Objects.nonNull(inputStream)) {
            try (inputStream) {
                readLogConfiguration(inputStream);
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            logger.log(Level.SEVERE, "file 'logging.properties' not found");
        }
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private Printer() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Prints the message to the console with a simplified logger.
     *
     * @param message the message
     */
    public static void print(String message) {

        if (logger.isLoggable(Level.INFO)) {
            logger.info(message);
        }
    }

    /**
     * Formats the message and prints it to the console with a simplified logger.
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
     * Prints a horizontal rule.
     */
    public static void printHor() {
        print("- ".repeat(50));
    }

    /**
     * Prints an end line made of up-pointing triangles.
     */
    public static void printEndLineOfTriangles() {
        print("▲".repeat(40));
    }

    /**
     * Prints the top border line.
     */
    public static void printTopBorderLine() {
        print("▄ ".repeat(25));
    }

    /**
     * Prints the bottom border line.
     */
    public static void printBottomBorderLine() {
        print("▀ ".repeat(25));
    }

    /**
     * Prints a low line.
     */
    public static void printLowLine() {
        print("_".repeat(100));
    }

    /**
     * Prints an overline.
     */
    public static void printOverline() {
        print("‾".repeat(100));
    }

    /**
     * Prints a separator line.
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
     * @param inputStream the {@link InputStream}
     * @throws IOException the {@link IOException}
     */
    @SuppressWarnings("java:S4792") // SonarQube: configuring loggers is security-sensitive
    private static void readLogConfiguration(InputStream inputStream) throws IOException {
        LogManager.getLogManager().readConfiguration(inputStream);
    }

}
