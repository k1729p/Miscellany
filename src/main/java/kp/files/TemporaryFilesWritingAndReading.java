package kp.files;

import kp.utils.Printer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Writing and reading the temporary file with bytes and the temporary file with
 * strings.
 */
public class TemporaryFilesWritingAndReading {

    /**
     * Writes to file and reads from file the bytes array.
     */
    public void writeAndReadBytes() {

        byte[] bytes = null;
        final Path tempFile = createTempFile();
        try {
            try (OutputStream outputStream = Files.newOutputStream(tempFile)) {
                outputStream.write(ConstantsForFiles.TEXT_LINES.getBytes());
            }
            try (InputStream inputStream = Files.newInputStream(tempFile)) {
                bytes = inputStream.readAllBytes();
            }
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        final String text = Stream.of(bytes).map(String::new).reduce(String::concat)
                .orElse("");
        Printer.printf("Temporary file[%s], file length[%d]", tempFile, tempFile.toFile().length());
        Printer.printf("▼▼▼ Bytes from file[%s] ▼▼▼", tempFile.getFileName());
        Printer.print(text);
        Printer.printEndLineOfTriangles();
        Printer.printHor();
    }

    /**
     * Writes to file and reads from file the string.
     */
    public void writeAndReadString() {

        final Path tempFile = createTempFile();
        String text = null;
        try {
            try (Writer writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8)) {
                writer.write(ConstantsForFiles.TEXT_LINES);
            }
            try (BufferedReader reader = Files.newBufferedReader(tempFile, StandardCharsets.UTF_8)) {
                text = reader.lines().map(str -> str.concat(System.lineSeparator())).reduce(String::concat).orElse("");
            }
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        Printer.printf("Temporary file[%s], file length[%d]", tempFile, tempFile.toFile().length());
        Printer.printf("▼▼▼ String from file[%s] ▼▼▼", tempFile.getFileName());
        Printer.print(text.strip());
        Printer.printEndLineOfTriangles();
        Printer.printHor();
    }

    /**
     * Creates the temporary file.
     *
     * @return the temporary file
     */
    private Path createTempFile() {

        try {
            return Files.createTempFile(Path.of(System.getProperty(ConstantsForFiles.TMP_DIR_KEY)), "data", ".txt");
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        return null;
    }

}
