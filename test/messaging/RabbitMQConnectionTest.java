package messaging;

import com.rabbitmq.client.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import play.inject.ApplicationLifecycle;

import java.io.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class RabbitMQConnectionTest {
    private static final String QUEUE_NAME = "testQueue";

    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Channel channel;

    private ApplicationLifecycle lifecycle;
    private DummyRabbitMQConnection rabbitMQConnection;

    @Before
    public void init() throws Exception {
        this.connectionFactory = mock(ConnectionFactory.class);
        connection = mock(Connection.class);
        channel = mock(Channel.class);

        when(connectionFactory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);

        this.lifecycle = mock(ApplicationLifecycle.class);
        this.rabbitMQConnection = new DummyRabbitMQConnection(this.lifecycle, QUEUE_NAME);
    }

    @Test
    public void testSetup() throws Exception {
        // Connection and channel are setup
        verify(connectionFactory).newConnection();
        verify(connection).createChannel();

        // The queue is setup
        ArgumentCaptor<String> queueCaptor = ArgumentCaptor.forClass(String.class);
        verify(channel).queueDeclare(queueCaptor.capture(), anyBoolean(), anyBoolean(), anyBoolean(), any());
        assertEquals(queueCaptor.getValue(), QUEUE_NAME);

        // The consumer is setup
        queueCaptor = ArgumentCaptor.forClass(String.class);
        verify(channel).basicConsume(queueCaptor.capture(), anyBoolean(), any());
        assertEquals(queueCaptor.getValue(), QUEUE_NAME);
    }

    @Test
    public void testShutdown() throws Exception {
        // Get the hooked callable
        ArgumentCaptor<Callable> callableCaptor = ArgumentCaptor.forClass(Callable.class);
        verify(lifecycle).addStopHook(callableCaptor.capture());

        // Verify that only after the stop hook is called, the connections are closed
        verify(connection, never()).close();
        verify(channel, never()).close();

        // Now verify that the connections are closed after the obtained callable is run
        Callable<? extends CompletionStage<?>> callable =
                (Callable<? extends CompletionStage<?>>) callableCaptor.getValue();
        callable.call().thenRun(() -> {
            try {
                verify(connection).close();
                verify(channel).close();
            }
            catch (Exception e) {
            }
        });
    }

    @Test
    public void testSendMessage() throws Exception {
        // Create a test message
        DummyMessage testMessage = new DummyMessage();
        testMessage.messageOne = "First";
        testMessage.messageTwo = "Second";

        // We want to capture the queue name and the bytes send to RabbitMQ
        ArgumentCaptor<String> queueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<byte[]> bytesCaptor = ArgumentCaptor.forClass(byte[].class);

        // Send the message and verify that the publisher is called
        rabbitMQConnection.sendMessage(testMessage);
        verify(channel).basicPublish(anyString(), queueCaptor.capture(), any(), bytesCaptor.capture());

        // Verify that the right queue name was used
        assertEquals(queueCaptor.getValue(), QUEUE_NAME);

        // Verify that the bytes sent to the queue is a correct, serialized version of our message
        Object obj = new ObjectInputStream(new ByteArrayInputStream(bytesCaptor.getValue())).readObject();
        assertTrue(obj instanceof DummyMessage);
        assertEquals(((DummyMessage) obj).messageOne, testMessage.messageOne);
        assertEquals(((DummyMessage) obj).messageTwo, testMessage.messageTwo);
    }

    @Test
    public void testReceiveMessage() throws Exception {
        // Create a test message
        DummyMessage testMessage = new DummyMessage();
        testMessage.messageOne = "First one";
        testMessage.messageTwo = "Second one";

        // We want to obtain the consumer that was created during setup
        ArgumentCaptor<Consumer> consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(channel).basicConsume(anyString(), anyBoolean(), consumerCaptor.capture());

        // The consumer is called with the serialized message
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ObjectOutputStream(out).writeObject(testMessage);
        consumerCaptor.getValue().handleDelivery(
                "", new Envelope(0L, false, "", ""), new AMQP.BasicProperties(), out.toByteArray());

        // Now confirm that the method onMessage was called with the correct message
        assertEquals(rabbitMQConnection.lastMessage.messageOne, testMessage.messageOne);
        assertEquals(rabbitMQConnection.lastMessage.messageTwo, testMessage.messageTwo);
    }

    private class DummyRabbitMQConnection extends RabbitMQConnection<DummyMessage> {
        private DummyMessage lastMessage;

        private DummyRabbitMQConnection(ApplicationLifecycle lifecycle, String queueName) {
            super(lifecycle, queueName);
        }

        @Override
        protected void onMessage(DummyMessage message) {
            lastMessage = message;
        }

        @Override
        protected ConnectionFactory getConnectionFactory() {
            return connectionFactory;
        }
    }
}
