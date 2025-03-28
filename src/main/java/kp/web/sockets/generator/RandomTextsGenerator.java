package kp.web.sockets.generator;

import kp.utils.Printer;
import kp.utils.Utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

/**
 * The random text generator.
 */
public class RandomTextsGenerator {

    private static final int RANDOM_TEXT_LENGTH = 100_000; // it is OK with length 100_000_000
    private static final boolean USE_RANDOM_TEXTS_FILE = true;
    private static final Path RANDOM_TEXTS_FILE = Paths.get(System.getProperty("java.io.tmpdir"))
            .resolve("RandomTexts.txt");

    private List<String> textsList;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Constructs a RandomTextsGenerator instance and initializes the list of random texts.
     *
     * @param iterations the number of iterations for generating random texts; defines the size of the text list.
     */
    public RandomTextsGenerator(int iterations) {

        final Instant start = Instant.now();
        initialize(iterations);
        final Instant finish = Instant.now();
        Printer.printf("RandomTextsGenerator(): text length[%s], use random texts file[%b], iterations[%d], %s",
                Utils.formatNumber(RANDOM_TEXT_LENGTH), USE_RANDOM_TEXTS_FILE, iterations,
                Utils.formatElapsed(start, finish));
    }

    /**
     * Gets text from the list for the server.
     *
     * @param number the text number
     * @return the text element
     */
    public String getTextForServer(int number) {
        return textsList.get(number - 1);
    }

    /**
     * Gets text from the list for the client.
     *
     * @param number the text number
     * @return the text element
     */
    public String getTextForClient(int number) {
        return textsList.get(textsList.size() / 2 + number - 1);
    }

    /**
     * Initializes the random text list.
     *
     * @param iterations the number of iterations
     */
    private void initialize(int iterations) {

        final int listSize = 2 * iterations;
        try {
            boolean fileReadingFlag = false;
            if (USE_RANDOM_TEXTS_FILE && RANDOM_TEXTS_FILE.toFile().canRead()) {
                textsList = Files.readAllLines(RANDOM_TEXTS_FILE);
                fileReadingFlag = listSize == textsList.size();
            }
            if (!fileReadingFlag) {
                Printer.printf("initialize(): failed reading from random texts file[%s]. Generating ...",
                        RANDOM_TEXTS_FILE);
                generateTextsList(iterations, listSize);
            }
        } catch (IOException e) {
            Printer.printf("initialize(): IOException[%s], random texts file[%s]", e.getMessage(), RANDOM_TEXTS_FILE);
            System.exit(1);
        }
    }

    /**
     * Generates the texts list.
     *
     * @param listSize      the size of the list
     * @param totalListSize the total size of the list
     * @throws IOException if an I/O error occurs
     */
    private void generateTextsList(int listSize, int totalListSize) throws IOException {

        final IntFunction<String> stampFunc = index -> String.format("#%03d",
                index <= listSize ? index : index - listSize);
        textsList = IntStream.rangeClosed(1, totalListSize).boxed()
                .map(index -> generateElement(stampFunc.apply(index))).toList();
        Files.write(RANDOM_TEXTS_FILE, textsList, Charset.defaultCharset());
    }

    /**
     * Generates a text list element.
     *
     * @param stamp the text stamp
     * @return the random text
     */
    private String generateElement(String stamp) {

        /* Generate random numbers within the range 48 (Unicode for 0) to 122 (Unicode for z).
         * Only allow numbers less than 57 (the digits 0-9) or
         * numbers greater than 65 and less than 90 (the letters A-Z) or
         * numbers greater than 97 (the letters a-z)
         */
        return secureRandom.ints(48, 123)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(RANDOM_TEXT_LENGTH - 4L)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .append(stamp).toString();
    }

}
