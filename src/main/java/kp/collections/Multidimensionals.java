package kp.collections;

import kp.utils.Printer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The multidimensional array and the multidimensional list.
 */
public class Multidimensionals {

    /**
     * Private constructor to prevent instantiation.
     */
    private Multidimensionals() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Goes from the multidimensional array to the multidimensional list.
     */
    static void multidimensionalArrayToMultidimensionalList() {

        final Integer[][][] array3d = {{{}}, {{}, {}, {null, null, null, 12345}}};
        final Integer[][][] arrCopy = {{{}}, {{}, {}, {null, null, null, 12345}}};
        Printer.printf("Two arrays 3-dim equals[%b], deep equals[%b]%n", Arrays.equals(array3d, arrCopy),
                Arrays.deepEquals(array3d, arrCopy));
        final Integer[][] array2d = array3d[1];
        final Integer[] array1d = array2d[2];

        final Function<Integer[], List<Integer>> mapper1d = arr -> Stream.of(arr)
                .collect(Collectors.toCollection(ArrayList::new));
        final Function<Integer[][], List<List<Integer>>> mapper2d = arr -> Stream.of(arr).map(mapper1d)
                .collect(Collectors.toCollection(ArrayList::new));
        // from array 3d to list 3d
        final List<List<List<Integer>>> list3d = Stream.of(array3d).map(mapper2d)
                .collect(Collectors.toCollection(ArrayList::new));
        // from array 2d to list 2d
        final List<List<Integer>> list2d = Stream.of(array2d).map(mapper1d)
                .collect(Collectors.toCollection(ArrayList::new));
        // from array 1d to list 1d
        final List<Integer> list1d = Stream.of(array1d).collect(Collectors.toCollection(ArrayList::new));

        Printer.printf("List-3-dim %s", list3d);
        Printer.printf("List-2-dim\t  %s", list2d);
        Printer.printf("List-1-dim\t\t   %s%n", list1d);
        final String listMsgFmt = "Element of the list:   List-3-dim[%d],  List-2-dim[%d],  List-1-dim[%d]";
        Printer.printf(listMsgFmt, list3d.get(1).get(2).get(3), list2d.get(2).get(3), list1d.get(3));
        list3d.get(1).get(2).set(3, 30000);
        Printer.printf(listMsgFmt, list3d.get(1).get(2).get(3), list2d.get(2).get(3), list1d.get(3));
        list2d.get(2).set(3, 20000);
        Printer.printf(listMsgFmt, list3d.get(1).get(2).get(3), list2d.get(2).get(3), list1d.get(3));
        list1d.set(3, 10000);
        Printer.printf(listMsgFmt, list3d.get(1).get(2).get(3), list2d.get(2).get(3), list1d.get(3));
        Printer.print("");

        final Function<Integer[][], List<List<Integer>>> mapper2dBacked = arr -> Stream.of(arr)//
                .map(Arrays::asList).toList();
        final List<List<List<Integer>>> list3dBacked = Stream.of(array3d).map(mapper2dBacked).toList();
        final List<List<Integer>> list2dBacked = mapper2dBacked.apply(array2d);
        final List<Integer> list1dBacked = Arrays.asList(array1d);

        final String arrayMsgFmt = "Element of the array: array-3-dim[%d], array-2-dim[%d], array-1-dim[%d]";
        final String listArrayMsgFmt = listMsgFmt + "(array backed)";
        Printer.printf(arrayMsgFmt, array3d[1][2][3], array2d[2][3], array1d[3]);
        Printer.printf(listArrayMsgFmt, list3dBacked.get(1).get(2).get(3), list2dBacked.get(2).get(3),
                list1dBacked.get(3));
        list1dBacked.set(3, 99999);
        Printer.printf(arrayMsgFmt, array3d[1][2][3], array2d[2][3], array1d[3]);
        Printer.printf(listArrayMsgFmt, list3dBacked.get(1).get(2).get(3), list2dBacked.get(2).get(3),
                list1dBacked.get(3));
        Printer.printHor();
    }

}
