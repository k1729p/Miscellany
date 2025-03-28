package kp.about.basics;

import kp.utils.Printer;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The annotation type <b>@Example</b> definition.
 */
@interface Example {
    /**
     * The array.
     *
     * @return the array
     */
    String[] array() default {"A", "B", "C"};
}

/**
 * Only local variables declarations (these variables are mostly unused).
 */
public interface Declarations {

    /**
     * This method has intentionally many unused declarations.
     */
    @SuppressWarnings("unused")
    static void declareLocalVariables() {

        Printer.print("Declarations");
        /*-
         * Function.
         */
        final Stream<Function<String, String>> stringFunctionStream = Stream.of(String::trim, String::toUpperCase);
        final Function<String, char[]> toCharArrayFunction = String::toCharArray;
        final Function<char[], String> valueOfStringFunction = String::valueOf;
        final Function<String, List<String>> asListFunction = Arrays::asList;

        final Function<String, Object> objectFunction = Function.identity().compose(Object::toString);
        final Function<String, Void> voidFunction1 = objectFunction.andThen(arg -> null);
        final Function<List<String>, String[]> toArrayFunction = arg -> arg.toArray(String[]::new);
        /*-
         * Primitive specializations of Function.
         */
        final IntFunction<String> valueOfIntFunction = String::valueOf;
        final IntFunction<int[]> intArrDim1Function = int[]::new;
        final IntFunction<int[][][]> intArrDim3Function = int[][][]::new;

        final ToIntFunction<String> toIntFunction = str -> Objects.nonNull(str) && str.isEmpty() ? 0 : 1;
        final ToDoubleFunction<String> toDoubleFunction = Double::valueOf;
        /*-
         * Operators - specializations of Function.
         */
        final UnaryOperator<Void> voidUnaryOperator = arg -> null;
        final UnaryOperator<String> joinUnaryOperator = str -> String.join("-");
        final UnaryOperator<String> toUpperCaseUnaryOperator = String::toUpperCase;
        final UnaryOperator<BigDecimal> bigDecimalUnaryOperator = BigDecimal::negate;
        final DoubleUnaryOperator xToY = Math::exp;
        final DoubleUnaryOperator yToX = Math::log;
        final DoubleUnaryOperator yToYToX = xToY.andThen(yToX);
        /*-
         * Two-arity specializations of Function.
         */
        final BiFunction<Integer, String, List<String>> nCopiesBiFunction = Collections::nCopies;
        final BiFunction<List<String>, String, Integer> frequencyBiFunction = Collections::frequency;
        final BiFunction<Integer, Long, Stream<Number>> numberStreamBiFunction = Stream::of;

        final ToDoubleBiFunction<Double, Integer> toDoubleBiFunction = Math::scalb;

        final BinaryOperator<String> stringAddBinaryOperator = (s, t) -> s + t;
        final BinaryOperator<String> stringJoinBinaryOperator = String::join;
        final BinaryOperator<String> maxLengthString = BinaryOperator.maxBy(Comparator.comparingInt(String::length));
        @SuppressWarnings("java:S4276") // switch off Sonarqube rule 'Functional Interfaces should be specialized'
        final BinaryOperator<Integer> integerBinaryOperator = BinaryOperator.maxBy(Comparator.reverseOrder());

        final BinaryOperator<BigDecimal> bigDecimalBinaryOperator = BigDecimal::multiply;
        final IntBinaryOperator intBinaryOperator = Math::multiplyExact;
        final LongBinaryOperator longBinaryOperator = Math::multiplyExact;
        final DoubleBinaryOperator doubleBinaryOperator = (s, t) -> s * t;
        /*-
         * Consumer.
         */
        final Consumer<Object> objectConsumer = arg -> {
        };
        final Consumer<List<?>> listConsumerFull = (final @Example(array = {"X", "Y", "Z"}) List<?> arg) -> {
        };
        final Consumer<Character> characterConsumer = objectConsumer::accept;
        final Consumer<Void> voidConsumer = objectConsumer::accept;
        final BiConsumer<Object, Object> objectBiConsumer = (arg1, arg2) -> {
        };
        final BiConsumer<Integer, String> biConsumer = objectBiConsumer::accept;
        final ObjDoubleConsumer<String> stringDoubleConsumer = objectBiConsumer::accept;
        /*-
         * Primitive specializations of Consumer.
         */
        final IntConsumer intConsumer = objectConsumer::accept;
        /*
         * Supplier.
         */
        final Supplier<Declarations> thisSupplier = () -> new Declarations() {
            final Supplier<String> stringSupplier = this::toString;
            final Supplier<String> nameSupplier = this.getClass()::getName;
            final IntSupplier intSupplier = this::hashCode;
        };
        final Supplier<Void> voidSupplier = () -> null;
        final Supplier<Character> characterSupplier = () -> Character.MAX_VALUE;
        final Supplier<Random> randomSupplier = Random::new;
        final Supplier<String> stringSupplier = Object.class::getName;
        final Supplier<List<String>> listSupplier = ArrayList::new;
        /*-
         * Primitive specializations of Supplier.
         */
        final IntSupplier intSupplier = () -> Integer.MAX_VALUE;
        /*-
         * Predicate.
         */
        final Stream<Predicate<List<String>>> listPredicateStream = Stream.of(List::isEmpty, Objects::nonNull);
        final Predicate<String> predicate = String::isEmpty;
        final ToIntFunction<String> testFunction = str -> predicate.test(str) ? 0 : 1;
        final Predicate<String> composedPredicate = predicate.or(Objects::isNull).negate().and(Objects::nonNull);
        final Predicate<String> prefixPredicate = str -> str.startsWith("prefix");

        final BiPredicate<String, String> stringMatchesPredicate = String::matches;
        final BiPredicate<String, String> patternMatchesPredicate = Pattern::matches;
        final BiPredicate<LocalDate, LocalDate> isBeforePredicate = LocalDate::isBefore;
        /*-
         * Predicate versus function.
         */
        new Declarations() {
            final Function<Object, Boolean> equalsFunction = this::equals;
            final Predicate<Object> equalsPredicate = this::equals;
        };
        /*-
         * Listener.
         */
        final Consumer<Object> processingConsumer = arg -> {
        };
        final ActionListener actionListener = processingConsumer::accept;
        final PropertyChangeListener propertyChangeListener = processingConsumer::accept;
        /*-
         * Collecting.
         */
        final Set<Integer> set1 = Stream.of(1).collect(Collectors.toSet());
        final List<Integer> list1 = Stream.of(1).toList();
        final List<Integer> list2 = Stream.of(1).collect(Collectors.toCollection(ArrayList::new));

        final Set<Integer> set2 = Stream.of(1).collect(HashSet::new, Set::add, Set::addAll);
        final List<Integer> list3 = Stream.of(1).collect(ArrayList::new, List::add, List::addAll);
        final Queue<Integer> queue = Stream.of(1).collect(LinkedList::new, Queue::add, Queue::addAll);
        final Deque<Integer> deque = Stream.of(1).collect(ArrayDeque::new, Deque::add, Deque::addAll);
        final Map<Integer, Integer> map = Map.of(1, 1).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        final StringBuilder stringBuilder = Stream.of(1).collect(StringBuilder::new, StringBuilder::append,
                StringBuilder::append);
        /*-
         * Action starts endless loop.
         */
        final IntSupplier endlessSupplier = () -> {
            while (true)
                ;
        };
        final IntUnaryOperator endlessOperator = arg -> {
            while (true)
                ;
        };
        final IntConsumer endlessConsumer = arg -> {
            while (true)
                ;
        };
        Printer.printHor();
    }

}