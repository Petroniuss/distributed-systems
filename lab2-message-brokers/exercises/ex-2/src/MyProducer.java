import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

public class MyProducer {

    private static final int DIRECT = 0;
    private static final int TOPIC = 1;

    private static final String DIRECT_EXCHANGE_NAME = "Direct_Exchange";
    private static final String TOPIC_EXCHANGE_NAME = "Topic_Exchange";

    private int exchange = DIRECT;
    private final Channel channel; 

    public static void main(String[] argv) throws Exception {
        new MyProducer().run();
    }

    public MyProducer() throws Exception { 
        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();

        // exchange
        channel.exchangeDeclare(DIRECT_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(TOPIC_EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
    }

    private void run() throws Exception {
        Logger.logProducer("Exchange Prodcer running!");
        Logger.logProducer("Enter command or message");
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            // read msg
            String message = br.readLine();

            if (message.equals("!direct")) {
                this.exchange = DIRECT;
            } else if (message.equals("!topic")) {
                this.exchange = TOPIC;
            } else {
                Logger.logProducer("Enter key");
                String routingKey = br.readLine();
                publish(message, routingKey);
            }
        }
    }

    private void publish(String message, String routingKey) throws Exception {
        final var exName = exchangeName();
        channel.basicPublish(exName, routingKey, null, message.getBytes(StandardCharsets.UTF_8.name()));
        Logger.logProducer(MessageFormat.format("Exchange: {2}, Key: {0}, Message: {1}", routingKey, message, exName).toString());
    }

    private String exchangeName() {
        return switch (exchange) {
            case DIRECT -> DIRECT_EXCHANGE_NAME;
            case TOPIC  -> TOPIC_EXCHANGE_NAME;
            default     ->  TOPIC_EXCHANGE_NAME;
        };
    }
}
