package kp.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.stream.Collectors;

import kp.collections.preferable.Preferable;
import kp.utils.Printer;

/**
 * The main class for the collections research.
 */
public class ApplicationForCollections {

	/**
	 * The hidden constructor.
	 */
	private ApplicationForCollections() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * The entry point for the application.
	 * 
	 * @param args the command-line arguments
	 */
	public static void main(String[] args) {

		Printer.printHor();
		showArraysMismatch();
		countLetterFrequency();

		Apportions.apportionSet();
		Apportions.apportionMapAndMerge();

		Preferable.preferArrayDeque();
		Preferable.preferLinkedHashMapAndLinkedHashSet();
		Preferable.preferCopyOnWriteArrayList();
		Preferable.preferConcurrentHashMap();
		Preferable.preferConcurrentSkipListMapAndConcurrentSkipListSet();

		Multidimensionals.multidimensionalArrayToMultidimensionalList();

		iterateOverVector();
	}

	/**
	 * Shows the arrays mismatch.
	 * 
	 */
	private static void showArraysMismatch() {

		final Integer[] arr1 = new Integer[] { 1, 2, 3, 4, 5 };
		final Integer[] arr2 = new Integer[] { 1, 2, 0, 4, 5 };
		Printer.printf("Array 1%s%nArray 2%s", Arrays.asList(arr1), Arrays.asList(arr2));
		Printer.printf("Arrays mismatch index[%d]", Arrays.mismatch(arr1, arr2));
		Printer.printHor();
	}

	/**
	 * Counts the letter frequency.
	 * 
	 */
	private static void countLetterFrequency() {

		final List<String> sourceList = new ArrayList<>();
		sourceList.addAll(List.of("a", "b", "c", "r", "s", "t", "x", "y", "z"));
		sourceList.addAll(List.of("b", "c", "r", "s", "t", "x", "y"));
		sourceList.addAll(List.of("c", "r", "s", "t", "x"));
		sourceList.addAll(List.of("r", "s", "t"));
		sourceList.addAll(List.of("s"));

		final Map<String, Integer> integerMap1 = sourceList.stream().collect(//
				Collectors.toMap(Function.identity(), str -> 1, Integer::sum, TreeMap::new));
		Printer.printf("Letter frequency map%s <- with 'Collectors::toMap'", integerMap1);

		final Map<String, Integer> integerMap2 = new TreeMap<>();
		sourceList.stream().forEach(//
				str -> integerMap2.merge(str, 1, Integer::sum));
		Printer.printf("Letter frequency map%s <- with 'Map::merge'", integerMap2);

		final ConcurrentHashMap<String, LongAdder> adderMap = new ConcurrentHashMap<>();
		sourceList.stream().forEach(//
				str1 -> adderMap.computeIfAbsent(str1, str2 -> new LongAdder()).increment());
		Printer.printf("Letter frequency map%s <- with 'LongAdder::increment'", new TreeMap<>(adderMap));

		Printer.printHor();
	}

	/**
	 * Iterates over a vector.
	 * 
	 */
	public static void iterateOverVector() {

		@SuppressWarnings("java:S1149") // switch off Sonarqube rule 'Synchronized class Vector should not be used'
		final Vector<Integer> vector = new Vector<>(Arrays.asList(2, 5, 3, 4, 1));
		Collections.sort(vector);
		Printer.print("Iterate over a vector:");
		final StringBuilder strBld1 = new StringBuilder();
		vector.forEach(arg -> strBld1.append(arg).append(" "));
		Printer.printObject(strBld1);
		/*
		 * Advice: use Iterator in preference to Enumeration.
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
