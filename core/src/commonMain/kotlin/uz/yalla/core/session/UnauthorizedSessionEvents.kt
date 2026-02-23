package uz.yalla.core.session

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

object UnauthorizedSessionEvents {
    private val eventsChannel = Channel<Unit>(capacity = Channel.CONFLATED)
    val events: Flow<Unit> = eventsChannel.receiveAsFlow()

    fun publish() {
        eventsChannel.trySend(Unit)
    }
}
