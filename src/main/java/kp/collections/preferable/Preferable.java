package kp.collections.preferable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

import kp.utils.Printer;

/**
 * The normally preferable coding practices for the collections.
 *
 */
public class Preferable {
	/*-
	 *  A concurrent collection is thread-safe, but not governed by a single exclusion lock
	 */

	private static final String ORIGINAL_MAP_FMT = "   %s original source map";
	private static final List<String> C_A_B_LIST = List.of("C", "A", "B");
	private static final Map<String, String> C_A_B_MAP = Map.of("Ck", "Cv", "Ak", "Av", "Bk", "Bv");

	/**
	 * The hidden constructor.
	 */
	private Preferable() {
		throw new IllegalStateException("Utility class");
	}

	/*-
	 * (this example was removed because 'Stream::collect' was rejected by 'SonarQube')
	 * 
	 * The 'Stream::toList' should be preferred.
	 * 'Stream::toList' accumulates the elements faster than 'Stream::collect'
	 * The returned list from Stream::toList is unmodifiable!
	 */

	/**
	 * The faster {@link ArrayDeque} should be preferred.<br>
	 * The faster {@link ConcurrentLinkedDeque} should be preferred.
	 * <p>
	 * <i>Speed</i>
	 */
	public static void preferArrayDeque() {
		/*-
		 * 'ArrayDeque' is likely to be:
		 *  - faster than 'Stack' when used as a stack
		 *  - faster than 'LinkedList' when used as a queue
		 */
		final Deque<String> arrayDeque = new ArrayDeque<>();
		arrayDeque.addAll(C_A_B_LIST);
		// executing 'arrayDeque.add(null)' causes 'NullPointerException'
		Printer.printf("faster %s ArrayDeque", arrayDeque);
		@SuppressWarnings("java:S1149") // switch off Sonarqube rule 'Synchronized class Stack should not be used'

		final Stack<String> stack = new Stack<>();
		stack.addAll(C_A_B_LIST);
		stack.add(null);
		Printer.printf("slower %s Stack%n", stack);

		final Deque<String> concurrentLinkedDeque = new ConcurrentLinkedDeque<>();
		concurrentLinkedDeque.addAll(C_A_B_LIST);
		// executing 'concurrentLinkedDeque.add(null)' causes 'NullPointerException'
		Printer.printf("faster %s ConcurrentLinkedDeque", concurrentLinkedDeque);
		final List<String> linkedList = new LinkedList<>();
		linkedList.addAll(C_A_B_LIST);
		linkedList.add(null);
		Printer.printf("slower %s LinkedList", linkedList);
		Printer.printHor();
	}

	/**
	 * The <b>ordered</b> {@link LinkedHashMap} should be preferred.<br>
	 * The <b>ordered</b> {@link LinkedHashSet} should be preferred.
	 * <p>
	 * <i>Ordering</i>
	 */
	public static void preferLinkedHashMapAndLinkedHashSet() {
		/*-
		 * 'LinkedHashMap' is preferable to 'HashMap' and 'Hashtable'
		 * because it has predictable iteration order.
		 * 
		 * 'LinkedHashSet' is preferable to 'HashSet'
		 * because it has predictable iteration order.
		 */
		final Map<String, String> map = C_A_B_MAP;
		Printer.printf(ORIGINAL_MAP_FMT, map);

		// produces a copy of a map that has the same order as the original
		final Map<String, String> linkedHashMap = new LinkedHashMap<>(map);
		Printer.printf("OK %s LinkedHashMap%n", linkedHashMap);

		final Set<String> linkedHashSet = new LinkedHashSet<>(map.keySet());
		Printer.printf("OK %s LinkedHashSet", linkedHashSet);
		Printer.printHor();
	}

