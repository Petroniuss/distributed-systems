package supplier

import cli.CLI
import com.github.ajalt.mordant.animation.textAnimation
import com.rabbitmq.client.*
import com.rabbitmq.client.BuiltinExchangeType.*
import message.OrderMessage
import message.OrderType
import message.ProcessedOrderMessage
import message.RabbitMQ
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class Supplier {
    private val orderTypes: List<OrderType>
    private val connection: Connection
    private val name: String

    init {
        CLI.displayIntro()
        val keywords = ArrayList<String>()

        name = CLI.prompt("Enter supplier name:")
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
            val exchangeName = RabbitMQ.EXCHANGE
            val queueName = RabbitMQ.createOrderQueueName(orderType)
            val routingKey = RabbitMQ.createOrderRoutingKey(orderType)

            channel.exchangeDeclare(exchangeName, TOPIC)
            channel.queueDeclare(queueName, false, false, true, null)
            channel.queueBind(queueName, exchangeName, routingKey)
            channel.basicConsume(queueName, ReceiveOrder(connection, name), SupplierCancelCallback())
        }

        CLI.success("Waiting for orders...")
    }
}

class ReceiveOrder(
    private val connection: Connection,
    private val supplierName: String): DeliverCallback {

    override fun handle(consumerTag: String?, delivery: Delivery) {
        val createOrderMessage = OrderMessage.deserialize(delivery.body)
        val crewName = createOrderMessage.crewName
        val crewId = createOrderMessage.crewId
        val orderType = createOrderMessage.orderType
        val orderId = UUID.randomUUID().toString()
        val receivedDate = LocalDateTime.now()

        val queueName = RabbitMQ.receiveOrderQueueName(crewName, crewId)
        CLI.info("Received order from: '$crewName', order: '$orderType'")

        connection.createChannel().use { channel ->
            val routingKey = RabbitMQ.receiveOrderRoutingKey(crewName, crewId)

            channel.queueDeclare(queueName, false, false, true, null)
            channel.queueBind(queueName, RabbitMQ.EXCHANGE, routingKey)

            val processedDate = LocalDateTime.now()
            val message = ProcessedOrderMessage(
                    crewName, crewId,
                    orderType, orderId,
                    receivedDate, processedDate)

            channel.basicPublish(
                RabbitMQ.EXCHANGE,
                routingKey,
                null,
                message.serialize()
            )
            CLI.info("Handled order for: '$crewName', order: '$orderType'")
        }
    }
}

class SupplierCancelCallback: CancelCallback {
    override fun handle(consumerTag: String?) {
        CLI.warning("$consumerTag canceled for some reason..")
    }
}



