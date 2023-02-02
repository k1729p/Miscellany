package kp.dates;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import kp.Constants;
import kp.utils.Printer;
import kp.utils.Utils;

/**
 * The dates and times aggregation.
 *
 */
public class DatesAndTimesAggregation {

	/**
	 * The sequential or parallel choice flag.
	 */
	static final boolean SEQUENTIAL_STREAM_FLAG = true;

	/**
	 * The constructor.
	 */
	public DatesAndTimesAggregation() {
		super();
	}

	/**
	 * Aggregates the leap days.
	 * 
	 */
	public void aggregateLeapDays() {

		final Collector<LocalDate, ?, Map<DayOfWeek, List<Integer>>> leapYearCollector = Collectors
				.groupingBy(LocalDate::getDayOfWeek, Collectors.mapping(LocalDate::getYear, Collectors.toList()));

		final Consumer<Entry<DayOfWeek, List<Integer>>> entryConsumer = entry -> Printer
				.printf("Day[%9s], leap years%s", entry.getKey(), entry.getValue());

		Printer.print("Leap years by day of week:");
		LocalDate.of(1904, 2, 29).datesUntil(LocalDate.of(2104, 2, 29), Period.ofYears(4))//
				.filter(date -> 29 == date.getDayOfMonth())//
				.collect(leapYearCollector).entrySet().forEach(entryConsumer);
		Printer.printHor();
	}

	/**
	 * Aggregates the seconds from one year.
	 * 
	 */
	public void aggregateOneYearSeconds() {

		Instant start = Instant.now();
		final List<LocalDateTime> dateList = createDateList(1, ChronoUnit.YEARS);
		Printer.printf("Created date list. %s", Utils.formatElapsed(start, Instant.now()));

		final Map<Integer, ? extends Map<Month, ? extends Map<Integer, ? extends Map<Integer, ? extends Map<Integer, ? extends Map<Integer, List<LocalDateTime>>>>>>>
		/*-*/ datesByFieldsMap;
		final Collector<LocalDateTime, ?, ? extends Map<Integer, ? extends Map<Month, ? extends Map<Integer, ? extends Map<Integer, ? extends Map<Integer, ? extends Map<Integer, List<LocalDateTime>>>>>>>>
		/*-*/ dateCollector;

		if (SEQUENTIAL_STREAM_FLAG) {
			dateCollector = getDateCollector();
			start = Instant.now();
			datesByFieldsMap = dateList.stream().sequential().collect(dateCollector);
			Printer.printf("Stream process finished. %s", Utils.formatElapsed(start, Instant.now()));
		} else {
			dateCollector = getDateCollectorForConcurrent();
			start = Instant.now();
			datesByFieldsMap = dateList.stream().parallel().collect(dateCollector);
			Printer.printf("Parallel stream process finished. %s", Utils.formatElapsed(start, Instant.now()));
		}
		showDateTimeContent(datesByFieldsMap);
		Printer.printHor();
	}

	/**
	 * Gets the date collector.
	 * 
	 * @return the year collector
	 */
	private Collector<LocalDateTime, ?, Map<Integer, Map<Month, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<LocalDateTime>>>>>>>> getDateCollector() {

		final Collector<LocalDateTime, ?, Map<Integer, List<LocalDateTime>>>
		/*-*/ secondCollector = Collectors.groupingBy(LocalDateTime::getSecond);
		final Collector<LocalDateTime, ?, Map<Integer, Map<Integer, List<LocalDateTime>>>>
		/*-*/ minuteCollector = Collectors.groupingBy(LocalDateTime::getMinute, TreeMap::new, secondCollector);
		final Collector<LocalDateTime, ?, Map<Integer, Map<Integer, Map<Integer, List<LocalDateTime>>>>>
		/*-*/ hourCollector = Collectors.groupingBy(LocalDateTime::getHour, TreeMap::new, minuteCollector);
		final Collector<LocalDateTime, ?, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<LocalDateTime>>>>>>
		/*-*/ dayCollector = Collectors.groupingBy(LocalDateTime::getDayOfMonth, TreeMap::new, hourCollector);
		final Collector<LocalDateTime, ?, Map<Month, Map<Integer, Map<Integer, Map<Integer, Map<Integer, List<LocalDateTime>>>>>>>
		/*-*/ monthCollector = Collectors.groupingBy(LocalDateTime::getMonth, TreeMap::new, dayCollector);
		return Collectors.groupingBy(LocalDateTime::getYear, TreeMap::new, monthCollector);
	}

