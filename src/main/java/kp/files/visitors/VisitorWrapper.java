package kp.files.visitors;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import kp.utils.Printer;

/**
 * The wrapper for the {@link SimpleFileVisitorExtension}.
 *
 */
public class VisitorWrapper {

	private static final boolean STANDARD_SOLUTION_FLAG = true;
	private static final Path BASE_DIRECTORY = Paths.get("src");

	/**
	 * The constructor.
	 */
	public VisitorWrapper() {
		super();
	}

	/**
	 * Searches keywords in files.
	 * 
	 */
	public void searchKeywordsInFiles() {

		final SimpleFileVisitorExtension visitor = new SimpleFileVisitorExtension();
		final Instant start = Instant.now();
		try {
			if (STANDARD_SOLUTION_FLAG) {
				Files.walkFileTree(BASE_DIRECTORY, visitor);
			} else {
				/*-
				 * The alternative solution which does not need
				 * the extending of a SimpleFileVisitor.
				 */
				processWithoutUsingFileVisitor(visitor.getVisitFileFunction());
			}
		} catch (IOException e) {
			Printer.printIOException(e);
			System.exit(1);
		}
		showResults(start, visitor.getResultMap());
		Printer.printHor();
	}

	/**
	 * Shows results.
	 * 
	 * @param start     the start instant
	 * @param resultMap the result map
	 */
	private void showResults(Instant start, Map<Path, Map<String, List<String>>> resultMap) {

		Printer.printf("►►► Result, number of searched files[%d], time elapsed[%tT.%<tL]", resultMap.size(),
				LocalTime.MIDNIGHT.plus(Duration.between(start, Instant.now())));
		resultMap.keySet().stream()/*-*/
				.filter(Predicate.not(pathKey -> resultMap.get(pathKey).isEmpty()))/*-*/
				.forEach(pathKey -> showResultsFromFile(resultMap, pathKey));
	}

	/**
	 * Shows results from file.
	 * 
	 * @param resultMap the result map
	 * @param pathKey   the path key
	 */
	private void showResultsFromFile(Map<Path, Map<String, List<String>>> resultMap, Path pathKey) {

		final BiConsumer<String, List<String>> action = (key, list) -> list.stream().map(String::trim)
				.forEach(line -> Printer.printf("[%-20s] line[%s]", key, line));
		Printer.printf("%n▼▼▼ number of keywords[%d] ▼▼▼ file[%s] ▼▼▼", resultMap.get(pathKey).size(), pathKey);
		resultMap.get(pathKey).forEach(action);
		Printer.printEndLineOfTriangles();
	}

	/**
	 * Processes without using the file visitor.
	 * 
	 * 
	 * @param visitorFunction the function for execution the visitor method
	 * @throws IOException if an I/O error occurs
	 */
	private void processWithoutUsingFileVisitor(Function<Path, FileVisitResult> visitorFunction) throws IOException {

		final Deque<Path> stack = new ArrayDeque<>();
		stack.push(BASE_DIRECTORY);
		while (!stack.isEmpty()) {
			final DirectoryStream<Path> stream = Files.newDirectoryStream(stack.pop());
			try (stream) {
				for (Path path : stream) {
					if (Objects.isNull(path.getFileName()) || SimpleFileVisitorExtension.EXCLUDED_DIRECTORIES
							.contains(path.getFileName().toString())) {
						Printer.printf("processWithoutUsingFileVisitor(): "
								+ "excluded directory, path[%s], returning 'SKIP_SUBTREE'", path);
						continue;
					}
					if (Files.isDirectory(path)) {
						stack.push(path);
					} else {
						visitorFunction.apply(path);
					}
				}
			}
		}
	}
}
