package kp.regex;

import kp.Constants;
import kp.utils.Printer;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The wrapper for the text replacing with the {@link Matcher}.
 */
public class ReplacingWithMatcher {

    /**
     * Private constructor to prevent instantiation.
     */
    private ReplacingWithMatcher() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Replaces all with the {@link Matcher} in single text line.
     */
    static void replaceAllInSingleLine() {
        /*
         * (^\\w+) - Matches a word at the beginning of the line.
         * (?<=▼)\\w+ - Matches words that follow the character '▼'.
         * \\w+(?=▲) - Matches words that precede the character '▲'.
         * (\\w+$) - Matches a word at the end of the line.
         */
        final Pattern pattern = Pattern.compile("(?x) (^\\w+) | (?<=▼)\\w+ | \\w+(?=▲) | (\\w+$)");
        String text = "abc-defg-hij▲klmnop▼qrs-tuvw-xyz";
        Printer.printf("Before replacements:  text original[%s]", text);

        final Matcher matcher = pattern.matcher(text);
        final Function<MatchResult, String> replacer1 = mr -> mr.group().toUpperCase();
        text = matcher.replaceAll(replacer1);
        Printer.printf("1st text replacement: text replaced[%s]", text);

        matcher.reset(text);
        final Function<MatchResult, String> replacer2 = mr -> new StringBuilder(mr.group()).reverse().toString();
        text = matcher.replaceAll(replacer2);
        Printer.printf("2nd text replacement: text replaced[%s]", text);
        Printer.printHor();
    }

    /**
     * Replaces all with the {@link Matcher} in many text lines.
     */
    static void replaceAllInMultiline() {

        // Thu Dec 31 23:59:59 CET 2099
        final Date exampleDate = Date.from(Constants.EXAMPLE_ZONED_DATE_TIME.toInstant());
        final String multilineText = IntStream.rangeClosed(1, 3).boxed().map(_ -> exampleDate).map(Date::toString)
                .collect(Collectors.joining(System.lineSeparator()));
		/*-
		'\p{L}' - A single code point in the category 'letter', POSIX character class 'any letter'.
		'\P{L}' - A single code point not in the category 'letter' (negation).
		'\p{Lu}' - An uppercase letter.
		'\R' - A linebreak matcher.
		 */
        final List<Pattern> patternList = Stream.of(
                /*-
                 * 0. Simple
                 * The group '((?:\w+\s?){3})' is capturing. Inside it, the group '(?:\w+\s?)' is non-capturing.
                 * The group '((?:\d+:?){3})' is capturing. Inside it, the group '(?:\d+:?)' is non-capturing.
                 */
                "((?:\\w+\\s?){3}) \\s ((?:\\d+:?){3}) .+ (\\d{4})",
                /*-
                 * 1. With lookbehind and lookahead
                 * Assigns time seconds to one of the three capturing groups.
                 * Lookahead and lookbehind groups are non-capturing. They assert a match without consuming characters.
                 */
                "(?<=:\\d) (?:([012]) | ([3456]) | ([789])) (?=\\s)",
                /*-
                 * 2. With linebreak sequence in the capturing group
                 */
                "(\\p{Alpha}{3}) \\s (\\p{Digit}{4} \\R \\p{Alpha}{3}) \\s (\\p{Alpha}{3})",
                /*-
                 * 3. The 'dot all' mode and back references
                 */
                "(?s) (\\p{Lu}\\p{L}{2}) .+ (\\p{Lu}\\p{L}{2}) \\s \\1 .+ (\\2 \\s \\1)",
                /*-
                 * 4. Nested capturing groups
                 */
                "   (((\\d{2}) : \\d{2}) : \\d{2})   ",
                /*-
                 * 5. Like pattern 4 above but only capturing groups
                 */
                ".* (((\\d{2}) : \\d{2}) : \\d{2}) .*"
        ).map(arg -> Pattern.compile(arg, Pattern.COMMENTS)).toList();

        for (int i = 0; i < patternList.size(); i++) {
            Printer.printf("i[%d], pattern[%s]", i, patternList.get(i));
            final Matcher matcher = patternList.get(i).matcher(multilineText);
            int j = 1;
            while (matcher.find()) {
                Printer.printf("i[%d], match[%d], start index[%2d], end index[%2d]:", i, j++, matcher.start(),
                        matcher.end());
                for (int k = 1; k <= matcher.groupCount(); k++) {
                    Printer.printf("\tgroup(%d)[%s]", k, matcher.group(k));
                }
            }
            Printer.printf("i[%d], multiline text before replacement:%n%s", i, multilineText);
            Printer.printf("i[%d], multiline text after  replacement:%n%s", i, matcher.replaceAll("[$1][$2][$3]"));
            Printer.printHor();
        }
    }

    /**
     * Appends the replacement using the {@link Matcher}.
     */
    static void appendReplacement() {

        final String text = "AAA=keyword1234=BBB=keyword5678=CCC";
        Printer.print(text);
        final Pattern pattern = Pattern.compile("=keyword\\d{4}=");
        final Matcher matcher = pattern.matcher(text);

        final StringBuffer strBuf = new StringBuffer();
        while (matcher.find()) {
            Printer.printf("► matcher.group()[%s]", matcher.group());
            matcher.appendReplacement(strBuf, "-REPLACEMENT-");
            Printer.printObject(strBuf);
        }
        matcher.appendTail(strBuf);
        Printer.printObject(strBuf);
        Printer.printHor();
    }

}
