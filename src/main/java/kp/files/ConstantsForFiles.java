package kp.files;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The constants for files and directories.
 */
@SuppressWarnings("doclint:missing")
public final class ConstantsForFiles {
    /**
     * Example directory.
     */
    public static final Path EXAMPLE_DIR = Path.of("src/main/resources");
    /**
     * Example XML file.
     */
    public static final Path EXAMPLE_XML_PATH = Path.of(EXAMPLE_DIR.toString(), "example.xml");
    /**
     * Example text file name.
     */
    public static final String EXAMPLE_TXT_FILE_NAME = "example.txt";
    /**
     * Example ZIP file name.
     */
    public static final String EXAMPLE_ZIP_FILE_NAME = "example.zip";
    /**
     * Example ZIP file.
     */
    public static final Path EXAMPLE_ZIP_PATH = Path.of(EXAMPLE_DIR.toString(), EXAMPLE_ZIP_FILE_NAME);
    /**
     * Example ZIP file entry.
     */
    public static final String EXAMPLE_ZIP_ENTRY = "data/example.xml";
    /**
     * Example properties file.
     */
    public static final Path EXAMPLE_PROPERTIES_PATH = Path.of(System.getProperty("user.dir"),//
            EXAMPLE_DIR.toString(), "example.properties");
    /**
     * The key for the system property with the temporary directory value.
     */
    public static final String TMP_DIR_KEY = "java.io.tmpdir";
    /**
     * The temporary directory.
     */
    public static final String TMP_DIR_VALUE = "c:\\Temp";
    /**
     * The content list.
     */
    public static final List<String> CONTENT_LIST = List.of("The first line.", "The second line.");
    /**
     * The function for text line generation.
     */
    private static final BiFunction<Character, Character, String> TEXT_FUN = (char1, char2) -> IntStream
            .rangeClosed(char1, char2).mapToObj(num -> (char) num).map(Object::toString)
            .collect(Collectors.joining());
    /**
     * The text line with digits.
     */
    private static final String DIGITS_LINE = TEXT_FUN.apply('0', '9');
    /**
     * The text line with capital letters.
     */
    private static final String CAPITAL_LETTERS_LINE = TEXT_FUN.apply('A', 'Z');
    /**
     * The text line with small letters.
     */
    private static final String SMALL_LETTERS_LINE = TEXT_FUN.apply('a', 'z');
    /**
     * The text lines.
     */
    public static final String TEXT_LINES = DIGITS_LINE.concat(System.lineSeparator())//
            .concat(CAPITAL_LETTERS_LINE).concat(System.lineSeparator())//
            .concat(SMALL_LETTERS_LINE);
    /**
     * Length of string to read.
     */
    public static final String XML_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    private ConstantsForFiles() {
        throw new IllegalStateException("Utility class");
    }
}
