package messaging;

import com.typesafe.config.ConfigFactory;
import enrichment.CollectionRecordUpdater;
import play.inject.ApplicationLifecycle;
import util.messaging.CollectionMessage;
import util.messaging.RabbitMQConnection;
import utils.MongoDB;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Represents the collection message RabbitMQ connection.
 */
@Singleton
public class CollectionMessageConnection extends RabbitMQConnection<CollectionMessage> {
    private final static String QUEUE_NAME = ConfigFactory.load().getString("rabbitmq.collectionRecord.queue");

    private final MongoDB mongoDB;

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
    }

    /**
     * Updates all matching records to include a reference to the collection level record.
     *
     * @param message The received message with the required information to perform the updates.
     */
    @Override
    protected void onMessage(CollectionMessage message) {
        new CollectionRecordUpdater(mongoDB, message).run();
    }
}