package kp.about.basics.inheritance;

/**
 * The non-subclassable, <b>sealed</b> interface.
 * <p>
 * The class hierarchy:
 * <ul>
 * <li>{@link SealedInterface}
 * <ul>
 * <li>{@link PermittedRecord}
 * <li>{@link PermittedSubclassFinal}
 * <li>{@link SealedClass}
 * <ul>
 * <li>{@link PermittedSubclassNonSealed}
 * <ul>
 * <li>{@link PermittedSubclassSubclassFinal}
 * </ul>
 * </ul>
 * </ul>
 * </ul>
 */
public sealed interface SealedInterface permits SealedClass, PermittedRecord, PermittedSubclassFinal {
}