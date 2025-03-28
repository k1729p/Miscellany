package kp.files;

import kp.utils.Printer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Reading the XML files.
 */
public class XmlFiles {

    /**
     * Reads the XML files.
     */
    public void readXmlFiles() {

        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.parse(ConstantsForFiles.EXAMPLE_XML_PATH.toString());
            Printer.printf("Document URI[%s]", document.getDocumentURI());

            final Element element = document.getDocumentElement();
            Printer.printf("Root element: name[%s]", element.getNodeName());
            final Node childNode = element.getChildNodes().item(1);
            Printer.printf("Child node: name[%s]", childNode.getNodeName());
            final NodeList nodeList = document.getElementsByTagName("third");
            Printer.printf("Node with tag 'third': name[%s]", nodeList.item(0).getNodeName());

            final XPathFactory xPathFactory = XPathFactory.newInstance();
            final XPath xPath = xPathFactory.newXPath();
            final XPathExpression xPathExpression = xPath.compile("/first/second/third/fourth/text()");
            final Node node = xPathExpression.evaluateExpression(document, Node.class);
            Printer.printf("Node from xpath expression: text[%s]", node.getTextContent());
        } catch (XPathExpressionException | ParserConfigurationException | SAXException e) {
            Printer.printException(e);
            System.exit(1);
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        final Predicate<String> predicate = Predicate.not(line -> line.contains("second>"));
        Printer.print("▼▼▼ XML fragment ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼");
        try (Stream<String> linesStream = Files.lines(ConstantsForFiles.EXAMPLE_XML_PATH)) {
            // show only xml file fragment
            linesStream.dropWhile(predicate).skip(1).takeWhile(predicate).forEach(Printer::print);
        } catch (IOException e) {
            Printer.printIOException(e);
            System.exit(1);
        }
        Printer.printEndLineOfTriangles();
        Printer.printHor();
    }
}
