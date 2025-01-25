package kp.sorting;

import kp.utils.Printer;

import java.text.Collator;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The main class for sorting research.
 */
public class ApplicationForSorting {

    /**
     * Private constructor to prevent instantiation.
     */
    private ApplicationForSorting() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The primary entry point for launching the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        Printer.printHor();
        sortStringArray();
        sortIntegerArray();
        sortStreamWithNaturalOrder();
        sortStreamWithLocale();
        sortMapByKeyOrByValue();
        sortWithComparableAndComparator();
    }

    /**
     * Sorts an array of {@link String} elements.
     */
    private static void sortStringArray() {

        final String[] arraySrc = "def,abc,ghi".split(",");
        Printer.printf("source 'String'  array %s", Arrays.asList(arraySrc));
        final String[] arrayCopy1 = Arrays.copyOf(arraySrc, arraySrc.length);
        final String[] arrayCopy2 = Arrays.copyOf(arraySrc, arraySrc.length);
        final String[] arrayCopy3 = Arrays.copyOf(arraySrc, arraySrc.length);

        Arrays.sort(arrayCopy1);// sort the array using the natural order
        Arrays.sort(arrayCopy2, String.CASE_INSENSITIVE_ORDER);
        Arrays.sort(arrayCopy3, String::compareToIgnoreCase);

        Printer.printf("sorted 'String'  array %s, arrayCopy1 equals: arraySrc[%b], arrayCopy2[%b], arrayCopy3[%b]",
                Arrays.asList(arrayCopy1), Arrays.equals(arrayCopy1, arraySrc), Arrays.equals(arrayCopy1, arrayCopy2),
                Arrays.equals(arrayCopy1, arrayCopy3));
        Printer.printHor();
    }

    /**
     * Sorts an array of {@link Integer} elements.
     */
    private static void sortIntegerArray() {

        final Integer[] arraySrc = {456, 123, 789};
        Printer.printf("source 'Integer' array %s", Arrays.asList(arraySrc));
        final Integer[] arrayCopy1 = Arrays.copyOf(arraySrc, arraySrc.length);
        final Integer[] arrayCopy2 = Arrays.copyOf(arraySrc, arraySrc.length);
        final Integer[] arrayCopy3 = Arrays.copyOf(arraySrc, arraySrc.length);

        Arrays.sort(arrayCopy1);// sort the array using the natural order
        Arrays.sort(arrayCopy2, Integer::compareTo);
        final Comparator<Integer> comparatorForIntegers = (first, second) -> {
            if (first < second) {
                return -1;
            } else {
                return first > second ? 1 : 0;
            }
        };
        Arrays.sort(arrayCopy3, comparatorForIntegers);
        Printer.printf("sorted 'Integer' array %s, arrayCopy1 equals: arraySrc[%b], arrayCopy2[%b], arrayCopy3[%b]",
                Arrays.asList(arrayCopy1), Arrays.equals(arrayCopy1, arraySrc), Arrays.equals(arrayCopy1, arrayCopy2),
                Arrays.equals(arrayCopy1, arrayCopy3));
        Printer.printHor();
    }

    /**
     * Sorts a {@link Stream} with the natural order and the reverse order.
     */
    private static void sortStreamWithNaturalOrder() {

        final Supplier<Stream<String>> streamSupplier = () -> Stream.of("B-1", "A-2", "D-3", "C-4");
        Printer.print("Unsorted stream collected into:");
        final Set<String> hashSet = streamSupplier.get().collect(Collectors.toCollection(HashSet::new));
        Printer.printf("  HashSet       %s (unordered)", hashSet);

        final Set<String> linkedHashSet = streamSupplier.get().collect(Collectors.toCollection(LinkedHashSet::new));
        Printer.printf("  LinkedHashSet %s (insertion order)", linkedHashSet);

        final Set<String> treeSet = streamSupplier.get().collect(Collectors.toCollection(TreeSet::new));
        Printer.printf("  TreeSet       %s (natural order)", treeSet);

        final List<String> list1 = streamSupplier.get().collect(Collectors.toCollection(ArrayList::new));
        Printer.printf("  List          %s (insertion order)", list1);

        Printer.print("Natural order sorted stream collected into:");
        final List<String> list2 = streamSupplier.get().sorted().toList();
        Printer.printf("  List          %s (insertion order)", list2);

        Printer.print("Reverse order sorted stream collected into:");
        final List<String> list3 = streamSupplier.get().sorted(Collections.reverseOrder()).toList();
        Printer.printf("  List          %s (insertion order)%n", list3);

        Printer.printf("Before list1 sorting: list1 equals list2 [%b]", list1.equals(list2));
        list1.sort(null);
        Printer.printf("After  list1 sorting: list1 equals list2 [%b]", list1.equals(list2));
        Printer.printHor();
    }

    /**
     * Sorts a {@link Stream} with the {@link Locale}.
     */
    private static void sortStreamWithLocale() {

        final Supplier<Stream<String>> streamSupplier = () -> Stream.of("Z", "z", "Ä", "ä", "À", "à", "A", "a");
        Printer.print("Sort a stream with the 'Locale.FRANCE':");
        final List<String> listUnsorted = streamSupplier.get().collect(Collectors.toCollection(ArrayList::new));
        Printer.printf("Collected unsorted stream \t\t\t\t%s", listUnsorted);

        final List<String> listNaturalOrder = streamSupplier.get().sorted().toList();
        Printer.printf("Collected sorted stream - natural order \t\t%s", listNaturalOrder);

        final Collator collator = Collator.getInstance(Locale.FRANCE);
        collator.setStrength(Collator.PRIMARY);
        final List<String> listPrimary = streamSupplier.get().sorted(collator).toList();
        Printer.printf("Collected sorted stream - collator strength 'PRIMARY'   %s", listPrimary);
        collator.setStrength(Collator.SECONDARY);
        final List<String> listSecondary = streamSupplier.get().sorted(collator).toList();
        Printer.printf("Collected sorted stream - collator strength 'SECONDARY' %s", listSecondary);
        collator.setStrength(Collator.TERTIARY);
        final List<String> listTertiary = streamSupplier.get().sorted(collator).toList();
        Printer.printf("Collected sorted stream - collator strength 'TERTIARY'  %s", listTertiary);

        final Map<String, String> mapTertiary = new TreeMap<>(collator);
        streamSupplier.get().forEach(arg -> mapTertiary.put(arg, "example"));
        Printer.printf("'TreeMap' (created with 'TERTIARY' collator) - its keys %s", mapTertiary.keySet());
        listUnsorted.sort(collator);
        Printer.printf("List (unsorted stream) sorted with 'TERTIARY' collator  %s", listUnsorted);
        Printer.printHor();
    }

    /**
     * Sorts a {@link Map} by the key and by the value.
     */
    private static void sortMapByKeyOrByValue() {

        final Map<String, String> map = Map.of("kB", "vC", "kC", "vA", "kA", "vB");
        Printer.printf("%s <-- source map", map);

        final Map<String, Comparator<? super Entry<String, String>>> comparatorMap = new TreeMap<>();
        comparatorMap.put("key", Map.Entry.comparingByKey());
        comparatorMap.put("key reverse", Collections.reverseOrder(Map.Entry.comparingByKey()));
        comparatorMap.put("value", Map.Entry.comparingByValue());
        comparatorMap.put("value reverse", Collections.reverseOrder(Map.Entry.comparingByValue()));

        comparatorMap.forEach((description, comparator) -> {
            final Map<String, String> sortedMap = new LinkedHashMap<>();
            map.entrySet().stream().sorted(comparator)
                    .forEachOrdered(arg -> sortedMap.put(arg.getKey(), arg.getValue()));
            Printer.printf("%s <-- map sorted by %s", sortedMap, description);
        });
        Printer.printHor();
    }

    /**
     * Sorts with the {@link Comparable} and the {@link Comparator}.
     */
    private static void sortWithComparableAndComparator() {

        Printer.print("Sort with comparable and comparator:");
        final List<ValueObject> listSrc = IntStream.rangeClosed(0, ValueObject.ITEMS)
                .mapToObj(i -> new ValueObject(ValueObject.ITEMS - i)).toList();
        Printer.printf("    source %s", listSrc);

        final Set<ValueObject> set = new TreeSet<>(listSrc);
        Printer.printf("  tree set %s", set);

        final List<ValueObject> list1 = listSrc.stream().sorted().toList();
        Printer.printf("   lists 1 %s (natural order)", list1);

        final Comparator<ValueObject> compMinInCen = Comparator.comparing(ValueObject::getValueForMinimumInCenter);
        final List<ValueObject> list2 = listSrc.stream().sorted(compMinInCen).toList();
        Printer.printf("   lists 2 %s (ascending from center)", list2);

        final Comparator<ValueObject> compEvnOddPairs = Comparator.comparing(ValueObject::getValueForEvenAndOdd);
        final List<ValueObject> list3 = listSrc.stream().sorted(compEvnOddPairs).toList();
        Printer.printf("   lists 3 %s (even & odd pairs ascending)", list3);
        // chain of comparators
        final Comparator<ValueObject> compFirstEvnThenOdd = Comparator.comparing(ValueObject::getValueForMod2)
                .thenComparing(Comparator.naturalOrder());
        final List<ValueObject> list4 = listSrc.stream().sorted(compFirstEvnThenOdd).toList();
        Printer.printf("   lists 4 %s (first even ascending then odd ascending)", list4);
        Printer.printHor();
    }
}

/**
 * The value object.
 */
class ValueObject implements Comparable<ValueObject> {
    /**
     * The items.
     */
    static final int ITEMS = 8;
    /**
     * The value.
     */
    private final int value;

    /**
     * Constructor.
     *
     * @param value the value
     */
    ValueObject(int value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     *
     * @param arg the argument
     * @return the result
     */
    @Override
    public int compareTo(ValueObject arg) {
        return Integer.compare(this.value, arg.value);
    }

    /**
     * Gets the value for minimum in the center.
     *
     * @return the result
     */
    int getValueForMinimumInCenter() {
        return value < (ITEMS / 2) + 1 ? value : -value;
    }

    /**
     * Gets the value for even and odd.
     *
     * @return the result
     */
    int getValueForEvenAndOdd() {
        return value / 2 + value % 2;
    }

    /**
     * Gets the value for modulo 2.
     *
     * @return the result
     */
    int getValueForMod2() {
        return -value % 2;
    }

    /**
     * {@inheritDoc}
     *
     * @return the result
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * {@inheritDoc}
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        return prime + value;
    }

    /**
     * {@inheritDoc}
     *
     * @param object the object
     * @return the result
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        return value == ((ValueObject) object).value;
    }

}
