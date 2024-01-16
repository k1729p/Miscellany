package kp.synchronizers;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import kp.utils.Printer;

/**
 * The {@link CyclicBarrier} launcher.
 *
 */
public class CyclicBarrierLauncher {

	private static final int PARTIES = 3;
	private final AtomicInteger atomic = new AtomicInteger(1);
	private final Runnable barrierAction = () -> Printer.printf(//
			"Finish action, atomic[%d]", atomic.getAndIncrement());
	private final CyclicBarrier cyclicBarrier = new CyclicBarrier(PARTIES, barrierAction);
	private final List<Worker> workerList = IntStream.rangeClosed(1, PARTIES).boxed()//
			.map(Worker::new).toList();

	/**
	 * The worker
	 */
	class Worker implements Runnable {
		private final int number;

		/**
		 * The constructor
		 * 
		 * @param number the number
		 */
		public Worker(int number) {
			super();
			this.number = number;
		}

		/**
		 * {@inheritDoc}
		 * 
		 */
		@Override
		public void run() {
			Printer.printf("Worker action, atomic[%d], number[%d]", atomic.get(), number);
			try {
				cyclicBarrier.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				Printer.printInterruptedException(e);
				System.exit(1);
			} catch (BrokenBarrierException e) {
				Printer.printException(e);
				System.exit(1);
			}
		}
	}

	/**
	 * The constructor.
	 */
	public CyclicBarrierLauncher() {
		super();
	}

	/**
	 * Launches three times the {@link CyclicBarrier}.
	 * 
	 */
	public void launchCyclicBarrierThreeTimes() {

		launchCyclicBarrier();
		Printer.printSeparatorLine();

		cyclicBarrier.reset();
		launchCyclicBarrier();
		Printer.printSeparatorLine();

		cyclicBarrier.reset();
		launchCyclicBarrier();
		Printer.printHor();
	}

	/**
	 * Launches the {@link CyclicBarrier}.
	 * 
	 */
	public void launchCyclicBarrier() {

		final List<Thread> threadList = workerList.stream().map(Thread::new).toList();
		threadList.forEach(Thread::start);

		threadList.forEach(thread -> {
			try {
				thread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				Printer.printInterruptedException(e);
				System.exit(1);
			}
		});
	}

}
