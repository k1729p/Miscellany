package kp.streams.fragmentation;

import kp.utils.Printer;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The main launcher for the {@link Stream} fragmentation research.
 */
public class ApplicationForStreamsFragmentation {

    /**
     * Private constructor to prevent instantiation.
     */
    private ApplicationForStreamsFragmentation() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The primary entry point for launching the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        Printer.printHor();
        skipAndLimit();
        dropAndTake();
        filter();
        match();
    }

    /**
     * Research the methods {@link Stream#skip} and {@link Stream#limit}.
     */
    private static void skipAndLimit() {

        final List<String> sourceList = IntStream.rangeClosed(1, 10).mapToObj(Integer::toString).toList();
        Printer.printf("Source list %s", sourceList);

        final List<String> beginList = sourceList.stream()
                .skip(0)
                .limit(3)
                .toList();
        Printer.printf("Begin list  %s", beginList);

        final List<String> middleList = sourceList.stream()
                .skip(3)
                .limit(4)
                .toList();
        Printer.printf("Middle list \t     %s", middleList);

        final List<String> endList = sourceList.stream()
                .skip(7)
                .limit(3)
                .toList();
        Printer.printf("End list \t\t\t %s", endList);
        Printer.printHor();
    }

    /**
     * Research the methods {@link Stream#dropWhile} and {@link Stream#takeWhile}.
     */
    private static void dropAndTake() {

        final List<String> sourceList = List.of("a", "b", "c", "dd", "e", "f", "gg", "h", "i");
        Printer.printf("Source list %s", sourceList);
        Predicate<String> predicate = arg -> arg.length() == 1;

        final List<String> beginList = sourceList.stream()
                .takeWhile(predicate)
                .toList();
        Printer.printf("Begin list  %s", beginList);

        final List<String> restList = sourceList.stream()
                .dropWhile(predicate)
                .toList();
        Printer.printf("Rest list \t     %s", restList);

        final List<String> middleList = sourceList.stream()
                .dropWhile(predicate)
                .skip(1)
                .takeWhile(predicate)
                .toList();
        Printer.printf("Middle list \t\t %s", middleList);

        final List<String> endList = sourceList.stream()
                .dropWhile(predicate).skip(1)
                .dropWhile(predicate).skip(1)
                .takeWhile(predicate)
                .toList();
        Printer.printf("End list \t\t\t   %s", endList);
        Printer.printHor();
    }

    /**
     * Research the method {@link Stream#filter}.
     * Filters out null, empty, and 'a' (case-insensitive) elements.
     */
    private static void filter() {

        final String[] textArray = {null, "", "a", "b", "A", "B"};
        Printer.printf("Text array\t\t\t%s", Arrays.asList(textArray));
        final Supplier<Stream<String>> supplier = () -> Stream.of(textArray);

        final List<String> notNullOrEmptyList = supplier.get()
                .filter(Objects::nonNull)
                .filter(Predicate.not(String::isEmpty))
                .toList();
        Printer.printf("Not null or empty element list\t\t%s", notNullOrEmptyList);

        final List<String> notAInList = supplier.get()
                .filter(Objects::nonNull)
                .filter(Predicate.not(String::isEmpty))
                .filter(Predicate.not("a"::equalsIgnoreCase))
                .toList();
        Printer.printf("Not 'A' element list\t\t\t   %s", notAInList);
        Printer.printHor();
    }

    /**
     * Research the methods {@link Stream#anyMatch} and {@link Stream#noneMatch}.
     */
    private static void match() {

        final List<String> sourceList = List.of("A", "B", "C");
        Printer.printf("Source list%s", sourceList);
        Printer.printf("Stream has any 'B' element[%b]", sourceList.stream().anyMatch("B"::equals));
        Printer.printf("Stream has no 'C' elements[%b]", sourceList.stream().noneMatch("C"::equals));
        Printer.printHor();
    }

}
