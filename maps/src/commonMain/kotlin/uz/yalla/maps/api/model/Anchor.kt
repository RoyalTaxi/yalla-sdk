package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable

/**
 * The fractional point of a marker icon pinned to its location, where (0,0) is the top-left and
 * (1,1) the bottom-right of the icon.
 */
@Immutable
public data class Anchor(
    val x: Float,
    val y: Float
) {
    public companion object {
        /** Icon center pinned to the location. */
        public val CENTER: Anchor = Anchor(0.5f, 0.5f)

        /** Icon bottom-center pinned to the location (the default for pins). */
        public val BOTTOM: Anchor = Anchor(0.5f, 1.0f)

        /** Icon top-center pinned to the location. */
        public val TOP: Anchor = Anchor(0.5f, 0.0f)
    }
}
