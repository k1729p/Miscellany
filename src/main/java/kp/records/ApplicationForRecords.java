package kp.records;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import kp.utils.Printer;

/**
 * The main launcher for records research.
 */
public class ApplicationForRecords {

	/**
	 * The hidden constructor.
	 */
	private ApplicationForRecords() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * The entry point for the application.
	 * 
	 * @param args the command-line arguments
	 */
	public static void main(String[] args) {

		Printer.printHor();
		processUnicodeScripts();
		processThings();
		processArticles();
		processCongruence();
		compareConstructing();
		processMultiplets();
		processAlphabet();
		processMoney();
		processBorders();
	}

	/**
	 * Processes Unicode scripts.
	 * 
	 */
	private static void processUnicodeScripts() {

		record UnicodeScripts() {
			static final List<String> UNI_LIST = Stream.of(Character.UnicodeScript.values())//
					.map(Enum::name).sorted().toList();
		}
		/**
		 * The canonical constructor for the scripts.<br>
		 * The results are sorted by the matched string.
		 * 
		 * @param line    the line
		 * @param matched the matched string
		 */
		record Scripts(String line, String matched) {
			static Scripts of(String line, Pattern pattern) {
				return new Scripts(line, pattern.matcher(line).replaceFirst("$1"));
			}

			static Comparator<Scripts> scriptsComparator() {
				return (scripts1, scripts2) -> scripts1.matched().compareTo(scripts2.matched());
			}
		}
		record EndingPatterns() {
			static final Pattern N = Pattern.compile("^.+_([^_]+N)$");
			static final Pattern I = Pattern.compile("^.+_([^_]+I)$");
		}

		UnicodeScripts.UNI_LIST.stream()//
				.filter(arg -> EndingPatterns.N.matcher(arg).find())//
				.map(str -> Scripts.of(str, EndingPatterns.N))//
				.sorted(Scripts.scriptsComparator())//
				.map(Scripts::line)//
				.forEach(Printer::print);
		Printer.printHor();

		UnicodeScripts.UNI_LIST.stream()//
				.filter(arg -> EndingPatterns.I.matcher(arg).find())//
				.map(str -> Scripts.of(str, EndingPatterns.I))//
				.sorted(Scripts.scriptsComparator())//
				.map(Scripts::line)//
				.forEach(Printer::print);
		Printer.printHor();
	}

	/**
	 * Processes things.
	 * 
	 */
	private static void processThings() {

		record ThingA(String label, int numA) {
		}
		record ThingB(ThingA thingA, int numB) {
		}
		record ThingC(ThingB thingB, int numC) {
		}

		final AtomicInteger atomicA = new AtomicInteger();
		final List<ThingA> listA = Stream.of("ABCDE".split("")).parallel()
				.map(label -> new ThingA(label, atomicA.incrementAndGet())).toList();

		final AtomicInteger atomicB = new AtomicInteger();
		final List<ThingB> listB = listA.stream().parallel().map(th -> new ThingB(th, atomicB.incrementAndGet()))
				.toList();

		final AtomicInteger atomicC = new AtomicInteger();
		final List<ThingC> listC = listB.stream().parallel().map(th -> new ThingC(th, atomicC.incrementAndGet()))
				.toList();

		final Consumer<ThingC> consumer = th -> Printer.printf("name[%s], numA[%5s], numB[%5s], numC[%5s]",
				th.thingB.thingA.label, "*".repeat(th.thingB.thingA.numA), "*".repeat(th.thingB.numB),
				"*".repeat(th.numC));
		listC.forEach(consumer);
		Printer.print(". ".repeat(20));
		final List<ThingC> sublistSortedByNumA = listC.stream()
				.filter(th -> th.thingB.thingA.numA > 1 && th.thingB.thingA.numA < 5)
				.sorted((th1, th2) -> Integer.compare(th1.thingB.thingA.numA, th2.thingB.thingA.numA)).toList();
		Printer.print("             sorted");
		sublistSortedByNumA.forEach(consumer);
		Printer.print(". ".repeat(20));
		final List<ThingC> sublistSortedByNumB = listC.stream().filter(th -> th.thingB.numB > 1 && th.thingB.numB < 5)
				.sorted((th1, th2) -> Integer.compare(th1.thingB.numB, th2.thingB.numB)).toList();
		Printer.print("                          sorted");
		sublistSortedByNumB.forEach(consumer);
		Printer.print(". ".repeat(20));
		final List<ThingC> sublistSortedByNumC = listC.stream().filter(th -> th.numC > 1 && th.numC < 5)
				.sorted((th1, th2) -> Integer.compare(th1.numC, th2.numC)).toList();
		Printer.print("                                       sorted");
		sublistSortedByNumC.forEach(consumer);
		Printer.printHor();
	}

