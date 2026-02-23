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

enum class CameraMoveStartedReason {
    UNKNOWN,
    NO_MOVEMENT_YET,
    GESTURE,
    API_ANIMATION,
    DEVELOPER_ANIMATION,
}

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

@Stable
class CameraPositionState(
    position: CameraPosition =
        CameraPosition(
            target = LatLng(0.0, 0.0),
            zoom = 10f
        )
) {
    var isMoving: Boolean by mutableStateOf(false)
        internal set

    var cameraMoveStartedReason: CameraMoveStartedReason by mutableStateOf(
        CameraMoveStartedReason.NO_MOVEMENT_YET
    )
        internal set

    internal var rawPosition: CameraPosition by mutableStateOf(position)

    internal var positionUpdater: ((CameraPosition) -> Unit)? = null

    var position: CameraPosition
        get() = rawPosition
        set(value) {
            positionUpdater?.invoke(value) ?: run { rawPosition = value }
        }

    internal val animationRequests = MutableSharedFlow<CameraAnimationRequest>(extraBufferCapacity = 1)

    internal val moveRequests = MutableSharedFlow<CameraPosition>(extraBufferCapacity = 1)

    suspend fun animate(
        position: CameraPosition,
        durationMs: Int = DEFAULT_ANIMATION_DURATION_MS
    ) {
        animationRequests.emit(CameraAnimationRequest.ToPosition(position, durationMs))
    }

    suspend fun animateToBounds(
        bounds: LatLngBounds,
        padding: Int = 64,
        durationMs: Int = DEFAULT_ANIMATION_DURATION_MS
    ) {
        animationRequests.emit(CameraAnimationRequest.ToBounds(bounds, padding, durationMs))
    }

    fun move(position: CameraPosition) {
        moveRequests.tryEmit(position)
    }
}

@Composable
fun rememberCameraPositionState(
    key: String? = null,
    init: CameraPositionState.() -> Unit = {}
): CameraPositionState = remember(key) { CameraPositionState().apply(init) }
