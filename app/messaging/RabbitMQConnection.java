package messaging;

import com.rabbitmq.client.*;
import com.typesafe.config.ConfigFactory;
import play.Logger;
import play.inject.ApplicationLifecycle;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public abstract class RabbitMQConnection<T extends Serializable> {
    private final static String HOST_NAME = ConfigFactory.load().getString("rabbitmq.host");

    private String queueName;
    private Connection connection;
    private Channel channel;

    public RabbitMQConnection(ApplicationLifecycle lifecycle, String queueName) {
        this.queueName = queueName;

        start();
        startConsumer();

        lifecycle.addStopHook(() -> {
            stop();
            return CompletableFuture.completedFuture(null);
        });
    }

    public void sendMessage(T message) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(message);
        channel.basicPublish("", queueName, null, out.toByteArray());
    }

    protected abstract void onMessage(T message);

    private void start() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST_NAME);

            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(queueName, true, false, false, null);

            Logger.info(String.format("Started RabbitMQ connection for the queue '%s'", queueName));
        }
        catch (IOException | TimeoutException e) {
            Logger.error(String.format("Failed to establish a RabbitMQ connection for the queue '%s'", queueName), e);
            throw new RuntimeException(e);
        }
    }

    private void startConsumer() {
        new Thread(() -> {
            try {
                channel.basicConsume(queueName, false, new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                               byte[] body) throws IOException {
                        T message = null;

                        try {
                            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(body));
                            message = (T) in.readObject();
                        }
                        catch (IOException | ClassNotFoundException e) {
                            Logger.error(String.format("Failed to read incoming message " +
                                    "from the queue '%s' with tag '%d'", queueName, envelope.getDeliveryTag()), e);
                        }

                        onMessage(message);
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    }
                });

                Logger.info(String.format("Established a RabbitMQ consumer for the queue '%s'", queueName));
            }
            catch (IOException e) {
                Logger.error(String.format("Failed to establish a RabbitMQ consumer for the queue '%s'", queueName), e);
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void stop() {
        try {
            if (connection != null && channel != null) {
                channel.close();
                connection.close();
            }
        }
        catch (IOException | TimeoutException e) {
            Logger.error(String.format("Failed to stop the RabbitMQ connection for the queue '%s'", queueName), e);
            throw new RuntimeException(e);
        }
    }
}