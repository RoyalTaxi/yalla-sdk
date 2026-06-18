package uz.yalla.core.session

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

public interface SessionEventBus {
    public val unauthorized: Flow<Unit>

    public fun publishUnauthorized()
}

public class DefaultSessionEventBus : SessionEventBus {
    private val channel = Channel<Unit>(capacity = Channel.CONFLATED)

    override val unauthorized: Flow<Unit> = channel.receiveAsFlow()

    override fun publishUnauthorized() {
        channel.trySend(Unit)
    }
}
