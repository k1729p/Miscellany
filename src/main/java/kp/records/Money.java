package kp.records;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * The money.
 *
 * @param value the {@link BigDecimal} value
 */
record Money(BigDecimal value) {
	enum Tier {
		BOTTOM(2), MIDDLE(5), TOP(8);

		final BigDecimal value;

		Tier(double value) {
			this.value = BigDecimal.valueOf(value);
		}
	}

	public static final Supplier<Stream<Money>> MONEY_STREAM_SUP = () -> DoubleStream
			.iterate(1, num -> num < 10, num -> num + 2).boxed().map(BigDecimal::new).map(Money::new);
	public static final Money ZERO = new Money(new BigDecimal(0));

	public Money add(Money money) {
		return new Money(value.add(money.value));
	}

	public static Money total(Stream<Money> stream) {
		return stream.reduce(Money.ZERO, Money::add);
	}

	public static Money nearest(Stream<Money> stream, BigDecimal target) {

		final Comparator<Money> comparator = Comparator.comparing(money -> Optional.of(money)//
				.map(Money::value)//
				.map(num -> num.add(target.negate()))//
				.map(BigDecimal::abs)//
				.orElse(BigDecimal.ZERO));
		return stream.collect(Collectors.reducing(//
				Money.ZERO, Function.identity(), BinaryOperator.minBy(comparator)));
	}

	public static DoubleSummaryStatistics statistics(Stream<Money> stream) {
		return stream.map(Money::value).map(BigDecimal::doubleValue).collect(//
				DoubleSummaryStatistics::new, DoubleSummaryStatistics::accept, DoubleSummaryStatistics::combine);
	}

	public static Map<Tier, List<Money>> groupByTier(Stream<Money> stream) {
		final Supplier<Map<Tier, List<Money>>> mapFactory = TreeMap::new;
		final Collector<Money, ?, List<Money>> downstream = Collectors.mapping(Function.identity(),
				Collectors.toList());
		return stream.collect(Collectors.groupingBy(Money::classify, mapFactory, downstream));
	}

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
