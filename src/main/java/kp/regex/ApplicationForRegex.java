package kp.regex;

import kp.Constants;
import kp.utils.Printer;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The main launcher for regular expressions research.
 */
public class ApplicationForRegex {

    /**
     * Private constructor to prevent instantiation.
     */
    private ApplicationForRegex() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The primary entry point for launching the application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        Printer.printHor();

        showGreedyReluctantPossessiveQuantifiers();
        showNamedCapturingGroups();

        StreamFiltering.filterWithMatchedRegexPredicateOne();
        StreamFiltering.filterWithMatchedRegexPredicateTwo();
        StreamFiltering.filterWithPatternPredicates();

        ReplacingWithMatcher.replaceAllInSingleLine();
        ReplacingWithMatcher.replaceAllInMultiline();
        ReplacingWithMatcher.appendReplacement();

        Tokenization.tokenizeWithPattern();
        Tokenization.tokenizeWithScanner();
        Tokenization.tokenizeWithScannerTextFromUrl();
    }

    /**
     * Shows the usage of the quantifiers: greedy, reluctant, and possessive.
     * <p>
     * The Java Tutorials: <a href=
     * "https://docs.oracle.com/javase/tutorial/essential/regex/quant.html">Quantifiers</a>
     */
    private static void showGreedyReluctantPossessiveQuantifiers() {

        final String text = "abc---abc---abc";
        final Map<String, List<String>> dataMap = new TreeMap<>();
        dataMap.put("greedy quantifiers", List.of(".*abc", "abc.*"));
        dataMap.put("possessive quantifiers", List.of(".*+abc", "abc.*+"));
        dataMap.put("reluctant quantifiers", List.of(".*?abc", "abc.*?"));

        final Consumer<Entry<String, List<String>>> consumer = entry -> {
            final StringBuilder strBld = new StringBuilder(entry.getKey());
            final UnaryOperator<String> patternReporter = pattern -> {
                strBld.append("\n\tpattern[").append(pattern).append("], ");
                return pattern;
            };
            final UnaryOperator<Matcher> matcherReporter = matcher -> {
                strBld.append("matches[").append(matcher.matches()).append("]\n");
                strBld.append("\t\tinput text  [").append(text).append("]\n");
                if (!matcher.matches()) {
                    strBld.append("\t\tno output\n");
                }
                return matcher;
            };
            entry.getValue().stream()
                    .map(patternReporter)
                    .map(Pattern::compile)
                    .map(pattern -> pattern.matcher(text))
                    .map(matcherReporter)
                    .filter(Matcher::matches)
                    .map(mat -> mat.replaceAll("►$0◄"))
                    .forEach(str -> strBld.append("\t\toutput text[").append(str).append("]\n"));
            Printer.print(strBld.toString());
        };
        dataMap.entrySet().forEach(consumer);
        Printer.printHor();
    }

    /**
     * Shows the named-capturing groups.
     * <p>
     * The defined named-capturing groups:
     * <ul>
     *  <li>DayOfWeek</li>
     *  <li>Date</li>
     *  <ul>
     *    <li>Month</li>
     *    <li>Day</li>
     *  </ul>
     *  <li>Time</li>
     *  <ul>
     *    <li>Hour</li>
     *    <li>Minute</li>
     *    <li>Second</li>
     *  </ul>
     *  <li>TimeZone</li>
     *  <li>Year</li>
     * </ul>
     * </p>
     */
    private static void showNamedCapturingGroups() {
		/*-
		    /- DayOfWeek
		    |
		    |   |===|- Date
		    |   v   v
		    |   /- Month
		    |   |   /-Day
		    |   |   |
		    Thu Dec 31 23:59:59 CET 2099
		               |  |  |  |   |
		               |  |  |  |   \-Year
		               |  |  |  \- TimeZone
		               |  |  |
		               |  |  \- Second
		               |  \- Minute
		               \- Hour
		               ^  ^  ^
		               |==|==|- Time
		 */
        final String regex = """
                (?x)
                (?<DayOfWeek> \\S{3}) \\s
                (?<Date> (?<Month> \\S{3}) \\s (?<Day> \\S{2})) \\s
                (?<Time> (?<Hour> \\S{2}) : (?<Minute> \\S{2}) : (?<Second> \\S{2})) \\s
                (?<TimeZone> \\S{3,4}) \\s
                (?<Year> \\S{4})""";
        final Date exampleDate = Date.from(Constants.EXAMPLE_ZONED_DATE_TIME.toInstant());
        final Matcher matcher = Pattern.compile(regex).matcher(exampleDate.toString());

        Printer.print("From named capturing groups:");
        if (matcher.find()) {
            Printer.printf("\tyear[%s], month[%s], day[%s], date[%s], day of the week[%s]",
                    matcher.group("Year"), matcher.group("Month"), matcher.group("Day"),
                    matcher.group("Date"), matcher.group("DayOfWeek"));
            Printer.printf("\thour[%s], minute[%s], second[%s], time[%s], time zone[%s]",
                    matcher.group("Hour"), matcher.group("Minute"), matcher.group("Second"),
                    matcher.group("Time"), matcher.group("TimeZone"));
        }
        Printer.printHor();
    }

}