	/**
	 * Processes articles.
	 * 
	 */
	private static void processArticles() {

		record Article(String name, List<String> labels, AtomicInteger local) {
			Article(String name) {
				this(name, new ArrayList<>(), new AtomicInteger());
			}

			static final AtomicInteger global = new AtomicInteger();

			static Article touch(Article article) {
				final String label = String.format("%02d^%02d", global.incrementAndGet(),
						article.local.incrementAndGet());
				article.labels.add(label);
				return article;
			}
		}
		record ArticleLists() {
			static final List<Article> ABC = Stream.of("A", "B", "C").map(Article::new).map(Article::touch)
					.map(Article::touch).toList();
			static final List<Article> XYZ = Stream.of("X", "Y", "Z").map(Article::new).map(Article::touch)
					.map(Article::touch).toList();
		}

		ArticleLists.ABC.stream().map(Article::touch).map(Article::touch).forEach(Printer::printObject);
		Printer.print("");
		ArticleLists.XYZ.stream().map(Article::touch).map(Article::touch).forEach(Printer::printObject);
		Printer.printHor();
	}

	/**
	 * Processes congruence.
	 * 
	 */
	private static void processCongruence() {
		/*-
		 * Congruence: a − b = n * modulus
		 * 
		 * The modulo operator: dividend % divisor = remainder
		 */
		interface Congruence {
			int minuend();

			int subtrahend();

			static Congruence of(int[] array) {

				int difference = array[0] - array[1];
				if (difference != 0) {
					if (difference % 5 == 0) {
						return new Mod5(array[0], array[1]);
					} else if (difference % 3 == 0) {
						return new Mod3(array[0], array[1]);
					} else if (difference % 2 == 0) {
						return new Mod2(array[0], array[1]);
					}
				}
				return new Mod1(array[0], array[1]);
			}

			record Mod1(int minuend, int subtrahend) implements Congruence {
			}

			record Mod2(int minuend, int subtrahend) implements Congruence {
			}

			record Mod3(int minuend, int subtrahend) implements Congruence {
			}

			record Mod5(int minuend, int subtrahend) implements Congruence {
			}
		}
		final int[][] array = { { 10, 3 }, { 20, 12 }, { 30, 6 }, { 40, 20 }, { 50, 1 } };
		final List<Congruence> list = Arrays.asList(array).stream().map(Congruence::of).toList();

		Printer.print("All Congruence implementations:");
		list.forEach(Printer::printObject);
		Printer.print("Filtered Congruence implementations:");
		list.stream().filter(elem -> elem instanceof Congruence.Mod5).forEach(Printer::printObject);
		Printer.printHor();
	}

	/**
	 * Compare records constructing: top down versus bottom up.
	 * 
	 */
	private static void compareConstructing() {

		final Supplier<Stream<String>> streamSupplier = () -> Stream.of("one", "two", "three");

		record A(String label) {
			record B(A a) {
				record C(B b) {
					C(String label) {
						this(new B(new A(label)));
					}
				}
			}
		}
		record X(Y y) {

			X(String label) {
				this(new Y(new Y.Z(label)));
			}

			record Y(Z z) {
				record Z(String label) {
				}
			}
		}
		Printer.print("Using constructor 'A.B.C::new':");
		streamSupplier.get().map(A.B.C::new).forEach(Printer::printObject);
		Printer.print("");
		Printer.print("Using constructor 'X::new':");
		streamSupplier.get().map(X::new).forEach(Printer::printObject);
		Printer.printHor();
	}

