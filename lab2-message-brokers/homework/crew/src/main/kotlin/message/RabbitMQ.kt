package message

import cli.CLI
import com.rabbitmq.client.*

object RabbitMQ {
    const val EXCHANGE = "Crew Exchange"

    private const val ORDER_PREFIX = "order"
    private const val CREATE_ORDER_PREFIX = "$ORDER_PREFIX.create"
    private const val RECEIVE_ORDER_PREFIX = "$ORDER_PREFIX.receive"

    private const val ADMIN_PREFIX = "admin"
    private const val ADMIN_SUPPLIER_PREFIX = "$ADMIN_PREFIX.supplier"
    private const val ADMIN_CREW_PREFIX = "$ADMIN_PREFIX.crew"

    fun receiveOrderQ(crewName: String, crewId: String): QueueMeta {
        val key = "$RECEIVE_ORDER_PREFIX.${crewName.toLowerCase()}.$crewId"
        return QueueMeta(key, key)
    }

    fun receiveOrderMessageKey(crewName: String, crewId: String): String {
        return "$RECEIVE_ORDER_PREFIX.${crewName.toLowerCase()}.$crewId"
    }

    fun createOrderQ(orderType: OrderType): QueueMeta {
        val key = "$CREATE_ORDER_PREFIX.${orderType.toString().toLowerCase()}"
        return QueueMeta(key, key)
    }

    fun createOrderMessageKey(orderType: OrderType): String {
        return "$CREATE_ORDER_PREFIX.${orderType.toString().toLowerCase()}"
    }

    // -------- Admin --------
    // admin listening

    data class QueueMeta(
        val queueName: String,
        val routingKey: String
    )

    fun adminFromSuppliersQ(): QueueMeta {
        val key = "$RECEIVE_ORDER_PREFIX.#"
        val qName = "admin_queue_from_suppliers"
        return QueueMeta(qName, key)
    }

    fun adminFromCrewsQ(): QueueMeta {
        val key = "$CREATE_ORDER_PREFIX.#"
        val qName = "admin_queue_from_crews"
        return QueueMeta(qName, key)
    }

    // admin sending
    fun adminToCrewQ(crewName: String): QueueMeta {
        val qName = "$ADMIN_CREW_PREFIX.$crewName"
        val key = "$ADMIN_CREW_PREFIX.#"
        return QueueMeta(qName, key)
    }

    fun adminToSupplierQ(supplierName: String): QueueMeta {
        val qName = "$ADMIN_SUPPLIER_PREFIX.$supplierName"
        val key = "$ADMIN_SUPPLIER_PREFIX.#"
        return QueueMeta(qName, key)
    }

    fun adminToAllQ(name: String): QueueMeta {
        val key = "$ADMIN_PREFIX.all.#"
        val qName = "$ADMIN_PREFIX.all.$name"

        return QueueMeta(qName, key)
    }

    fun adminToCrewsMsgKey(): String  {
        return "$ADMIN_CREW_PREFIX.all"
    }

    fun adminToSuppliersMsgKey(): String  {
        return "$ADMIN_CREW_PREFIX.all"
    }

    fun adminToAllMsgKey(): String  {
        return "$ADMIN_PREFIX.all.all"
    }

    fun publishMessage(meta: QueueMeta, connection: Connection, msgKey: String, message: ByteArray) {
        connection.createChannel().use { channel ->
            channel.queueDeclare(meta.queueName, false, false, true, null)
            channel.queueBind(meta.queueName, EXCHANGE, meta.routingKey)
            channel.basicPublish(EXCHANGE, msgKey,null, message)
        }
    }

    fun declareConsumerFromAdmin(meta: QueueMeta, connection: Connection) {
        val channel = declareQueue(meta, connection)
        channel.basicConsume(meta.queueName, ReceiveFromAdmin(channel), CancelCallbackHandler())
    }

    fun declareQueue(meta: QueueMeta, connection: Connection): Channel {
        val channel = connection.createChannel()
        val exchangeName = EXCHANGE
        val queueName = meta.queueName
        val routingKey = meta.routingKey

        channel.basicQos(1)
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC)
        channel.queueDeclare(queueName, false, false, true, null)
        channel.queueBind(queueName, exchangeName, routingKey)

        return channel
    }

}

class ReceiveFromAdmin(
    private val channel: Channel): DeliverCallback {

    override fun handle(consumerTag: String?, delivery: Delivery) {
        val adminMessage = AdminMessage.deserialize(delivery.body)
        CLI.success("$adminMessage")

        val deliveryTag = delivery.envelope.deliveryTag
        channel.basicAck(deliveryTag, true)
    }
}

class CancelCallbackHandler: CancelCallback {
    override fun handle(consumerTag: String?) {
        CLI.warning("$consumerTag canceled for some reason..")
    }
}
