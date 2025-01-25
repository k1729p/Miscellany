package kp.about.basics.inheritance;

import kp.utils.Printer;

import java.util.List;

/**
 * Demonstrates pattern matching.
 */
public class PatternMatching {

    /**
     * Matches without any class cast.
     */
    public void withoutAnyCast() {

        final SealedInterface anonymousClass = new PermittedSubclassNonSealed() {
        };
        final List<SealedInterface> list = List.of(
                new PermittedRecord(),
                new PermittedSubclassFinal(),
                new PermittedSubclassNonSealed(),
                anonymousClass,
                new PermittedSubclassSubclassFinal());
        list.forEach(item -> {
            String name;
            switch (item) {
                case PermittedRecord permitted -> name = permitted.getClass().getSimpleName();
                case PermittedSubclassFinal permitted -> name = permitted.getClass().getSimpleName();
                case PermittedSubclassSubclassFinal permitted -> name = permitted.getClass().getSimpleName();
                case PermittedSubclassNonSealed permitted -> {
                    name = permitted.getClass().getSimpleName();
                    if (name.isEmpty()) {
                        name = "« empty name for anonymous »";
                    }
                }
                case null, default -> name = "unknown";
            }
            Printer.printf("simple class name[%s]", name);
        });
        Printer.printHor();
    }
}
