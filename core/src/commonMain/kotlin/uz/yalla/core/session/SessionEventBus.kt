package uz.yalla.core.session

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

public interface SessionEventBus {
    public val unauthorized: Flow<Unit>

    public fun publishUnauthorized()
}

internal class DefaultSessionEventBus : SessionEventBus {
    private val _unauthorized =
        MutableSharedFlow<Unit>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

    override val unauthorized: Flow<Unit> = _unauthorized.asSharedFlow()

    override fun publishUnauthorized() {
        _unauthorized.tryEmit(Unit)
    }
}

public fun createSessionEventBus(): SessionEventBus = DefaultSessionEventBus()
