package kp.streams.collecting;

import kp.utils.Printer;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Flattening the streams.
 */
public class Flattening {

    /**
     * Private constructor to prevent instantiation.
     */
    private Flattening() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Transforms from the multidimensional byte array to the flat byte list.
     */
    static void transformsMultidimensionalToFlatForBytes() {

        final Byte[][][] byteArray = {{{1, 2}, {3, 4}}, {{5, 6}, {7, 8}}};
        Printer.printf("Byte array deep to string %s", Arrays.deepToString(byteArray));

        final Function<Byte[][], List<List<Byte>>> mapper = arr -> Stream.of(arr)
                .map(Arrays::asList)
                .toList();
        final List<List<List<Byte>>> multiDimList = Stream.of(byteArray)
                .map(mapper)
                .toList();
        Printer.printf("Multi dim byte list\t  %s", multiDimList);

        final List<Byte> flatList = multiDimList.stream()
                .flatMap(List::stream)
                .flatMap(List::stream)
                .toList();
        Printer.printf("Flat byte list\t\t  %s", flatList);
        Printer.printHor();
    }

    /**
     * Transforms from the multidimensional string list to the flat string list.
     */
    static void transformMultidimensionalToFlatForStrings() {

        final List<List<List<String>>> multiDimList = List.of(
                List.of(List.of("A", "B"), List.of("C", "D")),
                List.of(List.of("E", "F"), List.of("G", "H"))
        );
        Printer.printf("Multi dim list\t\t  %s", multiDimList);

        final StringBuilder strBld = new StringBuilder();
        multiDimList.forEach(
                listOfLists -> listOfLists.forEach(
                        list -> list.forEach(strBld::append)));
        Printer.printf("Multi dim list\t\t  [%s] (joined with consumers)", strBld.toString());

        final Supplier<Stream<String>> supplier = () -> multiDimList.stream()
                .flatMap(List::stream)
                .flatMap(List::stream);
        final List<String> flatList = supplier.get().toList();
        Printer.printf("Flat list\t\t  %s", flatList);

        Printer.printf("Flat list\t\t  [%s] (joined with collectors)", supplier.get().collect(Collectors.joining()));
        Printer.printHor();
    }

    /**
     * Compares the method {@link Stream#mapMulti} with the method
     * {@link Stream#flatMap} for numbers.
     * <p>
     * The method {@link Stream#mapMulti} is <b>preferable</b> to the method {@link Stream#flatMap}
     * due to the reasons listed below:
     * <ul>
     * <li>Performance: Use mapMulti when you want to avoid the overhead of creating intermediate streams and
     * when you need to add multiple elements per input element.</li>
     * <li>Control: Use mapMulti when you need more control over how elements are added to the downstream Consumer.</li>
     * </ul>
     * </p>
     */
    static void compareMapMultiVersusFlatMapForNumbers() {

        final List<Integer> sourceList = List.of(1, 2, 4, 8);
        Printer.printf("Source list%s", sourceList);

        final Function<Integer, Stream<Double>> mapperForFlat =
                num -> Stream.of(0.8 * num, 1.0 * num, 1.2 * num);
        Printer.print("Using 'flatMap()':");
        final List<Double> listFromFlat = sourceList.stream().flatMap(mapperForFlat).toList();
        Printer.printObject(listFromFlat);

        final BiConsumer<Integer, Consumer<Double>> mapperForMulti =
                (num, cons) -> mapperForFlat.apply(num).forEach(cons);
        Printer.print("Using 'mapMulti()':");
        final List<Double> listFromMulti = sourceList.stream().mapMulti(mapperForMulti).toList();
        Printer.printObject(listFromMulti);
        Printer.printHor();
    }

    /**
     * Compares the method {@link Stream#mapMulti} with the method
     * {@link Stream#flatMap} for strings.
     * <p>
     * The method {@link Stream#mapMulti} is <b>preferable</b> to the method {@link Stream#flatMap}
     * due to the reasons listed below:
     * <ul>
     * <li>Performance: Use mapMulti when you want to avoid the overhead of creating intermediate streams and
     * when you need to add multiple elements per input element.</li>
     * <li>Control: Use mapMulti when you need more control over how elements are added to the downstream Consumer.</li>
     * </ul>
     * </p>
     */
    static void compareMapMultiVersusFlatMapForStrings() {

        final List<String> sourceList = List.of("AB=CD", "EF=GH", "IJ=KL");
        Printer.printf("Source list%s", sourceList);

        final Function<String, Stream<String>> mapperForFlat = str -> Stream.of(str.split("="));
        Printer.print("Using 'flatMap()':");
        final List<String> listFromFlat = sourceList.stream().flatMap(mapperForFlat).toList();
        Printer.printObject(listFromFlat);

        final BiConsumer<String, Consumer<String>> mapperForMulti =
                (num, cons) -> mapperForFlat.apply(num).forEach(cons);
        Printer.print("Using 'mapMulti()':");
        final List<String> listFromMulti = sourceList.stream().mapMulti(mapperForMulti).toList();
        Printer.printObject(listFromMulti);
        Printer.printHor();
    }
}
