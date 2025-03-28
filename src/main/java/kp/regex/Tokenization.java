package kp.regex;

import kp.Constants;
import kp.utils.Printer;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The wrapper for splitting texts with the {@link Pattern} or with the
 * {@link Scanner}.
 * <p>
 * The <i>lexical analysis</i>, <i>lexing</i> or <i>tokenization</i>: the
 * process of converting a sequence of <i>characters</i> into a sequence of
 * <i>lexical tokens</i>.
 */
public class Tokenization {

    /**
     * Private constructor to prevent instantiation.
     */
    private Tokenization() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Splits the text with the {@link Pattern} around matches.
     * <p>
     * The 'StringTokenizer' class is a legacy!
     */
    static void tokenizeWithPattern() {

        final String textForSplit = "aBBBcB";
        final Pattern pattern = Pattern.compile("B");
        Printer.printf("Text for split[%s], regex[%s]", textForSplit, pattern.pattern());
        Printer.print("|1|2|3|4|5|");
        Printer.print("| B B B B |  (matched delimiters)");
        Printer.print("|a|_|_|c|_|");
        Printer.print("");

        // 'split(regex)' <- this method works as if by invoking 'split(regex, 0)'
        for (int limit = 0; limit < 8; limit++) {
            final StringBuilder strBld = new StringBuilder();
            final String[] tokens = pattern.split(textForSplit, limit);
            strBld.append(String.format("limit[%d], tokens:", limit));
            for (String token : tokens) {
                strBld.append(String.format(" [%s]", token));
            }
            Printer.printObject(strBld);
        }
        Printer.printHor();
    }

    /**
     * Splits the text with the {@link Scanner} around matches.
     */
    static void tokenizeWithScanner() {

        // Thu Dec 31 23:59:59 CET 2099
        final Date exampleDate = Date.from(Constants.EXAMPLE_ZONED_DATE_TIME.toInstant());
        final String multilineText = IntStream.rangeClosed(1, 3).boxed().map(_ -> exampleDate).map(Date::toString)
                .collect(Collectors.joining(System.lineSeparator()));
        Printer.printf("Multiline text for scanners:%n%s%n", multilineText);

        // '\p{L}' - POSIX character class 'any letter'
        final String delimiter1 = " \\p{L}+\\s";// "\\s\\p{L}+\\s" gives different result
        Printer.printf("Scanner 'next', delimiter[%s]:", delimiter1);
        try (Scanner scanner = new Scanner(multilineText)) {
            scanner.useDelimiter(delimiter1);
            int i = 1;
            while (scanner.hasNext()) {
                Printer.printf("  (%d) [%s]", i++, scanner.next());
            }
        }
        Printer.print("");

        final String delimiter2 = "\\s|:";
        Printer.printf("Scanner 'next', delimiter[%s], first line 'non integer', second line 'integer':", delimiter2);
        try (Scanner scanner = new Scanner(multilineText)) {
            scanner.useDelimiter(delimiter2);
            int i = 1;
            StringBuilder strBldNonInt = new StringBuilder();
            StringBuilder strBldInt = new StringBuilder();
            while (scanner.hasNext()) {
                if (scanner.hasNextInt()) {
                    strBldInt.append(String.format("(%d) [%s] ", i++, scanner.next()));
                } else {
                    strBldNonInt.append(String.format("(%d) [%s] ", i++, scanner.next()));
                }
            }
            Printer.printObject(strBldNonInt);
            Printer.printObject(strBldInt);
        }
        Printer.print("");

        final String delimiter3 = "\\s+|:|\\d+";
        final Set<String> tokenSet;
        try (Scanner scanner = new Scanner(multilineText)) {
            tokenSet = scanner
                    .useDelimiter(delimiter3)
                    .tokens()
                    .filter(Predicate.not(String::isEmpty))
                    .collect(Collectors.toSet());
        }
        Printer.printf("Scanner 'tokens', delimiter[%s], tokenSet%s%n", delimiter3, tokenSet);

        final Pattern pattern = Pattern.compile(
                "(?x) (\\w{3} \\s \\w{3} \\s \\d{2}) \\s (\\d{2} : \\d{2} : \\d{2}) .+ (\\d{4})");
        Printer.printf("Scanner 'findInLine', regex pattern[%s]:", pattern.pattern());
        try (Scanner scanner = new Scanner(multilineText)) {
            int lineNumber = 1;
            boolean flag = true;
            while (flag) {
                Printer.printf("  line number[%s]", lineNumber++);
                if (Objects.isNull(scanner.findInLine(pattern))) {
                    flag = false;
                    continue;
                }
                final MatchResult matchResult = scanner.match();
                for (int i = 1; i <= matchResult.groupCount(); i++) {
                    Printer.printf("    (%d) [%s]", i, matchResult.group(i));
                }
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                } else {
                    flag = false;
                }
            }
        }
        Printer.printHor();
    }

    /**
     * Splits the text with the {@link Scanner} around matches.
     * <p>
     * The text is read from Example Domain URL
     * <a href="http://example.org/">http://example.org/</a>
     */
    static void tokenizeWithScannerTextFromUrl() {

        /*-
         * Boundary matcher '\Z': the end of the input but for the final terminator, if any.
         * With '\Z' delimiter, it reads the entire file at once.
         */
        final String delimiter = "\\Z";
        final Pattern pattern = Pattern.compile(".*Example Domain.*");
        try {
            final URL url = URI.create("http://example.org/").toURL();
            Printer.printf("Scanner 'findAll', delimiter[%s], regex pattern[%s], url[%s]:", delimiter,
                    pattern.pattern(), url.toExternalForm());
            final URLConnection urlConnection = url.openConnection();
            Printer.printTopBorderLine();
            try (Scanner scanner = new Scanner(urlConnection.getInputStream())) {
                scanner.useDelimiter(delimiter)
                        .findAll(pattern).map(MatchResult::group).forEach(Printer::print);
            }
            Printer.printBottomBorderLine();
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        Printer.printHor();
    }
}
