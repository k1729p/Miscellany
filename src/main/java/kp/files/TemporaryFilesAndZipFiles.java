package kp.files;

import kp.utils.Printer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Writing and reading the temporary files and the ZIP files.
 */
public class TemporaryFilesAndZipFiles {

    /**
     * The constructor.
     */
    public TemporaryFilesAndZipFiles() {
        final Path previousTmpDir = Paths.get(System.getProperty(ConstantsForFiles.TMP_DIR_KEY));
        System.setProperty(ConstantsForFiles.TMP_DIR_KEY, ConstantsForFiles.TMP_DIR_VALUE);
        Printer.printf("Temporary directory from property 'java.io.tmpdir': previous[%s], actual[%s]", previousTmpDir,
                Paths.get(System.getProperty(ConstantsForFiles.TMP_DIR_KEY)));
        Printer.printHor();
    }

    /**
     * Writes and reads temporary files.
     */
    public void writeAndReadTemporaryFiles() {

        try {
            final Path tempDirectory = Files
                    .createTempDirectory(Path.of(System.getProperty(ConstantsForFiles.TMP_DIR_KEY)), "example");
            Printer.printf("Created temporary directory[%s]", tempDirectory);
            final Path tempSubDirectory = Files.createDirectories(tempDirectory.resolve("dir1").resolve("dir2"));
            Printer.printf("Created temporary subdirectory[%s]", tempSubDirectory);

            final Path fileInTempDir = tempDirectory.resolve(ConstantsForFiles.EXAMPLE_TXT_FILE_NAME);
            Printer.printf("Created  temporary file[%s]", fileInTempDir);
            Files.write(fileInTempDir, ConstantsForFiles.CONTENT_LIST, Charset.defaultCharset());

            Printer.printf("▼▼▼ From temporary file[%s] (lines stream) ▼▼▼", fileInTempDir);
            try (Stream<String> linesStream = Files.lines(fileInTempDir)) {
                linesStream.forEach(Printer::print);
            }
            Printer.printEndLineOfTriangles();
            Printer.printf("▼▼▼ From temporary file[%s] (read string) ▼▼▼", fileInTempDir);
            Files.writeString(fileInTempDir, "example string");
            Printer.print(Files.readString(fileInTempDir));
            Printer.printEndLineOfTriangles();
            final UnaryOperator<File> printMapper = arg -> {
                Printer.printf("Deleting temporary file [%s]", arg);
                return arg;
            };
            try (Stream<Path> pathStream = Files.walk(tempDirectory)) {
                pathStream.sorted(Comparator.reverseOrder()).map(Path::toFile).map(printMapper)//
                        .forEach(this::deleteFileWithCheck);
            }

            final String[] prefixAndSuffix = ConstantsForFiles.EXAMPLE_TXT_FILE_NAME.split("\\.");
            final Path tempFile = Files.createTempFile(Path.of(System.getProperty(ConstantsForFiles.TMP_DIR_KEY)),
                    prefixAndSuffix[0], ".".concat(prefixAndSuffix[1]));
            Printer.printf("%nCreated  temporary file[%s]", tempFile);
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(tempFile)) {
                for (String line : ConstantsForFiles.CONTENT_LIST) {
                    bufferedWriter.write(line.concat(System.lineSeparator()));
                }
            }
            Printer.printf("▼▼▼ From temporary file[%s] ▼▼▼", tempFile);
            try (BufferedReader bufferedReader = Files.newBufferedReader(tempFile)) {
                bufferedReader.lines().forEach(Printer::print);
            }
            Printer.printEndLineOfTriangles();
            Printer.printf("Deleting temporary file[%s], result[%b]", tempFile, Files.deleteIfExists(tempFile));
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        Printer.printHor();
    }

    /**
     * Writes and reads temporary ZIP files.
     */
    public void writeAndReadTemporaryZipFiles() {

        try {
            final String prefix = ConstantsForFiles.EXAMPLE_ZIP_FILE_NAME.substring(0,
                    ConstantsForFiles.EXAMPLE_ZIP_FILE_NAME.indexOf('.'));
            final String suffix = ConstantsForFiles.EXAMPLE_ZIP_FILE_NAME
                    .substring(ConstantsForFiles.EXAMPLE_ZIP_FILE_NAME.indexOf('.'));
            final Path tempFile = Files.createTempFile(Path.of(System.getProperty(ConstantsForFiles.TMP_DIR_KEY)),
                    prefix, suffix);
            Printer.printf("Created  temporary file[%s]", tempFile);
            writeZipFileContent(tempFile);
            readZipFileContent(tempFile);
            Printer.printf("Deleting temporary file[%s], result[%b]", tempFile, Files.deleteIfExists(tempFile));
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        Printer.printHor();
    }

    /**
     * Deletes the file with result check.
     *
     * @param file the file
     */
    private void deleteFileWithCheck(final File file) {

        try {
            Files.delete(file.toPath());
        } catch (SecurityException | IOException e) {
            Printer.printException("exception", e);
            System.exit(1);
        }
    }

    /**
     * Writes the content to the ZIP file.
     *
     * @param tempFile the temporary file
     */
    private void writeZipFileContent(final Path tempFile) {

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(//
                Files.newOutputStream(tempFile))) {
            final ZipEntry zipEntry = new ZipEntry(ConstantsForFiles.EXAMPLE_ZIP_ENTRY);
            zipOutputStream.putNextEntry(zipEntry);
            Printer.printf("Created zip entry[%s]", zipEntry);
            zipOutputStream.write(ConstantsForFiles.XML_CONTENT.getBytes());
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
    }

    /**
     * Reads the content from the ZIP file.
     *
     * @param tempFile the temporary file
     */
    private void readZipFileContent(final Path tempFile) {

        final Predicate<ZipEntry> predicate = zipEntry -> ConstantsForFiles.EXAMPLE_ZIP_ENTRY
                .equals(zipEntry.getName());
        final BiConsumer<ZipFile, ZipEntry> zipConsumer = (zipFile, zipEntry) -> {
            try (BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(zipFile.getInputStream(zipEntry)))) {
                bufferedReader.lines().forEach(Printer::print);
            } catch (IOException e) {
                Printer.printIOException(e);
                System.exit(1);
            }
        };
        try (ZipFile zipFile = new ZipFile(tempFile.toString())) {
            final Optional<? extends ZipEntry> zipEntryOpt = zipFile.stream().filter(predicate).findFirst();
            if (zipEntryOpt.isEmpty()) {
                Printer.print("No zip entry in zip file!");
                return;
            }
            Printer.printf("▼▼▼ From temporary file[%s], zip entry[%s] ▼▼▼", tempFile, zipEntryOpt.get().getName());
            zipConsumer.accept(zipFile, zipEntryOpt.get());
            Printer.printEndLineOfTriangles();
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
    }
}
