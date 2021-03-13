import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;

public class MyConsumer {

    public static void main(String[] argv) throws Exception {

        // info
        System.out.println("[Consumer]> Running!");

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // queue
        String QUEUE_NAME = "queue1";
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.basicQos(1);

        // consumer (handle msg)
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                int timeToSleep = Integer.parseInt(message);
                System.out.println("[Consumer]> Received: " + message);
                try {
                    Thread.sleep(timeToSleep * 1000);
                } catch (InterruptedException ignored) { }

                channel.basicAck(envelope.getDeliveryTag(), false);
                System.out.println("[Consumer]> Acknowledged: " + message);
            }
        };

        // start listening
        System.out.println("[Consumer]> Waiting for messages...");
        channel.basicConsume(QUEUE_NAME, false, consumer);
    }
}
