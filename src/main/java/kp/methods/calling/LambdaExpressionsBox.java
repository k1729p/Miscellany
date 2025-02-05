package kp.methods.calling;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.IntBinaryOperator;
import java.util.function.Supplier;

/**
 * The box with lambda expressions.
 */
public class LambdaExpressionsBox {
    /**
     * The biConsumer.
     */
    final BiConsumer<Integer, Integer> biConsumer;
    /**
     * The supplier.
     */
    final Supplier<Integer> supplier;
    /**
     * The biPredicate.
     */
    final BiPredicate<Integer, Integer> biPredicate;
    /**
     * The intBinaryOperator.
     */
    final IntBinaryOperator intBinaryOperator;

    /**
     * Constructor.
     *
     * @param biConsumer        the {@link BiConsumer}
     * @param supplier          the {@link Supplier}
     * @param biPredicate       the {@link BiPredicate}
     * @param intBinaryOperator the {@link IntBinaryOperator}
     */
    LambdaExpressionsBox(BiConsumer<Integer, Integer> biConsumer, Supplier<Integer> supplier,
                         BiPredicate<Integer, Integer> biPredicate, IntBinaryOperator intBinaryOperator) {
        this.biConsumer = biConsumer;
        this.supplier = supplier;
        this.biPredicate = biPredicate;
        this.intBinaryOperator = intBinaryOperator;
    }
}