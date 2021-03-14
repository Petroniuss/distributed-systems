@file:UseSerializers(LocalDateTimeSerializer::class)
package message

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class OrderMessage(
    val crewName: String,
    val crewId: String,
    val orderType: OrderType,
    val orderDate: LocalDateTime
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
    val receivedDate: LocalDateTime,
    val processedDate: LocalDateTime
) {
    fun serialize(): ByteArray {
        return Json.encodeToString(this).toByteArray()
    }

    companion object {
        fun deserialize(bytes: ByteArray): ProcessedOrderMessage {
            val json = String(bytes, StandardCharsets.UTF_8)
            return Json.decodeFromString(json)
        }
    }
}

@Serializable
data class AdminMessage(
    val msg: String
) {
    fun serialize(): ByteArray {
        return Json.encodeToString(this).toByteArray()
    }

    companion object {
        fun deserialize(bytes: ByteArray): AdminMessage {
            val json = String(bytes, StandardCharsets.UTF_8)
            return Json.decodeFromString(json)
        }
    }
}


object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(DateTimeFormatter.ISO_DATE_TIME))
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString())
    }
}
