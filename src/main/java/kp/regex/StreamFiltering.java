package kp.regex;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import kp.utils.Printer;

/**
 * The wrapper for the text stream filtering.
 *
 */
public class StreamFiltering {

	/**
	 * The hidden constructor.
	 */
	private StreamFiltering() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Filters stream with matched regex {@link Predicate}. The 1st example.
	 * 
	 */
	static void filterWithMatchedRegexPredicateOne() {

		// OK is word without any letter except an uppercase letter (subtraction)
		final String regex = "(?x) [ \\p{L} && [^\\p{Lu}] ]+";
		Printer.printf("Regex: %s", regex);

		final List<String> sourceList = List.of("One", "two", "Three");
		Printer.printf("Source: %s", sourceList);

		final Predicate<String> predicate = Predicate.not(arg -> arg.matches(regex));
		Printer.printf("Result: %s", sourceList.stream().filter(predicate).toList());
		Printer.printHor();
	}

	/**
	 * Filters stream with matched regex {@link Predicate}. The 2nd example.
	 * 
	 */
	static void filterWithMatchedRegexPredicateTwo() {

		final List<String> sourceList = Stream.of(Character.UnicodeScript.values()).map(Enum::name).toList();
		Printer.printf("Source list created from Enum 'Character.UnicodeScript', list size[%d]%n", sourceList.size());

		final List<String> regexList = List.of(//
				".{21,}", /*- 21 chars or more */
				".{1,3}", /*- 3 chars or less */
				"OLD.*", /*- starts with OLD */
				".*(.)\\1.*" /*- double letter */
		);
		final Consumer<String> consumer = regex -> {
			final List<String> matchedList = sourceList.stream().filter(arg -> arg.matches(regex)).toList();
			Printer.printf("Regex[%s], matched list size[%2d], matched list:%n  %s", regex, matchedList.size(),
					matchedList);
		};
		regexList.forEach(consumer);
		Printer.printHor();
	}

	/**
	 * Filters stream with predicates created from {@link Pattern}.
	 * 
	 */
	static void filterWithPatternPredicates() {

		final List<String> textList = List.of("ABC_1", "АBC_2", "AВC_3", "ABС_4");
		Printer.print("The 2nd, 3rd, and 4th item of the text list contain homoglyphs.");
		textList.forEach(text -> {
			final StringBuilder strBld = new StringBuilder();
			strBld.append(String.format("text \"%s\" ► code points:", text));
			text.chars().forEach(ch -> strBld.append(String.format(" '%s'(%4d)", Character.toString(ch), ch)));
			Printer.printObject(strBld);
		});
		Printer.print("");
		/*-
		Predefined character class '\W'	- it is a non-word character.
		*/
		final List<Pattern> patternList = Stream.of("\\W", "\\w+", "\\w\\W\\w_\\d").map(Pattern::compile).toList();
		/*- 
		For 'asPredicate()' the string or any of its substrings should match the pattern. 
		It is like "s -> matcher(s).find()".
		
		For 'asMatchPredicate()' the entire string should match the pattern.
		It is like "s -> matcher(s).matches()".
		*/
		patternList.forEach(pattern -> {
			final List<String> resultList1 = textList.stream().filter(pattern.asPredicate()).toList();
			Printer.printf("Filtered with      'asPredicate' pattern[%9s], result list %s", pattern, resultList1);

			final List<String> resultList2 = textList.stream().filter(pattern.asMatchPredicate()).toList();
			Printer.printf("Filtered with 'asMatchPredicate' pattern[%9s], result list %s", pattern, resultList2);
		});
		Printer.printHor();
	}

}
