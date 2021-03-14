package message

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

@Serializable
data class OrderMessage(
    val crewName: String,
    val crewId: String,
    val orderType: OrderType,
    @Contextual val orderDate: LocalDateTime
) {
    fun serialize(): ByteArray {
        return Json.encodeToString(this).toByteArray()
    }

    companion object {
        fun deserialize(bytes: ByteArray): OrderMessage {
            val json = String(bytes, StandardCharsets.UTF_8)
            return Json.decodeFromString(json)
        }
    }
}


@Serializable
data class ProcessedOrderMessage(
    val crewName: String,
    val supplierName: String,
    val orderType: OrderType,
    val orderId: String,
    @Contextual val receivedDate: LocalDateTime,
    @Contextual val processedDate: LocalDateTime
) {
    fun serialize(): ByteArray {
        return Json.encodeToString(this).toByteArray()
    }

    companion object {
        fun deserialize(bytes: ByteArray): OrderMessage {
            val json = String(bytes, StandardCharsets.UTF_8)
            return Json.decodeFromString(json)
        }
    }

}

@ExperimentalSerializationApi
@Serializer(forClass = LocalDateTime::class)
object LocalDateTimeSerializer




