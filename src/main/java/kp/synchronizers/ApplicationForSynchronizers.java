package kp.synchronizers;

import kp.synchronizers.locks.ConditionBoundLock;
import kp.synchronizers.locks.SharedResourceWithReentrantLock;
import kp.synchronizers.pausing.PausedByVolatile;
import kp.utils.Printer;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;

/**
 * The main class for synchronizers research.
 * <p>
 * Researched classes:
 * <ul>
 * <li>{@link CyclicBarrier}
 * <li>{@link Phaser}
 * </ul>
 */
public class ApplicationForSynchronizers {

    /**
     * The primary entry point for launching the application.
     *
     */
    public static void main() {

        Printer.printHor();
        final CyclicBarrierLauncher cyclicBarrierLauncher = new CyclicBarrierLauncher();
        cyclicBarrierLauncher.launchCyclicBarrierThreeTimes();

        final PhaserLauncher phaserLauncher = new PhaserLauncher();
        phaserLauncher.usePhaserToOpenGateForTasks();
        phaserLauncher.usePhaserToAwaitOtherTasks();

        PausedByVolatile.process();
        ConditionBoundLock.process();
        SharedResourceWithReentrantLock.process();
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private ApplicationForSynchronizers() {
        throw new IllegalStateException("Utility class");
    }
}
