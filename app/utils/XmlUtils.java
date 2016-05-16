package utils;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import play.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

/**
 * XML utilities.
 */
public class XmlUtils {
    private static final DocumentBuilder BUILDER;
    private static final Transformer TRANSFORMER;

    static {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            BUILDER = documentBuilderFactory.newDocumentBuilder();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            TRANSFORMER = transformerFactory.newTransformer();
            TRANSFORMER.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
        catch (ParserConfigurationException e) {
            Logger.error("Failed to initialize a document builder!", e);
            throw new RuntimeException(e);
        }
        catch (TransformerConfigurationException e) {
            Logger.error("Failed to initialize a transformer!", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses an XML document.
     *
     * @param xml The XML string.
     * @return The XML document.
     * @throws IOException
     * @throws SAXException
     */
    public static Document getDocument(String xml) throws IOException, SAXException {
        try {
            return BUILDER.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8"))));
        }
        catch (UnsupportedEncodingException e) {
            Logger.error("UTF-8 is not supported!", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes the XML document to a string.
     *
     * @param document The XML document.
     * @return The XML string.
     * @throws TransformerException
     */
    public static String getAsString(Document document) throws TransformerException {
        StringWriter writer = new StringWriter();
        TRANSFORMER.transform(new DOMSource(document), new StreamResult(writer));
        return writer.getBuffer().toString();
    }
}
