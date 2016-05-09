package util.edm;

import models.collection.CollectionRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by Josh on 5/9/16.
 */
public class EDM {
    private CollectionRecord cr;

    public EDM(CollectionRecord cr) {
        this.cr = cr;
    }

    public String createEDM() {
        String edm = null;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            docFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rdf = doc.createElementNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:RDF");
            rdf.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:edm", "http://www.europeana.eu/schemas/edm/");
            rdf.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:dc", "http://purl.org/dc/elements/1.1/");
            rdf.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:dcterms", "http://purl.org/dc/terms/");
            rdf.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:skos", "http://www.w3.org/2004/02/skos/core#");
            rdf.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ore", "http://www.openarchives.org/ore/terms/");

            doc.appendChild(rdf);

            /* providedCHO */
            Element providedCHO = doc.createElement("edm:providedCHO");
            rdf.appendChild(providedCHO);

            /* aggregation */
            Element aggregation = doc.createElement("ore:Aggregation");
            rdf.appendChild(aggregation);

            /* providedCHO elements */
            this.createElement(doc, providedCHO, "dc:title", cr.getTitle());
            this.createElement(doc, providedCHO, "edm:type", cr.getType());
            this.createElements(doc, providedCHO, "edm:language", cr.getLanguages());
            this.createElement(doc, providedCHO, "dc:description", cr.getDescription());
            this.createElements(doc, providedCHO, "dc:subject", cr.getSubjects());
            this.createElements(doc, providedCHO, "dc:subject", cr.getHopeTags());
            this.createElements(doc, providedCHO, "dc:coverage", cr.getCoverages());
            this.createElements(doc, providedCHO, "dcterms:spatial", cr.getSpatialCoverages());

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            edm = writer.toString();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tre) {
            tre.printStackTrace();
        }

        return edm;
    }

    private void createElements(Document doc, Element parent, String nodeName, List<String> values) {
        for (String value : values) {
            createElement(doc, parent, nodeName, value);
        }
    }

    private void createElement(Document doc, Element parent, String nodeName, String value) {
        if (value.length() > 0) {
            Element e = doc.createElement(nodeName);
            e.appendChild(doc.createTextNode(value));
            parent.appendChild(e);
        }
    }

}