	/**
	 * Processes multiplets.
	 * 
	 */
	private static void processMultiplets() {

		record Multiplet(Quadruplet quadruplet) {

			static Multiplet of(int value1, int value2, int value3, int value4) {
				return new Multiplet(//
						new Quadruplet(//
								new Doublet(//
										new Singlet(value1), //
										new Singlet(value2)), //
								new Doublet(//
										new Singlet(value3), //
										new Singlet(value4))));
			}
			record Quadruplet(Doublet doublet1, Doublet doublet2) {
			}

			record Doublet(Singlet singlet1, Singlet singlet2) {
			}

			record Singlet(int value) {
			}
		}
		Multiplet multiplet = Multiplet.of(1, 2, 3, 4);
		Printer.printf("Multiplet doublet1: %s", multiplet.quadruplet.doublet1);
		Printer.printf("Multiplet doublet2: %s", multiplet.quadruplet.doublet2);
		Printer.printHor();
	}

	/**
	 * Processes alphabet.
	 * 
	 */
	private static void processAlphabet() {

		record Source() {
			static final char FIRST = 65;// 'A'
			static final char LAST = 90;// 'Z'
			static final List<String> AL_LIST = IntStream.rangeClosed(FIRST, LAST).boxed()//
					.map(Character::toString).toList();
		}
		record Transformer(int modulus, String name, int score) {
			Transformer(int modulus) {
				this(modulus, null, 0);

			}

			Transformer computeScore(String name) {
				final int score = (name.codePointAt(0) - Source.FIRST) % modulus;
				return new Transformer(modulus, name, score);
			}

			static Comparator<Transformer> scoreComparator() {
				return Comparator.comparingInt(Transformer::score);
			}
		}
		record Finisher() {
			static List<String> sublist(List<String> list) {
				final int endIndex = list.indexOf(Character.toString(Source.LAST)) + 1;
				return endIndex <= list.size() - 1 ? list.subList(0, endIndex) : list;
			}
		}
		final Function<Transformer, List<String>> transformFun = transformer -> Source.AL_LIST.stream()//
				.map(transformer::computeScore)//
				.sorted(Transformer.scoreComparator())//
				.map(Transformer::name).toList();
		final Function<Transformer, List<String>> finishingFun = transformer -> Finisher
				.sublist(transformFun.apply(transformer));

		final Map<Integer, List<String>> targetMap = IntStream.rangeClosed(1, Source.LAST - Source.FIRST).boxed()
				.map(Transformer::new)//
				.collect(Collectors.toMap(Transformer::modulus, finishingFun));
		targetMap.entrySet()
				.forEach(entry -> Printer.printf("modulus[%02d], result%s", entry.getKey(), entry.getValue()));
		Printer.printHor();
	}

	/**
	 * Processes money.
	 * 
	 */
	private static void processMoney() {

		Printer.printf("Total: %s", Money.total(Money.MONEY_STREAM_SUP.get()));
		Printer.printf("Nearest to '6': %s", Money.nearest(Money.MONEY_STREAM_SUP.get(), new BigDecimal(6)));
		Printer.printf("Statistics: %s", Money.statistics(Money.MONEY_STREAM_SUP.get()));
		Printer.printf("Group by tier: %s", Money.groupByTier(Money.MONEY_STREAM_SUP.get()));
		Printer.printHor();
	}

	/**
	 * Processes borders.
	 * 
	 */
	private static void processBorders() {

		List.of(BorderAroundText.Light.of(), //
				BorderAroundText.Double.of(), //
				BorderAroundText.HeavySides.of(), //
				BorderAroundText.AllHeavy.of()//
		).forEach(border -> Printer.print(border.format("The text in the box.")));
		Printer.printHor();
	}

}