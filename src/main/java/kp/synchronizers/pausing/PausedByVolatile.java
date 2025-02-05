package kp.synchronizers.pausing;

import kp.utils.Printer;
import kp.utils.Utils;

/**
 * Demonstrates the use of a spin-wait loop for pausing a thread execution.
 */
public class PausedByVolatile {
    private volatile boolean endPause;

    /**
     * Initiates the pause process by starting a new thread and setting the end pause flag.
     */
    public static void process() {

        final PausedByVolatile pausedByVolatile = new PausedByVolatile();
        Thread.ofPlatform().start(pausedByVolatile::pause);
        Utils.sleepMillis(100);
        Printer.print("process(): setting end pause flag to true");
        pausedByVolatile.endPause = true;
        Utils.sleepMillis(100);
        Printer.printHor();
    }

    /**
     * Pauses the execution using a spin-wait loop until the endPause flag is set to true.
     */
    private void pause() {

        Printer.printf("pause():   endPause[%b], before invoking spin-wait loop", endPause);
        while (!endPause) {
            Thread.onSpinWait();
        }
        Printer.printf("pause():   endPause[%b], pause ended", endPause);
    }
}
