package kp.about.basics;

/**
 * Demonstrates the concepts of Stack Memory and Heap Space.
 * <p>
 * JVM options for stack memory and heap space:
 * </p>
 * <ul>
 * <li>Increase the stack size using '-Xss'</li>
 * <li>Increase or decrease the heap memory size using '-Xmx' and '-Xms'</li>
 * </ul>
 * <p>
 * Errors:
 * </p>
 * <ul>
 * <li>If there is no space in stack memory: StackOverflowError</li>
 * <li>If there is no space in heap memory: OutOfMemoryError</li>
 * </ul>
 */
public class StackMemoryVersusHeapSpace {

    /**
     * This method demonstrates the storage of reference variables and objects in stack and heap memory.
     * <p>
     * Stack Memory:
     * </p>
     * <ul>
     * <li>Stores method call frames, including local variables and reference variables.</li>
     * <li>Each thread has its own stack, which helps in ensuring thread safety.</li>
     * </ul>
     * <p>
     * Heap Space:
     * </p>
     * <ul>
     * <li>Used for dynamically allocated memory where objects created with new are stored.</li>
     * <li>Shared among all threads in the application.</li>
     * </ul>
     * <p>
     * Reference Variables:
     * </p>
     * <ul>
     * <li>When a reference variable is assigned a new object, it points to that object in heap space.</li>
     * </ul>
     */
    void referenceVariablesAndStackMemoryAndHeapSpace() {
        /*-
         * Reference variables are stored in stack memory.
         * The following are reference type local variables (not primitive types).
         */
        final Object refVar1;
        final Object refVar2;
        final Object refVar3;
        /*-
         * The created objects are stored in heap space.
         * All three reference variables point to the same object.
         */
        refVar3 = refVar2 = refVar1 = new Object();
        if (refVar1.hashCode() == 0 && refVar2.hashCode() == 0 && refVar3.hashCode() == 0) {
            // This should never happen. Logic only to silence IDEA warnings.
            System.exit(1);
        }
    }
}
