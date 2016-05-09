package util.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;

/**
 * Created by Josh on 4/25/16.
 */
public class RabbitMQConnection {
    private final static String HOST_NAME = ConfigFactory.load().getString("rabbitmq.host");
    private final static String QUEUE_NAME = ConfigFactory.load().getString("rabbitmq.queue");

    public void sendMessage(CollectionMessage c) throws IOException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST_NAME);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.basicPublish("", QUEUE_NAME, null, c.toString().getBytes("UTF-8"));
    }
}
