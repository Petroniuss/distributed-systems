package supplier

import cli.CLI
import com.rabbitmq.client.*
import com.rabbitmq.client.BuiltinExchangeType.*
import message.OrderMessage
import message.OrderType
import java.nio.charset.StandardCharsets

class Supplier {
    private val orderTypes: List<OrderType>
    private val connection: Connection

    init {
        CLI.displayIntro()
        val keywords = ArrayList<String>()

        do {
            val keyword = CLI.prompt("Enter keyword:")
            keywords.add(keyword)
        } while (keyword.isNotBlank())

        orderTypes = OrderType.match(keywords)
        CLI.success("Matched: $orderTypes")

        val factory = ConnectionFactory()
        connection = factory.newConnection("amqp://guest:guest@localhost:5672/")

        for (orderType in orderTypes) {
            val channel = connection.createChannel()
            val exchangeName = OrderType.exchange
            val queueName = orderType.queueName
            val routingKey = orderType.routingKey

            channel.exchangeDeclare(exchangeName, TOPIC)
            channel.queueDeclare(queueName, false, false, true, null)
            channel.queueBind(queueName, exchangeName, routingKey)
            channel.basicConsume(queueName, ReceiveOrder(connection), SupplierCancelCallback())
        }
    }
}

class ReceiveOrder(private val connection: Connection): DeliverCallback {
    override fun handle(consumerTag: String?, delivery: Delivery) {
        val orderMessage = OrderMessage.deserialize(delivery.body)
        CLI.info("Received order from: '${orderMessage.clientName}', order: '${orderMessage.orderType}'")

        // todo process order and send ack to client
        connection.createChannel().use { channel ->
            channel.queueDeclare("test_queue", false, false, false, null)
            val message = "Hello World!"
            channel.basicPublish(
                "",
                "",
                null,
                message.toByteArray(StandardCharsets.UTF_8)
            )
            println(" [x] Sent '$message'")
        }
    }
}

class SupplierCancelCallback: CancelCallback {
    override fun handle(consumerTag: String?) {
        CLI.warning("$consumerTag canceled for some reason..")
    }
}



