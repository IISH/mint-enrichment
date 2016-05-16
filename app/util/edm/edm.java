package util.edm;

import models.collection.CollectionRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import util.rdf.MINTThesaurus;
import util.rdf.SKOSThesaurus;
import util.select.HopeTags;

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
            String providedCHOID = "#providedCHO_" + cr.getUuid();
            Element providedCHO = doc.createElement("edm:providedCHO");
            providedCHO.setAttribute("rdf:resource", providedCHOID);
            rdf.appendChild(providedCHO);

            /* aggregation */
            Element aggregation = doc.createElement("ore:Aggregation");
            aggregation.setAttribute("rdf:resource", "#aggregation_" + cr.getUuid());
            rdf.appendChild(aggregation);

            /* providedCHO elements */
            this.createElement(doc, providedCHO, "dc:title", cr.getTitle());
            this.createElement(doc, providedCHO, "edm:type", cr.getType());
            this.createElementsLanguages(doc, providedCHO, "dc:language", cr.getLanguages());
            this.createElement(doc, providedCHO, "dc:description", cr.getDescription());

            this.createElements(doc, providedCHO, "dc:subject", cr.getSubjects());
            this.createElementsSKOS(doc, providedCHO, "dc:subject",
                    "http://www.socialhistoryportal.org/themes#HopeThemes", cr.getHopeTags());

            this.createElements(doc, providedCHO, "dc:coverage", cr.getCoverages());
            this.createElements(doc, providedCHO, "dcterms:spatial", cr.getSpatialCoverages());
            this.createElements(doc, providedCHO, "dc:contributor", cr.getContributors());
            this.createElements(doc, providedCHO, "dc:creator", cr.getCreators());
            this.createElement(doc, providedCHO, "dcterms:alternative", cr.getAlternative());
            this.createElement(doc, providedCHO, "dc:date", cr.getDate());
            this.createElement(doc, providedCHO, "dc:format", cr.getFormat());
            this.createElement(doc, providedCHO, "dc:source", cr.getSource());
            this.createElement(doc, providedCHO, "dcterms:extent", cr.getExtent());
            this.createElement(doc, providedCHO, "dcterms:provenance", cr.getProvenance());
            this.createElement(doc, providedCHO, "dcterms:tableOfContents", cr.getDate());

            /* Aggregation elements */
            this.createElementWithResource(doc, aggregation, "edm:aggregatedCHO", "rdf:about", providedCHOID);
            this.createElement(doc, aggregation, "edm:provider", cr.getProvider());
            this.createElement(doc, aggregation, "edm:dataProvider", cr.getDataProvider());
            this.createElementWithResource(doc, aggregation, "edm:isShownAt", "rdf:resource", cr.getIsShownAt());
            this.createElementWithResource(doc, aggregation, "edm:isShownBy", "rdf:resource", cr.getIsShownBy());
            this.createElementWithResource(doc, aggregation, "edm:rights", "rdf:resource", cr.getRights());

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

    private void createElement(Document doc, Element parent, String nodeName, String value) {
        if (value.length() > 0) {
            Element e = doc.createElement(nodeName);
            e.appendChild(doc.createTextNode(value));
            parent.appendChild(e);
        }
    }

    private void createElements(Document doc, Element parent, String nodeName, List<String> values) {
        for (String value : values) {
            createElement(doc, parent, nodeName, value);
        }
    }

    private void createElementsSKOS(Document doc, Element parent, String nodeName, String conceptScheme, List<String> values) {
        String repository = "http://mint-sparql.socialhistoryportal.org/HopeThemes/sparql";

        SKOSThesaurus skos = new SKOSThesaurus(repository,
                null, null, "en", conceptScheme, true);

        for (String value : values) {
            if (!value.equals("")) {
                Element e = doc.createElement(nodeName);
                e.appendChild(doc.createTextNode(skos.getPrefLabel(value, "en")));
                e.setAttribute("rdf:about", value);
                parent.appendChild(e);
            }
        }
    }

    private void createElementsLanguages(Document doc, Element parent, String nodeName, List<String> values) {
        String repository = "http://mint-sparql.socialhistoryportal.org/HopeLanguagesAndCountries/sparql";
        String conceptScheme = "http://mint.image.ece.ntua.gr/Vocabularies/Languages/LangThesaurus";

        MINTThesaurus skos = new MINTThesaurus(repository,
                null, null, "en", conceptScheme, true);

        for (String value : values) {
            if (!value.equals("")) {
                Element e = doc.createElement(nodeName);
                e.appendChild(doc.createTextNode(skos.getLanguageCode(value, "en")));
                parent.appendChild(e);
            }
        }
    }

    private void createElementWithResource(Document doc, Element parent, String nodeName, String attribute, String value) {
        if (value.length() > 0) {
            Element e = doc.createElement(nodeName);
            e.setAttribute(attribute, value);
            parent.appendChild(e);
        }
    }
}
