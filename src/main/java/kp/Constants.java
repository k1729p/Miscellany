package kp;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Function;

/**
 * The constants.
 */
public final class Constants {
    /**
     * The {@link ZonedDateTime} example value.
     */
    public static final ZonedDateTime EXAMPLE_ZONED_DATE_TIME = ZonedDateTime.parse("2099-12-31T23:59:59+01:00",
            DateTimeFormatter.ISO_ZONED_DATE_TIME);
    /**
     * The {@link LocalDate} example value.
     */
    public static final LocalDate EXAMPLE_LOCAL_DATE = EXAMPLE_ZONED_DATE_TIME.toLocalDate();
    /**
     * The {@link LocalDateTime} example value.
     */
    public static final LocalDateTime EXAMPLE_LOCAL_DATE_TIME = EXAMPLE_ZONED_DATE_TIME.toLocalDateTime();
    /**
     * Format the {@link Instant} as time.
     */
    public static final Function<Instant, String> INSTANT_AS_TIME_FUN = arg -> DateTimeFormatter
            .ofPattern("HH:mm:ss.SSS").withLocale(Locale.US).withZone(ZoneId.systemDefault()).format(arg);

    /**
     * Private constructor to prevent instantiation.
     */
    private Constants() {
        throw new IllegalStateException("Utility class");
    }
}
