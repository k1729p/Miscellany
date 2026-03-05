package kp.about.basics;

import kp.about.basics.inheritance.PatternMatching;
import kp.utils.Printer;

/**
 * The main class for basics research.
 */
public class ApplicationForBasics {

    /**
     * The execution control value for Declarations.
     */
    private static final boolean DECLARATIONS = false;
    /**
     * The execution control value for Declarations.
     */
    private static final boolean STACK_VS_HEAP = false;

    /**
     * Private constructor to prevent instantiation.
     */
    private ApplicationForBasics() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The primary entry point for launching the application.
     *
     */
    public static void main() {

        Printer.printHor();
        Var.launch();
        final PatternMatching patternMatching = new PatternMatching();
        patternMatching.withoutAnyCast();
        new StringInterning().showIdentityHashCodesForStrings();
        if (DECLARATIONS) {
            Declarations.declareLocalVariables();
        }
        if (STACK_VS_HEAP) {
            new StackMemoryVersusHeapSpace().referenceVariablesAndStackMemoryAndHeapSpace();
        }
    }
}
