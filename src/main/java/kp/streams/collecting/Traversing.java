package kp.streams.collecting;

import java.util.ArrayDeque;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kp.utils.Printer;

/**
 * Traversing the {@link List}, the {@link Queue}, and the {@link Deque}.
 *
 */
public class Traversing {

	/**
	 * The hidden constructor.
	 */
	private Traversing() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Traverses the {@link CopyOnWriteArrayList} with the {@link Iterator}.
	 * <p>
	 * It is a thread-safe variant of list. The {@link ArrayList} itself is not
	 * synchronized.
	 */
	static void traverseListWithIterator() {

		Printer.print("Traverse list:");
		/*-
		 * There was 'ConcurrentModificationException'
		 * when 'ArrayList::new' was used in place of 'CopyOnWriteArrayList::new'
		 */
		final List<String> list = Stream.of("E", "X", "A", "M", "P", "L", "E")
				.collect(Collectors.toCollection(CopyOnWriteArrayList::new));
		Printer.printf("Source  list: %s", list);
		final Iterator<String> iterator1 = list.iterator();
		list.removeIf("E"::equals);
		final Iterator<String> iterator2 = list.iterator();
		list.removeIf(Predicate.not("M"::equals));
		final Iterator<String> iterator3 = list.iterator();
		Printer.printf("Results list: %s", list);

		traverseList(iterator1);
		traverseList(iterator2);
		traverseList(iterator3);
		Printer.printHor();
	}

	/**
	 * Traverses the list with the {@link Iterator}.
	 * 
	 * @param iterator the {@link Iterator}
	 */
	private static void traverseList(Iterator<String> iterator) {

		final StringBuilder strBld = new StringBuilder();
		try {
			iterator.forEachRemaining(strBld::append);
		} catch (ConcurrentModificationException e) {
			Printer.print("Catched 'ConcurrentModificationException' from fail-fast");
		}
		Printer.printf("Traversing result[%s]", strBld.toString());
	}

	/**
	 * Traverses the {@link Queue}.
	 * 
	 */
	static void traverseQueue() {

		final Supplier<Stream<String>> streamSupplier = () -> Stream.of("E", "X", "A", "M", "P", "L", "E");
		Printer.print("Traverse queue:");
		StringBuilder strBld1 = streamSupplier.get().collect(StringBuilder::new, StringBuilder::append,
				StringBuilder::append);
		Printer.printf("Supplied source [%s]", strBld1);
		/*-
		 * For the 'Queue' interface, 'LinkedList' is the most commonly used implementation
		 */
		final Queue<String> linkedListQueue = streamSupplier.get().collect(Collectors.toCollection(LinkedList::new));
		final StringBuilder strBld2 = new StringBuilder();
		linkedListQueue.stream().forEach(strBld2::append);
		Printer.printf("Queue traversed [%s]", strBld2);

		final Queue<String> piorityQueue = streamSupplier.get().collect(Collectors.toCollection(PriorityQueue::new));
		final StringBuilder strBld3 = new StringBuilder();
		piorityQueue.stream().forEach(strBld3::append);
		Printer.printf("Queue traversed [%s] (priority queue)", strBld3);

		final StringBuilder strBld4 = new StringBuilder();
		while (!piorityQueue.isEmpty()) {
			strBld4.append(piorityQueue.poll());
		}
		Printer.printf("                [%s] ◄- pooled sorted from priority queue", strBld4);
		/*-
		 * When using 'LinkedList::new' in place of 'ConcurrentLinkedQueue::new'
		 * the queue is not thread safe.
		 */
		final Supplier<Queue<String>> supplier = ConcurrentLinkedQueue::new;
		final Queue<String> concurrentLinkedQueue = streamSupplier.get().collect(supplier, Queue::add, Queue::addAll);
		Printer.printf("ConcurrentLinkedQueue %s", concurrentLinkedQueue);
		Printer.printHor();
	}

	/**
	 * Traverses the {@link Deque}.
	 * 
	 */
	static void traverseDeque() {

		final Supplier<Stream<String>> streamSupplier = () -> Stream.of("E", "X", "A", "M", "P", "L", "E");
		StringBuilder strBld1 = streamSupplier.get().collect(StringBuilder::new, StringBuilder::append,
				StringBuilder::append);
		Printer.print("Traverse deque:");
		Printer.printf("Supplied source [%s]", strBld1);
		/*-
		 * The 'ArrayDeque' class is the resizable array implementation of the 'Deque' interface,
		 * whereas the 'LinkedList' class is the list implementation.
		 * 
		 * In terms of efficiency, 'ArrayDeque' is more efficient than
		 * the 'LinkedList' for add and remove operation at both ends.
		 * The best operation in a 'LinkedList' implementation is
		 * removing the current element during the iteration.
		 * 'LinkedList' implementations are not ideal structures to iterate.
		 * 
		 * For the 'Deque' interface, 'ArrayDeque' is the most commonly used implementation.
		 */
		final Deque<String> arrayDeque = streamSupplier.get().collect(Collectors.toCollection(ArrayDeque::new));
		// an iterator in proper sequence
		final StringBuilder strBld2 = new StringBuilder();
		arrayDeque.iterator().forEachRemaining(strBld2::append);
		Printer.printf("Deque traversed [%s] (in proper  order)", strBld2);

		// an iterator in reverse sequential order
		final StringBuilder strBld3 = new StringBuilder();
		arrayDeque.descendingIterator().forEachRemaining(strBld3::append);
		Printer.printf("Deque traversed [%s] (in reverse order)", strBld3);
		/*-
		 * When using 'ArrayDeque::new' in place of 'ConcurrentLinkedDeque::new'
		 * the deque is not thread safe.
		 */
		final Supplier<Deque<String>> supplier = ConcurrentLinkedDeque::new;
		final Deque<String> concurrentLinkedDeque = streamSupplier.get().collect(supplier, Deque::add, Deque::addAll);
		Printer.printf("ConcurrentLinkedDeque %s", concurrentLinkedDeque);
		Printer.printHor();
	}

}
