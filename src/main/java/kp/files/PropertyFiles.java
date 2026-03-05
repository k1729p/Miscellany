package kp.files;

import kp.utils.Printer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.PropertyResourceBundle;
import java.util.function.Consumer;

/**
 * Reading the property files.
 */
public class PropertyFiles {

    /**
     * Reads properties.
     */
    public void readProperties() {

        Printer.printf("▼▼▼ properties from file[%s] ▼▼▼", ConstantsForFiles.EXAMPLE_PROPERTIES_PATH);
        try (InputStream inputStream = Files.newInputStream(ConstantsForFiles.EXAMPLE_PROPERTIES_PATH)) {
            final PropertyResourceBundle properties = new PropertyResourceBundle(inputStream);
            final Consumer<String> action = key -> Printer.printf("key[%s], value[%s]",
                    key, properties.getString(key));
            properties.getKeys().asIterator().forEachRemaining(action);
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        Printer.printEndLineOfTriangles();
        Printer.printHor();
    }
}