package kp.files;

import kp.files.visitors.VisitorWrapper;
import kp.utils.Printer;

/**
 * The main launcher for files research.
 */
public class ApplicationForFiles {

    /**
     * Private constructor to prevent instantiation.
     */
    private ApplicationForFiles() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        Printer.printHor();
        final FilesAndZipFiles filesAndZipFiles = new FilesAndZipFiles();
        filesAndZipFiles.readFiles();
        filesAndZipFiles.listFiles();
        filesAndZipFiles.readZipFiles();
        filesAndZipFiles.listFilesInZipFile();

        final PropertyFiles propertyFiles = new PropertyFiles();
        propertyFiles.readProperties();

        final TemporaryFilesAndZipFiles temporaryFilesAndZipFiles = new TemporaryFilesAndZipFiles();
        temporaryFilesAndZipFiles.writeAndReadTemporaryFiles();
        temporaryFilesAndZipFiles.writeAndReadTemporaryZipFiles();

        final TemporaryFilesWritingAndReading temporaryFilesWritingAndReading = new TemporaryFilesWritingAndReading();
        temporaryFilesWritingAndReading.writeAndReadBytes();
        temporaryFilesWritingAndReading.writeAndReadString();

        final XmlFiles xmlFiles = new XmlFiles();
        xmlFiles.readXmlFiles();

        final VisitorWrapper visitorWrapper = new VisitorWrapper();
        visitorWrapper.searchKeywordsInFiles();
    }
}