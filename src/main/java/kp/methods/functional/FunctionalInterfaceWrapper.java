package kp.methods.functional;

import kp.utils.Printer;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * The wrapper for the functional interface.
 */
public interface FunctionalInterfaceWrapper {

    /**
     * Launches the functional interface.
     */
    static void launchFunctionalInterface() {
        final FunctionalInterfaceExample functionalInterfaceExample = arg -> Printer.printf("first(): arg[%s]", arg);
        functionalInterfaceExample.first("one");
        functionalInterfaceExample.second("two");
        FunctionalInterfaceExample.third("three");
        FunctionalInterfaceExample.fourth();
        FunctionalInterfaceExample.fifth("five");
        Printer.print("");

        final Stream<Consumer<String>> consumerStream = Stream.of(functionalInterfaceExample::first,
                functionalInterfaceExample::second, FunctionalInterfaceExample::third);
        consumerStream.forEach(consumer -> consumer.accept("consumer accepted value"));
        final Supplier<String> supplier = FunctionalInterfaceExample::fourth;
        Printer.printf("From 'fourth()' supplier: result[%s]", supplier.get());
        final Function<String, Void> function = FunctionalInterfaceExample::fifth;
        Printer.printf("From 'fifth()' function: 'Void' result[%s]", function.apply("function applied value"));
        Printer.printHor();
    }
}
