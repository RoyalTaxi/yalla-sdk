package uz.yalla.maps.api.model

import androidx.compose.runtime.Immutable

@Immutable
data class Anchor(val x: Float, val y: Float) {
    companion object {
        val CENTER = Anchor(0.5f, 0.5f)
        val BOTTOM = Anchor(0.5f, 1.0f)
        val TOP = Anchor(0.5f, 0.0f)
    }
}
