package kp.files.visitors;

import kp.utils.Printer;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The extension of the {@link SimpleFileVisitor}.
 */
public class SimpleFileVisitorExtension extends SimpleFileVisitor<Path> {

    /**
     * The excluded directories
     */
    static final List<String> EXCLUDED_DIRECTORIES = List.of("resources");
    private static final List<String> KEYWORDS = List.of("class VisitorWrapper", "cryptographic nonce");
    private static final List<String> EXCLUDED_FILES = List.of("html");
    private static final List<Pattern> PATTERNS = KEYWORDS.stream()
            .map(key -> Pattern.compile(".*".concat(key).concat(".*"), Pattern.CASE_INSENSITIVE)).toList();
    private static final boolean VERBOSE = false;

    private final Map<Path, Map<String, List<String>>> resultMap = new TreeMap<>();

    /**
     * Gets the result map
     *
     * @return the resultMap
     */
    public Map<Path, Map<String, List<String>>> getResultMap() {
        return resultMap;
    }

    /**
     * Gets the visit file function
     *
     * @return the visit file function
     */
    public Function<Path, FileVisitResult> getVisitFileFunction() {
        return path -> visitFile(path, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) {

        if (Objects.isNull(path.getFileName()) || EXCLUDED_DIRECTORIES.contains(path.getFileName().toString())) {
            Printer.printf("preVisitDirectory(): excluded directory, path[%s], returning 'SKIP_SUBTREE'", path);
            return FileVisitResult.SKIP_SUBTREE;
        }
        if (VERBOSE) {
            Printer.printf("preVisitDirectory(): path[%s]", path);
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {

        final Path fileName = path.getFileName();
        if (Objects.isNull(fileName) || fileName.toString().lastIndexOf('.') == -1) {
            Printer.printf("visitFile(): null file or file without extension, path[%s]", path);
            return FileVisitResult.CONTINUE;
        }
        if (!path.toFile().canRead()) {
            Printer.printf("visitFile(): unreadable path[%s]", path);
            return FileVisitResult.CONTINUE;
        }
        final String extension = fileName.toString().substring(fileName.toString().lastIndexOf('.') + 1);
        if (EXCLUDED_FILES.contains(extension)) {
            Printer.printf("visitFile(): excluded file with extension[%s], path[%s]", extension, path);
            return FileVisitResult.CONTINUE;
        }
        createMapForPath(path);
        if (VERBOSE) {
            Printer.printf("visitFile(): processed path[%s]", path);
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FileVisitResult visitFileFailed(Path path, IOException exception) {
        Printer.printf("visitFileFailed(): path[%s], IOException[%s]", path, exception);
        return FileVisitResult.CONTINUE;
    }

    /**
     * Creates map for path.
     *
     * @param path the path
     */
    private void createMapForPath(Path path) {

        final Map<String, List<String>> treeMap = new TreeMap<>();
        final Consumer<String> action = line -> IntStream.range(0, KEYWORDS.size()).boxed()
                .filter(i -> PATTERNS.get(i).matcher(line).find())
                .map(KEYWORDS::get)
                .map(keyword -> {
                    treeMap.putIfAbsent(keyword, new ArrayList<>());
                    return keyword;
                })
                .forEach(keyword -> treeMap.get(keyword).add(line));
        try {
            final String content = Files.readString(path);
            Stream.of(content.split("\\R+")).forEach(action);
        } catch (IOException ex) {
            Printer.printf("createMapForPath(): IOException[%s], path[%s]", ex.getMessage(), path);
            System.exit(1);
        }
        resultMap.put(path, treeMap);
    }

}