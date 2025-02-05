package kp.web.httpserver;

import com.sun.net.httpserver.HttpExchange;
import kp.utils.Printer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handlers for the web server.
 */
public class WebHandlers {
    /**
     * Path to the input tag file.
     */
    private static final Path INPUT_TAGS_FILE = new File("src/main/java/kp/web/httpserver/InputTags.html").toPath();
    /**
     * Number of characters in one combination.
     */
    private static final int NUM = 15;
    /**
     * Code points for circle emojis.
     */
    private static final List<Integer> HORIZONTAL_EMOJIS = List.of(
            0x1F534, 0x1F535, 0x1F7E0, 0x1F7E1, 0x1F7E2, 0x1F7E3, 0x1F7E4);
    /**
     * Code points for square emojis.
     */
    private static final List<Integer> VERTICAL_EMOJIS = List.of(
            0x1F7E5, 0x1F7E6, 0x1F7E7, 0x1F7E8, 0x1F7E9, 0x1F7EA, 0x1F7EB);
    /**
     * Atomic Integer for the key.
     */
    private static final AtomicInteger ATOMIC = new AtomicInteger();
    /**
     * Mapping of combining Unicode characters.
     */
    private static final Map<Integer, List<Character>> COMBINING_CHARACTERS = new LinkedHashMap<>();

    /*-
     * Copilot recommendation: If you are dealing with very exotic characters that may not be well-supported
     * across all editors and environments, it's reasonable to keep using Unicode escapes.
     */
    static {
        // U+0E49 THAI CHARACTER MAI THO
        COMBINING_CHARACTERS.put(ATOMIC.getAndIncrement(), List.of('\u0E49'));
        // U+0329 COMBINING VERTICAL LINE BELOW
        // U+030D COMBINING VERTICAL LINE ABOVE
        COMBINING_CHARACTERS.put(ATOMIC.getAndIncrement(), List.of('\u0329', '\u030D'));
        // U+0348 COMBINING DOUBLE VERTICAL LINE BELOW
        // U+030E COMBINING DOUBLE VERTICAL LINE ABOVE
        COMBINING_CHARACTERS.put(ATOMIC.getAndIncrement(), List.of('\u0348', '\u030E'));
        // U+034E COMBINING UPWARDS ARROW BELOW
        // U+035B COMBINING ZIGZAG ABOVE
        COMBINING_CHARACTERS.put(ATOMIC.getAndIncrement(), List.of('\u034E', '\u035B'));
        // U+0353 COMBINING X BELOW
        // U+033D COMBINING X ABOVE
        COMBINING_CHARACTERS.put(ATOMIC.getAndIncrement(), List.of('\u0353', '\u033D'));
        // U+031F COMBINING PLUS SIGN BELOW
        // U+0352 COMBINING FERMATA
        COMBINING_CHARACTERS.put(ATOMIC.getAndIncrement(), List.of('\u031F', '\u0352'));
        // U+0325 COMBINING RING BELOW
        // U+030A COMBINING RING ABOVE
        COMBINING_CHARACTERS.put(ATOMIC.getAndIncrement(), List.of('\u0325', '\u030A'));
        // U+0323 COMBINING DOT BELOW
        // U+0307 COMBINING DOT ABOVE
        COMBINING_CHARACTERS.put(ATOMIC.getAndIncrement(), List.of('\u0323', '\u0307'));
        // U+033A COMBINING INVERTED BRIDGE BELOW
        // U+0346 COMBINING BRIDGE ABOVE
        COMBINING_CHARACTERS.put(ATOMIC.getAndIncrement(), List.of('\u033A', '\u0346'));
        // U+0330 COMBINING TILDE BELOW
        // U+0303 COMBINING TILDE
        COMBINING_CHARACTERS.put(ATOMIC.getAndIncrement(), List.of('\u0330', '\u0303'));
        // U+0347 COMBINING EQUALS SIGN BELOW
        // U+0305 COMBINING OVERLINE
        COMBINING_CHARACTERS.put(ATOMIC.getAndIncrement(), List.of('\u0347', '\u0305'));
        // U+0332 COMBINING LOW LINE
        // U+0483 COMBINING CYRILLIC TITLO
        COMBINING_CHARACTERS.put(ATOMIC.getAndIncrement(), List.of('\u0332', '\u0483'));
        // U+0333 COMBINING DOUBLE LOW LINE
        // U+0484 COMBINING CYRILLIC PALATALIZATION
        COMBINING_CHARACTERS.put(ATOMIC.getAndIncrement(), List.of('\u0333', '\u0484'));
        // U+0E47 THAI CHARACTER MAITAIKHU
        COMBINING_CHARACTERS.put(ATOMIC.getAndIncrement(), List.of('\u0E47'));
    }

