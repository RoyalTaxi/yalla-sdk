package uz.yalla.maps.api.model

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import uz.yalla.core.geo.GeoPoint
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CameraPositionTest {
    private val base = CameraPosition(target = GeoPoint(40.0, 71.0), zoom = 15f, bearing = 90f, tilt = 30f)

    @Test
    fun equalWithinThresholds() {
        val nudged =
            base.copy(
                target = GeoPoint(40.0 + 5e-7, 71.0 - 5e-7),
                zoom = 15f + 5e-4f,
                bearing = 90f + 0.05f,
                tilt = 30f + 0.05f
            )
        assertTrue(base.approximatelyEquals(nudged))
    }

    @Test
    fun differsWhenLatLngExceedsEpsilon() {
        assertFalse(base.approximatelyEquals(base.copy(target = GeoPoint(40.0 + 1e-5, 71.0))))
    }

    @Test
    fun differsWhenZoomExceedsEpsilon() {
        assertFalse(base.approximatelyEquals(base.copy(zoom = 15f + 1e-2f)))
    }

    @Test
    fun differsWhenBearingExceedsEpsilon() {
        assertFalse(base.approximatelyEquals(base.copy(bearing = 90f + 1f)))
    }

    @Test
    fun paddingIsIgnored() {
        val withPadding = base.copy(padding = PaddingValues(8.dp))
        assertTrue(base.approximatelyEquals(withPadding))
    }
}
