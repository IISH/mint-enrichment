package enrichment;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.typesafe.config.ConfigFactory;
import util.messaging.CollectionMessage;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import play.Logger;
import utils.MongoDB;
import utils.XmlUtils;

import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * Updates all records of a dataset to include a reference to the collection level record.
 */
public class CollectionRecordUpdater {
    private final static int BULK_SIZE = 1000;

    private final static String COLLECTION_NAME = ConfigFactory.load().getString("collectionRecordUpdate.collection");
    private final static String ORG_METADATA_PREFIX = ConfigFactory.load().getString("collectionRecordUpdate.originalMetadataPrefix");
    private final static String NEW_METADATA_PREFIX = ConfigFactory.load().getString("collectionRecordUpdate.newMetadataPrefix");

    private MongoDB mongoDB;
    private CollectionMessage message;
    private CollectionRecordEnricher recordEnricher;

    /**
     * Sets up the CollectionRecordUpdater.
     *
     * @param mongoDB The MongoDB instance.
     * @param message The collection message with information about the updates to perform.
     */
    public CollectionRecordUpdater(MongoDB mongoDB, CollectionMessage message) {
        this.mongoDB = mongoDB;
        this.message = message;
        this.recordEnricher = new CollectionRecordEnricher(message.getCollectionRecordId());
    }

    /**
     * Updates all matching records to include a reference to the collection level record.
     */
    public void run() {
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
            updateRecord(record, bulkWrite);

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
     * @param record    The record to update.
     * @param bulkWrite The MongoDB bulk operation to write the result to.
     */
    private void updateRecord(BasicDBObject record, BulkWriteOperation bulkWrite) {
        try {
            String originalXml = (String) record.get("xmlRecord");
            Document document = XmlUtils.getDocument(originalXml);

            recordEnricher.enrich(document);

            record.removeField("_id"); // New record, force new id
            record.put("xmlRecord", XmlUtils.getAsString(document));
            ((BasicDBObject) record.get("namespace")).put("prefix", NEW_METADATA_PREFIX);

            bulkWrite.insert(record);
        }
        catch (TransformerException | IOException | SAXException e) {
            Logger.error(String.format("Failed to enrich a record with a " +
                    "collection record id '%s'.", message.getCollectionRecordId()), e);
        }
    }
}
