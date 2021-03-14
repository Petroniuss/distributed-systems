package admin

import cli.CLI
import com.rabbitmq.client.*
import message.*
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class Admin {
    private var state: State = State.ALL

    init {
        CLI.displayIntro()
        val factory = ConnectionFactory()
        val connection = factory.newConnection("amqp://guest:guest@localhost:5672/")

        // eavesdrop
        val suppQMeta = RabbitMQ.adminFromSuppliersQ()
        val fromSuppChannel = RabbitMQ.declareQueue(suppQMeta, connection)
        fromSuppChannel.basicConsume(suppQMeta.queueName, ReceiveOrderEavesDrop(fromSuppChannel), AdminCancelCallback())

        val crewQMeta = RabbitMQ.adminFromCrewsQ()
        val fromCrewChannel = RabbitMQ.declareQueue(crewQMeta, connection)
        fromCrewChannel.basicConsume(crewQMeta.queueName, CreateOrderEavesdrop(fromCrewChannel), AdminCancelCallback())

        val channel = connection.createChannel()

        while ( true ) {
            val line = CLI.prompt("Enter message>")
            val nullableSt = State.all().find { it.matches(line) }
            if (nullableSt != null) {
                state = nullableSt
            } else {
                val msg = AdminMessage(line)
                val msgSerialized = msg.serialize()
                val msgKey = state.routingKey()
                channel.basicPublish(RabbitMQ.EXCHANGE, msgKey, null, msgSerialized)
                CLI.success("[${state.name}] - $msg")
            }
        }
    }
}

enum class State(command: String) {
    CREW("!crews"),
    SUPPLIER("!suppliers"),
    ALL("!all");

    private val regex = command.toRegex()

    fun matches(string: String): Boolean {
        return regex.matches(string)
    }

    fun routingKey(): String {
        return when (this) {
            CREW -> RabbitMQ.adminToCrewsMsgKey()
            SUPPLIER -> RabbitMQ.adminToSuppliersMsgKey()
            ALL -> RabbitMQ.adminToAllMsgKey()
        }
    }

    companion object {
        fun all(): List<State> {
            return values().asList()
        }
    }
}

class CreateOrderEavesdrop(
    private val channel: Channel): DeliverCallback {

    override fun handle(consumerTag: String?, delivery: Delivery) {
        val createOrderMessage = OrderMessage.deserialize(delivery.body)
        CLI.info("$createOrderMessage")

        // ack
        val deliveryTag = delivery.envelope.deliveryTag
        channel.basicAck(deliveryTag, true)
    }
}

class ReceiveOrderEavesDrop(
    private val channel: Channel): DeliverCallback {

    override fun handle(consumerTag: String?, delivery: Delivery) {
        val processedOrderMessage = ProcessedOrderMessage.deserialize(delivery.body)
        CLI.info("$processedOrderMessage")

        // ack
        val deliveryTag = delivery.envelope.deliveryTag
        channel.basicAck(deliveryTag, true)
    }
}

class AdminCancelCallback: CancelCallback {
    override fun handle(consumerTag: String?) {
        CLI.warning("$consumerTag canceled for some reason..")
    }
}



