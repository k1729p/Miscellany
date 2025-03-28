package kp.methods.calling;

import kp.utils.Printer;

/**
 * The processor which uses the lambda expressions box.
 */
public class Processor {
    private final LambdaExpressionsBox lambdaExpressionsBox;

    /**
     * The constructor.
     */
    Processor() {
        this.lambdaExpressionsBox = new LambdaExpressionsBox(
                this::methodForBiConsumer,
                this::methodForSupplier,
                this::methodForBiPredicate,
                this::methodForIntBinaryOperator);
    }

    /**
     * Gets the box with lambda expressions.
     *
     * @return the lambda expressions box
     */
    LambdaExpressionsBox getLambdaExpressionsBox() {
        return lambdaExpressionsBox;
    }

    /**
     * The method for two-arity consumer.
     *
     * @param number1 the number 1
     * @param number2 the number 2
     */
    void methodForBiConsumer(int number1, int number2) {

        Printer.printf("methodForBiConsumer():   number1[%d], number2[%d]", number1, number2);
    }

    /**
     * The method for supplier.
     *
     * @return the result
     */
    int methodForSupplier() {

        final int result = 3;
        Printer.printf("methodForSupplier():   result[%d]", result);
        return result;
    }

    /**
     * The method for two-arity predicate.
     *
     * @param number1 the number 1
     * @param number2 the number 2
     * @return the result
     */
    boolean methodForBiPredicate(int number1, int number2) {

        final boolean result = number1 < number2;
        Printer.printf("methodForBiPredicate():   number1[%d], number2[%d], result[%b]", number1, number2, result);
        return result;
    }

    /**
     * The method for binary operator.
     *
     * @param number1 the number 1
     * @param number2 the number 2
     * @return the result
     */
    int methodForIntBinaryOperator(int number1, int number2) {

        final int result = 3;
        Printer.printf("methodForIntBinaryOperator():   number1[%d], number2[%d], result[%d]", number1, number2,
                result);
        return result;
    }
}