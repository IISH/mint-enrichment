package utils;

import com.google.inject.ImplementedBy;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;

@ImplementedBy(MongoDBImpl.class)
public interface MongoDB {
    DB getDB();

    Mongo getMongo();

    GridFS getGridFS();
}
