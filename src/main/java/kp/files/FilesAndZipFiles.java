package kp.files;

import kp.utils.Printer;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Reading the files and the ZIP files.
 */
public class FilesAndZipFiles {

    private final Consumer<Reader> readerConsumer = reader -> {
        final CharBuffer charBuffer = CharBuffer.allocate(ConstantsForFiles.XML_CONTENT.length());
        try {
            final int characters = reader.read(charBuffer);
            if (characters < 0) {
                return;
            }
        } catch (IOException e) {
            Printer.printIOException(e);
            return;
        }
        charBuffer.flip();
        Printer.printObject(charBuffer);
    };

    /**
     * Reads the files.
     */
    public void readFiles() {

        final Consumer<InputStream> inputStreamConsumer = inputStream -> {
            final byte[] bytes = new byte[ConstantsForFiles.XML_CONTENT.length()];
            try {
                int totalNumberOfBytesRead = inputStream.read(bytes);
                if (totalNumberOfBytesRead < 0) {
                    Printer.print("There is no more data to read");
                }
            } catch (IOException e) {
                Printer.printIOException(e);
                System.exit(1);
            }
            final String content = new String(bytes);
            Printer.print(content);
        };
        final Consumer<InputStream> inputStreamConsumerWithReadAll = inputStream -> {
            byte[] bytes = null;
            try {
                bytes = inputStream.readAllBytes();
            } catch (IOException e) {
                Printer.printIOException(e);
                System.exit(1);
            }
            final String content = new String(bytes);
            Printer.print(content.substring(0, ConstantsForFiles.XML_CONTENT.length()));
        };
        final Consumer<InputStream> inputStreamConsumerWithTransfer = inputStream -> {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                inputStream.transferTo(outputStream);
            } catch (IOException e) {
                Printer.printIOException(e);
                System.exit(1);
            }
            final String content = outputStream.toString();
            Printer.print(content.substring(0, ConstantsForFiles.XML_CONTENT.length()));
        };
        try {
            // faster
            Printer.print("▼▼▼ Files.newInputStream(...) ▼▼▼▼▼▼▼▼▼ with 'read(...)'");
            try (InputStream inputStream = Files.newInputStream(ConstantsForFiles.EXAMPLE_XML_PATH)) {
                inputStreamConsumer.accept(inputStream);
            }
            Printer.printEndLineOfTriangles();

            Printer.print("▼▼▼ Files.newInputStream(...) ▼▼▼▼▼▼▼▼▼ with 'readAllBytes()'");
            try (InputStream inputStream = Files.newInputStream(ConstantsForFiles.EXAMPLE_XML_PATH)) {
                inputStreamConsumerWithReadAll.accept(inputStream);
            }
            Printer.printEndLineOfTriangles();

            Printer.print("▼▼▼ Files.newInputStream(...) ▼▼▼▼▼▼▼▼▼ with 'transferTo()'");
            try (InputStream inputStream = Files.newInputStream(ConstantsForFiles.EXAMPLE_XML_PATH)) {
                inputStreamConsumerWithTransfer.accept(inputStream);
            }
            Printer.printEndLineOfTriangles();

            // for big files 'FileInputStream' is faster
            Printer.print("▼▼▼ new FileInputStream(...) ▼▼▼▼▼▼▼▼▼▼");
            try (InputStream inputStream = new FileInputStream(ConstantsForFiles.EXAMPLE_XML_PATH.toFile())) {
                inputStreamConsumer.accept(inputStream);
            }
            Printer.printEndLineOfTriangles();

            final FileChannel fileChannel1 = FileChannel.open(ConstantsForFiles.EXAMPLE_XML_PATH);
            final InputStream inputStream1 = Channels.newInputStream(fileChannel1);
            Printer.print("▼▼▼ Channels.newInputStream(...) ▼▼▼▼▼▼");
            try (fileChannel1; inputStream1) {
                inputStreamConsumer.accept(inputStream1);
            }
            Printer.printEndLineOfTriangles();

            // faster
            final FileChannel fileChannel2 = FileChannel.open(ConstantsForFiles.EXAMPLE_XML_PATH);
            final Reader reader1 = Channels.newReader(fileChannel2, StandardCharsets.UTF_8);
            Printer.print("▼▼▼ Channels.newReader(...) ▼▼▼▼▼▼▼▼▼▼▼");
            try (fileChannel2; reader1) {
                readerConsumer.accept(reader1);
            }
            Printer.printEndLineOfTriangles();

            Printer.print("▼▼▼ new InputStreamReader(...) ▼▼▼▼▼▼▼▼");
            try (final InputStream inputStream2 = Files.newInputStream(ConstantsForFiles.EXAMPLE_XML_PATH);
                 final Reader reader2 = new InputStreamReader(inputStream2, StandardCharsets.UTF_8)) {
                readerConsumer.accept(reader2);
            }
            Printer.printEndLineOfTriangles();

            Printer.print("▼▼▼ Files.newBufferedReader(...) ▼▼▼▼▼▼");
            try (BufferedReader bufferedReader = Files.newBufferedReader(ConstantsForFiles.EXAMPLE_XML_PATH)) {
                final Stream<String> linesStream = bufferedReader.lines();
                linesStream.findFirst().ifPresent(Printer::print);
            }
            Printer.printEndLineOfTriangles();
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        Printer.printHor();
    }

