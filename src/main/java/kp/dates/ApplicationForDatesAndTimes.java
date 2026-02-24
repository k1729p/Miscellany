package kp.dates;

import kp.utils.Printer;

/**
 * The main class for the dates and times research.
 */
public class ApplicationForDatesAndTimes {

    /**
     * Private constructor to prevent instantiation.
     */
    private ApplicationForDatesAndTimes() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The primary entry point for launching the application.
     *
     */
    public static void main() {

        Printer.printHor();
        final DatesAndTimesAggregation datesAndTimesAggregation = new DatesAndTimesAggregation();
        datesAndTimesAggregation.aggregateLeapDays();
        datesAndTimesAggregation.aggregateOneYearSeconds();

        final DatesAndTimesChanging datesAndTimesChanging = new DatesAndTimesChanging();
        datesAndTimesChanging.formatDate();
        datesAndTimesChanging.convertDateToAndFro();
        datesAndTimesChanging.adjustDate();
        datesAndTimesChanging.queryTemporalObjects();
        datesAndTimesChanging.calculateAmountOfTimeBetween();
        datesAndTimesChanging.addToOrSubtractFromInstant();
        datesAndTimesChanging.fragmentizeTime();
    }

}
