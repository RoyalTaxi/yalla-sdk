package uz.yalla.maps.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableSharedFlow
import uz.yalla.maps.model.CameraPosition
import uz.yalla.maps.model.LatLng
import uz.yalla.maps.model.LatLngBounds

/**
 * Reason the map camera started moving.
 *
 * Used by [CameraPositionState] to distinguish user gestures from programmatic animations.
 *
 * @since 0.0.1
 */
enum class CameraMoveStartedReason {
    /** Movement reason could not be determined. */
    UNKNOWN,

    /** Camera has not moved since state creation. */
    NO_MOVEMENT_YET,

    /** Movement was triggered by a user gesture (pan, pinch, etc.). */
    GESTURE,

    /** Movement was triggered by an API animation (e.g., `animate`). */
    API_ANIMATION,

    /** Movement was triggered by a developer animation. */
    DEVELOPER_ANIMATION,
}

/**
 * Default animation duration in milliseconds for [CameraPositionState.animate].
 *
 * @since 0.0.1
 */
const val DEFAULT_ANIMATION_DURATION_MS: Int = 300

internal sealed class CameraAnimationRequest {
    abstract val durationMs: Int

    data class ToPosition(
        val position: CameraPosition,
        override val durationMs: Int,
    ) : CameraAnimationRequest()

    data class ToBounds(
        val bounds: LatLngBounds,
        val padding: Int,
        override val durationMs: Int,
    ) : CameraAnimationRequest()
}

/**
 * Mutable state holder for the map camera position.
 *
 * Bridges the shared Kotlin layer and the platform map SDK by exposing reactive
 * [position], [isMoving], and [cameraMoveStartedReason] properties. Camera
 * changes can be applied via [animate], [animateToBounds], or [move].
 *
 * @param position Initial camera position.
 * @since 0.0.1
 */
@Stable
class CameraPositionState(
    position: CameraPosition =
        CameraPosition(
            target = LatLng(0.0, 0.0),
            zoom = 10f
        )
) {
    /**
     * Whether the camera is currently in motion (animating or being dragged).
     *
     * @since 0.0.1
     */
    var isMoving: Boolean by mutableStateOf(false)
        internal set

    /**
     * Reason the most recent camera movement started.
     *
     * @since 0.0.1
     */
    var cameraMoveStartedReason: CameraMoveStartedReason by mutableStateOf(
        CameraMoveStartedReason.NO_MOVEMENT_YET
    )
        internal set

    internal var rawPosition: CameraPosition by mutableStateOf(position)

    internal var positionUpdater: ((CameraPosition) -> Unit)? = null

    /**
     * The current camera position. Setting this triggers an immediate (non-animated) move.
     *
     * @since 0.0.1
     */
    var position: CameraPosition
        get() = rawPosition
        set(value) {
            positionUpdater?.invoke(value) ?: run { rawPosition = value }
        }

    internal val animationRequests = MutableSharedFlow<CameraAnimationRequest>(extraBufferCapacity = 1)

    internal val moveRequests = MutableSharedFlow<CameraPosition>(extraBufferCapacity = 1)

    /**
     * Smoothly animates the camera to the given [position].
     *
     * @param position Target camera position.
     * @param durationMs Animation duration in milliseconds.
     * @since 0.0.1
     */
    suspend fun animate(
        position: CameraPosition,
        durationMs: Int = DEFAULT_ANIMATION_DURATION_MS
    ) {
        animationRequests.emit(CameraAnimationRequest.ToPosition(position, durationMs))
    }

    /**
     * Smoothly animates the camera to fit the given [bounds] within the viewport.
     *
     * @param bounds Geographic bounding rectangle to fit.
     * @param padding Pixel padding around the bounds.
     * @param durationMs Animation duration in milliseconds.
     * @since 0.0.1
     */
    suspend fun animateToBounds(
        bounds: LatLngBounds,
        padding: Int = 64,
        durationMs: Int = DEFAULT_ANIMATION_DURATION_MS
    ) {
        animationRequests.emit(CameraAnimationRequest.ToBounds(bounds, padding, durationMs))
    }

    /**
     * Instantly moves the camera to the given [position] without animation.
     *
     * @param position Target camera position.
     * @since 0.0.1
     */
    fun move(position: CameraPosition) {
        moveRequests.tryEmit(position)
    }
}

/**
 * Creates and remembers a [CameraPositionState], optionally applying an [init] block.
 *
 * @param key Optional key for state identity across recompositions.
 * @param init Configuration block applied to the new state before it is returned.
 * @return A remembered [CameraPositionState].
 * @since 0.0.1
 */
@Composable
fun rememberCameraPositionState(
    key: String? = null,
    init: CameraPositionState.() -> Unit = {}
): CameraPositionState = remember(key) { CameraPositionState().apply(init) }
