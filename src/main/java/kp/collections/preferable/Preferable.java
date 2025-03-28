package kp.collections.preferable;

import kp.utils.Printer;

import java.util.*;
import java.util.concurrent.*;

/**
 * Demonstrates preferable coding practices for collections.
 */
public class Preferable {
    /*-
     * A concurrent collection is thread-safe, but not governed by a single exclusion lock.
     *
     * The list returned from 'Stream::toList' is unmodifiable.
     * Hint: in a multithreaded environment, using immutable collections can prevent
     * issues related to concurrent modifications and reduce the need for synchronization.
     *
     * The 'Stream::toList' should be preferred over 'Stream::collect'.
     * 'Stream::toList' accumulates the elements faster than 'Stream::collect'.
     */
    private static final String ORIGINAL_MAP_FMT = "   %s original source map";
    private static final List<String> C_A_B_LIST = List.of("C", "A", "B");
    private static final Map<String, String> C_A_B_MAP = Map.ofEntries(
            Map.entry("Ck", "Cv"), Map.entry("Ak", "Av"), Map.entry("Bk", "Bv"));

    /**
     * Private constructor to prevent instantiation.
     */
    private Preferable() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Demonstrates the preference for:
     * <ul>
     * <li>faster {@link ArrayDeque}</li>
     * <li>faster {@link ConcurrentLinkedDeque}</li>
     * </ul>
     * <p>
     * <i>Speed</i>
     * </p>
     */
    public static void preferArrayDeque() {
        /*-
         * 'ArrayDeque' is likely to be:
         *  - faster than 'Stack' when used as a stack
         *  - faster than 'LinkedList' when used as a queue
         */
        final Deque<String> arrayDeque = new ArrayDeque<>(C_A_B_LIST);
        /*-
         * Executing 'arrayDeque.add(null)' causes 'NullPointerException'.
         */
        Printer.printf("faster %s ArrayDeque", arrayDeque);

        @SuppressWarnings("java:S1149") // switch off Sonarqube rule 'Synchronized class Stack should not be used'
        final Stack<String> stack = new Stack<>();
        stack.addAll(C_A_B_LIST);
        stack.add(null);
        Printer.printf("slower %s Stack%n", stack);

        final Deque<String> concurrentLinkedDeque = new ConcurrentLinkedDeque<>(C_A_B_LIST);
        /*-
         * Executing 'concurrentLinkedDeque.add(null)' causes 'NullPointerException'.
         */
        Printer.printf("faster %s ConcurrentLinkedDeque", concurrentLinkedDeque);
        final List<String> linkedList = new LinkedList<>(C_A_B_LIST);
        linkedList.add(null);
        Printer.printf("slower %s LinkedList", linkedList);
        Printer.printHor();
    }

    /**
     * Demonstrates the preference for:
     * <ul>
     * <li>ordered {@link LinkedHashMap}</li>
     * <li>ordered {@link LinkedHashSet}</li>
     * </ul>
     * <p>
     * <i>Ordering</i>
     * </p>
     */
    public static void preferLinkedHashMapAndLinkedHashSet() {
        final Map<String, String> map = C_A_B_MAP;
        Printer.printf(ORIGINAL_MAP_FMT, map);
        /*-
         * 'LinkedHashMap' is preferable to 'HashMap' and 'Hashtable'
         * because it has predictable iteration order.
         *
         * Constructor creates a copy of a map that has the same order as the original.
         */
        final Map<String, String> linkedHashMap = new LinkedHashMap<>(map);
        Printer.printf("OK %s LinkedHashMap%n", linkedHashMap);
        /*-
         * 'LinkedHashSet' is preferable to 'HashSet'
         * because it has predictable iteration order.
         */
        final Set<String> linkedHashSet = new LinkedHashSet<>(map.keySet());
        Printer.printf("OK %s LinkedHashSet", linkedHashSet);
        Printer.printHor();
    }

