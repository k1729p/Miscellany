package kp.records;

import kp.utils.Printer;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The main launcher for records research.
 */
public class ApplicationForRecords {

    /**
     * Private constructor to prevent instantiation.
     */
    private ApplicationForRecords() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The primary entry point for launching the application.
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
        processTasks();
        RecordPatterns.processRecordPatterns();
    }

    /**
     * Processes Unicode scripts.
     */
    private static void processUnicodeScripts() {
        /*-
         * Record to hold the list of Unicode scripts names.
         */
        record UnicodeScripts() {
            static final List<String> UNI_LIST = Stream.of(Character.UnicodeScript.values())
                    .map(Enum::name).sorted().toList();
        }
        /*-
         * Record to represent a script with its matched pattern.
         *
         * The canonical constructor for the scripts.
         * The results are sorted by the matched string (last part of the name) to group similar scripts together
         * (e.g., OLD_SOUTH_ARABIAN and OLD_NORTH_ARABIAN are both grouped under Arabian).
         *
         * Parameters: the line with Unicode script name, the matched string.
         */
        record Scripts(String line, String matched) {
            static Scripts of(String line, Pattern pattern) {
                return new Scripts(line, pattern.matcher(line).replaceFirst("$1"));
            }

            static Comparator<Scripts> scriptsComparator() {
                return Comparator.comparing(Scripts::matched);
            }
        }
        /*-
         * Record for the end of line patterns used in matching Unicode script names.
         */
        record SuffixPatterns() {
            // Pattern to match Unicode script names ending with 'AN'
            static final Pattern AN = Pattern.compile("^.+_([^_]+AN)$");
            // Pattern to match Unicode script names ending with 'VI'
            static final Pattern VI = Pattern.compile("^.+_([^_]+VI)$");
        }

        UnicodeScripts.UNI_LIST.stream()
                .filter(arg -> SuffixPatterns.AN.matcher(arg).find())
                .map(str -> Scripts.of(str, SuffixPatterns.AN))
                .sorted(Scripts.scriptsComparator())
                .map(Scripts::line)
                .forEach(Printer::print);
        Printer.printHor();

        UnicodeScripts.UNI_LIST.stream()
                .filter(arg -> SuffixPatterns.VI.matcher(arg).find())
                .map(str -> Scripts.of(str, SuffixPatterns.VI))
                .sorted(Scripts.scriptsComparator())
                .map(Scripts::line)
                .forEach(Printer::print);
        Printer.printHor();
    }

    /**
     * Processes things.
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
                .sorted(Comparator.comparingInt(th -> th.thingB.thingA.numA)).toList();
        Printer.print("             sorted");
        sublistSortedByNumA.forEach(consumer);
        Printer.print(". ".repeat(20));
        final List<ThingC> sublistSortedByNumB = listC.stream()
                .filter(th -> th.thingB.numB > 1 && th.thingB.numB < 5)
                .sorted(Comparator.comparingInt(th -> th.thingB.numB)).toList();
        Printer.print("                          sorted");
        sublistSortedByNumB.forEach(consumer);
        Printer.print(". ".repeat(20));
        final List<ThingC> sublistSortedByNumC = listC.stream().filter(th -> th.numC > 1 && th.numC < 5)
                .sorted(Comparator.comparingInt(th -> th.numC)).toList();
        Printer.print("                                       sorted");
        sublistSortedByNumC.forEach(consumer);
        Printer.printHor();
    }

    /**
     * Processes articles.
     */
    private static void processArticles() {

        record Article(String name, List<String> labels, AtomicInteger local) {
            Article(String name) {
                this(name, new ArrayList<>(), new AtomicInteger());
            }

            static final AtomicInteger global = new AtomicInteger();

            static Article touch(Article article) {
                final String label = "%02d^%02d".formatted(global.incrementAndGet(),
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
     */
    private static void processCongruence() {

        final int[][] array = {{10, 3}, {20, 12}, {30, 6}, {40, 20}, {50, 1}};
        final List<Congruence> list = Arrays.stream(array).map(Congruence::of).toList();

        Printer.print("All Congruence implementations:");
        list.forEach(Printer::printObject);
        Printer.print("Filtered Congruence implementations:");
        list.stream().filter(elem -> elem instanceof Congruence.Mod5).forEach(Printer::printObject);
        Printer.printHor();

        // useless code line added only to silent IDEA 'unused' warnings
        array[0] = new int[]{list.getFirst().minuend(), list.getFirst().subtrahend()};
    }

    /**
     * Compare records constructing: top down versus bottom up.
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
     */
    private static void processMultiplets() {

        record Multiplet(Quadruplet quadruplet) {

            /**
             * Creates a new multiplet from four integer values.
             *
             * @param value1 the first value
             * @param value2 the second value
             * @param value3 the third value
             * @param value4 the fourth value
             * @return the multiplet
             */
            static Multiplet of(int value1, int value2, int value3, int value4) {
                return new Multiplet(
                        new Quadruplet(
                                new Doublet(
                                        new Singlet(value1),
                                        new Singlet(value2)),
                                new Doublet(
                                        new Singlet(value3),
                                        new Singlet(value4))));
            }

            /**
             * Represents a quadruplet.
             *
             * @param doublet1 the first doublet
             * @param doublet2 the second doublet
             */
            record Quadruplet(Doublet doublet1, Doublet doublet2) {
            }

            /**
             * Represents a doublet.
             *
             * @param singlet1 the first singlet
             * @param singlet2 the second singlet
             */
            record Doublet(Singlet singlet1, Singlet singlet2) {
            }

            /**
             * Represents a singlet.
             *
             * @param value the value
             */
            record Singlet(int value) {
            }
        }
        final Multiplet multiplet = Multiplet.of(1, 2, 3, 4);
        Printer.printf("Multiplet doublet1: %s", multiplet.quadruplet.doublet1);
        Printer.printf("Multiplet doublet2: %s", multiplet.quadruplet.doublet2);
        Printer.printHor();
        // to switch off IDEA warnings (they propose 'inline value' action)
        Multiplet.of(5, 6, 7, 8);
    }

    /**
     * Processes the alphabet by transforming the letters and sorting them based on a computed score.
     */
    private static void processAlphabet() {
        /*-
         * A record to hold the source alphabet list.
         */
        record Source() {
            static final char FIRST = 65;// 'A'
            static final char LAST = 90;// 'Z'
            static final List<String> AL_LIST = IntStream.rangeClosed(FIRST, LAST).boxed()
                    .map(Character::toString).toList();
        }
        /*-
         * A record to transform and compute the score of each alphabet letter.
         *
         * @param modulus the modulus value
         * @param name    the name of the letter
         * @param score   the computed score
         */
        record Transformer(int modulus, String name, int score) {
            /**
             * Constructs a Transformer with only the modulus value.
             *
             * @param modulus the modulus value
             */
            Transformer(int modulus) {
                this(modulus, null, 0);
            }

            /**
             * Computes the score for a given letter name.
             *
             * @param name the name of the letter
             * @return a new Transformer with the computed score
             */
            Transformer computeScore(String name) {
                final int score = (name.codePointAt(0) - Source.FIRST) % modulus;
                return new Transformer(modulus, name, score);
            }

            /**
             * Comparator to compare Transformers based on their scores.
             *
             * @return the comparator
             */
            static Comparator<Transformer> scoreComparator() {
                return Comparator.comparingInt(Transformer::score);
            }
        }
        final Function<Transformer, List<String>> valueMapper = transformer -> Source.AL_LIST.stream()
                .map(transformer::computeScore)
                .filter(trans -> trans.score == 0)
                .sorted(Transformer.scoreComparator())
                .map(Transformer::name).toList();

        // Collecting the transformed results into a map
        final Map<Integer, List<String>> targetMap = IntStream.rangeClosed(1, Source.LAST - Source.FIRST).boxed()
                .map(Transformer::new)
                .collect(Collectors.toMap(Transformer::modulus, valueMapper));
        targetMap.forEach((key, value) -> Printer.printf("modulus[%02d], result%s", key, value));
        Printer.printHor();
    }

    /**
     * Processes money.
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
     */
    private static void processBorders() {

        Stream.of(BorderAroundText.Light.of(),
                BorderAroundText.Double.of(),
                BorderAroundText.HeavySides.of(),
                BorderAroundText.AllHeavy.of()
        ).map(border -> border.format("The text in the box.")).forEach(Printer::print);
        Printer.printHor();
    }

    /**
     * Processes callable and runnable tasks using a single-thread executor.
     */
    private static void processTasks() {
        /*-
         * A record representing a task that implements a {@link Runnable}.
         *
         * @param number the number
         * @param string the string
         */
        record RunnableTask(Number number, String string) implements Runnable {
            @Override
            public void run() {
                Printer.printf("run(): number[%s], string[%s]", number, string);
            }
        }
        /*-
         * A record representing a task that implements a {@link Callable}.
         *
         * @param number the number
         * @param string the string
         */
        record CallableTask(Number number, String string) implements Callable<String> {
            @Override
            public String call() {
                Printer.printf("call(): number[%s], string[%s]", number, string);
                return number + string;
            }
        }
        try (final ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            final Future<?> runFuture = executorService.submit(new RunnableTask(123, "abc"));
            Printer.printf("runnable task result[%s]", runFuture.get());

            final Future<String> callFuture = executorService.submit(new CallableTask(456L, "xyz"));
            Printer.printf("callable task result[%s]", callFuture.get());
        } catch (InterruptedException | ExecutionException | RuntimeException e) {
            Thread.currentThread().interrupt(); // Preserve interrupt status
            switch (e) {
                case InterruptedException interruptedException ->
                        Printer.printInterruptedException(interruptedException);
                case ExecutionException executionException -> Printer.printExecutionException(executionException);
                default -> Printer.printException(e);
            }
            System.exit(1);
        }
        Printer.printHor();
    }

    /**
     * The Congruence interface and its implementations.
     * Provides a way to create different congruence classes based on modulus.
     * <p>
     * Congruence: a − b = n * modulus<br>
     * The modulo operator: dividend % divisor = remainder
     * </p>
     */
    interface Congruence {
        int minuend();

        int subtrahend();

        /**
         * Creates an instance of a specific congruence class based on the difference of the array elements.
         *
         * @param array the array containing two integers
         * @return the specific congruence class instance
         */
        static Congruence of(int[] array) {

            final int difference = array[0] - array[1];
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

        /**
         * Mod1 congruence class.
         * Represents a congruence modulo 1.
         */
        record Mod1(int minuend, int subtrahend) implements Congruence {
        }

        /**
         * Mod2 congruence class.
         * Represents a congruence modulo 2.
         */
        record Mod2(int minuend, int subtrahend) implements Congruence {
        }

        /**
         * Mod3 congruence class.
         * Represents a congruence modulo 3.
         */
        record Mod3(int minuend, int subtrahend) implements Congruence {
        }

        /**
         * Mod5 congruence class.
         * Represents a congruence modulo 5.
         */
        record Mod5(int minuend, int subtrahend) implements Congruence {
        }
    }
}