package kp.about.basics;


import kp.utils.Printer;

import java.io.*;
import java.util.Base64;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * String pool is an example of the Flyweight Design Pattern.
 * <p>
 * The Java String Pool is stored in the Heap space.
 * </p>
 * <p>
 * The string interning process:
 * storing only one copy of each literal String in the pool.
 * </p>
 */
public class StringInterning {
    /**
     * Shows the identity hash codes for strings.
     */
    void showIdentityHashCodesForStrings() {

        // Using constructor always creates a new object in heap memory.
        final String constructed = new String("abc".getBytes()); //the same result gives "abc"
        /*-
         * When we create a String using literal – it is interned.
         * If string is absent in String Pool, it will create a new String object.
         * With 'String::intern' string object could be added to the pool.
         */
        final String literalLower = "abc"; // the same result gives "a" + "b" + "c"
        final String concatenated = "a".concat("b").concat("c");
        final String deserialized = serializeAndDeserialize(concatenated);
        // In 'String::toUpperCase' there is 'String:new'
        final String toUpper = literalLower.toUpperCase();
        final String literalUpper = "ABC";
        final String toLower = literalUpper.toLowerCase();
        final Set<String> set = new HashSet<>();
        set.add(concatenated);
        set.add(constructed);
        set.add(deserialized);
        set.add(literalLower);
        set.add(literalUpper);
        set.add(toLower);
        set.add(toUpper);
        Printer.printf("set%s", set);

        Printer.printf("""
                                                |-----------|------------------|
                                                | hash code | identityHashCode |
                        |---------------|-----------|  ▼▼▼  |     ▼▼▼    |------------|
                        | name          | to string |  ▼▼▼  |     ▼▼▼    |  interned  |
                        |---------------|-----------|-------|------------|------------|
                        | literal lower | %s       | %5d | %10d |            |
                        | constructed   | %s       | %5d | %10d | %10d |
                        | concatenated  | %s       | %5d | %10d | %10d |
                        | to lower      | %s       | %5d | %10d | %10d |
                        | deserialized  | %s       | %5d | %10d | %10d |
                        |               |           |       |            |            |
                        | literal upper | %s       | %5d | %10d |            |
                        | to upper      | %s       | %5d | %10d | %10d |
                        |---------------|-----------|-------|------------|------------|""",
                literalLower,
                literalLower.hashCode(),
                System.identityHashCode(literalLower),
                constructed,
                constructed.hashCode(),
                System.identityHashCode(constructed),
                System.identityHashCode(constructed.intern()),
                concatenated,
                concatenated.hashCode(),
                System.identityHashCode(concatenated),
                System.identityHashCode(concatenated.intern()),
                toLower,
                toLower.hashCode(),
                System.identityHashCode(toLower),
                System.identityHashCode(toLower.intern()),
                deserialized,
                deserialized.hashCode(),
                System.identityHashCode(deserialized),
                System.identityHashCode(deserialized.intern()),
                literalUpper,
                literalUpper.hashCode(),
                System.identityHashCode(literalUpper),
                toUpper,
                toUpper.hashCode(),
                System.identityHashCode(toUpper),
                System.identityHashCode(toUpper.intern())
        );
        Printer.printHor();
    }


    /**
     * Serializes and deserializes the string.
     *
     * @param string the string to serialize and deserialize.
     * @return deserialized string
     */
    private String serializeAndDeserialize(String string) {

        byte[] serializedBytes = new byte[0];
        // Serialization
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(string);
            objectOutputStream.flush();
            serializedBytes = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            if (e instanceof IOException exception) {
                Printer.printIOException(exception);
            } else {
                Printer.printException(e);
            }
            System.exit(1);
        }
        // Deserialization
        String deserialized = null;
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(serializedBytes))) {
            deserialized = Optional.ofNullable(objectInputStream.readObject())
                    .filter(String.class::isInstance).map(String.class::cast).orElse("");
        } catch (Exception e) {
            switch (e) {
                case IOException exception -> Printer.printIOException(exception);
                case ClassNotFoundException exception -> Printer.printException(exception);
                default -> Printer.printException(e);
            }
            System.exit(1);
        }
        showBytes(serializedBytes);
        return deserialized;

    }

    /**
     * Shows serialized and deserialized bytes.
     *
     * @param serializedBytes the serialized bytes
     */
    private void showBytes(byte[] serializedBytes) {
        // Bytes deserialization
        byte[] deserializedBytes = new byte[0];
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedBytes)) {
            deserializedBytes = byteArrayInputStream.readAllBytes();
        } catch (Exception e) {
            if (e instanceof IOException exception) {
                Printer.printIOException(exception);
            } else {
                Printer.printException(e);
            }
            System.exit(1);
        }
        Printer.printf("encoded to string: serialized bytes[%s], deserialized bytes[%s]",
                Base64.getEncoder().encodeToString(serializedBytes),
                Base64.getEncoder().encodeToString(deserializedBytes));
    }

}
