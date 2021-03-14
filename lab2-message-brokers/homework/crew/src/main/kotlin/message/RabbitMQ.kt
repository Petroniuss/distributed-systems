package message

object RabbitMQ {
    const val EXCHANGE = "ORDER_EXCHANGE"

    private const val ORDER_PREFIX = "order"
    private const val CREATE_ORDER_PREFIX = "$ORDER_PREFIX.create"
    private const val RECEIVE_ORDER_PREFIX = "$ORDER_PREFIX.receive"

    fun receiveOrderQueueName(crewName: String, crewId: String): String {
        return "$RECEIVE_ORDER_PREFIX.${crewName.toLowerCase()}.$crewId"
    }

    fun receiveOrderRoutingKey(crewName: String, crewId: String): String {
        return "$RECEIVE_ORDER_PREFIX.${crewName.toLowerCase()}.$crewId"
    }

    fun createOrderQueueName(orderType: OrderType): String {
        return "$CREATE_ORDER_PREFIX.${orderType.toString().toLowerCase()}"
    }

    fun createOrderRoutingKey(orderType: OrderType): String {
        return "$CREATE_ORDER_PREFIX.${orderType.toString().toLowerCase()}"
    }

}
