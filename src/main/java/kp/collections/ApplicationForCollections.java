package kp.collections;

import kp.collections.preferable.Preferable;
import kp.utils.Printer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The main class for the collections research.
 */
public class ApplicationForCollections {

    /**
     * Private constructor to prevent instantiation.
     */
    private ApplicationForCollections() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The primary entry point for launching the application.
     *
     */
    public static void main() {

        Printer.printHor();
        showArraysMismatch();
        countLetterFrequency();

        Apportions.apportionSetAndMerge();
        Apportions.apportionMapAndMerge();

        Preferable.preferArrayDeque();
        Preferable.preferLinkedHashMapAndLinkedHashSet();
        Preferable.preferCopyOnWriteArrayList();
        Preferable.preferConcurrentHashMap();
        Preferable.preferConcurrentSkipListMapAndConcurrentSkipListSet();

        InstanceChecking.checkInstanceAndFindElement();

        Multidimensionals.multidimensionalArrayToMultidimensionalList();

        Sequenced.process();

        iterateOverVector();
    }

    /**
     * Shows the arrays mismatch.
     */
    private static void showArraysMismatch() {

        final Integer[] arr1 = new Integer[]{1, 2, 3, 4, 5};
        final Integer[] arr2 = new Integer[]{1, 2, 0, 4, 5};
        Printer.printf("Array 1%s%nArray 2%s", Arrays.asList(arr1), Arrays.asList(arr2));
        Printer.printf("Arrays mismatch index[%d]", Arrays.mismatch(arr1, arr2));
        Printer.printHor();
    }

    /**
     * Counts the letter frequency.
     */
    private static void countLetterFrequency() {

        final List<String> sourceList = new ArrayList<>();
        sourceList.addAll(List.of("a", "b", "c", "r", "s", "t", "x", "y", "z"));
        sourceList.addAll(List.of("b", "c", "r", "s", "t", "x", "y"));
        sourceList.addAll(List.of("c", "r", "s", "t", "x"));
        sourceList.addAll(List.of("r", "s", "t"));
        sourceList.add("s");

        final Map<String, Integer> treeMap1 = sourceList.stream().collect(//
                Collectors.toMap(Function.identity(), _ -> 1, Integer::sum, TreeMap::new));
        Printer.printf("Letter frequency map%s <- with 'Collectors::toMap'", treeMap1);

        final Map<String, Integer> treeMap2 = new TreeMap<>();
        sourceList.forEach(//
                str -> treeMap2.merge(str, 1, Integer::sum));
        Printer.printf("Letter frequency map%s <- with 'Map::merge'", treeMap2);
        /*-
         * The 'ConcurrentHashMap' can be used
         * as a scalable frequency map (a form of histogram or multiset)
         * by using 'LongAdder' values and initializing via 'computeIfAbsent'.
         * */
        final Map<String, LongAdder> concurrentHashMap = new ConcurrentHashMap<>();
        sourceList.forEach(//
                str -> concurrentHashMap.computeIfAbsent(str, _ -> new LongAdder()).increment());
        Printer.printf("Letter frequency map%s <- with 'LongAdder::increment'", new TreeMap<>(concurrentHashMap));

        Printer.printHor();
    }

    /**
     * Iterates over a vector.
     */
    public static void iterateOverVector() {

        @SuppressWarnings("java:S1149") // switch off Sonarqube rule 'Synchronized class Vector should not be used'
        final Vector<Integer> vector = new Vector<>(Arrays.asList(2, 5, 3, 4, 1));
        Collections.sort(vector);
        Printer.print("Iterate over a vector:");
        final StringBuilder strBld1 = new StringBuilder();
        vector.forEach(arg -> strBld1.append(arg).append(" "));
        Printer.printObject(strBld1);
        /*-
         * Advice: use 'Iterator' in preference to 'Enumeration'.
         */
        final Iterator<Integer> iterator1 = vector.iterator();
        final StringBuilder strBld2 = new StringBuilder();
        iterator1.forEachRemaining(arg -> strBld2.append(arg).append(" "));
        Printer.printObject(strBld2);

        final Enumeration<Integer> enumeration = vector.elements();
        final Iterator<Integer> iterator2 = enumeration.asIterator();
        final StringBuilder strBld3 = new StringBuilder();
        iterator2.forEachRemaining(arg -> strBld3.append(arg).append(" "));
        Printer.printObject(strBld3);
        Printer.printHor();
    }

}
