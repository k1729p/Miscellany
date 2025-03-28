package kp.streams.collecting;

import kp.utils.Printer;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Iterating over the stream.
 */
public class Iterating {

    /**
     * Private constructor to prevent instantiation.
     */
    private Iterating() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Iterate over the iterative stream.
     */
    static void process() {
        /*
         * Integer
         */
        final int seedInt = 1;
        final IntPredicate hasNextInt = num -> num <= 3;
        final IntUnaryOperator nextInt = num -> ++num;
        final List<Integer> intList = IntStream.iterate(seedInt, hasNextInt, nextInt).boxed().toList();
        Printer.printf("Iteration result number list%s", intList);
        /*
         * String
         */
        final String seedString = "a";
        final Predicate<String> hasNextString = str -> str.codePointAt(0) < "a".codePointAt(0) + 3;
        final UnaryOperator<String> nextString = str -> Character.toString(str.codePointAt(0) + 1);
        final List<String> stringList = Stream.iterate(seedString, hasNextString, nextString).toList();
        Printer.printf("Iteration result string list%s", stringList);
        /*
         * Array
         */
        final String[] seedArray = new String[]{"a"};
        final Predicate<String[]> hasNextArray = arr -> arr.length <= 3;
        final UnaryOperator<String[]> nextArray = arr -> {
            arr = Arrays.copyOf(arr, arr.length + 1);
            arr[arr.length - 1] = Character.toString(arr[arr.length - 2].codePointAt(0) + 1);
            return arr;
        };
        final List<String[]> arrayList = Stream.iterate(seedArray, hasNextArray, nextArray).toList();
        Printer.printf("Iteration result  array list%s", Arrays.deepToString(arrayList.toArray(Object[]::new)));
        /*
         * List
         */
        final List<String> seedList = new ArrayList<>();
        seedList.add("a");
        final Predicate<List<String>> hasNextList = list -> list.size() <= 3;
        final UnaryOperator<List<String>> nextList = list -> {
            final List<String> resultList = new ArrayList<>(list);
            resultList.add(Character.toString(resultList.getLast().codePointAt(0) + 1));
            return resultList;
        };
        final List<List<String>> listList = Stream.iterate(seedList, hasNextList, nextList).toList();
        Printer.printf("Iteration result   list list%s", listList);
        /*
         * Map
         */
        final Map<String, String> seedMap = new TreeMap<>();
        seedMap.put("a", "A");
        final Predicate<Map<String, String>> hasNextMap = map -> map.size() <= 3;
        final UnaryOperator<Map<String, String>> nextMap = map -> {
            final String key = map.keySet().toArray(String[]::new)[map.size() - 1];
            final String value = map.get(key);
            final Map<String, String> resultMap = new TreeMap<>(map);
            resultMap.put(Character.toString(key.codePointAt(0) + 1),
                    Character.toString(value.codePointAt(0) + 1));
            return resultMap;
        };
        final List<Map<String, String>> mapList = Stream.iterate(seedMap, hasNextMap, nextMap).toList();
        Printer.printf("Iteration result    map list%s", mapList);
        Printer.printHor();
    }

}
