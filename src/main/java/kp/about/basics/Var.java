package kp.about.basics;

import kp.utils.Printer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The local variable type inference. Using a var instead of a type.
 */
public interface Var {
    /**
     * Launches.
     */
    static void launch() {

        final List<?> notVar = List.of(1, 2, 3);
        Printer.printf("'notVar' list \t\tclass[%s]%n", notVar.getClass().getName());
        final byte one = 1;
        final var mixedList = List.of(/*-*/
                one, /*- Byte */
                2, /*- Integer */
                3.0, /*- Double */
                BigInteger.valueOf(4), /*- BigInteger */
                BigDecimal.valueOf(5.0), /*- BigDecimal */
                '6', /*- Character */
                "7"/*- String */
        );
        Printer.printf("Mixed  list size[%d], \tclass[%s], elements:", mixedList.size(),
                mixedList.getClass().getName());
        for (var elem : mixedList) {
            Printer.printf("\telement[%3s], class[%20s]", elem, elem.getClass().getName());
        }
        final StringBuilder strBld = new StringBuilder();
        strBld.append("Mixed list values from loop  :");
        for (java.io.Serializable serializable : mixedList) {
            strBld.append(" ").append(serializable);
        }
        strBld.append("\nMixed list values from stream:");
        final var mixedStream = mixedList.stream();
        mixedStream.forEach(elem -> strBld.append(" ").append(elem));
        strBld.append(System.lineSeparator());
        Printer.printObject(strBld);

        final var box = new Box<>((byte) 1, BigDecimal.TEN) {
        };
        final var boxT = box.getT();
        Printer.printf("box.t: value[%2s], class[%20s]", boxT, boxT.getClass().getName());
        final var boxU = box.getU();
        Printer.printf("box.u: value[%2s], class[%20s]", boxU, boxU.getClass().getName());
        Printer.printHor();
    }

    /**
     * This method is intentionally not used.
     *
     * @throws IOException the {@link IOException}
     */
    @SuppressWarnings("unused")
    static void unused() throws IOException {

        final var arrayList = new ArrayList<String>();
        final var linkedList = new LinkedList<>();

        final var url = URI.create("https://example.org/").toURL();
        final var connection = url.openConnection();
        final var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        final var path = Paths.get("src/main/java/kp/Constants.java");
        final var bytes = Files.readAllBytes(path);
        final var array = new String[][][]{{{}}};
        for (var arr1 : array) {
            for (var arr2 : arr1) {
                for (var arr3 : arr2) {
                    Printer.print("unused -> in loop");
                }
            }
        }
        var list = new ArrayList<String>();

        /*-
         * Putting line 'list = new LinkedList<>();' in code causes
         * the error "Cannot infer type arguments for LinkedList<>"
         */
    }
}

/**
 * Box.
 *
 * @param <T> the T
 * @param <U> the U
 */
class Box<T, U> {
    /**
     * t the t
     */
    private final T t;
    /**
     * u the u
     */
    private final U u;

    /**
     * Constructor.
     *
     * @param t the t
     * @param u the u
     */
    Box(T t, U u) {
        this.t = t;
        this.u = u;
    }

    /**
     * Gets the t.
     *
     * @return the t
     */
    T getT() {
        return t;
    }

    /**
     * Gets the u.
     *
     * @return the u
     */
    U getU() {
        return u;
    }
}
