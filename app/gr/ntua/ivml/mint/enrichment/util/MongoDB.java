package gr.ntua.ivml.mint.enrichment.util;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;

import java.net.UnknownHostException;

public class MongoDB {
    private static Mongo mongo = null;
    private static DB db = null;
    private static GridFS gridFS = null;

    static {
        String host = Config.get("mongo.host");
        int port = Config.getInt("mongo.port");
        String database = Config.get("mongo.db");

        String username = Config.get("mongo.username");
        String password = Config.get("mongo.password");
        boolean authenticate = !Config.getBoolean("mongo.noauth");

        try {
            mongo = new Mongo(host, port);
            db = mongo.getDB(database);
            gridFS = new GridFS(db);

            if (authenticate) {
                boolean auth = db.authenticate(username, password.toCharArray());
                if (!auth) {
                    System.err.println("MongoDB authentication failed");
                }
            }
        }
        catch (UnknownHostException | MongoException e) {
            e.printStackTrace();
        }

        System.out.println("MongoDB connection started");
    }

    public static DB getDB() {
        return db;
    }

    public static Mongo getMongo() {
        return mongo;
    }

    public static GridFS getGridFS() {
        return gridFS;
    }
}
