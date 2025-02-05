package kp.streams.teeing;

import kp.Constants;
import kp.utils.Printer;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The main launcher for the research of the method {@link Collectors#teeing}.
 */
public class ApplicationForCollectorsTeeing {

    private static final String DATE_FMT = "%tF";
    private static final String DATE_TIME_FMT = "%1$tF %1$tT";
    private static final String VAL_FMT = "[%s]";
    private static final String UNUSED = "unused";
    private static final Function<ZonedDateTime, String> DATE_KEY_FUN =
            zonedDateTime -> String.format(DATE_TIME_FMT, zonedDateTime);

    /**
     * Private constructor to prevent instantiation.
     */
    private ApplicationForCollectorsTeeing() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The primary entry point for launching the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        Printer.printHor();
        processJoining();
        processZonedDateTime();
        processAtomicSimple();
        processAtomicDetailed();
    }

    /**
     * Processes the strings concatenation.
     */
    private static void processJoining() {

        final BiFunction<List<String>, String, List<String>> merger = (list, joinedString) -> {
            list.add(joinedString);
            return list;
        };
        final Collector<String, ?, List<String>> compositeCollector = Collectors.teeing(
                Collectors.toList(), Collectors.joining("-"), merger);
        final List<String> resultList = Stream.of("Apple", "Banana", "Orange").collect(compositeCollector);
        Printer.printObject(resultList);
        Printer.printHor();
    }

    /**
     * Processes the {@link ZonedDateTime}.
     */
    private static void processZonedDateTime() {

        final BiConsumer<Map<String, Map<String, List<String>>>, ZonedDateTime> accumulator1 =
                (map, zonedDateTime) -> {
                    final String dateKey = DATE_KEY_FUN.apply(zonedDateTime);
                    final Map<String, List<String>> dateMap = map.computeIfAbsent(dateKey, _ -> new TreeMap<>());
                    final String label = String.format("'%s' in month '%s'",
                            zonedDateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                            zonedDateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
                    final List<String> list = dateMap.computeIfAbsent(label, _ -> new ArrayList<>());
                    final DayOfWeek dayOfWeek = zonedDateTime.getDayOfWeek();
                    final Month month = zonedDateTime.getMonth();
                    for (int i = 0; i <= 5; i++) {
                        final ZonedDateTime dateTime = zonedDateTime.with(TemporalAdjusters.dayOfWeekInMonth(i, dayOfWeek));
                        if (month == dateTime.getMonth()) {
                            list.add(String.format(DATE_FMT, dateTime));
                        }
                    }
                };
        final BiConsumer<Map<String, Map<String, List<String>>>, ZonedDateTime> accumulator2 =
                (map, zonedDateTime) -> {
                    final String dateKey = DATE_KEY_FUN.apply(zonedDateTime);
                    final Map<String, List<String>> dateMap = map.computeIfAbsent(dateKey, _ -> new TreeMap<>());
                    final String label = "formatted date & time";
                    final List<String> list = dateMap.computeIfAbsent(label, _ -> new ArrayList<>());
                    list.add(String.format(DATE_TIME_FMT, zonedDateTime));
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.FULL);
                    list.add(zonedDateTime.format(dateTimeFormatter));
                    dateTimeFormatter = DateTimeFormatter.ofPattern("qqqq 'of the year', w 'week of the year', eeee B");
                    list.add(zonedDateTime.format(dateTimeFormatter));
                };
        final BiConsumer<Map<String, Map<String, List<String>>>, ZonedDateTime> accumulator3 =
                (map, zonedDateTime) -> {
                    final String dateKey = DATE_KEY_FUN.apply(zonedDateTime);
                    final Map<String, List<String>> dateMap = map.computeIfAbsent(dateKey, _ -> new TreeMap<>());
                    final String label = "six days earlier & six days later";
                    final List<String> list = dateMap.computeIfAbsent(label, _ -> new ArrayList<>());
                    list.add(String.format(DATE_FMT, zonedDateTime.minusDays(6)));
                    list.add(String.format(DATE_FMT, zonedDateTime.plusDays(6)));
                };
        final BiConsumer<Map<String, Map<String, List<String>>>, ZonedDateTime> accumulator4 =
                (map, zonedDateTime) -> {
                    final String dateKey = DATE_KEY_FUN.apply(zonedDateTime);
                    final Map<String, List<String>> dateMap = map.computeIfAbsent(dateKey, _ -> new TreeMap<>());
                    final String label = "six hours earlier & six hours later";
                    final List<String> list = dateMap.computeIfAbsent(label, _ -> new ArrayList<>());
                    list.add(String.format(DATE_TIME_FMT, zonedDateTime.minusHours(6)));
                    list.add(String.format(DATE_TIME_FMT, zonedDateTime.plusHours(6)));
                };
        final BinaryOperator<Map<String, Map<String, List<String>>>> merger =
                (map1, map2) -> {
                    final TreeMap<String, Map<String, List<String>>> mergedMap = new TreeMap<>();
                    Stream.of(map1, map2).forEach(srcMap -> srcMap.forEach(
                            (key, value) -> {
                                final Map<String, List<String>> innerMergedMap = mergedMap.computeIfAbsent(key, _ -> new TreeMap<>());
                                value.forEach((innerKey, innerValue) -> {
                                    final List<String> list = innerMergedMap.computeIfAbsent(innerKey, _ -> new ArrayList<>());
                                    list.addAll(innerValue);
                                });
                            }));
                    return mergedMap;
                };
        final Collector<ZonedDateTime, ?, Map<String, Map<String, List<String>>>> compositeCollector12 = Collectors
                .teeing(Collector.of(TreeMap::new, accumulator1, merger),
                        Collector.of(TreeMap::new, accumulator2, merger),
                        merger);
        final Collector<ZonedDateTime, ?, Map<String, Map<String, List<String>>>> compositeCollector34 = Collectors
                .teeing(Collector.of(TreeMap::new, accumulator3, merger),
                        Collector.of(TreeMap::new, accumulator4, merger),
                        merger);
        final Collector<ZonedDateTime, ?, Map<String, Map<String, List<String>>>> compositeCollectorTotal = Collectors
                .teeing(compositeCollector12, compositeCollector34, merger);

        final Stream<ZonedDateTime> stream = Stream.of(Constants.EXAMPLE_ZONED_DATE_TIME,
                Constants.EXAMPLE_ZONED_DATE_TIME.plusWeeks(16), Constants.EXAMPLE_ZONED_DATE_TIME.plusMonths(16));
        final Map<String, Map<String, List<String>>> resultMap = stream.collect(compositeCollectorTotal);
        resultMap.forEach((topKey, topMap) -> {
            Printer.printf(VAL_FMT, topKey);
            topMap.forEach((bottomKey, bottomList) -> {
                Printer.printf("\t" + VAL_FMT, bottomKey);
                bottomList.forEach(text -> Printer.printf("\t\t" + VAL_FMT, text));
            });
        });
        Printer.printHor();
    }

    /**
     * Processes the atomic numbers.
     */
    private static void processAtomicSimple() {

        final AtomicInteger atomicAcc = new AtomicInteger();
        final AtomicInteger atomicMrg = new AtomicInteger();
        final BiConsumer<Map<String, List<String>>, String> accumulator = (map, key) -> {
            final List<String> list = map.computeIfAbsent(key, _ -> new ArrayList<>());
            list.add(String.format("%02d-acc ", atomicAcc.incrementAndGet()));
        };
        final BinaryOperator<Map<String, List<String>>> merger = (map1, map2) -> {
            final TreeMap<String, List<String>> mergedMap = new TreeMap<>();
            Stream.of(map1, map2).forEach(
                    srcMap -> srcMap.forEach((srcKey, srcValue) -> {
                        final List<String> list = mergedMap.computeIfAbsent(srcKey, _ -> new ArrayList<>());
                        srcValue.forEach(
                                item -> list.add(String.format("%s %02d-mrg ", item, atomicMrg.incrementAndGet())));
                    }));
            return mergedMap;
        };
        final Collector<String, ?, Map<String, List<String>>> compositeCollector = Collectors.teeing(
                Collector.of(TreeMap::new, accumulator, merger),
                Collector.of(TreeMap::new, accumulator, merger),
                merger);
        final Collector<String, ?, Map<String, List<String>>> compositeCollectorSuper = Collectors.teeing(
                compositeCollector,
                compositeCollector,
                merger);
        final Collector<String, ?, Map<String, List<String>>> compositeCollectorTotal = Collectors.teeing(
                compositeCollectorSuper,
                compositeCollectorSuper,
                merger);

        final Stream<String> stream = Stream.of("ABC".split(""));
        final Map<String, List<String>> resultMap = stream.collect(compositeCollectorTotal);
        resultMap.forEach((key, value1) -> {
            Printer.printf(VAL_FMT, key);
            value1.forEach(value -> Printer.printf("\t" + VAL_FMT, value));
        });
        Printer.printHor();
    }

    /**
     * Processes the atomic numbers. More details in the labels.
     */
    private static void processAtomicDetailed() {

        final AtomicInteger atomicAcc = new AtomicInteger();
        final AtomicInteger atomicMrg = new AtomicInteger();
        final AtomicInteger atomicRes = new AtomicInteger();
        final Function<String, BiConsumer<Map<String, List<String>>, String>> accumulatorFun =
                label -> (map, key) -> {
                    final List<String> list = map.computeIfAbsent(key, _ -> new ArrayList<>());
                    list.add(String.format("%s-%02d ", label, atomicAcc.incrementAndGet()));
                };

        final Function<String, BinaryOperator<Map<String, List<String>>>> mergerFun =
                label -> (map1, map2) -> {
                    int mergeNumber = atomicMrg.incrementAndGet();
                    final TreeMap<String, List<String>> mergedMap = new TreeMap<>();
                    Stream.of(map1, map2).forEach(
                            srcMap -> srcMap.forEach((srcKey, srcValue) -> {
                                final List<String> list = mergedMap.computeIfAbsent(srcKey, _ -> new ArrayList<>());
                                srcValue.forEach(value -> {
                                    final String result = String.format("%s %s-%02d-%02d ",
                                            value, label, atomicRes.incrementAndGet(), mergeNumber);
                                    list.add(result);
                                });
                            }));
                    return mergedMap;
                };
        final Collector<String, ?, Map<String, List<String>>> compositeCollector1 = Collectors.teeing(
                Collector.of(TreeMap::new, accumulatorFun.apply("1stAcc"), mergerFun.apply(UNUSED)),
                Collector.of(TreeMap::new, accumulatorFun.apply("2ndAcc"), mergerFun.apply(UNUSED)),
                mergerFun.apply("1stMrg"));
        final Collector<String, ?, Map<String, List<String>>> compositeCollector2 = Collectors.teeing(
                Collector.of(TreeMap::new, accumulatorFun.apply("3rdAcc"), mergerFun.apply(UNUSED)),
                Collector.of(TreeMap::new, accumulatorFun.apply("4thAcc"), mergerFun.apply(UNUSED)),
                mergerFun.apply("2ndMrg"));
        final Collector<String, ?, Map<String, List<String>>> compositeCollector3 = Collectors.teeing(
                Collector.of(TreeMap::new, accumulatorFun.apply("5thAcc"), mergerFun.apply(UNUSED)),
                Collector.of(TreeMap::new, accumulatorFun.apply("6thAcc"), mergerFun.apply(UNUSED)),
                mergerFun.apply("3rdMrg"));
        final Collector<String, ?, Map<String, List<String>>> compositeCollector4 = Collectors.teeing(
                Collector.of(TreeMap::new, accumulatorFun.apply("7thAcc"), mergerFun.apply(UNUSED)),
                Collector.of(TreeMap::new, accumulatorFun.apply("8thAcc"), mergerFun.apply(UNUSED)),
                mergerFun.apply("4thMrg"));
        final Collector<String, ?, Map<String, List<String>>> compositeCollector12 = Collectors
                .teeing(compositeCollector1, compositeCollector2, mergerFun.apply("5thMrg"));
        final Collector<String, ?, Map<String, List<String>>> compositeCollector34 = Collectors
                .teeing(compositeCollector3, compositeCollector4, mergerFun.apply("6thMrg"));
        final Collector<String, ?, Map<String, List<String>>> compositeCollector1234 = Collectors
                .teeing(compositeCollector12, compositeCollector34, mergerFun.apply("7thMrg"));

        final Stream<String> stream = Stream.of("XYZ".split(""));
        final Map<String, List<String>> resultMap = stream.collect(compositeCollector1234);
        resultMap.forEach((key, value1) -> {
            Printer.printf(VAL_FMT, key);
            value1.forEach(value -> Printer.printf("\t" + VAL_FMT, value));
        });
        Printer.printHor();
    }

}
