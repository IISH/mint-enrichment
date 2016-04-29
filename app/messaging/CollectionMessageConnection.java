package messaging;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.typesafe.config.ConfigFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import play.Logger;
import play.inject.ApplicationLifecycle;
import utils.MongoDB;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Represents the collection message RabbitMQ connection.
 */
@Singleton
public class CollectionMessageConnection extends RabbitMQConnection<CollectionMessage> {
    private final static int BULK_SIZE = 1000;

    private final static String QUEUE_NAME = ConfigFactory.load().getString("rabbitmq.collectionRecord.queue");
    private final static String COLLECTION_NAME = ConfigFactory.load().getString("rabbitmq.collectionRecord.collection");
    private final static String ORG_METADATA_PREFIX = ConfigFactory.load().getString("rabbitmq.collectionRecord.originalMetadataPrefix");
    private final static String NEW_METADATA_PREFIX = ConfigFactory.load().getString("rabbitmq.collectionRecord.newMetadataPrefix");

    private final MongoDB mongoDB;
    private final DocumentBuilder documentBuilder;
    private final Transformer transformer;

    /**
     * Sets up a publisher and a consumer for the given collection message queue.
     *
     * @param lifecycle Required to add a stop hook which closes the connections.
     * @param mongoDB   Required to perform actions on the records in MongoDB for received collection messages.
     */
    @Inject
    public CollectionMessageConnection(ApplicationLifecycle lifecycle, MongoDB mongoDB) {
        super(lifecycle, QUEUE_NAME);
        this.mongoDB = mongoDB;

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
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
     * Updates all matching records to include a reference to the collection level record.
     *
     * @param message The received message with the required information to perform the updates.
     */
    @Override
    protected void onMessage(CollectionMessage message) {
        BasicDBObject query = new BasicDBObject();
        query.put("isPublished", true);
        query.put("datasetId", message.getSetId());
        query.put("namespace.prefix", ORG_METADATA_PREFIX);

        DBCollection collection = mongoDB.getDB().getCollection(COLLECTION_NAME);
        DBCursor cursor = collection.find(query);

        int counter = 0;
        BulkWriteOperation bulkWrite = collection.initializeUnorderedBulkOperation();

        while (cursor.hasNext()) {
            BasicDBObject record = (BasicDBObject) cursor.next();
            updateRecord(record, message.getCollectionRecordId(), bulkWrite);

            counter++;
            if (counter >= BULK_SIZE) {
                bulkWrite.execute();
                counter = 0;
                bulkWrite = collection.initializeUnorderedBulkOperation();
            }
        }

        if (counter > 0) {
            bulkWrite.execute();
        }
    }

    /**
     * Updates the given EDM record with a link to the collection level record.
     *
     * @param record             The record to update.
     * @param collectionRecordId The identifier of the collection level record.
     * @param bulkWrite          The MongoDB bulk operation to write the result to.
     */
    private void updateRecord(BasicDBObject record, String collectionRecordId, BulkWriteOperation bulkWrite) {
        String originalXml = (String) record.get("xmlRecord");
        String updatedXml = updateXml(originalXml, collectionRecordId);

        record.removeField("_id"); // New record, force new id
        record.put("xmlRecord", updatedXml);
        ((BasicDBObject) record.get("namespace")).put("prefix", NEW_METADATA_PREFIX);

        bulkWrite.insert(record);
    }

    /**
     * Updates the given EDM XML to include an 'isPartOf' element with a reference to the collection level record.
     *
     * @param xml                A string representing the EDM XML.
     * @param collectionRecordId The identifier of the collection level record.
     * @return A string representing the enriched EDM XML.
     */
    private String updateXml(String xml, String collectionRecordId) {
        try {
            Document document = documentBuilder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("UTF-8"))));
            document.getDocumentElement().setPrefix(NEW_METADATA_PREFIX);

            // Create the 'isPartOf' element
            Element isPartOfElement = document.createElement("dcterms:isPartOf");
            isPartOfElement.setAttribute("rdf:resource", collectionRecordId);

            // Add the new element to the document
            NodeList nodes = document.getElementsByTagNameNS("http://www.europeana.eu/schemas/edm/", "ProvidedCHO");
            if (nodes.getLength() > 0) {
                Element providedChoElem = (Element) nodes.item(0);
                providedChoElem.appendChild(isPartOfElement);
            }

            // Write the document back to a string
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return writer.getBuffer().toString();
        }
        catch (SAXException | IOException | TransformerException e) {
            Logger.error(String.format("Failed to enrich a record with a " +
                    "collection record id '%s'.", collectionRecordId), e);
            return xml;
        }
    }
}