	/**
	 * Gets the date collector for concurrent.
	 * 
	 * @return the year collector
	 */
	private Collector<LocalDateTime, ?, ConcurrentMap<Integer, ConcurrentMap<Month, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, List<LocalDateTime>>>>>>>> getDateCollectorForConcurrent() {

		final Collector<LocalDateTime, ?, ConcurrentMap<Integer, List<LocalDateTime>>>
		/*-*/ secondCollector = Collectors.groupingByConcurrent(LocalDateTime::getSecond, ConcurrentSkipListMap::new,
				Collectors.mapping(Function.identity(), Collectors.toList()));
		final Collector<LocalDateTime, ?, ConcurrentMap<Integer, ConcurrentMap<Integer, List<LocalDateTime>>>>
		/*-*/ minuteCollector = Collectors.groupingByConcurrent(LocalDateTime::getMinute, ConcurrentSkipListMap::new,
				secondCollector);
		final Collector<LocalDateTime, ?, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, List<LocalDateTime>>>>>
		/*-*/ hourCollector = Collectors.groupingByConcurrent(LocalDateTime::getHour, ConcurrentSkipListMap::new,
				minuteCollector);
		final Collector<LocalDateTime, ?, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, List<LocalDateTime>>>>>>
		/*-*/ dayCollector = Collectors.groupingByConcurrent(LocalDateTime::getDayOfMonth, ConcurrentSkipListMap::new,
				hourCollector);
		final Collector<LocalDateTime, ?, ConcurrentMap<Month, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, ConcurrentMap<Integer, List<LocalDateTime>>>>>>>
		/*-*/ monthCollector = Collectors.groupingByConcurrent(LocalDateTime::getMonth, ConcurrentSkipListMap::new,
				dayCollector);
		return Collectors.groupingByConcurrent(LocalDateTime::getYear, ConcurrentSkipListMap::new, monthCollector);
	}

	/**
	 * Creates the date list.
	 * 
	 * @param amount the amount
	 * @param unit   the unit
	 * @return the stream
	 */
	private List<LocalDateTime> createDateList(long amount, ChronoUnit unit) {

		final List<LocalDateTime> dateList = new ArrayList<>();
		for (LocalDateTime date = Constants.EXAMPLE_LOCAL_DATE_TIME.minus(amount, unit); date
				.isBefore(Constants.EXAMPLE_LOCAL_DATE_TIME); date = date.plusSeconds(1L)) {
			dateList.add(date);
		}
		return dateList;
	}

	/**
	 * Shows the date and time content.
	 * 
	 * @param datesByFieldsMap the dates by fields map
	 */
	private void showDateTimeContent(
			Map<Integer, ? extends Map<Month, ? extends Map<Integer, ? extends Map<Integer, ? extends Map<Integer, ? extends Map<Integer, List<LocalDateTime>>>>>>> datesByFieldsMap) {

		AtomicInteger atomic = new AtomicInteger();
		iterateOverYears(datesByFieldsMap, atomic);
		Printer.printf("In one year there are [%s]seconds", Utils.formatNumber(atomic.get()));
		showSelectedDates(datesByFieldsMap, atomic);
	}

	/**
	 * Iterates over the years.
	 * 
	 * @param yMap   the years map
	 * @param atomic the {@link AtomicInteger}
	 */
	private void iterateOverYears(
			Map<Integer, ? extends Map<Month, ? extends Map<Integer, ? extends Map<Integer, ? extends Map<Integer, ? extends Map<Integer, List<LocalDateTime>>>>>>> yMap,
			AtomicInteger atomic) {

		yMap.entrySet().forEach(entry -> iterateOverMonths(entry.getValue(), atomic));
	}

