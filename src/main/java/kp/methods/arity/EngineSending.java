package kp.methods.arity;

import kp.Constants;
import kp.utils.Printer;

import java.util.Date;
import java.util.function.BiConsumer;

/**
 * The engine with sending.
 */
public final class EngineSending {
    /**
     * The example date.
     */
    private static final Date EXAMPLE_DATE = Date.from(Constants.EXAMPLE_ZONED_DATE_TIME.toInstant());

    /**
     * The field with quaternary consumer. It returns four values.
     */
    public static final BiConsumer<BiConsumer<Integer, Double>, BiConsumer<String, Date>> QUATERNARY_CONSUMER =
            (consumer1, consumer2) -> {
                final int num1 = 123;
                final double num2 = 456.789;
                final String str = "abc";
                consumer1.accept(num1, num2);
                consumer2.accept(str, EXAMPLE_DATE);
                Printer.printf("EngineSending.QUATERNARY_CONSUMER():"
                               + "\t1st[%d], 2nd[%6.3f], 3rd[%s], 4th[%tF]", num1, num2, str, EXAMPLE_DATE);
            };

    /**
     * Private constructor to prevent instantiation.
     */
    private EngineSending() {
        throw new IllegalStateException("Utility class");
    }
}
