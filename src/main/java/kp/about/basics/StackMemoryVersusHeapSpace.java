package kp.about.basics;

/**
 * Demonstrates the concepts of Stack Memory and Heap Space.
 * <hr>
 * Stack Memory versus Heap Space
 * <ul>
 *  <li>Stack Memory
 *   <ul>
 *    <li>Used for static memory allocation.</li>
 *    <li>Stores method call frames, including local variables and reference variables.</li>
 *    <li>Each thread has its own stack, which helps in ensuring thread safety.</li>
 *   </ul>
 *  </li>
 * </ul>
 * <ul>
 *  <li>Heap Space
 *   <ul>
 *    <li>Used for dynamically allocated memory.</li>
 *    <li>Stores objects created with new.</li>
 *    <li>Shared among all threads in the application.</li>
 *   </ul>
 *  </li>
 * </ul>
 * <hr>
 * Local Variables versus Global Variables (Static Fields)
 * <ul>
 *  <li>Local Variables: Stored in the stack memory.
 *   <ul>
 *    <li>Primitive values are directly stored.</li>
 *    <li>Reference variables store the reference to objects in the heap space.</li>
 *    <li>Are allocated on the stack and automatically deallocated when the method call completes.</li>
 *   </ul>
 *  </li>
 *  <li>Global Variables: Stored in the method area within the heap memory.
 *   <ul>
 *    <li>The actual values for primitive types and the references for reference types are stored in the method area.</li>
 *    <li>The objects themselves are stored in the heap space.</li>
 *   </ul>
 *  </li>
 * </ul>
 * <hr>
 * JVM options for stack memory and heap space:
 * <ul>
 *  <li>Increase the stack size using '-Xss'</li>
 *  <li>Increase or decrease the heap memory size using '-Xmx' and '-Xms'</li>
 * </ul>
 * <hr>
 * Memory errors:
 * <ul>
 *  <li>If there is no space in stack memory: {@link StackOverflowError}</li>
 *  <li>If there is no space in heap memory: {@link OutOfMemoryError}</li>
 * </ul>
 * <hr>
 * Variable names itself are used by the compiler and are not stored in memory during runtime.
 */
public class StackMemoryVersusHeapSpace {

    private static final int GLOBAL_PRIMITIVE_VARIABLE = Integer.MAX_VALUE;
    private static final Object GLOBAL_REFERENCE_VARIABLE = new Object();

    /**
     * This method demonstrates the storage in stack memory and in heap memory.
     */
    void referenceVariablesAndStackMemoryAndHeapSpace() {
        /*-
         * Primitive type variables are stored in stack memory.
         * The actual value of the local variable of primitive type is stored in the stack memory,
         */
        final int localPrimitiveVariable;
        /*-
         * Reference variables are stored in stack memory.
         * When a reference variable is assigned a new object, it points to that object in heap space.
         * The following are reference type local variables (not primitive types).
         */
        final Object localReferenceVariableOne;
        final Object localReferenceVariableTwo;

        localPrimitiveVariable = Integer.MAX_VALUE;
        /*-
         * The created object is stored in heap space.
         * Both reference variables point to the same object.
         */
        localReferenceVariableTwo = localReferenceVariableOne = new Object();

        if (localReferenceVariableOne.hashCode() + localReferenceVariableTwo.hashCode() == localPrimitiveVariable &&
            GLOBAL_REFERENCE_VARIABLE.hashCode() == GLOBAL_PRIMITIVE_VARIABLE) {
            // This should never happen. Logic only to silence IDEA warnings.
            System.exit(1);
        }
    }
}
