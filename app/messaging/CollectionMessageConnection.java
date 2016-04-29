package messaging;

import com.typesafe.config.ConfigFactory;
import play.Logger;
import play.inject.ApplicationLifecycle;
import utils.MongoDB;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CollectionMessageConnection extends RabbitMQConnection<CollectionMessage> {
    private final static String QUEUE_NAME = ConfigFactory.load().getString("rabbitmq.collectionRecord.queue");
    private final static String COLLECTION_NAME = ConfigFactory.load().getString("rabbitmq.collectionRecord.collection");
    private final static String ORG_METADATA_PREFIX = ConfigFactory.load().getString("rabbitmq.collectionRecord.originalMetadataPrefix");
    private final static String NEW_METADATA_PREFIX = ConfigFactory.load().getString("rabbitmq.collectionRecord.newMetadataPrefix");

    private final MongoDB mongoDB;

    @Inject
    public CollectionMessageConnection(ApplicationLifecycle lifecycle, MongoDB mongoDB) {
        super(lifecycle, QUEUE_NAME);
        this.mongoDB = mongoDB;
    }

    @Override
    protected void onMessage(CollectionMessage message) {
       Logger.info("Received message: " + message);
    }
}