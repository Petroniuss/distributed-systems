package message

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime

@Serializable
data class OrderMessage(
    val clientName: String,
    val orderType: OrderType
) {
    companion object {
        fun deserialize(bytes: ByteArray): OrderMessage {
            val json = String(bytes, StandardCharsets.UTF_8)
            return Json.decodeFromString(json)
        }
    }

    fun serialize() {
        val json = Json.encodeToString(this)
    }
}


@Serializable
data class ProcessedOrderMessage(
    val clientName: String,
    val orderType: OrderType,
    val orderId: Long,
    @Contextual val date: LocalDateTime
) {
    companion object {
        fun deserialize(bytes: ByteArray): OrderMessage {
            val json = String(bytes, StandardCharsets.UTF_8)
            return Json.decodeFromString(json)
        }
    }

    fun serialize() {
        val json = Json.encodeToString(this)
    }
}




