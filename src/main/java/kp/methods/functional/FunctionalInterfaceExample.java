package kp.methods.functional;

import kp.utils.Printer;

/**
 * Any interface with a Single Abstract Method is a <B>functional interface</B>.
 * <br>
 * This interface is annotated with an informative annotation {@link FunctionalInterface}.
 */
@FunctionalInterface
public interface FunctionalInterfaceExample {

    /**
     * The abstract method.
     *
     * @param arg the arguments
     */
    void first(String arg);

    /**
     * The default method.
     *
     * @param arg the arguments
     */
    default void second(String arg) {
        Printer.printf("second(): arg[%s]%n\t'this' class[%s]", arg, this.getClass().getSimpleName());
        secondPrivate(arg);
    }

    /**
     * The static unary method (takes one argument).
     *
     * @param arg the arguments
     */
    static void third(String arg) {
        Printer.printf("third(): arg[%s]", arg);
        thirdPrivate(arg);
    }

    /**
     * The static nullary method (takes no arguments).
     *
     * @return the result
     */
    static String fourth() {
        String result = "four";
        Printer.printf("fourth(): returning result[%s]", result);
        return result;
    }

    /**
     * The class method with the return type {@link Void}.
     *
     * @param arg the arguments
     * @return the result with type {@link Void}
     */
    static Void fifth(String arg) {
        Printer.printf("fifth(): arg[%s]", arg);
        return null;
    }

    /**
     * The private method.
     *
     * @param arg the arguments
     */
    private void secondPrivate(String arg) {
        Printer.printf("secondPrivate(): arg[%s]", arg);
    }

    /**
     * The private method.
     *
     * @param arg the arguments
     */
    private static void thirdPrivate(String arg) {
        Printer.printf("thirdPrivate(): arg[%s]", arg);
    }
}