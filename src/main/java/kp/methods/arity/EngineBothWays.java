package kp.methods.arity;

import kp.utils.Printer;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * The engine with sending and receiving.
 */
public final class EngineBothWays {
    /**
     * The field with function.
     */
    public static final BiFunction<BiConsumer<Integer, Double>, Consumer<String>, BiFunction<Integer, Double, Consumer<String>>> FUNCTION =
            (cons1, cons2) -> {
                BiFunction<Integer, Double, Consumer<String>> function = (num1, num2) -> str -> Printer.printf(
                        "EngineBothWays.FUNCTION(): received params A.1[%d], A.2[%2.1f], A.3[%s]", num1, num2, str);
                final int num1 = 444;
                final double num2 = 5.5;
                final String str = "fff";
                cons1.accept(num1, num2);
                cons2.accept(str);
                Printer.printf("EngineBothWays.FUNCTION(): sending  params B.1[%d], B.2[%2.1f], B.3[%s]", num1, num2,
                        str);
                return function;
            };

    /**
     * Private constructor to prevent instantiation.
     */
    private EngineBothWays() {
        throw new IllegalStateException("Utility class");
    }
}
