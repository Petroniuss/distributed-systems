import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyProducer {

    public static void main(String[] argv) throws Exception {

        // info
        System.out.println("[producer]> running!");

        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        final InputStreamReader in = new InputStreamReader(System.in);
        final BufferedReader reader = new BufferedReader(in);

        // queue
        String QUEUE_NAME = "queue1";
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);        


        while (true ) {
            readPublish(reader, channel, QUEUE_NAME);
        }

        // close
        // channel.close();
        // connection.close();
    }


    private static void readPublish(BufferedReader reader, Channel channel, String queueName) throws IOException {
        final String message = reader.readLine();
        channel.basicPublish("", queueName, null, message.getBytes());
        System.out.println("[Producer]> sent: " + message);
    }
}
