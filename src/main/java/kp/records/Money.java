package kp.records;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * Represents a monetary value.
 *
 * @param value the {@link BigDecimal} value
 */
record Money(BigDecimal value) {
    /**
     * Enum representing different tiers of monetary values.
     */
    enum Tier {
        BOTTOM(2),
        MIDDLE(5),
        TOP(8);

        final BigDecimal value;

        Tier(double value) {
            this.value = BigDecimal.valueOf(value);
        }
    }

    public static final Supplier<Stream<Money>> MONEY_STREAM_SUP = () -> DoubleStream
            .iterate(1, num -> num < 10, num -> num + 2).boxed()
            .map(BigDecimal::new).map(Money::new);
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    /**
     * Adds the given money value to this money value.
     *
     * @param money the money to add
     * @return the result of the addition
     */
    public Money add(Money money) {
        return new Money(value.add(money.value));
    }

    /**
     * Computes the total sum of a stream of money values.
     *
     * @param stream the stream of money values
     * @return the total sum
     */
    public static Money total(Stream<Money> stream) {
        return stream.reduce(Money.ZERO, Money::add);
    }

    /**
     * Finds the money value nearest to the target value within the stream.
     *
     * @param stream the stream of money values
     * @param target the target value
     * @return the nearest money value
     */
    public static Money nearest(Stream<Money> stream, BigDecimal target) {

        final Comparator<Money> comparator = Comparator.comparing(money -> Optional.of(money)
                .map(Money::value)
                .map(num -> num.subtract(target))
                .map(BigDecimal::abs)
                .orElse(BigDecimal.ZERO));
        return stream.reduce(Money.ZERO, BinaryOperator.minBy(comparator));
    }

    /**
     * Computes statistics for a stream of money values.
     *
     * @param stream the stream of money values
     * @return the statistics
     */
    public static DoubleSummaryStatistics statistics(Stream<Money> stream) {
        return stream.map(Money::value).map(BigDecimal::doubleValue).collect(
                DoubleSummaryStatistics::new, DoubleSummaryStatistics::accept, DoubleSummaryStatistics::combine);
    }

    /**
     * Groups the money values by their respective tiers.
     *
     * @param stream the stream of money values
     * @return a map of tiers to lists of money values
     */
    public static Map<Tier, List<Money>> groupByTier(Stream<Money> stream) {

        final Collector<Money, ?, List<Money>> downstream = Collectors.mapping(Function.identity(),
                Collectors.toList());
        return stream.collect(Collectors.groupingBy(Money::classify, TreeMap::new, downstream));
    }

    /**
     * Classifies this money value into a tier.
     *
     * @return the tier of this money value
     */
    private Tier classify() {
        if (value.compareTo(Tier.BOTTOM.value) < 1) {
            return Tier.BOTTOM;
        } else if (value.compareTo(Tier.TOP.value) < 0) {
            return Tier.MIDDLE;
        } else {
            return Tier.TOP;
        }
    }

}
