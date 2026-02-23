package uz.yalla.firebase.messaging

/**
 * Interface for handling push notification events.
 * Implement this on each platform to handle notifications appropriately.
 */
interface MessagingDelegate {
    /**
     * Called when a new FCM token is received.
     */
    fun onNewToken(token: String)

    /**
     * Called when a push notification is received while app is in foreground.
     */
    fun onMessageReceived(
        title: String?,
        body: String?,
        data: Map<String, String>
    )
}
