package messaging;

import java.io.Serializable;

/**
 * Created by Josh on 4/25/16.
 */
public class CollectionMessage implements Serializable {
    private Integer setId;
    private String collectionRecordId;

    public CollectionMessage() {
    }

    public CollectionMessage(Integer setId, String collectionRecordId) {
        super();
        this.setId = setId;
        this.collectionRecordId = collectionRecordId;
    }

    public Integer getSetId() {
        return setId;
    }

    public void setSetId(Integer setId) {
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
