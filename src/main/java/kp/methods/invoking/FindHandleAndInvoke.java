package kp.methods.invoking;

import kp.utils.Printer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.List;

/**
 * Find the method handle and invoke it.
 */
public class FindHandleAndInvoke {
    /**
     * The signature of a method 'equals(...)'
     */
    private static final MethodType METHOD_TYPE_EQ = MethodType.methodType(boolean.class, Object.class);
    /**
     * The signature of a method 'hashCode(...)'
     */
    private static final MethodType METHOD_TYPE_HC = MethodType.methodType(int.class);
    /**
     * The signature of a method 'toString(...)'
     */
    private static final MethodType METHOD_TYPE_TS = MethodType.methodType(String.class);
    /**
     * The signature of a setter method
     */
    @SuppressWarnings("unused")
    private static final MethodType METHOD_TYPE_SET = MethodType.methodType(void.class, Object.class);
    /**
     * The signature of a method 'compare(...)' from Comparator<String>
     */
    @SuppressWarnings("unused")
    private static final MethodType METHOD_TYPE_SC = MethodType.methodType(int.class, String.class, String.class);

    /**
     * The box.
     */
    static class Box {
        int number;
    }

    /**
     * Invokes the methods of the Object class.
     */
    public void invokeObjectMethods() {

        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        final FindHandleAndInvoke findHandleAndInvoke = new FindHandleAndInvoke() {
            @Override
            public String toString() {
                return "example";
            }
        };
        List.of(new Object(), "a", findHandleAndInvoke).forEach(object -> {
            try {
                final MethodHandle methodHandleEQ = lookup.findVirtual(object.getClass(), "equals", METHOD_TYPE_EQ);
                final MethodHandle methodHandleHC = lookup.findVirtual(object.getClass(), "hashCode", METHOD_TYPE_HC);
                final MethodHandle methodHandleTS = lookup.findVirtual(object.getClass(), "toString", METHOD_TYPE_TS);

                Printer.printf("Invoking results: 'equals()'[%b], 'hashCode()'[%8x], 'toString()'[%s]",
                        methodHandleEQ.invoke(object, object), methodHandleHC.invoke(object),
                        methodHandleTS.invoke(object));
            } catch (Throwable e) {
                Printer.printException(e);
                System.exit(1);
            }
        });
        Printer.printHor();

    }

    /**
     * Invokes first the setter method then the getter method.
     */
    public void invokeFirstSetterThenGetter() {

        final Box box = new Box();
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            final MethodHandle methodHandleSET = lookup.findSetter(Box.class, "number", int.class);
            final MethodHandle methodHandleGET = lookup.findGetter(Box.class, "number", int.class);

            methodHandleSET.invoke(box, 123);
            final int number = (int) methodHandleGET.invoke(box);
            Printer.printf("Invoking result from getter: box.number[%d]", number);
        } catch (Throwable e) {
            Printer.printException(e);
            System.exit(1);
        }
        Printer.printHor();
    }

    /**
     * Replaces an array element.
     */
    public void replaceArrayElement() {

        final String[] strArray = {"A", "expected", "C"};
        Printer.printf("Array before%s", List.of(strArray));

        final VarHandle varHandle = MethodHandles.arrayElementVarHandle(String[].class);
        final boolean result = varHandle.compareAndSet(strArray, 1, "expected", "replaced");

        Printer.printf("Array  after%s", List.of(strArray));
        Printer.printf("ReplacingWithMatcher the array element was successful[%b]", result);
        Printer.printHor();
    }
}
