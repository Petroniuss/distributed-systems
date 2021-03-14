package supplier

import cli.CLI
import com.rabbitmq.client.*
import message.OrderMessage
import message.OrderType
import message.ProcessedOrderMessage
import message.RabbitMQ
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class Supplier {
    private val orderTypes: Set<OrderType>
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

        orderTypes = OrderType.match(keywords).toSet()
        CLI.success("Matched: $orderTypes")

        val factory = ConnectionFactory()
        connection = factory.newConnection("amqp://guest:guest@localhost:5672/")

        // declare admin queue
        RabbitMQ.declareQueue(RabbitMQ.adminFromSuppliersQ(), connection)

        // consumers for supplied orders
        for (orderType in orderTypes) {
            val meta = RabbitMQ.createOrderQ(orderType)
            val channel = RabbitMQ.declareQueue(meta, connection)

            channel.basicConsume(meta.queueName, ReceiveOrder(channel, connection, name), SupplierCancelCallback())
        }

        // consumer for admin messages
        RabbitMQ.declareConsumerFromAdmin(RabbitMQ.adminToSupplierQ(name), connection)
        RabbitMQ.declareConsumerFromAdmin(RabbitMQ.adminToAllQ(name), connection)

        CLI.success("Waiting for orders...")
    }
}

class ReceiveOrder(
    private val channel: Channel,
    private val connection: Connection,
    private val supplierName: String): DeliverCallback {

    override fun handle(consumerTag: String?, delivery: Delivery) {
        val createOrderMessage = OrderMessage.deserialize(delivery.body)
        val crewName = createOrderMessage.crewName
        val crewId = createOrderMessage.crewId
        val orderType = createOrderMessage.orderType
        val orderId = UUID.randomUUID().toString()
        val receivedDate = LocalDateTime.now()

        CLI.info("Received order from: '$crewName', order: '$orderType'")

        val processedDate = LocalDateTime.now()
        val message = ProcessedOrderMessage(
            crewName, supplierName,
            orderType, orderId,
            receivedDate, processedDate).serialize()

        // publish
        val meta = RabbitMQ.receiveOrderQ(crewName, crewId)
        val msgKey = RabbitMQ.receiveOrderMessageKey(crewName, crewId)
        RabbitMQ.publishMessage(meta, connection, msgKey, message)

        // ack
        val deliveryTag = delivery.envelope.deliveryTag
        channel.basicAck(deliveryTag, true)
        CLI.info("Processed order: '$orderType' for: '$crewName'")
    }
}

class SupplierCancelCallback: CancelCallback {
    override fun handle(consumerTag: String?) {
        CLI.warning("$consumerTag canceled for some reason..")
    }
}



