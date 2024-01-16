package kp.streams.collecting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import kp.utils.Printer;

/**
 * The streams with 'null' and {@link Optional} elements.
 *
 */
public class NullsAndOptionals {

	/**
	 * The hidden constructor.
	 */
	private NullsAndOptionals() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Concatenates with the {@link Stream} from nullable element.
	 * 
	 */
	static void concatenateWithStreamFromNullable() {

		final Supplier<Stream<String>> supplierB = () -> Stream.of("A", "B");
		final Supplier<Stream<String>> supplierE = () -> Stream.of("D", "E");
		for (String nullableElement : new String[] { "C", null }) {
			final Stream<String> singleElementStream = Stream.ofNullable(nullableElement);
			final Stream<String> stream = Stream.concat(//
					Stream.concat(supplierB.get(), singleElementStream), //
					supplierE.get());
			Printer.printf("Concatenated with stream from nullable element[%4s], result%s", nullableElement,
					stream.toList());
		}
		Printer.printHor();
	}

	/**
	 * The nulls in a {@link Stream} without the {@link Optional}s.
	 * 
	 */
	static void nullsInStreamWithoutOptionals() {

		final Supplier<List<List<Integer>>> supplier = () -> {
			final List<List<Integer>> listOfLists = new ArrayList<>();
			for (int i = 1; i <= 3; i += 2) {
				final List<Integer> list = IntStream.rangeClosed(i, i + 1).boxed().toList();
				listOfLists.add(new ArrayList<>(list));
			}
			return listOfLists;
		};
		AtomicInteger atomic = new AtomicInteger();
		while (atomic.incrementAndGet() <= 5) {
			List<List<Integer>> srcList = prepareSourceList(supplier.get(), atomic.get());
			transformList(srcList);
		}
		Printer.printHor();
	}

	/**
	 * Prepares the source list.
	 * 
	 * @param srcList  the source list
	 * @param selector the selector
	 * @return the source list
	 */
	private static List<List<Integer>> prepareSourceList(List<List<Integer>> srcList, int selector) {

		switch (selector) {
		case 1:
			Printer.print("List unchanged:");
			break;
		case 2:
			srcList.remove(1);
			Printer.print("\nT.1 Remove the element on top dimension:");
			break;
		case 3:
			srcList.set(1, null);
			Printer.print("\nT.2 Set 'null' on top dimension:");
			break;
		case 4:
			srcList.get(1).remove(1);
			Printer.print("\nB.1 Remove the element on bottom dimension:");
			break;
		case 5:
			srcList.get(1).set(1, null);
			Printer.print("\nB.2 Set 'null' on bottom dimension:");
			break;
		default:
			break;
		}
		return srcList;
	}

	/**
	 * Transforms the list.
	 * 
	 * @param srcList the source list
	 */
	private static void transformList(List<List<Integer>> srcList) {

		Printer.printf("   Source list size[%d], source list%s", srcList.size(), srcList);
		final List<Integer> trgList = srcList.stream()//
				.filter(Objects::nonNull)/*- guards against NPE */
				.flatMap(List<Integer>::stream)//
				.filter(Objects::nonNull)/*- guards against null element in list */
				.toList();
		Printer.printf("   Target list size[%d], target list%s", trgList.size(), trgList);
	}

	/**
	 * The nulls in a {@link Stream} with the {@link Optional}s.
	 * 
	 */
	static void nullsInStreamWithOptionals() {

		final Supplier<Optional<List<Optional<List<Optional<Integer>>>>>> supplier = () -> {
			final Optional<List<Optional<List<Optional<Integer>>>>> optional = Optional.of(new ArrayList<>());
			for (int i = 1; i <= 3; i += 2) {
				final List<Optional<Integer>> list = IntStream.rangeClosed(i, i + 1).boxed()//
						.map(Optional::of).toList();
				optional.get().add(Optional.of(new ArrayList<>(list)));
			}
			return optional;
		};
		final List<Optional<List<Optional<List<Optional<Integer>>>>>> optionalList = IntStream.rangeClosed(1, 8).boxed()
				.map(arg -> supplier.get()).collect(Collectors.toCollection(ArrayList::new));
		final List<String> messageList = prepareOptionalList(optionalList);
		IntStream.range(0, optionalList.size())
				.forEach(index -> transformListWithOptionals(optionalList.get(index), messageList.get(index)));
		Printer.printHor();
	}

