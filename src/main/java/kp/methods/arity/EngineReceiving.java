package kp.methods.arity;

import kp.utils.Printer;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * The engine with receiving.
 */
public final class EngineReceiving {
    /**
     * The field with ternary function.
     */
    public static final BiFunction<String, String, Consumer<String>> TERNARY_FUNCTION =
            (arg1, arg2) -> arg3 -> Printer.printf("EngineReceiving.TERNARY_FUNCTION():\t"
                                                   + "1st[%s], 2nd[%s], 3rd[%s]", arg1, arg2, arg3);

    /**
     * The field with quaternary function.
     */
    public static final BiFunction<String, String, BiConsumer<String, String>> QUATERNARY_FUNCTION =
            (arg1, arg2) -> (arg3, arg4) -> Printer.printf("EngineReceiving.QUATERNARY_FUNCTION():\t"
                                                           + "1st[%s], 2nd[%s], 3rd[%s], 4th[%s]", arg1, arg2, arg3, arg4);

    /**
     * The field with quinary function.
     */
    public static final BiFunction<String, String, BiFunction<String, String, Consumer<String>>> QUINARY_FUNCTION =
            (arg1, arg2) -> (arg3, arg4) -> arg5 -> Printer.printf("EngineReceiving.QUINARY_FUNCTION():\t"
                                                                   + "1st[%s], 2nd[%s], 3rd[%s], 4th[%s], 5th[%s]", arg1, arg2, arg3, arg4, arg5);

    /**
     * The field with septenary function.
     */
    public static final BiFunction<String, String, BiFunction<String, String, BiFunction<String, String, Consumer<String>>>> SEPTENARY_FUNCTION =
            (arg1, arg2) -> (arg3,
                             arg4) -> (arg5, arg6) -> arg7 -> Printer.printf("EngineReceiving.SEPTENARY_FUNCTION():\t"
                                                                             + "1st[%s], 2nd[%s], 3rd[%s], 4th[%s], 5th[%s], 6th[%s], 7th[%s]", arg1, arg2, arg3, arg4,
                    arg5, arg6, arg7);

    /**
     * Private constructor to prevent instantiation.
     */
    private EngineReceiving() {
        throw new IllegalStateException("Utility class");
    }
}