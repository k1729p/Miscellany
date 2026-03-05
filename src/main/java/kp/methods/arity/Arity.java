package kp.methods.arity;

import kp.utils.Printer;

import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * The multi arity.
 */
public interface Arity {

    /**
     * Research sending.
     *
     * @param ternaryFunction    the ternary function
     * @param quaternaryFunction the quaternary function
     * @param quinaryFunction    the quinary function
     * @param septenaryFunction  the septenary function
     */
    static void methodWithFiveFunctionParameters(BiFunction<String, String, Consumer<String>> ternaryFunction,
                                                 BiFunction<String, String, BiConsumer<String, String>> quaternaryFunction,
                                                 BiFunction<String, String, BiFunction<String, String, Consumer<String>>> quinaryFunction,
                                                 BiFunction<String, String, BiFunction<String, String, BiFunction<String, String, Consumer<String>>>> septenaryFunction) {

        ternaryFunction.apply("a", "b").accept("c");
        quaternaryFunction.apply("a", "b").accept("c", "d");
        quinaryFunction.apply("a", "b").apply("c", "d").accept("e");
        septenaryFunction.apply("a", "b").apply("c", "d").apply("e", "f").accept("g");
        Printer.printHor();
    }

    /**
     * Research receiving.
     *
     * @param quaternaryConsumer the quaternary consumer
     */
    static void methodWithConsumerParameter(
            BiConsumer<BiConsumer<Integer, Double>, BiConsumer<String, Date>> quaternaryConsumer) {

        final StringBuilder strBuf = new StringBuilder();
        strBuf.append("Returned from 'quaternaryConsumer':\t");
        final BiConsumer<Integer, Double> numConsumer = (num1, num2) -> strBuf.append(
                String.format("1st[%d], 2nd[%6.3f]", num1, num2));
        final BiConsumer<String, Date> strAndDateConsumer = (str, date) -> strBuf.append(
                String.format(", 3rd[%s], 4th[%tF]", str, date));
        quaternaryConsumer.accept(numConsumer, strAndDateConsumer);
        Printer.print(strBuf.toString());
        Printer.printHor();
    }

    /**
     * Research sending with receiving.
     *
     * @param biFunction the function
     */
    static void methodWithFunctionParameter(
            BiFunction<BiConsumer<Integer, Double>, Consumer<String>, BiFunction<Integer, Double, Consumer<String>>> biFunction) {

        final StringBuilder strBuf = new StringBuilder();
        final BiConsumer<Integer, Double> numConsumer = (num1, num2) -> strBuf.append(
                String.format("B.1[%d], B.2[%2.1f]", num1, num2));
        final Consumer<String> strConsumer = str -> strBuf.append(String.format(", B.3[%s]", str));
        final int num1 = 111;
        final double num2 = 2.2;
        final String str = "ccc";
        Printer.printf("Sending to 'biFunction':\t\t   A.1[%d], A.2[%2.1f], A.3[%s]", num1, num2, str);
        biFunction.apply(numConsumer, strConsumer).apply(num1, num2).accept(str);
        Printer.printf("Returned from 'biFunction':\t\t   %s", strBuf.toString());
        Printer.printHor();
    }
}