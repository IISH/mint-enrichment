package utils;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MongoDBMock implements MongoDB {
    private Mongo mongoMock = null;
    private DB dbMock = null;
    private GridFS gridFSMock = null;

    public MongoDBMock() {
        this.mongoMock = mock(Mongo.class);
        this.dbMock = mock(DB.class);
        this.gridFSMock = mock(GridFS.class);

        when(mongoMock.getDB(anyString())).thenReturn(this.dbMock);
    }

    @Override
    public DB getDB() {
        return dbMock;
    }

    @Override
    public Mongo getMongo() {
        return mongoMock;
    }

    @Override
    public GridFS getGridFS() {
        return gridFSMock;
    }
}
