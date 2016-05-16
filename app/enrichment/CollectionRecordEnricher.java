package enrichment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * An enricher that enriches document with a reference to a collection level record.
 */
public class CollectionRecordEnricher implements Enricher {
    private String collectionRecordId;

    /**
     * Creates an enricher that enriches an EDM record with a reference to the collection level record.
     *
     * @param collectionRecordId The reference to the collection level record.
     */
    public CollectionRecordEnricher(String collectionRecordId) {
        this.collectionRecordId = collectionRecordId;
    }

    /**
     * Updates the given EDM document to include an 'isPartOf' element with a reference to the collection level record.
     *
     * @param document The EDM document.
     */
    @Override
    public void enrich(Document document) {
        //document.getDocumentElement().setPrefix(NEW_METADATA_PREFIX);

        // Create the 'isPartOf' element
        Element isPartOfElement = document.createElement("dcterms:isPartOf");
        isPartOfElement.setAttribute("rdf:resource", collectionRecordId);

        // Add the new element to the document
        NodeList nodes = document.getElementsByTagNameNS("http://www.europeana.eu/schemas/edm/", "ProvidedCHO");
        if (nodes.getLength() > 0) {
            Element providedChoElem = (Element) nodes.item(0);
            providedChoElem.appendChild(isPartOfElement);
        }
    }
}
