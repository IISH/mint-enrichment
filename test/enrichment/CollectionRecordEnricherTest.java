package enrichment;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import utils.XmlUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CollectionRecordEnricherTest {
    private static final String COLLECTION_RECORD_ID = "collection-record-id";
    private CollectionRecordEnricher collectionRecordEnricher;

    @Before
    public void initialize() {
        XMLUnit.setIgnoreWhitespace(true);
        this.collectionRecordEnricher = new CollectionRecordEnricher(COLLECTION_RECORD_ID);
    }

    @Test
    public void successfulEnrichmentTest() throws Exception {
        // Get the original EDM record
        String orgEDM = IOUtils.toString(CollectionRecordEnricherTest.class.getResourceAsStream("edm1.xml"), "UTF-8");
        Document document = XmlUtils.getDocument(orgEDM);

        // Perform the enrichment
        collectionRecordEnricher.enrich(document);

        // Grab the new XML string
        String newEDM = XmlUtils.getAsString(document);

        // There should be a difference
        XMLAssert.assertXMLNotEqual(orgEDM, newEDM);

        // Make sure the 'isPart' element was added, find out if the element ProvidedCHO exists
        document = XmlUtils.getDocument(newEDM);
        NodeList providedCHO = document.getElementsByTagNameNS("http://www.europeana.eu/schemas/edm/", "ProvidedCHO");
        assertTrue(providedCHO.getLength() > 0);

        // Make sure the 'isPart' element was added, find out if the element isPart exists
        NodeList isPartOf = ((Element) providedCHO.item(0)).getElementsByTagNameNS("http://purl.org/dc/terms/", "isPartOf");
        assertTrue(isPartOf.getLength() > 0);

        // Make sure the 'isPart' element was added, find out if the attribute resource has the correct value
        String attr = ((Element) isPartOf.item(0)).getAttributeNS("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "resource");
        assertEquals(attr, COLLECTION_RECORD_ID);
    }

    @Test
    public void unsuccessfulEnrichmentTest() throws Exception {
        // Get the original EDM record
        String orgEDM = IOUtils.toString(CollectionRecordEnricherTest.class.getResourceAsStream("edm2.xml"), "UTF-8");
        Document document = XmlUtils.getDocument(orgEDM);

        // Perform the enrichment
        collectionRecordEnricher.enrich(document);

        // Grab the new XML string
        String newEDM = XmlUtils.getAsString(document);

        // There should not be a difference
        XMLAssert.assertXMLEqual(orgEDM, newEDM);

        // Make sure the 'isPart' element was not added, find out if the element ProvidedCHO exists
        document = XmlUtils.getDocument(newEDM);
        NodeList providedCHO = document.getElementsByTagNameNS("http://www.europeana.eu/schemas/edm/", "ProvidedCHO");
        assertEquals(providedCHO.getLength(), 0);
    }
}
