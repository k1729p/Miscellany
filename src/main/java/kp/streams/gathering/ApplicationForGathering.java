package kp.streams.gathering;

import kp.utils.Printer;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Gatherers;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The main launcher for the research of the gathering operations.
 * <p>
 * Researched methods:
 * </p>
 * <ul>
 * <li>{@link Gatherers#fold}</li>
 * <li>{@link Gatherers#scan}</li>
 * <li>{@link Gatherers#windowFixed}</li>
 * <li>{@link Gatherers#windowSliding}</li>
 * </ul>
 * <p>
 * <a href="https://openjdk.org/jeps/485">JEP 485: Stream Gatherers</a>
 * </p>
 */
public class ApplicationForGathering {
    private static final List<Person> LIST_OF_PERSONS = List.of(
            new Person(1, 5, 4, "Alice"),
            new Person(2, 1, 5, "Bob"),
            new Person(3, 2, 1, "Carol"),
            new Person(4, 3, 2, "Dave"),
            new Person(5, 4, 3, "Eve")
    );
    private static final BiFunction<List<Person>, Person, List<Person>> FOLDER_AB =
            (list, person) -> {
                if (person.scoreA() >= 3 && person.scoreB() >= 3) {
                    list.add(person);
                }
                return list;
            };
    private static final BiFunction<List<Person>, Person, List<Person>> FOLDER_BC =
            (list, person) -> {
                if (person.scoreB() >= 3 && person.scoreC() >= 3) {
                    list.add(person);
                }
                return list;
            };
    private static final BiFunction<List<Person>, Person, List<Person>> FOLDER_AC =
            (list, person) -> {
                if (person.scoreA() >= 3 && person.scoreC() >= 3) {
                    list.add(person);
                }
                return list;
            };

    /**
     * Represents the person.
     *
     * @param scoreA the score A
     * @param scoreB the score B
     * @param scoreC the score C
     * @param name   the person name
     */
    record Person(int scoreA, int scoreB, int scoreC, String name) {
        Person {
            if (scoreA < 1 || scoreB < 0 || scoreC < 0 || Objects.isNull(name) || name.isEmpty()) {
                throw new IllegalArgumentException("Invalid parameters");
            }
        }
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private ApplicationForGathering() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The primary entry point for launching the application.
     *
     */
    public static void main() {

        processWithGatherersFold();
        processWithGatherersScan();
        processWithGatherersWindowFixed();
        processWithGatherersWindowSliding();
    }

    /**
     * Processes with {@link Gatherers#fold}.
     * <ul>
     * <li>{@link Gatherers#fold} is a stateful many-to-one gatherer.</li>
     * <li>An example of the incremental reordering function.</li>
     * </ul>
     */
    private static void processWithGatherersFold() {

        Printer.printHor();
        final String fragment = ", evens:";
        final BiFunction<String, Integer, String> folderFunction =
                (string, number) -> number % 2 == 0 ?
                        "%s %s".formatted(string, number) :
                        string.replaceFirst(fragment, "%s %s".formatted(number, fragment));
        final Optional<String> reducedNumber = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .gather(Gatherers.fold(() -> fragment, folderFunction)).findFirst();
        final String message = reducedNumber.map("odds: %s"::formatted).orElse("failed");
        Printer.printf("Gatherers fold. Message with odd and even numbers:%n\t %s", message);
        Printer.printHor();

        final Consumer<Person> consumer = arg -> Printer.printf("\t%s".formatted(arg));
        Printer.printf("Gatherers fold.");
        Printer.printf("Persons with best score A and best score B:");
        LIST_OF_PERSONS.stream().gather(Gatherers.fold(ArrayList::new, FOLDER_AB)).findFirst()
                .ifPresent(list -> list.forEach(consumer));
        Printer.printf("Persons with best score B and best score C:");
        LIST_OF_PERSONS.stream().gather(Gatherers.fold(ArrayList::new, FOLDER_BC)).findFirst()
                .ifPresent(list -> list.forEach(consumer));
        Printer.printf("Persons with best score A and best score C:");
        LIST_OF_PERSONS.stream().gather(Gatherers.fold(ArrayList::new, FOLDER_AC)).findFirst()
                .ifPresent(list -> list.forEach(consumer));
        Printer.printHor();
    }

    /**
     * Processes with {@link Gatherers#scan}.
     * <ul>
     * <li>{@link Gatherers#scan} is a stateful one-to-one gatherer.</li>
     * <li>An example of the incremental accumulation (prefix scan) function.</li>
     * </ul>
     */
    private static void processWithGatherersScan() {

        final ZonedDateTime startDateTime = ZonedDateTime.parse(
                "2000-01-01T00:00:00+01:00", DateTimeFormatter.ISO_ZONED_DATE_TIME);

        final BiFunction<ZonedDateTime, Integer, ZonedDateTime> scannerFunction = ZonedDateTime::plusHours;
        final List<String> accumulatedDates = Stream.of(0, 18, 12, 6)
                .gather(Gatherers.scan(() -> startDateTime, scannerFunction))
                .map(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")::format).toList();
        Printer.printf("Gatherers scan. Accumulated dates:");
        accumulatedDates.forEach(arg -> Printer.printf("█\t%s".formatted(arg)));
        Printer.printHor();
    }

    /**
     * Processes with {@link Gatherers#windowFixed}.
     * <ul>
     * <li>{@link Gatherers#windowFixed} is a stateful many-to-many gatherer.</li>
     * <li>An example of the windowing (grouping elements into batches) function.</li>
     * </ul>
     */
    private static void processWithGatherersWindowFixed() {

        final List<List<List<Integer>>> bottomWindows = IntStream.rangeClosed(1, 8).boxed()
                .gather(Gatherers.windowFixed(2)).gather(Gatherers.windowFixed(2))
                .toList();
        Printer.printf("     Windows in windows: %s", bottomWindows);
        Printer.printHor();

        IntStream.rangeClosed(2, 4).forEach(windowSize -> {
            final List<List<Integer>> windows = IntStream.rangeClosed(1, 8).boxed()
                    .gather(Gatherers.windowFixed(windowSize))
                    .toList();
            Printer.printf("  Fixed windows, size[%d]: %s", windowSize, windows);
        });
        Printer.printHor();
    }

    /**
     * Processes with {@link Gatherers#windowSliding}.
     * <ul>
     * <li>{@link Gatherers#windowSliding} is a stateful many-to-many gatherer.</li>
     * <li>An example of the windowing (grouping elements into batches) function.</li>
     * </ul>
     */
    private static void processWithGatherersWindowSliding() {

        IntStream.rangeClosed(2, 4).forEach(windowSize -> {
            final List<List<Integer>> windows = IntStream.rangeClosed(1, 8).boxed()
                    .gather(Gatherers.windowSliding(windowSize))
                    .toList();
            Printer.printf("Sliding windows, size[%d]: %s", windowSize, windows);
        });
        Printer.printHor();
    }

}
