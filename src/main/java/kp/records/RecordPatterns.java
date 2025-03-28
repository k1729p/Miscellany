package kp.records;

import kp.utils.Printer;

/**
 * Research the record patterns.
 */
public class RecordPatterns {
    /**
     * Represents one circle.
     *
     * @param point the point
     * @param color the color
     */
    private record Circle(Point point, Color color) {
    }

    /**
     * Represents two circles.
     *
     * @param firstCircle  the first circle
     * @param secondCircle the second circle
     */
    private record TwoCircles(Circle firstCircle, Circle secondCircle) {
    }

    /**
     * Represents three circles.
     *
     * @param firstCircle  the first circle
     * @param secondCircle the second circle
     * @param thirdCircle  the third circle
     */
    private record ThreeCircles(Circle firstCircle, Circle secondCircle, Circle thirdCircle) {
    }

    /**
     * Represents the point with coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    private record Point(int x, int y) {
        // Compact constructor declaration
        Point {
            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("Invalid dimensions: %d, %d".formatted(x, y));
            }
        }
    }

    /**
     * Represents the color.
     */
    private enum Color {
        RED, GREEN, BLUE
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private RecordPatterns() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Processes the record patterns.
     */
    public static void processRecordPatterns() {

        Printer.printf("Record patterns using 'instanceof'.");
        show(new ThreeCircles(new Circle(new Point(11, 11), Color.RED),
                new Circle(new Point(22, 22), Color.GREEN),
                new Circle(new Point(33, 33), Color.BLUE)));
        Printer.printHor();
        Printer.printf("Record patterns using 'switch'.");
        showUsingSwitch(new Circle(new Point(1, 1), Color.RED));
        showUsingSwitch(new TwoCircles(new Circle(new Point(1, 1), Color.RED),
                new Circle(new Point(2, 2), Color.GREEN)));
        showUsingSwitch(new ThreeCircles(new Circle(new Point(1, 1), Color.RED),
                new Circle(new Point(2, 2), Color.GREEN),
                new Circle(new Point(3, 3), Color.BLUE)));
        Printer.printHor();
    }

    /**
     * Displays the object content.
     *
     * @param object the object
     */
    private static void show(Object object) {

        if (object instanceof ThreeCircles(
                Circle(Point _, Color firstColor),
                Circle(Point _, Color secondColor),
                Circle(Point _, Color thirdColor)
        )) {
            Printer.printf("Three circles: 1st color[%s], 2nd color[%s], 3rd color[%s]",
                    firstColor, secondColor, thirdColor);
        }
    }

    /**
     * Displays the object content using switch logic.
     *
     * @param object the object
     */
    private static void showUsingSwitch(Object object) {

        final String message = switch (object) {
            case Circle(Point _, Color firstColor) -> "One circle:    1st color[%s]".formatted(firstColor);

            case TwoCircles(Circle(Point _, Color firstColor), Circle(Point _, Color secondColor)) ->
                    "Two circles:   1st color[%s], 2nd color[%s]".formatted(firstColor, secondColor);

            case ThreeCircles(
                    Circle(Point _, Color firstColor), Circle(Point _, Color secondColor),
                    Circle(Point _, Color thirdColor)
            ) -> "Three circles: 1st color[%s], 2nd color[%s], 3rd color[%s]"
                    .formatted(firstColor, secondColor, thirdColor);

            case Long l -> "Unexpected Long %d".formatted(l);
            case Double d -> "Unexpected Double %f".formatted(d);
            case String s -> "Unexpected String %s".formatted(s);
            default -> "Unknown default";
        };
        Printer.print(message);
    }

}
