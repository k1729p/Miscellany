package kp.synchronizers.locks;

import kp.utils.Printer;
import kp.utils.Utils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

/**
 * Utilizes {@link ReentrantLock} and {@link Condition} to manage a {@link Deque} with concurrent access.
 * <p>
 * {@link Lock} and {@link Condition}:
 * </p>
 * <ul>
 * <li>{@link Lock} replaces the use of 'synchronized' methods and statements</li>
 * <li>{@link Condition} replaces the use of the Object monitor methods</li>
 * </ul>
 */
public class ConditionBoundLock {

    private static final int CAPACITY_LIMIT = 4;
    private static final int LIST_SIZE = 4;
    private static final List<Integer> DATA_1 = IntStream.rangeClosed(101, 100 + LIST_SIZE).boxed().toList();
    private static final List<Integer> DATA_2 = IntStream.rangeClosed(20001, 20000 + LIST_SIZE).boxed().toList();
    private static final AtomicBoolean PAUSE_ATOMIC = new AtomicBoolean();

    private final Lock lock = new ReentrantLock();
    private final Condition canPopCondition = lock.newCondition();
    private final Condition canPushCondition = lock.newCondition();
    private final Deque<Integer> deque = new ArrayDeque<>();

    /**
     * Starts the threads to process pushing and popping from the deque.
     */
    public static void process() {

        final ConditionBoundLock conditionBoundLock = new ConditionBoundLock();
        IntStream.rangeClosed(0, 1).forEach(_ -> {
            Thread.ofPlatform().name("Pop-Thread").start(() -> IntStream.range(0, 2 * LIST_SIZE)
                    .forEach(_ -> conditionBoundLock.popFromDeque()));
            Utils.sleepMillis(10);
            Thread.ofPlatform().name("Push-Thr-1").start(() -> DATA_1.forEach(conditionBoundLock::pushToDeque));
            Thread.ofPlatform().name("Push-Thr-2").start(() -> DATA_2.forEach(conditionBoundLock::pushToDeque));
            Utils.sleepMillis(500);
            Printer.printHor();
            PAUSE_ATOMIC.set(true);
        });
    }

    /**
     * Pushes an element to the deque.
     *
     * @param element the element to push
     */
    private void pushToDeque(int element) {

        final String name = Thread.currentThread().getName();
        try {
            lock.lock();
            while (deque.size() == CAPACITY_LIMIT) {
                Printer.printf("name[%s] |█|█|█|█|█| await 'CAN PUSH' condition before element[%d] push", name, element);
                canPushCondition.await();
            }
            deque.push(element);
            canPopCondition.signalAll();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserve interrupt status
            Printer.printInterruptedException(e);
            System.exit(1);
        } finally {
            lock.unlock();
        }
        Printer.printf("name[%s] ►►►►► push element[%s]", name, element);
    }

    /**
     * Pops an element from the deque.
     */
    private void popFromDeque() {

        final String name = Thread.currentThread().getName();
        try {
            lock.lock();
            while (deque.isEmpty()) {
                Printer.printf("name[%s] |_|_|_|_|_| await 'CAN POP'  condition before element pop", name);
                canPopCondition.await();
            }
            final int element = deque.pop();
            canPushCondition.signalAll();
            Printer.printf("name[%s] ◄◄◄◄◄  pop element[%d]", name, element);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserve interrupt status
            Printer.printInterruptedException(e);
            System.exit(1);
        } finally {
            lock.unlock();
        }
        if (PAUSE_ATOMIC.get()) {
            Utils.sleepMillis(10);
        }
    }
}