    /**
     * Lists the files.
     */
    public void listFiles() {
        try {
            final Consumer<Path> pathConsumer = path -> Printer.printf("path[%s], absolute[%s], file[%s]", path,
                    path.toAbsolutePath(), path.getFileName());
            Printer.print("▼▼▼ list '*.xml' and '*.xsd' files ▼▼▼▼▼▼▼▼▼▼▼▼ with 'DirectoryStream'");
            try (DirectoryStream<Path> dirStream = Files
                    .newDirectoryStream(ConstantsForFiles.EXAMPLE_XML_PATH.getParent(), "*.{xml,xsd}")) {
                dirStream.forEach(pathConsumer);
            }
            Printer.printEndLineOfTriangles();

            final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:**\\\\*.zip");
            Printer.print("▼▼▼ list '*.zip' files             ▼▼▼▼▼▼▼▼▼▼▼▼ with 'Files.walk(...)'");
            try (Stream<Path> pathStream = Files.walk(ConstantsForFiles.EXAMPLE_XML_PATH.getParent())) {
                pathStream.filter(pathMatcher::matches).forEach(pathConsumer);
            }
            Printer.printEndLineOfTriangles();

            Printer.print("▼▼▼ list '*.zip' files             ▼▼▼▼▼▼▼▼▼▼▼▼ with 'Files.list(...)'");
            try (Stream<Path> pathStream = Files.list(ConstantsForFiles.EXAMPLE_XML_PATH.getParent())) {
                pathStream.filter(pathMatcher::matches).forEach(pathConsumer);
            }
            Printer.printEndLineOfTriangles();
            // java.io.FileFilter - since Java 1.2
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        Printer.printHor();
    }

    /**
     * Reads the ZIP files.
     */
    public void readZipFiles() {

        final BiConsumer<ZipFile, ZipEntry> zipConsumer = (zipFile, zipEntry) -> {
            try (Reader reader = new BufferedReader(
                    new InputStreamReader(
                            zipFile.getInputStream(zipEntry)))) {
                readerConsumer.accept(reader);
            } catch (IOException e) {
                Printer.printIOException(e);
                System.exit(1);
            }
        };
        final Predicate<ZipEntry> predicate = zipEntry -> ConstantsForFiles.EXAMPLE_ZIP_ENTRY
                .equals(zipEntry.getName());
        Printer.print("▼▼▼ zipFile.stream() ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");
        try (ZipFile zipFile = new ZipFile(ConstantsForFiles.EXAMPLE_ZIP_PATH.toString())) {
            final Optional<? extends ZipEntry> zipEntryOpt = zipFile.stream().filter(predicate).findFirst();
            if (zipEntryOpt.isEmpty()) {
                Printer.print("No zip entry in zip file!");
                return;
            }
            zipConsumer.accept(zipFile, zipEntryOpt.get());
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        Printer.printEndLineOfTriangles();
        /*-
         * ZIP file system allows copying, moving, and renaming files.
         */
        try (FileSystem fileSystem = FileSystems.newFileSystem(ConstantsForFiles.EXAMPLE_ZIP_PATH)) {
            final Path pathInZipFile = fileSystem.getPath(ConstantsForFiles.EXAMPLE_ZIP_ENTRY);
            Printer.print("▼▼▼ ZIP File as FileSystem ▼▼▼▼▼▼▼▼▼▼▼▼");
            try (InputStream inputStream = Files.newInputStream(pathInZipFile);
                 final Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                readerConsumer.accept(reader);
            }
            Printer.printEndLineOfTriangles();
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        Printer.printHor();
    }

    /**
     * Lists the files in the ZIP file.
     */
    public void listFilesInZipFile() {

        final Consumer<ZipEntry> entryConsumer = entry -> Printer.printf("zipped %s[%s]",
                entry.isDirectory() ? " dir" : "file", entry.getName());
        Printer.print("▼▼▼ list files in zip file ▼▼▼▼▼▼▼▼▼▼▼▼");
        try (ZipFile zipFile = new ZipFile(ConstantsForFiles.EXAMPLE_ZIP_PATH.toString())) {
            zipFile.stream().forEach(entryConsumer);
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        Printer.printEndLineOfTriangles();
        Printer.printHor();
    }
}