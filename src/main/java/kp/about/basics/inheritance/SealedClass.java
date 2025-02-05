package kp.about.basics.inheritance;

/**
 * The non-subclassable, <b>sealed</b> class.
 */
public abstract sealed class SealedClass implements SealedInterface permits PermittedSubclassNonSealed {

    /**
     * Constructor.
     * The access modifier 'protected' prevents direct instantiation while allowing subclasses to call this constructor.
     */
    protected SealedClass() {
    }
}