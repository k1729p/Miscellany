package kp.about.basics;

import kp.about.basics.inheritance.PatternMatching;
import kp.utils.Printer;

/**
 * The main class for basics research.
 */
public class ApplicationForBasics {

	/**
	 * The execution control value.
	 */
	private static final boolean DECLARATIONS = false;

	/**
	 * The hidden constructor.
	 */
	private ApplicationForBasics() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * The entry point for the application.
	 * 
	 * @param args the command-line arguments
	 */
	public static void main(String[] args) {

		Printer.printHor();
		if (DECLARATIONS) {
			Declarations.declareLocalVariables();
		}
		Var.launch();
		final PatternMatching patternMatching = new PatternMatching();
		patternMatching.withoutAnyCast();
	}
}
