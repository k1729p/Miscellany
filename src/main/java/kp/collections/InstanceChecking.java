package kp.collections;

import kp.utils.Printer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The class instance checking.
 */
public class InstanceChecking {

    /**
     * Private constructor to prevent instantiation.
     */
    private InstanceChecking() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Checks the class instance and finds the first element in a stream.
     */
    static void checkInstanceAndFindElement() {

        final List<Number> list = createListWithNumbers();
        final byte number1 = list.stream().filter(Byte.class::isInstance).map(Byte.class::cast).findFirst()
                .orElseThrow();
        final short number2 = list.stream().filter(Short.class::isInstance).map(Short.class::cast).findFirst()
                .orElseThrow();
        final int number3 = list.stream().filter(Integer.class::isInstance).map(Integer.class::cast).findFirst()
                .orElseThrow();
        final long number4 = list.stream().filter(Long.class::isInstance).map(Long.class::cast).findFirst()
                .orElseThrow();
        Printer.printf("byte[%s], short[%s], int[%s], long[%s]", number1, number2, number3, number4);

        final float number5 = list.stream().filter(Float.class::isInstance).map(Float.class::cast).findFirst()
                .orElseThrow();
        final double number6 = list.stream().filter(Double.class::isInstance).map(Double.class::cast).findFirst()
                .orElseThrow();
        Printer.printf("float[%s], double[%s]", number5, number6);

        final BigInteger number7 = list.stream().filter(BigInteger.class::isInstance).map(BigInteger.class::cast)
                .findFirst().orElseThrow();
        final BigDecimal number8 = list.stream().filter(BigDecimal.class::isInstance).map(BigDecimal.class::cast)
                .findFirst().orElseThrow();
        Printer.printf("BigInteger[%s], BigDecimal[%s]", number7, number8);

        final AtomicInteger number9 = list.stream().filter(AtomicInteger.class::isInstance)
                .map(AtomicInteger.class::cast).findFirst().orElseThrow();
        final AtomicLong number10 = list.stream().filter(AtomicLong.class::isInstance).map(AtomicLong.class::cast)
                .findFirst().orElseThrow();
        Printer.printf("AtomicInteger[%s], AtomicLong[%s]", number9, number10);
        Printer.printHor();
    }

    /**
     * Creates the list and fills it with the data.
     *
     * @return the list
     */
    private static List<Number> createListWithNumbers() {

        final List<Number> list = new ArrayList<>();
        list.add(Byte.valueOf("-128"));
        list.add(Short.valueOf("-1234"));
        list.add(Integer.valueOf("-2345"));
        list.add(Long.valueOf("-3456"));
        list.add(Float.valueOf("-45.67"));
        list.add(Double.valueOf("-56.78"));
        list.add(BigInteger.valueOf(-6789));
        list.add(BigDecimal.valueOf(-78.91));
        list.add(new AtomicInteger(-8912));
        list.add(new AtomicLong(-9123));
        return list;
    }

}