	/**
	 * The {@link CopyOnWriteArrayList} should be preferred.
	 * <p>
	 * <i>Synchronization</i>
	 */
	public static void preferCopyOnWriteArrayList() {
		/*-
		 * 'CopyOnWriteArrayList' is preferable to a synchronized 'ArrayList'.
		 * 
		 * The 'ArrayList' itself is not synchronized.
		 * The 'CopyOnWriteArrayList' is a thread-safe variant of 'ArrayList'. 
		 */
		final List<String> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
		copyOnWriteArrayList.addAll(C_A_B_LIST);
		copyOnWriteArrayList.add(null);
		Printer.printf("OK %s CopyOnWriteArrayList", copyOnWriteArrayList);

		final List<String> synchronizedArrayList = Collections.synchronizedList(new ArrayList<>());
		synchronizedArrayList.addAll(C_A_B_LIST);
		synchronizedArrayList.add(null);
		Printer.printf("NO %s synchronized ArrayList", synchronizedArrayList);
		Printer.printHor();
	}

	/**
	 * The {@link ConcurrentHashMap} should be preferred.
	 * <p>
	 * <i>Synchronization</i>
	 */
	public static void preferConcurrentHashMap() {
		/*-
		 * 'ConcurrentHashMap' is preferable to a synchronized 'HashMap'
		 * 
		 * 'Hashtable' versus 'HashMap':
		 * 'Hashtable' is synchronized and 'HashMap' permits nulls
		 */
		final Map<String, String> map = C_A_B_MAP;
		Printer.printf(ORIGINAL_MAP_FMT, map);

		final ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap<>();
		concurrentHashMap.putAll(map);
		// executing 'concurrentHashMap.put(null, null)' causes 'NullPointerException'
		// executing 'concurrentHashMap.put("key", null)' causes 'NullPointerException'
		Printer.printf("OK %s ConcurrentHashMap", concurrentHashMap);

		final Map<String, String> synchronizedHashMap = Collections.synchronizedMap(new HashMap<>());
		synchronizedHashMap.put(null, null);
		synchronizedHashMap.put("Dk", null);
		synchronizedHashMap.putAll(map);
		Printer.printf("NO %s synchronized HashMap", synchronizedHashMap);
		Printer.printHor();
	}

	/**
	 * The {@link ConcurrentSkipListMap} should be preferred.<br>
	 * The {@link ConcurrentSkipListSet} should be preferred.
	 * <p>
	 * <i>Synchronization</i>
	 */
	public static void preferConcurrentSkipListMapAndConcurrentSkipListSet() {
		/*-
		 * 'ConcurrentSkipListMap' is preferable to a synchronized 'TreeMap'.
		 * 
		 * Skip lists are data structures that can be used in place of balanced trees.
		 * Skip lists use probabilistic balancing rather than strictly enforced balancing.
		 */
		final Map<String, String> map = C_A_B_MAP;
		Printer.printf(ORIGINAL_MAP_FMT, map);

		final SortedMap<String, String> concurrentSkipListMap = new ConcurrentSkipListMap<>();
		concurrentSkipListMap.putAll(map);
		// executing 'concurrentSkipListMap.put(null, null)' causes
		// 'NullPointerException'
		// executing 'concurrentSkipListMap.put("key", null)' causes
		// 'NullPointerException'
		Printer.printf("OK %s ConcurrentSkipListMap", concurrentSkipListMap);

		final SortedMap<String, String> synchronizedTreeMap = Collections.synchronizedSortedMap(new TreeMap<>());
		// executing 'synchronizedTreeMap.put(null, null)' causes 'NullPointerException'
		synchronizedTreeMap.put("Dk", null);
		synchronizedTreeMap.putAll(map);
		Printer.printf("NO %s synchronized TreeMap%n", synchronizedTreeMap);

		final SortedSet<String> concurrentSkipListSet = new ConcurrentSkipListSet<>();
		concurrentSkipListSet.addAll(map.keySet());
		Printer.printf("OK %s ConcurrentSkipListSet", concurrentSkipListSet);

		Printer.printHor();
	}
}
