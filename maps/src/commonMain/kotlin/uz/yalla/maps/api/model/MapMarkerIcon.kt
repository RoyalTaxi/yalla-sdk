package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable

@Immutable
public sealed class MapMarkerIcon {
    public data class Resource(val name: String) : MapMarkerIcon()

    public data class Pin(
        val colorArgb: Int,
        val label: String? = null
    ) : MapMarkerIcon()

    public data class Dot(
        val fillColorArgb: Int,
        val strokeColorArgb: Int,
        val diameterDp: Float = 14f,
        val strokeWidthDp: Float = 4f
    ) : MapMarkerIcon()

    public class Bytes(public val data: ByteArray) : MapMarkerIcon() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Bytes) return false
            return data.contentEquals(other.data)
        }

        override fun hashCode(): Int = data.contentHashCode()
    }
}
