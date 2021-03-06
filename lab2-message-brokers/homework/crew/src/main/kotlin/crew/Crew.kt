package crew

import cli.CLI
import com.github.ajalt.mordant.animation.ProgressAnimation
import com.rabbitmq.client.*
import com.rabbitmq.client.BuiltinExchangeType.*
import message.OrderMessage
import message.OrderType
import message.ProcessedOrderMessage
import message.RabbitMQ
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class Crew {
    private val connection: Connection
    private val name: String
    private val id: String = UUID.randomUUID().toString()
    private val processed: MutableList<ProcessedOrder> = ArrayList()
    private var left = 0

    init {
        CLI.displayIntro()
        val keywords = ArrayList<String>()

        name = CLI.prompt("Enter crew name:")

        do {
            val keyword = CLI.prompt("Enter supply name:")
            keywords.add(keyword)
        } while (keyword.isNotBlank())

        val orderTypes = OrderType.match(keywords)
        left = orderTypes.size
        CLI.success("Matched: $orderTypes")

        val factory = ConnectionFactory()
        connection = factory.newConnection("amqp://guest:guest@localhost:5672/")

        // order supplies
        for (orderType in orderTypes) {
            val meta = RabbitMQ.createOrderQ(orderType)
            RabbitMQ.declareQueue(meta, connection).use { channel ->
                val msgKey = RabbitMQ.createOrderMessageKey(orderType)
                val orderDate = LocalDateTime.now()
                val createOrderMsg = OrderMessage(name, id, orderType, orderDate)

                channel.basicPublish(RabbitMQ.EXCHANGE, msgKey, null, createOrderMsg.serialize())
                CLI.info("Ordered $orderType")
            }
        }

        val header = listOf("Ordered supplies")
        val ordered = orderTypes.map { it.toString() }.map { listOf(it) }
        CLI.table(header, ordered)

        // consume responses
        val meta = RabbitMQ.receiveOrderQ(name, id)
        val channel = RabbitMQ.declareQueue(meta, connection)
        channel.basicConsume(meta.queueName, ReceiveProcessedOrder(this, channel), ReceiveCancelled())
        CLI.success("Waiting for supplies.. queue: ${meta.queueName}")

        // consume messages from admin
        RabbitMQ.declareConsumerFromAdmin(RabbitMQ.adminToCrewQ(name), connection)
        RabbitMQ.declareConsumerFromAdmin(RabbitMQ.adminToAllQ(name), connection)
    }

    fun progress(processedOrderMessage: ProcessedOrderMessage) {
        CLI.info("$processedOrderMessage")
        val receivedDate = LocalDateTime.now()
        this.left -= 1
        val processedOrder = ProcessedOrder(processedOrderMessage, receivedDate)
        this.processed.add(processedOrder)

        if (this.left <= 0) {
            val rows = processed.map { it.toRow() }
            val header = ProcessedOrder.header()
            CLI.table(header, rows)
        }
    }

}

class ReceiveProcessedOrder(
    private val crew: Crew,
    private val channel: Channel): DeliverCallback {

    override fun handle(consumerTag: String?, delivery: Delivery) {
        val processedOrderMessage = ProcessedOrderMessage.deserialize(delivery.body)
        crew.progress(processedOrderMessage)

        val deliveryTag = delivery.envelope.deliveryTag
        channel.basicAck(deliveryTag, true)
    }
}

class ReceiveCancelled(): CancelCallback{
    override fun handle(consumerTag: String?) {
        CLI.warning("Finished.")
    }
}


data class ProcessedOrder(val processedOrderMessage: ProcessedOrderMessage,
                          val crewReceivedDate: LocalDateTime) {

    fun toRow(): List<String> {
        val orderType = processedOrderMessage.orderType.toString()
        val supplierName = processedOrderMessage.supplierName
        val orderId = processedOrderMessage.orderId
        val processedDate = processedOrderMessage.processedDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val supplierReceivedDate = processedOrderMessage.receivedDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val receivedDate = crewReceivedDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        return listOf(orderType, supplierName, orderId, supplierReceivedDate, processedDate, receivedDate)
    }

    companion object {
        fun header(): List<String> {
            return listOf("order type", "supplier name", "order id", "supplier received date", "processed date", "crew received date")
        }
    }

}



