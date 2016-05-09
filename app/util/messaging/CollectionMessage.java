package util.messaging;

import java.io.Serializable;

/**
 * Created by Josh on 4/25/16.
 */
public class CollectionMessage implements Serializable {
    private String setId;
    private String collectionRecordId;

    public CollectionMessage() {
    }

    public CollectionMessage(String setId, String collectionRecordId) {
        super();
        this.setId = setId;
        this.collectionRecordId = collectionRecordId;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getCollectionRecordId() {
        return collectionRecordId;
    }

    public void setCollectionRecordId(String collectionRecordId) {
        this.collectionRecordId = collectionRecordId;
    }

    @Override
    public String toString() {
        return "CollectionMessage [setId=" + setId + ", collectionRecordId=" + collectionRecordId + "]";
    }
}
