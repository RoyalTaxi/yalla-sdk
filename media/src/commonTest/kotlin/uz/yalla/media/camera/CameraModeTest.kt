package uz.yalla.media.camera

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotEquals

class CameraModeTest {
    @Test
    fun shouldReturnBackWhenInverseFront() {
        val result = CameraMode.Front.inverse()
        assertIs<CameraMode.Back>(result)
    }

    @Test
    fun shouldReturnFrontWhenInverseBack() {
        val result = CameraMode.Back.inverse()
        assertIs<CameraMode.Front>(result)
    }

    @Test
    fun shouldReturnOriginalModeWhenInverseTwice() {
        assertIs<CameraMode.Front>(CameraMode.Front.inverse().inverse())
        assertIs<CameraMode.Back>(CameraMode.Back.inverse().inverse())
    }

    @Test
    fun shouldReturnDifferentIdsForFrontAndBack() {
        assertNotEquals(CameraMode.Front.toId(), CameraMode.Back.toId())
    }

    @Test
    fun shouldRoundTripFrontThroughId() {
        val id = CameraMode.Front.toId()
        val restored = cameraModeFromId(id)
        assertIs<CameraMode.Front>(restored)
    }

    @Test
    fun shouldRoundTripBackThroughId() {
        val id = CameraMode.Back.toId()
        val restored = cameraModeFromId(id)
        assertIs<CameraMode.Back>(restored)
    }

    @Test
    fun shouldThrowOnUnknownCameraId() {
        assertFailsWith<IllegalArgumentException> {
            cameraModeFromId(-1)
        }
    }

    @Test
    fun shouldThrowOnArbitraryCameraId() {
        assertFailsWith<IllegalArgumentException> {
            cameraModeFromId(999)
        }
    }
}
