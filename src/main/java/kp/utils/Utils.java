package kp.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Utilities.
 */
public interface Utils {

    /**
     * Calculates elapsed times.
     *
     * @param instantList the instant list
     * @return the formatted message with elapsed times
     */
    static String calculateElapsedTimes(List<Instant> instantList) {

        if (instantList.size() < 2) {
            return "";
        }
        final StringBuilder strBuf = new StringBuilder();
        for (int i = 0; i < instantList.size() - 1; i++) {
            strBuf.append(String.format("%d.diff[%3dms]", i + 1,
                    Duration.between(instantList.get(i), instantList.get(i + 1)).toMillis()));
            if (i < instantList.size() - 2) {
                strBuf.append(", ");
            }
        }
        return strBuf.toString();
    }

    /**
     * Breaks the line using the given line length.
     *
     * @param message the input message
     * @param length  the line length
     * @return the output message
     */
    static String breakLine(String message, int length) {

        final StringBuilder strBuf = new StringBuilder();
        int begin = 0;
        int end = 0;
        while (end < message.length()) {
            if (begin + length < message.length()) {
                end = end + length;
            } else {
                end = message.length();
            }
            strBuf.append(message, begin, end).append(System.lineSeparator());
            begin = end;
        }
        return strBuf.toString();
    }

    /**
     * Formats elapsed time.
     *
     * @param start  the start
     * @param finish the finish
     * @return the formatted elapsed time
     */
    static String formatElapsed(Instant start, Instant finish) {
        return formatElapsed("time elapsed", Duration.between(start, finish));
    }

    /**
     * Formats elapsed time.
     *
     * @param label    the label
     * @param duration the duration
     * @return the formatted elapsed time
     */
    static String formatElapsed(String label, Duration duration) {

        final long time = duration.toNanos();
        final long nanos = time % 1_000;
        final long micros = time / 1_000 % 1_000;
        final long millis = time / 1_000_000 % 1_000;
        final long seconds = time / 1_000_000_000;
        if (seconds > 0) {
            return String.format("%s[%ds %3dms %3dμs %3dns]", label, seconds, millis, micros, nanos);
        } else if (millis > 0) {
            return String.format("%s[%3dms %3dμs %3dns]", label, millis, micros, nanos);
        } else if (micros > 0) {
            return String.format("%s[%3dμs %3dns]", label, micros, nanos);
        } else {
            return String.format("%s[%3dns]", label, nanos);
        }
    }

    /**
     * Formats number.
     *
     * @param number the number
     * @return the formatted string
     */
    static String formatNumber(long number) {

        final NumberFormat numberFormat = NumberFormat.getInstance();
        if (!(numberFormat instanceof DecimalFormat decimalFormat)) {
            return numberFormat.format(number);
        }
        final DecimalFormatSymbols decimalFormatSymbols = decimalFormat.getDecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator('\'');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        return decimalFormat.format(number);
    }

    /**
     * Pauses for milliseconds.
     *
     * @param milliseconds the milliseconds
     */
    static void sleepMillis(int milliseconds) {

        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();// Preserve interrupt status
        }
    }
}