package utils;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import play.Logger;

import javax.inject.Singleton;
import java.net.UnknownHostException;

@Singleton
public class MongoDB {
    private Mongo mongo = null;
    private DB db = null;
    private GridFS gridFS = null;

    public MongoDB() {
        Config config = ConfigFactory.load();

        String host = config.getString("mongo.host");
        int port = config.getInt("mongo.port");
        String database = config.getString("mongo.db");

        String username = config.getString("mongo.username");
        String password = config.getString("mongo.password");
        boolean authenticate = !config.getBoolean("mongo.noauth");

        try {
            mongo = new Mongo(host, port);
            db = mongo.getDB(database);
            gridFS = new GridFS(db);

            if (authenticate) {
                boolean auth = db.authenticate(username, password.toCharArray());
                if (!auth) {
                    Logger.error("MongoDB authentication failed");
                }
            }
        }
        catch (UnknownHostException | MongoException e) {
            Logger.error("Connection to MongoDB failed!", e);
            throw new RuntimeException(e);
        }

        Logger.info("MongoDB connection started");
    }

    public DB getDB() {
        return db;
    }

    public Mongo getMongo() {
        return mongo;
    }

    public GridFS getGridFS() {
        return gridFS;
    }
}
