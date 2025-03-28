package kp.collections;

import kp.utils.Printer;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The apportions of the sets and the maps.
 */
public class Apportions {

    /**
     * Private constructor to prevent instantiation.
     */
    private Apportions() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Apportions the {@link ConcurrentSkipListSet}.
     */
    static void apportionSetAndMerge() {

        final NavigableSet<String> srcSet = new ConcurrentSkipListSet<>(Set.of("a", "b", "c", "d", "e"));
        Printer.printf("Source set %s", srcSet);
        final Set<String> headSet = srcSet.headSet("c");
        Printer.printf("Head   set %s", headSet);
        final Set<String> subSet = srcSet.subSet("b", "e");
        Printer.printf("Sub    set    %s", subSet);
        final Set<String> tailSet = srcSet.tailSet("d");
        Printer.printf("Tail   set          %s%n", tailSet);

        final Set<String> mergedSet = Stream.of(headSet, subSet, tailSet)//
                .flatMap(Collection::stream).collect(Collectors.toSet());
        Printer.printf("Merged 3 sets ('head' + 'sub' + 'tail'): %s", mergedSet);
        Printer.printf("'b' <--   lower[%s],  floor[%s]", srcSet.lower("b"), srcSet.floor("b"));
        Printer.printf("'d' <-- ceiling[%s], higher[%s]", srcSet.ceiling("d"), srcSet.higher("d"));

        srcSet.removeIf("b"::equals);
        srcSet.removeIf("d"::equals);
        Printer.print("After removal of two elements from source set: 'b' and 'd'");
        Printer.printf("Source set %s", srcSet);
        Printer.printf("Head   set %s", headSet);
        Printer.printf("Sub    set    %s", subSet);
        Printer.printf("Tail   set       %s", tailSet);
        Printer.printHor();
    }

    /**
     * Apportions the {@link ConcurrentSkipListMap}.
     */
    static void apportionMapAndMerge() {

        final NavigableMap<String, String> srcMap = new ConcurrentSkipListMap<>(
                Map.of("aK", "AV", "bK", "BV", "cK", "CV", "dK", "DV", "eK", "EV"));
        Printer.printf("Source map %s", srcMap);
        final Map<String, String> headMap = srcMap.headMap("cK");
        Printer.printf("Head   map %s", headMap);
        final Map<String, String> subMap = srcMap.subMap("bK", "eK");
        Printer.printf("Sub    map        %s", subMap);
        final Map<String, String> tailMap = srcMap.tailMap("dK");
        Printer.printf("Tail   map                      %s%n", tailMap);

        final Collector<Map.Entry<String, String>, ?, TreeMap<String, String>> collector = Collectors
                .toMap(Map.Entry::getKey, Map.Entry::getValue, String::concat, TreeMap::new);
        final Map<String, String> mergedMap = Stream.of(headMap, subMap, tailMap)//
                .flatMap(map -> map.entrySet().stream())//
                .collect(collector);
        /*-
         * Beware !
         * With chosen merge function 'String::concat' the merged value from 'B V' and 'B V' is 'B V B V'.
         */
        Printer.printf("Merged 3 maps ('head' + 'sub' + 'tail'): %s", mergedMap);
        Printer.printf("'bK' <--   lowerEntry[%s],  floorEntry[%s]", srcMap.lowerEntry("bK"),
                srcMap.floorEntry("bK"));
        Printer.printf("'dK' <-- ceilingEntry[%s], higherEntry[%s]", srcMap.ceilingEntry("dK"),
                srcMap.higherEntry("dK"));

        srcMap.entrySet().removeIf(entry -> "bK".equals(entry.getKey()));
        srcMap.entrySet().removeIf(entry -> "DV".equals(entry.getValue()));
        Printer.print("After removal of two elements from source map: with key 'bK' and with value 'DV'");
        Printer.printf("Source map %s", srcMap);
        Printer.printf("Head   map %s", headMap);
        Printer.printf("Sub    map        %s", subMap);
        Printer.printf("Tail   map               %s", tailMap);
        Printer.printHor();
    }

}
