package kp.collections;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kp.utils.Printer;

/**
 * The apportions of the sets and the maps.
 *
 */
public class Apportions {

	/**
	 * The hidden constructor.
	 */
	private Apportions() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Apportions the concurrent set.
	 * 
	 */
	public static void apportionSet() {

		final ConcurrentSkipListSet<String> set = Stream.of("aE", "bE", "cE", "dE", "eE")
				.collect(Collectors.toCollection(ConcurrentSkipListSet::new));
		Printer.printf("Source set %s", set);
		final Set<String> headSet = set.headSet("cE");
		Printer.printf("Head   set %s", headSet);
		final Set<String> subSet = set.subSet("bE", "eE");
		Printer.printf("Sub    set     %s", subSet);
		final Set<String> tailSet = set.tailSet("dE");
		Printer.printf("Tail   set             %s%n", tailSet);

		set.removeIf("bE"::equals);
		set.removeIf("dE"::equals);
		Printer.print("After removal of two elements from source set: 'bE' and 'dE'");
		Printer.printf("Source set %s", set);
		Printer.printf("Head   set %s", headSet);
		Printer.printf("Sub    set     %s", subSet);
		Printer.printf("Tail   set         %s", tailSet);
		Printer.printHor();
	}

	/**
	 * Apportions the concurrent map.
	 * 
	 */
	public static void apportionMapAndMerge() {

		final ConcurrentNavigableMap<String, String> srcMap = new ConcurrentSkipListMap<>();
		srcMap.putAll(Map.of("aK", "AV", "bK", "BV", "cK", "CV", "dK", "DV", "eK", "EV"));
		Printer.printf("Source map %s", srcMap);
		final Map<String, String> headMap = srcMap.headMap("cK");
		Printer.printf("Head   map %s", headMap);
		final Map<String, String> subMap = srcMap.subMap("bK", "eK");
		Printer.printf("Sub    map        %s", subMap);
		final Map<String, String> tailMap = srcMap.tailMap("dK");
		Printer.printf("Tail   map                      %s", tailMap);

		final Collector<Map.Entry<String, String>, ?, TreeMap<String, String>> collector = Collectors
				.toMap(Map.Entry::getKey, Map.Entry::getValue, String::concat, TreeMap::new);
		final Map<String, String> mergedThreeMaps = Stream.of(headMap, subMap, tailMap)//
				.flatMap(map -> map.entrySet().stream())//
				.collect(collector);
		Printer.printf("Merged 3 maps ('head' + 'sub' + 'tail'): %s%n", mergedThreeMaps);

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
