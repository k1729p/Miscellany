package kp.about.basics.inheritance;

/**
 * The not subclassable, <b>sealed</b> class.
 *
 */
public abstract sealed class SealedClass implements SealedInterface permits PermittedSubclassNonSealed {

	int field;

	/**
	 * Constructor.
	 * 
	 */
	protected SealedClass() {
		super();
	}
}