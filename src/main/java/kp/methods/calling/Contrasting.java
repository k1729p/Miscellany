package kp.methods.calling;

import kp.utils.Printer;

import java.util.function.Consumer;

/**
 * Contrasting the methods calling.
 */
public class Contrasting {

    /**
     * The example interface used by anonymous class.
     */
    interface TheInterface {
        default void defaultInterfaceMethod() {
            Printer.print("defaultInterfaceMethod():");
        }
    }

    /**
     * Using <b>local class</b> versus using <b>anonymous class</b> versus using
     * <b>lambda expression</b>.<br>
     * <br>
     * Alternatives:
     * <ol>
     * <li>local class
     * <li>anonymous class
     * <li>lambda expression
     * <li>static nested class
     * <li>inner class
     * </ol>
     */
    public static void usingLocalClassVersusAnonymousClassVersusLambdaExpression() {

        /*
         * The example local class is used here below.
         */
        class TheLocalClass {
            void localClassMethod() {
                Printer.print("localClassMethod():");
            }
        }
        final TheLocalClass theLocalClass = new TheLocalClass();
        theLocalClass.localClassMethod();

        final TheInterface anonymousClass1 = new TheInterface() {
        };
        anonymousClass1.defaultInterfaceMethod();
        final TheInterface anonymousClass2 = new TheInterface() {
            @Override
            public void defaultInterfaceMethod() {
                Printer.print("defaultInterfaceMethod(): overridden");
            }
        };
        anonymousClass2.defaultInterfaceMethod();

        final Consumer<Void> consumer = _ -> Printer.print("consumer operation");
        consumer.accept(null);
        Printer.printHor();
    }

    /**
     * Accessing methods with the lambda expressions or calling them directly on the
     * processor.
     */
    public static void lambdaAccessingVersusCallingDirectly() {

        final int number1 = 1;
        final int number2 = 2;
        final Processor processor = new Processor();
        final LambdaExpressionsBox lambdaExpressionsBox = processor.getLambdaExpressionsBox();

        lambdaExpressionsBox.biConsumer.accept(number1, number2);
        processor.methodForBiConsumer(number1, number2);
        Printer.printf("Contrasted 'biConsumer': number1[%d], number2[%d]%n", number1, number2);

        final int resultInt11 = lambdaExpressionsBox.supplier.get();
        final int resultInt12 = processor.methodForSupplier();
        Printer.printf("Contrasted 'supplier': result[%d]/[%d]%n", resultInt11, resultInt12);

        final boolean resultBoolean1 = lambdaExpressionsBox.biPredicate.test(number1, number2);
        final boolean resultBoolean2 = processor.methodForBiPredicate(number1, number2);
        Printer.printf("Contrasted 'biPredicate': number1[%d], number2[%d], result[%b]/[%b]%n", number1, number2,
                resultBoolean1, resultBoolean2);

        final int resultInt21 = lambdaExpressionsBox.intBinaryOperator.applyAsInt(number1, number2);
        final int resultInt22 = processor.methodForIntBinaryOperator(number1, number2);
        Printer.printf("Contrasted 'intBinaryOperator': number1[%d], number2[%d], result[%d]/[%d]", number1, number2,
                resultInt21, resultInt22);
        Printer.printHor();
    }
}
