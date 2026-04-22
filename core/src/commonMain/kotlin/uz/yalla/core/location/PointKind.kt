package uz.yalla.core.location

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Custom [KSerializer] for [PointKind].
 *
 * Encodes as the plain `String` [PointKind.wireValue] and delegates deserialization to
 * [PointKind.Companion.from], which falls back to [PointKind.POINT] for unrecognized
 * wire values instead of throwing.
 *
 * Wire format: a single JSON string (e.g. `"start"`, `"point"`, `"stop"`).
 *
 * @since 0.0.8
 */
object PointKindSerializer : KSerializer<PointKind> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("PointKind", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: PointKind) {
        encoder.encodeString(value.wireValue)
    }

    override fun deserialize(decoder: Decoder): PointKind =
        PointKind.from(decoder.decodeString())
}

/**
 * Classification of a waypoint in a route.
 *
 * Determines the role of a [PointRequest] in a multi-stop route calculation.
 * Serializes as a plain JSON string via [PointKindSerializer]; unknown wire values
 * degrade gracefully to [POINT] (the "unspecified intermediate" default).
 *
 * @property wireValue API wire-format identifier
 * @see PointRequest
 * @since 0.0.1
 */
@Serializable(with = PointKindSerializer::class)
enum class PointKind(val wireValue: String) {
    /** Pickup point / route origin. */
    START("start"),

    /** Intermediate waypoint (e.g., an extra stop). */
    POINT("point"),

    /** Final destination / route end. */
    STOP("stop");

    companion object {
        /**
         * Parses a wire-format string into the corresponding [PointKind].
         *
         * Falls back to [POINT] for `null` or unrecognized values; [POINT] is the safest
         * default as it represents an unspecified intermediate waypoint.
         *
         * @param wireValue API wire-format string, or `null`
         * @return The matching [PointKind], or [POINT] for unknown values
         */
        fun from(wireValue: String?): PointKind =
            entries.find { it.wireValue == wireValue } ?: POINT
    }
}
