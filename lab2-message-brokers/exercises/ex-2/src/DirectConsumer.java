import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class DirectConsumer {

    private static final String DIRECT_EXCHANGE_NAME = "Direct_Exchange";

    private final Channel channel;
    private final String routingKey;
    private final String queueName;
    private final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] argv) throws Exception {
        new DirectConsumer().run();
    }

    DirectConsumer() throws Exception {
        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();

        // exchange
        channel.exchangeDeclare(DIRECT_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        this.routingKey = br.readLine(); 

        // queue & bind
        this.queueName = channel.queueDeclare().getQueue();
        channel.queueBind(this.queueName, DIRECT_EXCHANGE_NAME, routingKey);
        Logger.logConsumer("Created queue { queueName: " + queueName + ", routingKey: " + routingKey + " }");

    }

    public void run() throws Exception {
        Logger.logConsumer("Direct Consumer up!");

        // consumer (message handling)
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8.name());
                Logger.logConsumer("Received: " + message);
            }
        };

        // start listening
        System.out.println("Waiting for messages...");
        channel.basicConsume(queueName, true, consumer);
    }
}