    /**
     * Demonstrates the preference for {@link CopyOnWriteArrayList}.
     * <p>
     * <i>Synchronization</i>
     * </p>
     */
    public static void preferCopyOnWriteArrayList() {
        /*-
         * 'CopyOnWriteArrayList' is preferable to a synchronized 'ArrayList'.
         *
         * The 'ArrayList' itself is not synchronized.
         * The 'CopyOnWriteArrayList' is a thread-safe variant of 'ArrayList'.
         */
        final List<String> copyOnWriteArrayList = new CopyOnWriteArrayList<>(C_A_B_LIST);
        copyOnWriteArrayList.add(null);
        Printer.printf("OK %s CopyOnWriteArrayList", copyOnWriteArrayList);

        final List<String> synchronizedArrayList = Collections.synchronizedList(new ArrayList<>());
        synchronizedArrayList.addAll(C_A_B_LIST);
        synchronizedArrayList.add(null);
        Printer.printf("NO %s synchronized ArrayList", synchronizedArrayList);
        Printer.printHor();
    }

    /**
     * Demonstrates the preference for {@link ConcurrentHashMap}.
     * <p>
     * <i>Synchronization</i>
     * </p>
     */
    public static void preferConcurrentHashMap() {
        final Map<String, String> map = C_A_B_MAP;
        Printer.printf(ORIGINAL_MAP_FMT, map);
        /*-
         * 'ConcurrentHashMap' is preferable to a synchronized 'HashMap'
         *
         * 'Hashtable' versus 'HashMap':
         * 'Hashtable' is synchronized and 'HashMap' permits nulls
         */
        final ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap<>(map);
        /*-
         * Executing 'concurrentHashMap.put(null, null)' causes 'NullPointerException'.
         * Executing 'concurrentHashMap.put("key", null)' causes 'NullPointerException'.
         */
        Printer.printf("OK %s ConcurrentHashMap", concurrentHashMap);

        final Map<String, String> synchronizedHashMap = Collections.synchronizedMap(new HashMap<>());
        synchronizedHashMap.put(null, null);
        synchronizedHashMap.put("Dk", null);
        synchronizedHashMap.putAll(map);
        Printer.printf("NO %s synchronized HashMap", synchronizedHashMap);
        Printer.printHor();
    }

    /**
     * Demonstrates the preference for:
     * <ul>
     * <li>{@link ConcurrentSkipListMap}</li>
     * <li>{@link ConcurrentSkipListSet}</li>
     * </ul>
     * <p>
     * <i>Synchronization</i>
     * </p>
     */
    public static void preferConcurrentSkipListMapAndConcurrentSkipListSet() {
        final Map<String, String> map = C_A_B_MAP;
        Printer.printf(ORIGINAL_MAP_FMT, map);
        /*-
         * 'ConcurrentSkipListMap' is preferable to a synchronized 'TreeMap'.
         *
         * Skip lists are data structures that can be used in place of balanced trees.
         * Skip lists use probabilistic balancing rather than strictly enforced balancing.
         */
        final ConcurrentNavigableMap<String, String> concurrentSkipListMap = new ConcurrentSkipListMap<>(map);
        /*-
         * Executing 'concurrentSkipListMap.put(null, null)' causes 'NullPointerException'
         * Executing 'concurrentSkipListMap.put("key", null)' causes 'NullPointerException'
         */
        Printer.printf("OK %s ConcurrentSkipListMap", concurrentSkipListMap);

        final SortedMap<String, String> synchronizedTreeMap = Collections.synchronizedSortedMap(new TreeMap<>());
        /*-
         * Executing 'synchronizedTreeMap.put(null, null)' causes 'NullPointerException'
         */
        synchronizedTreeMap.put("Dk", null);
        synchronizedTreeMap.putAll(map);
        Printer.printf("NO %s synchronized TreeMap%n", synchronizedTreeMap);

        final NavigableSet<String> concurrentSkipListSet = new ConcurrentSkipListSet<>(map.keySet());
        Printer.printf("OK %s ConcurrentSkipListSet", concurrentSkipListSet);

        Printer.printHor();
    }
}
