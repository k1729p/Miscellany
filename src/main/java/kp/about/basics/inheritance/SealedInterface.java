package kp.about.basics.inheritance;

/**
 * The not subclassable, <b>sealed</b> interface.
 * <p>
 * The class hierarchy:
 * <ol>
 * <li>{@link SealedInterface}
 * <ol>
 * <li>{@link PermittedRecord}
 * <li>{@link PermittedSubclassFinal}
 * <li>{@link SealedClass}
 * <ol>
 * <li>{@link PermittedSubclassNonSealed}
 * <ol>
 * <li>{@link PermittedSubclassSubclassFinal}
 * </ol>
 * </ol>
 * </ol>
 * </ol>
 */
public sealed interface SealedInterface permits SealedClass, PermittedRecord, PermittedSubclassFinal {
}