package kp.dates;

import kp.Constants;
import kp.utils.Printer;
import kp.utils.Utils;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.*;

/**
 * The dates and times changing.
 */
public class DatesAndTimesChanging {

    private static final Date EXAMPLE_DATE = Date.from(Constants.EXAMPLE_ZONED_DATE_TIME.toInstant());

    /**
     * Formats date.
     */
    public void formatDate() {

        Printer.printf("date[%s]", EXAMPLE_DATE);
        final Calendar exampleCalendar = GregorianCalendar.from(Constants.EXAMPLE_ZONED_DATE_TIME);
        Printer.printf("    [%s]◄-calendar", exampleCalendar.getTime());
        Printer.printf("    [%s]◄-localDateTime", Constants.EXAMPLE_LOCAL_DATE_TIME);

        Printer.printf("date[%1$tF %1$tT], calendar[%2$tF %2$tT], localDateTime[%2$tF %2$tT]", EXAMPLE_DATE,
                exampleCalendar, Constants.EXAMPLE_LOCAL_DATE_TIME);

        Printer.printf("date[%s %s] (with DateFormat)", DateFormat.getDateInstance().format(EXAMPLE_DATE),
                DateFormat.getTimeInstance().format(EXAMPLE_DATE));
        final String patternString = "date[{0,date} {0,time}] (with MessageFormat)";
        Printer.print(MessageFormat.format(patternString, EXAMPLE_DATE));

        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Printer.printf("    [%s]◄-localDateTime (with DateTimeFormatter)",
                dateTimeFormatter.format(Constants.EXAMPLE_LOCAL_DATE_TIME));
        // SimpleDateFormat is not thread-safe
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Printer.printf("date[%s] (with SimpleDateFormat)", dateFormat.format(EXAMPLE_DATE));

        final DateFormat dateFormatUS = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.US);
        Printer.printf("date[%s] (with US locale)", dateFormatUS.format(EXAMPLE_DATE));
        final DateFormat dateFormatFR = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.FRANCE);
        Printer.printf("date[%s] (with FR locale)", dateFormatFR.format(EXAMPLE_DATE));
        Printer.printHor();
    }

    /**
     * Converts date.
     */
    public void convertDateToAndFro() {

        Printer.printf("utilDate\t[%1$s]", EXAMPLE_DATE);
        // >>>>>
        java.time.LocalDate localDate1 = LocalDate.ofInstant(EXAMPLE_DATE.toInstant(), ZoneId.systemDefault());
        java.time.LocalTime localTime1 = LocalTime.ofInstant(EXAMPLE_DATE.toInstant(), ZoneId.systemDefault());
        java.time.LocalDateTime localDateTime1 = LocalDateTime.ofInstant(EXAMPLE_DATE.toInstant(),
                ZoneId.systemDefault());
        java.time.ZonedDateTime zonedDateTime1 = ZonedDateTime.ofInstant(EXAMPLE_DATE.toInstant(),
                ZoneId.systemDefault());
        Printer.printf("""
                        localDate\t[%s]
                        localTime\t\t   [%s]
                        localDateTime\t[%s]
                        zonedDateTime\t[%s]""", localDate1,
                localTime1, localDateTime1, zonedDateTime1);

        // <<<<<
        @SuppressWarnings("unused")
        java.util.Date date1 = Date.from(localDate1.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        @SuppressWarnings("unused")
        java.util.Date date2 = Date.from(localDate1.atTime(localTime1).atZone(ZoneId.systemDefault()).toInstant());
        @SuppressWarnings("unused")
        java.util.Date date3 = Date.from(localDateTime1.atZone(ZoneId.systemDefault()).toInstant());
        @SuppressWarnings("unused")
        java.util.Date date4 = Date.from(zonedDateTime1.toInstant());

        // >>>>> SQL
        java.sql.Date sqlDate = new java.sql.Date(EXAMPLE_DATE.getTime());
        java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(EXAMPLE_DATE.getTime());
        Printer.printf("""
                sqlDate\t\t[%s]
                sqlTimestamp\t[%s]""", sqlDate, sqlTimestamp);

        // <<<<< SQL
        @SuppressWarnings("unused")
        java.util.Date date5 = java.sql.Date.valueOf(localDate1);
        @SuppressWarnings("unused")
        java.util.Date date6 = java.sql.Timestamp.valueOf(localDateTime1);

        // SQL >>>>>
        @SuppressWarnings("unused")
        java.time.LocalDate localDate2 = sqlDate.toLocalDate();
        @SuppressWarnings("unused")
        java.time.LocalDateTime localDateTime2 = sqlTimestamp.toLocalDateTime();
        Printer.printHor();
    }

    /**
     * Adjusts date.
     */
    public void adjustDate() {

        final Map<String, TemporalAdjuster> adjusterMap = new LinkedHashMap<>();
        adjusterMap.put("two days later",
                TemporalAdjusters.ofDateAdjuster(date -> date.plusDays(2)));
        adjusterMap.put("first day of month",
                TemporalAdjusters.firstDayOfMonth());
        adjusterMap.put("first Monday in month",
                TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        adjusterMap.put("last day of month",
                TemporalAdjusters.lastDayOfMonth());
        adjusterMap.put("first day of next month",
                TemporalAdjusters.firstDayOfNextMonth());

        adjusterMap.forEach((key, value) -> {
            final LocalDate localDate = Constants.EXAMPLE_LOCAL_DATE.with(value);
            Printer.printf("Adjusted as %-23s: day of week[%9s], day of month[%2d], month[%9s]", key,
                    localDate.getDayOfWeek(), localDate.getDayOfMonth(), localDate.getMonth());
        });
        Printer.printHor();
    }

    /**
     * Query temporal objects.
     */
    public void queryTemporalObjects() {

        Printer.printf("Time[%tT], hour is even[%B], minute is even[%B], second is even[%s]%n",
                Constants.EXAMPLE_LOCAL_DATE_TIME,
                Constants.EXAMPLE_LOCAL_DATE_TIME.query(this::isHourEven),
                Constants.EXAMPLE_LOCAL_DATE_TIME.query(this::isMinuteEven),
                Constants.EXAMPLE_LOCAL_DATE_TIME.query(this::isSecondEven));

        final LocalDateTime exampleMonday = Constants.EXAMPLE_LOCAL_DATE_TIME.truncatedTo(ChronoUnit.HOURS)
                .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
        final LocalDateTime exampleSaturday = Constants.EXAMPLE_LOCAL_DATE_TIME.truncatedTo(ChronoUnit.HOURS)
                .with(TemporalAdjusters.firstInMonth(DayOfWeek.SATURDAY));
        final List<LocalDateTime> localDateTimeList = List.of(
                exampleMonday.withHour(9).minusMinutes(1),
                exampleMonday.withHour(9),
                exampleMonday.withHour(17).minusMinutes(1),
                exampleMonday.withHour(17),
                exampleSaturday.withHour(9)
        );
        final TemporalQuery<Boolean> workingHoursQuery = temporal -> {
            final LocalTime localTime = LocalTime.from(temporal);
            boolean flag = DayOfWeek.SATURDAY != DayOfWeek.from(temporal);
            flag = flag && DayOfWeek.SUNDAY != DayOfWeek.from(temporal);
            flag = flag && LocalTime.of(8, 59).isBefore(localTime);
            flag = flag && LocalTime.of(17, 0).isAfter(localTime);
            return flag;
        };
        localDateTimeList.forEach(temporal -> Printer.printf(
                "Date[%1$9s %2$tF %2$tT] is within limits of working time[%3$B]",
                DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK)), temporal, temporal.query(workingHoursQuery)));
        Printer.printHor();
    }

    /**
     * Checks if the hour is even.
     *
     * @param temporal the temporal
     * @return the result flag
     */
    private boolean isHourEven(TemporalAccessor temporal) {
        return temporal.get(ChronoField.HOUR_OF_DAY) % 2 == 0;
    }

    /**
     * Checks if the minute is even.
     *
     * @param temporal the temporal
     * @return the result flag
     */
    private boolean isMinuteEven(TemporalAccessor temporal) {
        return temporal.get(ChronoField.MINUTE_OF_HOUR) % 2 == 0;
    }

    /**
     * Checks if the second is even.
     *
     * @param temporal the temporal
     * @return the result string
     */
    private String isSecondEven(TemporalAccessor temporal) {
        return temporal.get(ChronoField.SECOND_OF_MINUTE) % 2 == 0 ? "YES" : "NO";
    }

    /**
     * Calculates the amount of the time between the start and the end.
     */
    public void calculateAmountOfTimeBetween() {

        final LocalDateTime startDateTime = Constants.EXAMPLE_LOCAL_DATE_TIME;
        final LocalDateTime endDateTime1 = startDateTime.plusHours(2).minusMinutes(58).minusSeconds(59);
        Printer.printf("""
                        Chrono units between start time[%1$tF %1$tT] and end time[%2$tF %2$tT]:
                        \thours[%3$d] or minutes[%4$d] or seconds[%5$s] or millis[%6$s]""",
                startDateTime, endDateTime1,
                ChronoUnit.HOURS.between(startDateTime, endDateTime1),
                ChronoUnit.MINUTES.between(startDateTime, endDateTime1),
                Utils.formatNumber(ChronoUnit.SECONDS.between(startDateTime, endDateTime1)),
                Utils.formatNumber(ChronoUnit.MILLIS.between(startDateTime, endDateTime1)));

        final LocalDateTime endDateTime2 = startDateTime.plusMonths(1).plusWeeks(1).minusDays(1);
        Printer.printf("""
                        Chrono units between start time[%1$tF %1$tT] and end time[%2$tF %2$tT]:
                        \tmonths[%3$d] or weeks[%4$d] or days[%5$d]
                        """,
                startDateTime, endDateTime2,
                ChronoUnit.MONTHS.between(startDateTime, endDateTime2),
                ChronoUnit.WEEKS.between(startDateTime, endDateTime2),
                ChronoUnit.DAYS.between(startDateTime, endDateTime2));

        final LocalTime startTime = Constants.EXAMPLE_LOCAL_DATE_TIME.toLocalTime().minusSeconds(1L);
        final LocalTime endTime = startTime.plusSeconds(2).minusNanos(999_999_999);
        final Duration duration1 = Duration.between(startTime, endTime);
        Printer.printf("""
                        Duration between start time[%s] and end time[%s]:
                        \tduration[%s], seconds[%s] and nanos[%s]""",
                startTime, endTime, duration1, duration1.getSeconds(), duration1.getNano());

        final Duration duration2 = Duration.parse("P1DT2H3M4.005054321S");
        Printer.printf("Duration[%s]: seconds(total)[%s]"
                       + "\tdays[%s], hours[%s], minutes[%s], seconds[%s], millis[%s], nanos[%s]%n", duration2,
                Utils.formatNumber(duration2.toSeconds()), duration2.toDaysPart(), duration2.toHoursPart(),
                duration2.toMinutesPart(), duration2.toSecondsPart(), duration2.toMillisPart(),
                Utils.formatNumber(duration2.toNanosPart()));

        final LocalDate startDate = Constants.EXAMPLE_LOCAL_DATE;
        final LocalDate endDate = startDate.plusMonths(1).plusWeeks(1).minusDays(1);
        final Period period1 = Period.between(startDate, endDate);
        Printer.printf("Period between start date[%s] and end date[%s]: period[%s] or months[%s] and days[%s]",
                startDate, endDate, period1, period1.getMonths(), period1.getDays());

        final Period period2 = Period.of(1, 2, 3);
        Printer.printf("Period[%s]: months(total)[%d]%n\tyears[%d], months[%d], days[%d]", period2,
                period2.toTotalMonths(), period2.getYears(), period2.getMonths(), period2.getDays());
        Printer.printHor();
    }

    /**
     * Adds to instant or subtract from instant.
     */
    public void addToOrSubtractFromInstant() {

        final Instant base = LocalDateTime.of(2000, 1, 1, 0, 0).toInstant(ZoneOffset.UTC);
        final Duration days = Duration.ofDays(1);
        final Duration hours = Duration.ofHours(2);
        final Duration minutes = Duration.ofMinutes(3);
        final Duration seconds = Duration.ofSeconds(4);
        final Duration millis = Duration.ofMillis(5);
        final Duration micros = Duration.of(6, ChronoUnit.MICROS);
        final Duration nanos = Duration.ofNanos(7);
        final Instant earlier = base.minus(days).minus(hours).minus(minutes).minus(seconds)
                .minus(millis).minus(micros).minus(nanos);
        final Instant later = base.plus(days).plus(hours).plus(minutes).plus(seconds)
                .plus(millis).plus(micros).plus(nanos);
        Printer.printf("▲▲▲ earlier[%s] ▲▲▲", earlier);
        Printer.printf("       base[%s]", base.plus(Duration.ofNanos(1)));
        Printer.printf("▼▼▼   later[%s] ▼▼▼", later);

        final LocalDate baseDate = LocalDate.ofInstant(base, ZoneId.systemDefault());
        final LocalDate earlierDate = LocalDate.ofInstant(earlier, ZoneId.systemDefault());
        final LocalDate laterDate = LocalDate.ofInstant(later, ZoneId.systemDefault());
        Printer.printf("Duration between    base and later[%s], period between    base and later[%s]",
                Duration.between(base, later), Period.between(baseDate, laterDate));
        Printer.printf("Duration between earlier and later[%s], period between earlier and later[%s]",
                Duration.between(earlier, later), Period.between(earlierDate, laterDate));

        Printer.printf("%nFar future [%s]",
                ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
                        .plus(1L, ChronoUnit.MILLENNIA)
                        .plus(2L, ChronoUnit.CENTURIES)
                        .plus(3L, ChronoUnit.DECADES)
                        .plusYears(4L)
                        .plusMonths(5L - 1L)
                        .plusDays(6L - 1L)
                        .plusHours(7L)
                        .plusMinutes(8L)
                        .plusSeconds(9L)
                        .plus(1, ChronoUnit.MILLIS)
                        .plus(2, ChronoUnit.MICROS)
                        .plusNanos(3));
        Printer.printHor();
    }

    /**
     * Fragmentizes time.
     */
    public void fragmentizeTime() {

        Printer.printf("Number of 12 minute periods in 4 hours[%d]",
                Duration.ofHours(4).dividedBy(Duration.ofMinutes(12)));
        Printer.print("The clocks, which ticks only per the specified duration:");
        final Instant before = Instant.now();
        final Clock clock3 = Clock.tick(Clock.systemDefaultZone(), Duration.ofSeconds(3));
        final Instant instant3 = clock3.instant();
        final Clock clock4 = Clock.tick(Clock.systemDefaultZone(), Duration.ofSeconds(4));
        final Instant instant4 = clock4.instant();
        final Clock clock5 = Clock.tick(Clock.systemDefaultZone(), Duration.ofSeconds(5));
        final Instant instant5 = clock5.instant();
        final Instant after = Instant.now();
        Printer.printf("Time before     [%s]", Constants.INSTANT_AS_TIME_FUN.apply(before));
        Printer.printf("Clock tick 3 sec[%s]", Constants.INSTANT_AS_TIME_FUN.apply(instant3));
        Printer.printf("Clock tick 4 sec[%s]", Constants.INSTANT_AS_TIME_FUN.apply(instant4));
        Printer.printf("Clock tick 5 sec[%s]", Constants.INSTANT_AS_TIME_FUN.apply(instant5));
        Printer.printf("Time after      [%s]", Constants.INSTANT_AS_TIME_FUN.apply(after));
        Printer.printHor();
    }
}