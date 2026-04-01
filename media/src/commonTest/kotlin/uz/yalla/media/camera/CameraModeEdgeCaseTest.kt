package uz.yalla.media.camera

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class CameraModeEdgeCaseTest {
    // --- Sealed class instances ---

    @Test
    fun frontShouldBeSameInstance() {
        assertSame(CameraMode.Front, CameraMode.Front)
    }

    @Test
    fun backShouldBeSameInstance() {
        assertSame(CameraMode.Back, CameraMode.Back)
    }

    @Test
    fun frontShouldEqualItself() {
        assertEquals(CameraMode.Front, CameraMode.Front)
    }

    @Test
    fun backShouldEqualItself() {
        assertEquals(CameraMode.Back, CameraMode.Back)
    }

    @Test
    fun frontShouldNotEqualBack() {
        assertNotEquals(CameraMode.Front as CameraMode, CameraMode.Back as CameraMode)
    }

    // --- Subtype checks ---

    @Test
    fun frontShouldBeCameraMode() {
        assertIs<CameraMode>(CameraMode.Front)
    }

    @Test
    fun backShouldBeCameraMode() {
        assertIs<CameraMode>(CameraMode.Back)
    }

    // --- inverse symmetry ---

    @Test
    fun inverseOfFrontShouldBeBack() {
        assertIs<CameraMode.Back>(CameraMode.Front.inverse())
    }

    @Test
    fun inverseOfBackShouldBeFront() {
        assertIs<CameraMode.Front>(CameraMode.Back.inverse())
    }

    @Test
    fun inverseIsInvolutionForFront() {
        val result = CameraMode.Front.inverse().inverse()
        assertIs<CameraMode.Front>(result)
    }

    @Test
    fun inverseIsInvolutionForBack() {
        val result = CameraMode.Back.inverse().inverse()
        assertIs<CameraMode.Back>(result)
    }

    @Test
    fun tripleInverseOfFrontShouldBeBack() {
        val result =
            CameraMode.Front
                .inverse()
                .inverse()
                .inverse()
        assertIs<CameraMode.Back>(result)
    }

    @Test
    fun tripleInverseOfBackShouldBeFront() {
        val result =
            CameraMode.Back
                .inverse()
                .inverse()
                .inverse()
        assertIs<CameraMode.Front>(result)
    }

    // --- toId uniqueness ---

    @Test
    fun frontAndBackShouldHaveDifferentIds() {
        assertNotEquals(CameraMode.Front.toId(), CameraMode.Back.toId())
    }

    @Test
    fun frontIdShouldBeConsistent() {
        assertEquals(CameraMode.Front.toId(), CameraMode.Front.toId())
    }

    @Test
    fun backIdShouldBeConsistent() {
        assertEquals(CameraMode.Back.toId(), CameraMode.Back.toId())
    }

    @Test
    fun frontIdShouldBeNonNegative() {
        assertTrue(CameraMode.Front.toId() >= 0)
    }

    @Test
    fun backIdShouldBeNonNegative() {
        assertTrue(CameraMode.Back.toId() >= 0)
    }

    // --- cameraModeFromId round-trip ---

    @Test
    fun roundTripFrontThroughId() {
        val id = CameraMode.Front.toId()
        val restored = cameraModeFromId(id)
        assertEquals(CameraMode.Front, restored)
    }

    @Test
    fun roundTripBackThroughId() {
        val id = CameraMode.Back.toId()
        val restored = cameraModeFromId(id)
        assertEquals(CameraMode.Back, restored)
    }

    @Test
    fun roundTripThroughInverseAndId() {
        val original = CameraMode.Front
        val inversed = original.inverse()
        val id = inversed.toId()
        val restored = cameraModeFromId(id)
        assertIs<CameraMode.Back>(restored)
    }

    // --- cameraModeFromId invalid inputs ---

    @Test
    fun shouldThrowOnNegativeId() {
        assertFailsWith<IllegalArgumentException> {
            cameraModeFromId(-1)
        }
    }

    @Test
    fun shouldThrowOnLargeId() {
        assertFailsWith<IllegalArgumentException> {
            cameraModeFromId(999)
        }
    }

    @Test
    fun shouldThrowOnMinValueId() {
        assertFailsWith<IllegalArgumentException> {
            cameraModeFromId(Int.MIN_VALUE)
        }
    }

    @Test
    fun shouldThrowOnMaxValueId() {
        assertFailsWith<IllegalArgumentException> {
            cameraModeFromId(Int.MAX_VALUE)
        }
    }

    @Test
    fun errorMessageShouldContainUnknownId() {
        val exception =
            assertFailsWith<IllegalArgumentException> {
                cameraModeFromId(42)
            }
        assertTrue(exception.message?.contains("42") == true)
    }

    // --- When expression exhaustiveness ---

    @Test
    fun shouldBeExhaustiveInWhenExpression() {
        val modes = listOf(CameraMode.Front, CameraMode.Back)
        val names =
            modes.map { mode ->
                when (mode) {
                    CameraMode.Front -> "front"
                    CameraMode.Back -> "back"
                }
            }
        assertEquals(listOf("front", "back"), names)
    }

    // --- HashCode ---

    @Test
    fun frontShouldHaveConsistentHashCode() {
        assertEquals(CameraMode.Front.hashCode(), CameraMode.Front.hashCode())
    }

    @Test
    fun backShouldHaveConsistentHashCode() {
        assertEquals(CameraMode.Back.hashCode(), CameraMode.Back.hashCode())
    }

    // --- Collection usage ---

    @Test
    fun shouldWorkAsSetElements() {
        val modes = setOf(CameraMode.Front, CameraMode.Back, CameraMode.Front)
        assertEquals(2, modes.size)
    }

    @Test
    fun shouldWorkAsMapKeys() {
        val map =
            mapOf(
                CameraMode.Front to "selfie",
                CameraMode.Back to "world",
            )
        assertEquals("selfie", map[CameraMode.Front])
        assertEquals("world", map[CameraMode.Back])
    }
}
