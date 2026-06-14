package uz.yalla.core.session

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

public object UnauthorizedSessionEvents {
    private val eventsChannel = Channel<Unit>(capacity = Channel.CONFLATED)
    public val events: Flow<Unit> = eventsChannel.receiveAsFlow()

    public fun publish() {
        eventsChannel.trySend(Unit)
    }

    public fun drainPendingEventIfExists() {
        eventsChannel.tryReceive()
    }
}
