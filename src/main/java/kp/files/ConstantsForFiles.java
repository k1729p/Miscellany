package kp.files;

import java.nio.file.Path;

/**
 * The constants for files and directories.
 *
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
	public static final Path EXAMPLE_PROPERTIES_PATH = Path.of(System.getProperty("user.dir"), EXAMPLE_DIR.toString(),
			"example.properties");
	/**
	 * Length of string to read.
	 */
	public static final String XML_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	private ConstantsForFiles() {
		throw new IllegalStateException("Utility class");
	}
}
