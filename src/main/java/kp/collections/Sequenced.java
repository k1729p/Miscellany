package kp.collections;

import kp.utils.Printer;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Research the {@link SequencedCollection}, the {@link SequencedSet}, the
 * {@link SortedSet}, the {@link SequencedMap}, and the {@link SortedMap}.
 */
public class Sequenced {
    /**
     * Private constructor to prevent instantiation.
     */
    private Sequenced() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Process the sequenced collections and maps.
     */
    static void process() {
        Printer.print("▼▼▼ interface 'SequencedCollection' ▼▼▼▼");
        processSequencedCollection(new ArrayList<>());
        Printer.printEndLineOfTriangles();
        Printer.print("▼▼▼ interface 'SequencedCollection' ▼▼▼▼");
        processSequencedCollection(new ArrayDeque<>());
        Printer.printEndLineOfTriangles();
        Printer.print("▼▼▼ interface 'SequencedSet' ▼▼▼▼▼▼▼▼▼▼▼");
        processSequencedSet(new LinkedHashSet<>());
        Printer.printEndLineOfTriangles();
        Printer.print("▼▼▼ interface 'SortedSet' ▼▼▼▼▼▼▼▼▼▼▼▼▼▼");
        processSortedSet(new TreeSet<>());
        Printer.printEndLineOfTriangles();
        Printer.print("▼▼▼ interface 'SortedSet' ▼▼▼▼▼▼▼▼▼▼▼▼▼▼");
        processSortedSet(new ConcurrentSkipListSet<>());
        Printer.printEndLineOfTriangles();
        Printer.print("▼▼▼ interface 'SequencedMap' ▼▼▼▼▼▼▼▼▼▼▼");
        processSequencedMap(new LinkedHashMap<>());
        Printer.printEndLineOfTriangles();
        Printer.print("▼▼▼ interface 'SortedMap' ▼▼▼▼▼▼▼▼▼▼▼▼▼▼");
        processSortedMap(new TreeMap<>());
        Printer.printEndLineOfTriangles();
        Printer.print("▼▼▼ interface 'SortedMap' ▼▼▼▼▼▼▼▼▼▼▼▼▼▼");
        processSortedMap(new ConcurrentSkipListMap<>());
        Printer.printEndLineOfTriangles();
        Printer.printHor();
    }

    /**
     * Process the {@link SequencedCollection}.
     *
     * @param sequencedCollection the {@link SequencedCollection}
     */
    private static void processSequencedCollection(SequencedCollection<String> sequencedCollection) {

        sequencedCollection.addFirst("b");
        sequencedCollection.addLast("c");
        sequencedCollection.addFirst("a");
        sequencedCollection.addLast("d");
        showSequencedCollection(sequencedCollection);
    }

    /**
     * Process the {@link SequencedSet}.
     *
     * @param sequencedSet the {@link SequencedSet}
     */
    private static void processSequencedSet(SequencedSet<String> sequencedSet) {
        processSequencedCollection(sequencedSet);
    }

    /**
     * Process the {@link SortedSet}.
     *
     * @param sortedSet the {@link SortedSet}
     */
    private static void processSortedSet(SortedSet<String> sortedSet) {

        sortedSet.add("b");
        sortedSet.add("d");
        sortedSet.add("a");
        sortedSet.add("c");
        showSequencedCollection(sortedSet);
    }

    /**
     * Process the {@link SequencedMap}.
     *
     * @param sequencedMap the {@link SequencedMap}
     */
    private static void processSequencedMap(SequencedMap<String, String> sequencedMap) {

        sequencedMap.putFirst("B", "e");
        sequencedMap.putLast("C", "f");
        sequencedMap.putFirst("A", "g");
        sequencedMap.putLast("D", "h");
        showSequencedMap(sequencedMap);
    }

    /**
     * Process the {@link SortedMap}.
     *
     * @param sortedMap the {@link SortedMap}
     */
    private static void processSortedMap(SortedMap<String, String> sortedMap) {

        sortedMap.put("B", "e");
        sortedMap.put("D", "h");
        sortedMap.put("A", "g");
        sortedMap.put("C", "f");
        showSequencedMap(sortedMap);
    }

    /**
     * Show the {@link SequencedCollection}.
     *
     * @param sequencedCollection the {@link SequencedCollection}
     */
    private static void showSequencedCollection(SequencedCollection<String> sequencedCollection) {

        final String label = sequencedCollection.getClass().getSimpleName();
        Printer.printf("%s%s, first[%s], last[%s], reversed %s%s", label, sequencedCollection,
                sequencedCollection.getFirst(), sequencedCollection.getLast(), label, sequencedCollection.reversed());
        Printer.printf("%s remove first[%s], remove last[%s]", label, sequencedCollection.removeFirst(),
                sequencedCollection.removeLast());
        Printer.printf("%s%s <-- after remove", label, sequencedCollection);
    }

    /**
     * Show the {@link SequencedMap}.
     *
     * @param sequencedMap the {@link SequencedMap}
     */
    private static void showSequencedMap(SequencedMap<String, String> sequencedMap) {

        final String label = sequencedMap.getClass().getSimpleName();
        Printer.printf("%s%s, reversed %s%s", label, sequencedMap, label, sequencedMap.reversed());
        Printer.printf("%s first key[%s], last key[%s], first value[%s], last value[%s]", label,
                sequencedMap.sequencedKeySet().getFirst(), sequencedMap.sequencedKeySet().getLast(),
                sequencedMap.sequencedValues().getFirst(), sequencedMap.sequencedValues().getLast());
        Printer.printf("%s entrySet%s, first entry[%s], last entry[%s]", label, sequencedMap.sequencedEntrySet(),
                sequencedMap.firstEntry(), sequencedMap.lastEntry());
        Printer.printf("%s pool first entry[%s], pool last entry[%s]", label, sequencedMap.pollFirstEntry(),
                sequencedMap.pollLastEntry());
        Printer.printf("%s%s <-- after pool", label, sequencedMap);
    }
}
