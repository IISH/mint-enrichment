package enrichment;

import com.mongodb.*;
import util.messaging.CollectionMessage;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.w3c.dom.Document;
import utils.MongoDB;
import utils.MongoDBMock;
import utils.XmlUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class CollectionRecordUpdaterTest {
    private static final String COLLECTION_RECORD_ID = "collection-record-id";

    private MongoDB mongoDB;
    private DBCollection collection;
    private BulkWriteOperation bulkWrite;

    @Before
    public void initialize() {
        XMLUnit.setIgnoreWhitespace(true);

        mongoDB = new MongoDBMock();

        this.collection = mock(DBCollection.class);
        when(mongoDB.getDB().getCollection(anyString())).thenReturn(collection);

        this.bulkWrite = mock(BulkWriteOperation.class);
        when(collection.initializeUnorderedBulkOperation()).thenReturn(bulkWrite);
    }

    @Test
    public void testBulkUpdate() throws Exception {
        // First set up the query results
        BasicDBObject namespace = new BasicDBObject();
        namespace.put("prefix", "org");
        namespace.put("uri", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

        String orgEDM1 = IOUtils.toString(CollectionRecordUpdaterTest.class.getResourceAsStream("edm1.xml"), "UTF-8");
        BasicDBObject firstResult = new BasicDBObject();
        firstResult.put("_id", 1);
        firstResult.put("xmlRecord", orgEDM1);
        firstResult.put("namespace", namespace);

        String orgEDM2 = IOUtils.toString(CollectionRecordUpdaterTest.class.getResourceAsStream("edm2.xml"), "UTF-8");
        BasicDBObject secondResult = new BasicDBObject();
        secondResult.put("_id", 2);
        secondResult.put("xmlRecord", orgEDM2);
        secondResult.put("namespace", namespace);

        TestDBCursor testDBCursor = new TestDBCursor(Arrays.asList(firstResult, secondResult));
        when(collection.find(any())).thenReturn(testDBCursor);

        // And perform the enrichment ourselves
        CollectionRecordEnricher collectionRecordEnricher = new CollectionRecordEnricher(COLLECTION_RECORD_ID);
        Document edm1 = XmlUtils.getDocument(orgEDM1);
        collectionRecordEnricher.enrich(edm1);
        Document edm2 = XmlUtils.getDocument(orgEDM2);
        collectionRecordEnricher.enrich(edm2);

        // Now perform the bulk update
        CollectionMessage message = new CollectionMessage(1, COLLECTION_RECORD_ID);
        CollectionRecordUpdater collectionRecordUpdater = new CollectionRecordUpdater(this.mongoDB, message);
        collectionRecordUpdater.run();

        // Now make sure we can capture the new inserts
        ArgumentCaptor<BasicDBObject> insertCaptor = ArgumentCaptor.forClass(BasicDBObject.class);
        verify(bulkWrite, times(2)).insert(insertCaptor.capture());

        // Now check the inserts
        List<BasicDBObject> inserts = insertCaptor.getAllValues();
        assertEquals(inserts.size(), 2);

        // Check the first insert
        BasicDBObject first = inserts.get(0);
        assertTrue((first.getString("_id") == null) || !first.getString("_id").equals(firstResult.getString("_id")));
        assertEquals("new", ((BasicDBObject) first.get("namespace")).getString("prefix"));
        XMLAssert.assertXMLEqual(XmlUtils.getAsString(edm1), first.getString("xmlRecord"));

        // Check the second insert
        BasicDBObject second = inserts.get(1);
        assertTrue((second.getString("_id") == null) || !second.getString("_id").equals(secondResult.getString("_id")));
        assertEquals("new", ((BasicDBObject) second.get("namespace")).getString("prefix"));
        XMLAssert.assertXMLEqual(XmlUtils.getAsString(edm2), second.getString("xmlRecord"));
    }

    private class TestDBCursor extends DBCursor {
        private Iterator<DBObject> iterator;

        private TestDBCursor(List<DBObject> results) {
            super(mock(DBCollection.class), null, null, null);
            this.iterator = results.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public DBObject next() {
            return this.iterator.next();
        }
    }
}
