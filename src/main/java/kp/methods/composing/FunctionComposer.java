package kp.methods.composing;

import kp.utils.Printer;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The function composer.
 */
public interface FunctionComposer {
    /**
     * The {@link Supplier}.
     */
    Supplier<Stream<Integer>> supplier = () -> IntStream.rangeClosed(1, 9).boxed();
    /**
     * The {@link StringBuilder}
     */
    StringBuilder stringBuilder = new StringBuilder();
    /**
     * The mapper for {@link Cardinal}.
     */
    Function<Integer, Cardinal> mapperCardinal = number -> {
        final Cardinal result = Stream.of(Cardinal.values())
                .filter(arg -> arg.ordinal() + 1 == number).findFirst().orElse(null);
        stringBuilder.append(String.format("%-6s", result));
        return result;
    };
    /**
     * The mapper for {@link OrdinalSpatial}.
     */
    Function<Integer, OrdinalSpatial> mapperOrdinalSpatial = ordinal -> {
        final OrdinalSpatial result = Stream.of(OrdinalSpatial.values())
                .filter(arg -> arg.ordinal() == ordinal).findFirst().orElse(null);
        stringBuilder.append(String.format(" → %-7s", result));
        return result;
    };
    /**
     * The mapper for {@link OrdinalPrecedence}.
     */
    Function<Integer, OrdinalPrecedence> mapperOrdinalPrecedence = ordinal -> {
        final OrdinalPrecedence result = Stream.of(OrdinalPrecedence.values())
                .filter(arg -> arg.ordinal() == ordinal).findFirst().orElse(null);
        stringBuilder.append(String.format(" → %-10s", result));
        return result;
    };
    /**
     * The mapper for {@link Multiplier}.
     */
    Function<Integer, Multiplier> mapperMultiplier = ordinal -> {
        final Multiplier result = Stream.of(Multiplier.values())
                .filter(arg -> arg.ordinal() == ordinal).findFirst().orElse(null);
        stringBuilder.append(String.format(" → %-9s", result));
        return result;
    };

    /**
     * Composes the functions.
     */
    static void composeFunctions() {

        final Function<Integer, String> composedFunction = mapperCardinal.andThen(Cardinal::ordinal)
                .andThen(mapperOrdinalSpatial).andThen(OrdinalSpatial::ordinal)
                .andThen(mapperOrdinalPrecedence).andThen(OrdinalPrecedence::ordinal)
                .andThen(mapperMultiplier).andThen(Multiplier::ordinal)
                .andThen(ordinal -> String.valueOf(++ordinal));

        supplier.get().map(composedFunction).forEach(arg -> stringBuilder.append(String.format(" → %s%n", arg)));
        Printer.printObject(stringBuilder);
        Printer.printHor();
    }
}
