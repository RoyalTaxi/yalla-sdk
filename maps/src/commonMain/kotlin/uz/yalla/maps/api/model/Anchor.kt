package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable

@Immutable
public data class Anchor(
    val x: Float,
    val y: Float
) {
    public companion object {
        public val CENTER: Anchor = Anchor(0.5f, 0.5f)
        public val BOTTOM: Anchor = Anchor(0.5f, 1.0f)
        public val TOP: Anchor = Anchor(0.5f, 0.0f)
    }
}
