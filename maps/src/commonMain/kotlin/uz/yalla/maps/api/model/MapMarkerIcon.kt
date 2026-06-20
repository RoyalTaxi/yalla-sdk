package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable

/** How a [MapMarker] is rendered. */
@Immutable
public sealed class MapMarkerIcon {
    /** A named platform image resource. */
    public data class Resource(
        val name: String
    ) : MapMarkerIcon()

    /** A teardrop pin tinted [colorArgb] with an optional text [label]. */
    public data class Pin(
        val colorArgb: Int,
        val label: String? = null
    ) : MapMarkerIcon()

    /** A filled dot with an optional stroke ring. */
    public data class Dot(
        val fillColorArgb: Int,
        val strokeColorArgb: Int,
        val diameterDp: Float = 14f,
        val strokeWidthDp: Float = 4f
    ) : MapMarkerIcon()

    /** Raw encoded image bytes (e.g. a server-supplied PNG). */
    public class Bytes(
        public val data: ByteArray
    ) : MapMarkerIcon() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Bytes) return false
            return data.contentEquals(other.data)
        }

        override fun hashCode(): Int = data.contentHashCode()
    }
}
