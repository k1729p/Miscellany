package kp.synchronizers.locks;

import kp.utils.Printer;
import kp.utils.Utils;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Demonstrates controlled access to a shared resource by multiple threads using a {@link ReentrantLock}.
 */
public class SharedResourceWithReentrantLock {
    private final ReentrantLock reentrantLock = new ReentrantLock();

    /**
     * Processes the controlled access with a reentrant lock.
     */
    public static void process() {

        final SharedResourceWithReentrantLock sharedResource = new SharedResourceWithReentrantLock();
        Thread.ofPlatform().start(() -> sharedResource.accessResourceWithReentrantLock("▄"));
        Thread.ofPlatform().start(() -> sharedResource.accessResourceWithReentrantLock("▄"));
        Utils.sleepMillis(1_000);
        Printer.printHor();
    }

    /**
     * Accesses the shared resource with a reentrant lock and demonstrates recursive locking.
     * The method will recurse until the hold count reaches a specified limit, at which point it will sleep.
     *
     * @param level the current level of recursion, represented as a string
     */
    private void accessResourceWithReentrantLock(String level) {

        // A condition that stops the recursion
        if (reentrantLock.getHoldCount() == 3) {
            Printer.printf("level[%-7s], thread name[%s], hold count[%d], ▼▼▼ before sleep ▼▼▼",
                    level, Thread.currentThread().getName(), reentrantLock.getHoldCount());
            Utils.sleepMillis(250);
            Printer.printf("level[%-7s], thread name[%s], hold count[%d], ▲▲▲ after  sleep ▲▲▲",
                    level, Thread.currentThread().getName(), reentrantLock.getHoldCount());
            return;
        }
        boolean acquiredFlag;
        while (true) {
            acquiredFlag = reentrantLock.tryLock();
            Printer.printf("level[%-7s], thread name[%s], hold count[%d], lock was acquired[%b]",
                    level, Thread.currentThread().getName(), reentrantLock.getHoldCount(), acquiredFlag);
            if (acquiredFlag) {
                break;
            }
            Utils.sleepMillis(100);
        }
        try {
            // Access the resource protected by this lock
            accessResourceWithReentrantLock(level.concat(" ▄"));
        } finally {
            reentrantLock.unlock();
        }
    }
}