    /**
     * HTML fragment for the page beginning.
     */
    private static final String BEGIN = """
            <!DOCTYPE html>
            <html lang='en'>
            <head>
            <meta charset='utf-8'>
            """;
    /**
     * HTML fragment for the 'Home' page.
     */
    private static final String HOME_FRAG = """
            </head><body bgcolor="wheat">
            <h1><a href='input_tags'>HTML5 Input Tags</a></h1>
            <h1><a href='emojis'>Emojis</a></h1>
            <h1><a href='combining'>Combining Unicode Characters</a></h1>
            """;
    /**
     * HTML fragment for the 'Emojis' page.
     */
    private static final String EMOJIS_FRAG = """
            <style>
              div {
                font-size: 500%;
                float: left;
              }
              div.bordered {
                border-style: solid;
                border-width: thin;
                background-color: aliceblue;
              }
              span {
                writing-mode: vertical-lr;
                text-orientation: upright;
              }
            </style>
            </head><body>
            """;
    /**
     * HTML 'div' for the 'Emojis' page.
     */
    private static final String EMOJI_DIV = """
            <div class='bordered'>%s</div>
            """;
    /**
     * HTML 'div' and 'span' for the 'Emojis' page.
     */
    private static final String EMOJI_SPAN_DIV = """
            <div>
              <span>%s</span>
            </div>
            """;
    /**
     * HTML fragment for the 'Combining Unicode Characters' page.
     */
    private static final String COMBINING_FRAG = """
            <style>
              div {
                font-size: 500%;
                margin-top: 400px;
                margin-left: 80px;
                float: left;
              }
            </style>
            </head><body>
            """;

    /**
     * HTML 'div' color for the 'Combining Unicode Characters' page.
     */
    private static final List<String> DIV_COLOR = Stream.of(
                    "red", "green", "lime", "blue", "cyan", "magenta", "salmon", "darkgrey", "orange")
            .map(color -> String.format("<div style='color:%s'>%%s</div>%%n", color))
            .toList();
    /**
     * HTML fragment for the page end.
     */
    private static final String END = """
            </body>
            </html>
            """;

    /**
     * Enumeration for pages.
     */
    private enum Page {
        /**
         * 'Home' page.
         */
        HOME,
        /**
         * 'Emojis' page.
         */
        EMOJIS,
        /**
         * 'Combining Unicode Characters' page.
         */
        COMBINING
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private WebHandlers() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Handles the 'Home' page.
     *
     * @param httpExchange the {@link HttpExchange}.
     */
    static void handleHome(HttpExchange httpExchange) {

        try {
            handle(httpExchange, prepareContent(Page.HOME));
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
    }

    /**
     * Handles the 'Input Tags' page.
     *
     * @param httpExchange the {@link HttpExchange}.
     */
    static void handleInputTags(HttpExchange httpExchange) {

        try (BufferedReader bufferedReader = Files.newBufferedReader(INPUT_TAGS_FILE)) {
            handle(httpExchange,
                    bufferedReader.lines().collect(Collectors.joining(System.lineSeparator())).getBytes());
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
    }

    /**
     * Handles the 'Emojis' page.
     *
     * @param httpExchange the {@link HttpExchange}.
     */
    static void handleEmojis(HttpExchange httpExchange) {

        try {
            handle(httpExchange, prepareContent(Page.EMOJIS));
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
    }

    /**
     * Handles the 'Combining Unicode Characters' page.
     *
     * @param httpExchange the {@link HttpExchange}.
     */
    static void handleCombining(HttpExchange httpExchange) {

        try {
            handle(httpExchange, prepareContent(Page.COMBINING));
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
    }

    /**
     * Handles the response.
     *
     * @param httpExchange the {@link HttpExchange}.
     * @param bytes        the response bytes.
     * @throws IOException if an I/O error occurs.
     */
    private static void handle(HttpExchange httpExchange, byte[] bytes) throws IOException {

        httpExchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);

        try (OutputStream output = httpExchange.getResponseBody()) {
            output.write(bytes);
            output.flush();
        }
        httpExchange.close();
    }

    /**
     * Prepares the content for the given page.
     *
     * @param page the {@link Page}.
     * @return the content bytes.
     */
    private static byte[] prepareContent(Page page) {

        final StringBuilder strBld = new StringBuilder();
        strBld.append(BEGIN);
        switch (page) {
            case COMBINING -> {
                strBld.append(COMBINING_FRAG);
                final Function<Entry<Integer, List<Character>>, String> mapper =
                        entry -> String.format(
                                DIV_COLOR.get(entry.getKey() % DIV_COLOR.size()), getCombinedCharacters(entry.getValue()));
                COMBINING_CHARACTERS.entrySet().stream().map(mapper).forEach(strBld::append);
            }
            case EMOJIS -> strBld.append(EMOJIS_FRAG).append(getHorizontalEmojiDivs()).append(getVerticalEmojiDivs());
            default -> strBld.append(HOME_FRAG); // the 'Home' page
        }
        strBld.append(END);
        return strBld.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Gets combined Unicode characters.
     *
     * @param characterList the list of {@link Character} elements.
     * @return the combined characters.
     */
    private static String getCombinedCharacters(List<Character> characterList) {

        final BiConsumer<StringBuilder, Character> accumulator = (strBuf, character) -> strBuf.append(
                Collections.nCopies(NUM, character).stream().map(String::valueOf).collect(Collectors.joining()));
        return characterList.stream().collect(StringBuilder::new, accumulator, StringBuilder::append).toString();
    }

    /**
     * Gets horizontal emoji 'div' elements.
     *
     * @return the 'div' elements.
     */
    private static String getHorizontalEmojiDivs() {

        final StringBuilder strBuf = HORIZONTAL_EMOJIS.stream().collect(StringBuilder::new,
                StringBuilder::appendCodePoint, StringBuilder::append);
        return String.format(EMOJI_DIV, strBuf);
    }

    /**
     * Gets vertical emoji 'div' elements.
     *
     * @return the 'div' elements.
     */
    private static String getVerticalEmojiDivs() {

        final StringBuilder strBuf = VERTICAL_EMOJIS.stream().collect(StringBuilder::new,
                StringBuilder::appendCodePoint, StringBuilder::append);
        return String.format(EMOJI_SPAN_DIV, strBuf);
    }
}