	/**
	 * Prepares the {@link List} with the {@link Optional}s.
	 * 
	 * @param optionalList the {@link List} with the {@link Optional}s
	 * @return the message list
	 */
	private static List<String> prepareOptionalList(
			List<Optional<List<Optional<List<Optional<Integer>>>>>> optionalList) {

		final List<String> messageList = new ArrayList<>();
		Optional<List<Optional<List<Optional<Integer>>>>> topOptional;
		Optional<List<Optional<Integer>>> bottomOptional;
		// case 1
		messageList.add("List of optionals unchanged:");
		// case 2
		optionalList.set(1, Optional.empty());
		messageList.add("\nSet 'empty' source list optional:");
		// case 3
		topOptional = optionalList.get(2);
        topOptional.ifPresent(optionals -> optionals.remove(1));
		messageList.add("\nT.1 Remove the optional on top dimension:");
		// case 4
		topOptional = optionalList.get(3);
        topOptional.ifPresent(optionals -> optionals.set(1, null));
		messageList.add("\nT.2 Set 'null' optional on top dimension:");
		// case 5
		topOptional = optionalList.get(4);
        topOptional.ifPresent(optionals -> optionals.set(1, Optional.empty()));
		messageList.add("\nT.3 Set 'empty' optional on top dimension:");
		// case 6
		topOptional = optionalList.get(5);
		if (topOptional.isPresent()) {
			bottomOptional = topOptional.get().get(1);
            bottomOptional.ifPresent(optionals -> optionals.remove(1));
		}
		messageList.add("\nB.1 Remove the optional on bottom dimension:");
		// case 7
		topOptional = optionalList.get(6);
		if (topOptional.isPresent()) {
			bottomOptional = topOptional.get().get(1);
            bottomOptional.ifPresent(optionals -> optionals.set(1, null));
		}
		messageList.add("\nB.2 Set 'null' optional on bottom dimension:");
		// case 8
		topOptional = optionalList.get(7);
		if (topOptional.isPresent()) {
			bottomOptional = topOptional.get().get(1);
            bottomOptional.ifPresent(optionals -> optionals.set(1, Optional.empty()));
		}
		messageList.add("\nB.3 Set 'empty' optional on bottom dimension:");
		return messageList;
	}

	/**
	 * Transforms the list with the {@link Optional}s.
	 * 
	 * @param srcList the source list
	 * @param message the message
	 */
	private static void transformListWithOptionals(Optional<List<Optional<List<Optional<Integer>>>>> srcList,
			String message) {

		Printer.print(message);
		if (srcList.isEmpty()) {
			Printer.print("   Source list is 'empty'");
		} else {
			Printer.printf("   Source list size[%d], source list%s", srcList.get().size(), srcList.get());
		}
		final List<Integer> trgList = srcList.stream()//
				.flatMap(List::stream)//
				.filter(Objects::nonNull)/*- guards against NPE (when 'null' on top dimension)*/
				.flatMap(Optional::stream)//
				.flatMap(List::stream)//
				.filter(Objects::nonNull)/*- guards against NPE (when 'null' on bottom dimension)*/
				.filter(Optional::isPresent)/*- guards against NoSuchElementException ('empty' optional on bottom dimension)*/
				.map(Optional::get)//
				.toList();
		Printer.printf("   Target list size[%d], target list%s", trgList.size(), trgList);
	}

}
