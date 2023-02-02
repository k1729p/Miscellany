package kp.processes.programs;

import java.util.logging.Logger;

/**
 * The single-file source-code program.
 * 
 */
public class SingleFileSourceCodeProgram {
	private static final Logger logger = Logger.getLogger(SingleFileSourceCodeProgram.class.getName());

	/**
	 * The hidden constructor.
	 */
	private SingleFileSourceCodeProgram() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * The entry point for the application.
	 * 
	 * @param args the command-line arguments
	 */
	public static void main(String[] args) {
		logger.info("main():");
	}
}