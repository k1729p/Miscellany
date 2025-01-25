package kp.records;

import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * The border around a text.
 */
public interface BorderAroundText {
    /**
     * Formats the text with a specific border.
     *
     * @param text the text to format
     * @return the formatted text
     */
    String format(String text);

    /**
     * The format function.
     */
    Function<String, BiFunction<String, String, String>> FORMAT_FUN = text ->
            (format, border) -> String.format(format,
                    String.join("", Collections.nCopies(text.length(), border)), text);

    /**
     * The light border.
     *
     * @param boxFormat the box format
     * @param boxBorder the box border
     */
    record Light(String boxFormat, String boxBorder) implements BorderAroundText {
        /**
         * Creates the default light border.
         *
         * @return the light border
         */
        public static BorderAroundText of() {
            return new Light("""
                    ┌─%1$s─┐
                    │ %2$s │
                    └─%1$s─┘""", "─");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String format(String text) {
            return FORMAT_FUN.apply(text).apply(boxFormat, boxBorder);
        }

    }

    /**
     * The double border.
     *
     * @param boxFormat the box format
     * @param boxBorder the box border
     */
    record Double(String boxFormat, String boxBorder) implements BorderAroundText {
        /**
         * Creates the default double border.
         *
         * @return the double border
         */
        public static BorderAroundText of() {
            return new Double("""
                    ╔═%1$s═╗
                    ║ %2$s ║
                    ╚═%1$s═╝""", "═");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String format(String text) {
            return FORMAT_FUN.apply(text).apply(boxFormat, boxBorder);
        }
    }

    /**
     * The border with heavy left and right sides.
     *
     * @param boxFormat the box format
     * @param boxBorder the box border
     */
    record HeavySides(String boxFormat, String boxBorder) implements BorderAroundText {
        /**
         * Creates the default heavy sides border.
         *
         * @return the heavy sides border
         */
        public static BorderAroundText of() {
            return new HeavySides("""
                    ▄─%1$s─▄
                    █ %2$s █
                    ▀─%1$s─▀""", "─");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String format(String text) {
            return FORMAT_FUN.apply(text).apply(boxFormat, boxBorder);
        }
    }

    /**
     * The border with all heavy sides.
     *
     * @param boxFormat the box format
     * @param boxBorder the box border
     */
    record AllHeavy(String boxFormat, String boxBorder) implements BorderAroundText {
        /**
         * Creates the default all heavy border.
         *
         * @return the all heavy border
         */
        public static BorderAroundText of() {
            return new AllHeavy("""
                    █▀%1$s▀█
                    █ %2$s █
                    ▀▀%1$s▀▀""", "▀");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String format(String text) {
            return FORMAT_FUN.apply(text).apply(boxFormat, boxBorder);
        }
    }

}