	/**
	 * Iterates over the months.
	 * 
	 * @param mMap   the months map
	 * @param atomic the {@link AtomicInteger}
	 */
	private void iterateOverMonths(
			Map<Month, ? extends Map<Integer, ? extends Map<Integer, ? extends Map<Integer, ? extends Map<Integer, List<LocalDateTime>>>>>> mMap,
			AtomicInteger atomic) {

		mMap.entrySet().forEach(entry -> iterateOverDays(entry.getValue(), atomic));
	}

	/**
	 * Iterates over the days.
	 * 
	 * @param dMap   the days map
	 * @param atomic the {@link AtomicInteger}
	 */
	private void iterateOverDays(//
			Map<Integer, ? extends Map<Integer, ? extends Map<Integer, ? extends Map<Integer, List<LocalDateTime>>>>> dMap,
			AtomicInteger atomic) {

		dMap.entrySet().forEach(entry -> iterateOverHours(entry.getValue(), atomic));
	}

	/**
	 * Iterates over the hours.
	 * 
	 * @param hMap   the hours map
	 * @param atomic the {@link AtomicInteger}
	 */
	private void iterateOverHours(
			Map<Integer, ? extends Map<Integer, ? extends Map<Integer, List<LocalDateTime>>>> hMap,
			AtomicInteger atomic) {

		hMap.entrySet().forEach(entry -> iterateOverMinutes(entry.getValue(), atomic));
	}

	/**
	 * Iterates over the minutes.
	 * 
	 * @param mMap   the minutes map
	 * @param atomic the {@link AtomicInteger}
	 */
	private void iterateOverMinutes(Map<Integer, ? extends Map<Integer, List<LocalDateTime>>> mMap,
			AtomicInteger atomic) {

		mMap.values().forEach(map -> map.keySet().forEach(second -> atomic.incrementAndGet()));
	}

	/**
	 * Show selected dates at 0%, 25%, 50%, 75%, and 100% of total.
	 * 
	 * @param datesByFieldsMap the dates by fields map
	 * @param totalAtomic      the total {@link AtomicInteger}
	 */
	private void showSelectedDates(
			Map<Integer, ? extends Map<Month, ? extends Map<Integer, ? extends Map<Integer, ? extends Map<Integer, ? extends Map<Integer, List<LocalDateTime>>>>>>> datesByFieldsMap,
			AtomicInteger totalAtomic) {

		AtomicInteger indexAtomic = new AtomicInteger();
		datesByFieldsMap.entrySet().forEach(//
				yearEntry -> yearEntry.getValue().entrySet().forEach(//
						monthEntry -> monthEntry.getValue().entrySet().forEach(//
								dayEntry -> dayEntry.getValue().entrySet().forEach(//
										hourEntry -> hourEntry.getValue().entrySet().forEach(//
												minuteEntry -> minuteEntry.getValue().entrySet().forEach(//
														secondEntry -> {
															final String percent = createPercentLabel(totalAtomic.get(),
																	indexAtomic.getAndIncrement());
															if (!percent.isEmpty()) {
																Printer.printf(
																		"Date at %s%% boundary: year[%d], month[%2d], day[%2d], hour[%2d], minute[%2d], second[%2d]",
																		percent, yearEntry.getKey(),
																		monthEntry.getKey().getValue(),
																		dayEntry.getKey(), hourEntry.getKey(),
																		minuteEntry.getKey(), secondEntry.getKey());
															}
														}))))));
	}

	/**
	 * Creates percent label.
	 * 
	 * @param total the total
	 * @param index the index
	 * @return the percent label
	 */
	private String createPercentLabel(int total, int index) {

		String percent = "";
		if (index == 0) {
			percent = "  0";
		} else if (index == total / 4) {
			percent = " 25";
		} else if (index == total / 2) {
			percent = " 50";
		} else if (index == 3 * total / 4) {
			percent = " 75";
		} else if (index == total - 1) {
			percent = "100";
		}
		return percent;
	}

}