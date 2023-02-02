package kp.synchronizers;

import static kp.Constants.LINE_SEP;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;

import kp.utils.Printer;
import kp.utils.Utils;

/**
 * The {@link Phaser} launcher.
 * <p>
 * The {@link Phaser} is a reusable synchronization barrier.
 */
public class PhaserLaucher {

	/**
	 * The {@link SecureRandom}.
	 */
	private Random random;

	/**
	 * The constructor.
	 * 
	 */
	public PhaserLaucher() {
		super();
		try {
			random = SecureRandom.getInstanceStrong();
		} catch (NoSuchAlgorithmException e) {
			Printer.printException(e);
			System.exit(1);
		}
	}

	/**
	 * Uses the {@link Phaser} to open the gate for tasks.
	 * <p>
	 * First it calls the method {@link Phaser#register} and then it calls the
	 * method {@link Phaser#arriveAndDeregister}.
	 * 
	 */
	void usePhaserToOpenGateForTasks() {

		@SuppressWarnings("java:S1149") // switch off Sonarqube rule 'don't use synchronized class StringBuffer'
		final StringBuffer strBuf = new StringBuffer();
		final List<Runnable> tasks = List.of(//
				() -> strBuf.append("«Task A»\t"), //
				() -> strBuf.append("«Task B»\t"), //
				() -> strBuf.append("«Task C»\t"));

		final Phaser startingGate = new Phaser(1); // '1' to register self
		strBuf.append("▐1▌ before tasks starting").append(LINE_SEP);
		for (Runnable task : tasks) {
			startingGate.register();
			CompletableFuture.runAsync(startingGate::arriveAndAwaitAdvance).thenRunAsync(task);
		}
		strBuf.append("▐2▌ after  tasks starting").append(LINE_SEP);
		Utils.sleepMillis(10);
		// deregister self to allow threads to proceed
		startingGate.arriveAndDeregister();
		strBuf.append("▐3▌ after deregistration").append(LINE_SEP);
		Utils.sleepMillis(10);
		strBuf.append(LINE_SEP).append("▐4▌ after sleep");
		Printer.printObject(strBuf);
		Printer.printHor();
	}

	/**
	 * Uses the method {@link Phaser#arriveAndAwaitAdvance} to await all other
	 * tasks.
	 * 
	 */
	void usePhaserToAwaitOtherTasks() {

		final AtomicInteger atomic = new AtomicInteger();
		final int parties = 4;
		try (final ExecutorService pool = Executors.newFixedThreadPool(10)) {
			while (atomic.incrementAndGet() <= 3) {
				final Phaser phaser = new Phaser(parties);
				final Runnable taskA = () -> taskAction("A", phaser, atomic);
				final Runnable taskB = () -> taskAction("B", phaser, atomic);
				final Runnable taskC = () -> taskAction("C", phaser, atomic);
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
	 * Executes task action with the {@link Phaser}.
	 * 
	 * @param label  the label
	 * @param phaser the {@link Phaser}
	 * @param atomic the {@link AtomicInteger}
	 */
	private void taskAction(String label, Phaser phaser, AtomicInteger atomic) {

		Printer.printf("task[%s], atomic[%d], START", label, atomic.get());
		Utils.sleepMillis(random.nextInt(6));
		Printer.printf("task[%s], atomic[%d], FINISH", label, atomic.get());
		phaser.arriveAndDeregister();
	}

}
