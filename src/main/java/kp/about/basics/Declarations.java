package kp.about.basics;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kp.utils.Printer;

/**
 * The annotation type <b>@Example</b> definition.
 *
 */
@interface Example {
	/**
	 * The array.
	 * 
	 * @return the array
	 */
	String[] array() default { "A", "B", "C" };
}

/**
 * Only local variables declarations (these variables are mostly unused).
 *
 */
public interface Declarations {

	/**
	 * This method has intentionally many unused declarations.
	 */
	@SuppressWarnings("unused")
	static void declareLocalVariables() {

		Printer.print("Declarations");
		/*
		 * Function.
		 */
		final Stream<Function<String, String>> stringFunctionStream = Stream.of(String::trim, String::toUpperCase);
		final Function<String, char[]> toCharArrayFunction = String::toCharArray;
		final Function<char[], String> valueOfStringFunction = String::valueOf;
		final Function<String, List<String>> asListFunction = Arrays::asList;

		final Function<String, Object> objectFunction = Function.identity().compose(Object::toString);
		final Function<String, Void> voidFunction1 = objectFunction.andThen(arg -> null);
		final Function<List<String>, String[]> toArrayFunction = arg -> arg.toArray(String[]::new);
		/*
		 * Primitive specializations of Function.
		 */
		final IntFunction<String> valueOfIntegerFunction = String::valueOf;
		final IntFunction<int[]> intArrDim1Function1 = arg -> new int[arg];
		final IntFunction<int[]> intArrDim1Function2 = int[]::new;
		final IntFunction<int[][][]> intArrDim3Function1 = arg -> new int[arg][][];
		final IntFunction<int[][][]> intArrDim3Function2 = int[][][]::new;

		final ToIntFunction<String> toIntFunction = str -> Objects.nonNull(str) && str.isEmpty() ? 0 : 1;
		final ToDoubleFunction<String> toDoubleFunction = Double::valueOf;
		/*
		 * Operators - specializations of Function.
		 */
		final UnaryOperator<Void> voidUnaryOperator = arg -> null;
		final UnaryOperator<String> stringUnaryOperator = str -> String.join("-");
		final UnaryOperator<BigDecimal> bigDecimalUnaryOperator = BigDecimal::negate;
		final DoubleUnaryOperator xToY = Math::exp;
		final DoubleUnaryOperator yToX = Math::log;
		final DoubleUnaryOperator yToYToX = xToY.andThen(yToX);

		final UnaryOperator<String> unaryOperator001 = String::toUpperCase;
		final UnaryOperator<String> unaryOperator002 = unaryOperator001::apply;
		/*
		 * Two-arity specializations of Function.
		 */
		final BiFunction<Integer, String, List<String>> nCopiesFunction = Collections::nCopies;
		final BiFunction<List<String>, String, Integer> frequencyFunction = Collections::frequency;
		final BiFunction<Integer, Long, Stream<Number>> numberStreamBiFunction = Stream::of;

		final ToDoubleBiFunction<Double, Integer> toDoubleBiFunction = Math::scalb;

		final BinaryOperator<String> stringBinaryOperator = String::join;
		final BinaryOperator<String> maxLengthString = BinaryOperator.maxBy(Comparator.comparingInt(String::length));
		@SuppressWarnings("java:S4276") // switch off Sonarqube rule 'Functional Interfaces should be specialized'
		final BinaryOperator<Integer> integerBinaryOperator = BinaryOperator.maxBy(Comparator.reverseOrder());

		final BinaryOperator<BigDecimal> bigDecimalBinaryOperator = BigDecimal::multiply;
		final IntBinaryOperator intBinaryOperator = Math::multiplyExact;
		final LongBinaryOperator longBinaryOperator = Math::multiplyExact;
		final DoubleBinaryOperator doubleBinaryOperator = (s, t) -> s * t;

		final BinaryOperator<String> binaryOperator001 = (s, t) -> s + t;
		final BinaryOperator<String> binaryOperator002 = binaryOperator001::apply;
		/*
		 * Consumer.
		 */
		final Consumer<Object> objectConsumer = arg -> {
		};
		final Consumer<List<?>> listConsumerFull = (final @Example(array = { "A", "B", "C" }) List<?> arg) -> {
		};
		final Consumer<Character> characterConsumer = objectConsumer::accept;
		final Consumer<Void> voidConsumer = objectConsumer::accept;
		final BiConsumer<Object, Object> objectBiConsumer = (arg1, arg2) -> {
		};
		final BiConsumer<Integer, String> biConsumer = objectBiConsumer::accept;
		final ObjDoubleConsumer<String> stringDoubleConsumer = objectBiConsumer::accept;
		/*
		 * Primitive specializations of Consumer.
		 */
		final IntConsumer intConsumer = objectConsumer::accept;
		/*
		 * Supplier.
		 */
		final Supplier<Declarations> thisSupplier = () -> new Declarations() {
			final Supplier<String> stringSupplier = this::toString;
			final Supplier<String> stringSupplierBis = stringSupplier::get;
			final Supplier<String> nameSupplier = this.getClass()::getName;
			final IntSupplier intSupplier = this::hashCode;
		};
		final Supplier<Void> voidSupplier = () -> null;
		final Supplier<Character> characterSupplier = () -> Character.MAX_VALUE;
		final Supplier<Character> characterSupplierBis = characterSupplier::get;
		final Supplier<Random> randomSupplier = Random::new;
		final Supplier<String> stringSupplier = Object.class::getName;
		final Supplier<List<String>> listSupplier = ArrayList::new;
		/*
		 * Primitive specializations of Supplier.
		 */
		final IntSupplier intSupplier1 = () -> Integer.MAX_VALUE;
		final IntSupplier intSupplier2 = intSupplier1::getAsInt;
		/*
		 * Predicate.
		 */
		final Stream<Predicate<List<String>>> listPredicateStream = Stream.of(List::isEmpty, Objects::nonNull);
		final Predicate<String> predicate1 = String::isEmpty;
		final ToIntFunction<String> testFunction = str -> predicate1.test(str) ? 0 : 1;
		final Predicate<String> predicate2 = predicate1::test;
		final Predicate<String> composedPredicate = predicate1.or(Objects::isNull).negate().and(Objects::nonNull);
		final Predicate<String> prefixPredicate = str -> str.startsWith("prefix");

		final BiPredicate<String, String> stringMatchesPredicate = String::matches;
		final BiPredicate<String, String> patternMatchesPredicate = Pattern::matches;
		final BiPredicate<LocalDate, LocalDate> isBeforePredicate = LocalDate::isBefore;
		/*
		 * Predicate versus function.
		 */
		new Declarations() {
			final Function<Object, Boolean> function = this::equals;
			final Predicate<Object> predicate = this::equals;
		};
		/*
		 * Listener.
		 */
		final Consumer<Object> processingConsumer = arg -> {
		};
		final ActionListener actionListener = processingConsumer::accept;
		final PropertyChangeListener propertyChangeListener = processingConsumer::accept;
		/*
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
		/*
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