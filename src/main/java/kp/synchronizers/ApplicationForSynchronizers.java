package kp.synchronizers;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;

import kp.utils.Printer;

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
	 * The constructor.
	 */
	public ApplicationForSynchronizers() {
		super();
	}

	/**
	 * The entry point for the application.
	 * 
	 * @param args the command-line arguments
	 */
	public static void main(String[] args) {

		Printer.printHor();
		CyclicBarrierLauncher cyclicBarrierLauncher = new CyclicBarrierLauncher();
		cyclicBarrierLauncher.launchCyclicBarrierThreeTimes();

		final PhaserLauncher phaserLauncher = new PhaserLauncher();
		phaserLauncher.usePhaserToOpenGateForTasks();
		phaserLauncher.usePhaserToAwaitOtherTasks();
	}
}
