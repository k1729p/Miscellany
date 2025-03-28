package kp.synchronizers;

import kp.utils.Printer;
import kp.utils.Utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link Phaser} launcher.
 * <p>
 * The {@link Phaser} is a reusable synchronization barrier.
 */
public class PhaserLauncher {

    /**
     * The {@link SecureRandom}.
     */
    private static final SecureRandom random;

    static {
        SecureRandom tempRandom = null;
        try {
            tempRandom = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            Printer.printException(e);
            System.exit(1);
        }
        random = tempRandom;
    }

    /**
     * Uses the {@link Phaser} to open the gate for tasks.
     * <p>
     * First, it calls the method {@link Phaser#register} and then it calls the
     * method {@link Phaser#arriveAndDeregister}.
     */
    void usePhaserToOpenGateForTasks() {

        @SuppressWarnings("java:S1149") // switch off Sonarqube rule 'don't use synchronized class StringBuffer'
        final StringBuffer strBuf = new StringBuffer();
        final List<Runnable> tasks = List.of(
                () -> strBuf.append("«Task A»\t"),
                () -> strBuf.append("«Task B»\t"),
                () -> strBuf.append("«Task C»\t"));

        final Phaser startingGate = new Phaser(1); // '1' to register self
        strBuf.append("▐1▌ before tasks starting").append(System.lineSeparator());
        for (Runnable task : tasks) {
            startingGate.register();
            CompletableFuture.runAsync(startingGate::arriveAndAwaitAdvance).thenRunAsync(task);
        }
        strBuf.append("▐2▌ after  tasks starting").append(System.lineSeparator());
        Utils.sleepMillis(10);
        // deregister self to allow threads to proceed
        startingGate.arriveAndDeregister();
        strBuf.append("▐3▌ after de-registration").append(System.lineSeparator());
        Utils.sleepMillis(10);
        strBuf.append(System.lineSeparator()).append("▐4▌ after sleep");
        Printer.printObject(strBuf);
        Printer.printHor();
    }

    /**
     * Uses the method {@link Phaser#arriveAndAwaitAdvance} to await all other
     * tasks.
     */
    void usePhaserToAwaitOtherTasks() {

        final AtomicInteger atomic = new AtomicInteger();
        final int parties = 4;
        try (final ExecutorService pool = Executors.newFixedThreadPool(10)) {
            while (atomic.incrementAndGet() <= 3) {
                final Phaser phaser = new Phaser(parties);
                final Runnable taskA = new Task("A", phaser, atomic);
                final Runnable taskB = new Task("B", phaser, atomic);
                final Runnable taskC = new Task("C", phaser, atomic);
                Printer.printf("▼▼▼      atomic[%d], before tasks executing ▼▼▼", atomic.get());
                pool.execute(taskA);
                pool.execute(taskB);
                pool.execute(taskC);
                Printer.printf("► ► ► ►  atomic[%d], arriving at the phaser ◄ ◄ ◄ ◄", atomic.get());
                phaser.arriveAndAwaitAdvance();
                Printer.printf("▲▲▲      atomic[%d], after awaiting others  ▲▲▲%n", atomic.get());
            }
        }
        Printer.printHor();
    }

    /**
     * Task record to encapsulate task actions.
     *
     * @param label  the label
     * @param phaser the {@link Phaser}
     * @param atomic the {@link AtomicInteger}
     */
    record Task(String label, Phaser phaser, AtomicInteger atomic) implements Runnable {
        @Override
        public void run() {
            Printer.printf("task[%s], atomic[%d], START", label, atomic.get());
            Utils.sleepMillis(random.nextInt(6));
            Printer.printf("task[%s], atomic[%d], FINISH", label, atomic.get());
            phaser.arriveAndDeregister();
        }
    }

}
