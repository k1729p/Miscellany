package kp.streams.collecting;

import kp.utils.Printer;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The main launcher for the {@link Stream} collecting research.
 */
public class ApplicationForStreamsCollecting {

    /**
     * Private constructor to prevent instantiation.
     */
    private ApplicationForStreamsCollecting() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The primary entry point for launching the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        Printer.printHor();
        useStringJoiners();
        collectStream();
        countWithoutPipelineExecution();
        groupByDifferenceFromExpected();

        Iterating.process();

        NullsAndOptionals.concatenateWithStreamFromNullable();
        NullsAndOptionals.nullsInStreamWithoutOptionals();
        NullsAndOptionals.nullsInStreamWithOptionals();

        Traversing.traverseListWithIterator();
        Traversing.traverseQueue();
        Traversing.traverseDeque();

        Flattening.transformsMultidimensionalToFlatForBytes();
        Flattening.transformMultidimensionalToFlatForStrings();
        Flattening.compareMapMultiVersusFlatMapForNumbers();
        Flattening.compareMapMultiVersusFlatMapForStrings();
    }

    /**
     * Uses the string joiners.
     */
    private static void useStringJoiners() {

        Printer.print("Using 'String.join()':");
        final String delimiter = ", ";
        final Set<String> set = Set.of("R", "S", "T");
        Printer.printf("\t\t set[%s]", String.join(delimiter, set));

        Printer.print("Using 'StringJoiner':");
        final StringJoiner joinerForSet = new StringJoiner(delimiter, "\t\t set[", "]");
        set.forEach(joinerForSet::add);
        Printer.printObject(joinerForSet);

        final StringJoiner joinerForSortedSet = new StringJoiner(delimiter, "\t  sorted set[", "]");
        final Set<String> sortedSet = new TreeSet<>(set);
        sortedSet.forEach(joinerForSortedSet::add);
        Printer.printObject(joinerForSortedSet);

        final StringJoiner joinerForList = new StringJoiner(delimiter, "\t\tlist[", "]");
        List.of("K", "L", "M").forEach(joinerForList::add);
        Printer.printObject(joinerForList);

        final StringJoiner mergeJoiner = new StringJoiner(delimiter, "\t merged list[", "]");
        Printer.printObject(mergeJoiner.merge(joinerForSortedSet).merge(joinerForList));
        Printer.printHor();
    }

    /**
     * Collects the stream.
     */
    private static void collectStream() {

        // the supplier of a sorted stream
        final Supplier<Stream<String>> supplier = () -> Stream.of("DBCADBCA".split("")).sorted();
        final CharSequence delimiter = "█";
        final CharSequence prefix = "►";
        final CharSequence suffix = "◄";

        // equivalent to: reducing(0L, e -> 1L, Long::sum)
        Printer.printf("Counting[%s]", supplier.get().count());
        Printer.printf("Reducing[%s]", supplier.get().reduce(String::concat).orElse("REDUCE ERROR"));
        Printer.printf("Joining [%s]%n", supplier.get().collect(Collectors.joining(delimiter, prefix, suffix)));

        Printer.printf("List    %s (unmodifiable list)", supplier.get().toList());
        Printer.printf("List    %s (modifiable list)", supplier.get().collect(Collectors.toCollection(ArrayList::new)));
        Printer.printf("List    %s (from distinct stream)", supplier.get().distinct().toList());
        Printer.printf("Set     %s", supplier.get().collect(Collectors.toSet()));// This is an unordered Collector.
        Printer.printHor();
    }

    /**
     * Count the stream items without the execution of the pipeline.
     */
    private static void countWithoutPipelineExecution() {

        final AtomicInteger atomic = new AtomicInteger();
        final UnaryOperator<String> operator = arg -> {
            atomic.incrementAndGet();
            return arg;
        };
        final Supplier<Stream<String>> supplier1 = () -> Stream.of("12345678".split("")).map(operator);
        final Supplier<Stream<String>> supplier2 = () -> Stream.concat(
                Stream.concat(
                        Stream.of("01", "02", "03").map(operator),
                        Stream.of("04", "05", "06").map(operator)),
                Stream.concat(
                        Stream.of("07", "08", "09").map(operator),
                        Stream.of("10", "11", "12").map(operator))
        );
        /*-
         * An implementation may choose to not execute the stream pipeline,
         * if it is capable of computing the count directly from the stream source.
         */
        Printer.printf("Results of 'Stream.count()' execution: stream 1 count[%d], stream 2 count[%d]",
                supplier1.get().count(), supplier2.get().count());
        Printer.printf("atomic[%d] after 'Stream.count()' execution", atomic.get());
        Printer.printf("Results of pipeline execution : stream 1 size[%d], stream 2 size[%d]",
                supplier1.get().toList().size(), supplier2.get().toList().size());
        Printer.printf("atomic[%d] after pipeline execution", atomic.get());
        Printer.printHor();
    }

    /**
     * Groups the elements by the difference from the expected value "50".
     */
    private static void groupByDifferenceFromExpected() {

        final int expected = 50;
        final Supplier<Stream<Integer>> supplier = () -> IntStream.rangeClosed(40, 60).boxed();
        final Function<Integer, Character> classifier = number -> {
            final int delta = number - expected;
            if (delta < -5) {
                return 'A';
            } else if (delta < -1) {
                return 'B';
            } else if (delta <= 1) {
                return 'C';
            } else if (delta <= 5) {
                return 'D';
            } else {
                return 'E';
            }
        };
        final Map<Character, List<Integer>> resultMap = supplier.get()
                .collect(Collectors.groupingBy(classifier));
        Printer.print("Map of grouped elements:");
        Printer.printf("   %s", resultMap);

        final Map<Character, Long> numberMap = supplier.get()
                .collect(Collectors.groupingBy(classifier, Collectors.counting()));
        Printer.print("Map of the number of elements in groups:");
        Printer.printf("   %s%n", numberMap);

        final Map<Boolean, List<Integer>> partitionedMap = supplier.get()
                .collect(Collectors.partitioningBy(arg -> expected - 1 <= arg && arg <= expected + 1));
        Printer.print("The partitioned elements with key 'true' have the value 'expected' +- 1.");
        Printer.print("Map of partitioned elements:");
        Printer.printf("   %s%n", partitionedMap);

        resultMap.replaceAll((_, valueList) -> {
            valueList.replaceAll(value -> value - expected);
            return valueList;
        });
        Printer.print("Map of grouped differences from the value 'expected':");
        Printer.printf("   %s", resultMap);
        Printer.printHor();
    }

}
