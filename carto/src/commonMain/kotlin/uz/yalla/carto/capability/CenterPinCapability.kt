package uz.yalla.carto.capability

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.yalla.carto.CartoBuilder
import uz.yalla.carto.CartoMap
import uz.yalla.carto.capability.framework.Capability
import uz.yalla.carto.capability.framework.CapabilityContext
import uz.yalla.carto.capability.framework.CapabilityHandle
import uz.yalla.carto.capability.framework.CapabilityKey
import uz.yalla.carto.state.CenterPinState
import uz.yalla.carto.state.MapEvent

public class CenterPinHandle internal constructor(
    scope: CoroutineScope,
    events: SharedFlow<MapEvent>
) : CapabilityHandle {
    private val mutableState = MutableStateFlow(CenterPinState.INITIAL)
    public val state: StateFlow<CenterPinState> = mutableState.asStateFlow()

    init {
        events
            .onEach { event ->
                when (event) {
                    is MapEvent.CameraMoved ->
                        mutableState.value =
                            mutableState.value.copy(
                                point = event.view.target,
                                isMoving = true,
                                isByUser = event.isByUser
                            )
                    is MapEvent.CameraIdle ->
                        mutableState.value =
                            CenterPinState(point = event.view.target, isMoving = false, isByUser = event.isByUser)
                    else -> Unit
                }
            }.launchIn(scope)
    }
}

public object CenterPinCapability : Capability<CenterPinHandle> {
    override val key: CapabilityKey<CenterPinHandle> = CapabilityKey("center-pin")

    override fun create(context: CapabilityContext): CenterPinHandle = CenterPinHandle(context.scope, context.events)
}

public fun CartoBuilder.centerPin() {
    add(CenterPinCapability)
}

public val CartoMap.centerPin: CenterPinHandle get() = capability(CenterPinCapability.key)
