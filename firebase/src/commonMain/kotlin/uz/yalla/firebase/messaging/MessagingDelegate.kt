package uz.yalla.firebase.messaging

/**
 * Platform-specific delegate for handling Firebase Cloud Messaging (FCM) events.
 *
 * Implement this interface in your platform entry point (Android `Service`, iOS `AppDelegate`)
 * to receive FCM token refreshes and foreground message delivery. The implementation is
 * responsible for forwarding these events to the appropriate application layer
 * (e.g. sending the token to your backend, displaying a local notification).
 *
 * Usage example:
 * ```kotlin
 * class MyMessagingDelegate : MessagingDelegate {
 *     override fun onNewToken(token: String) {
 *         // Send token to your server
 *         myBackend.updatePushToken(token)
 *     }
 *
 *     override fun onMessageReceived(title: String?, body: String?, data: Map<String, String>) {
 *         // Show a local notification or update UI
 *         notificationManager.show(title, body)
 *     }
 * }
 * ```
 *
 * @since 0.0.1
 */
interface MessagingDelegate {
    /**
     * Called when a new or refreshed FCM registration token is available.
     *
     * This is triggered on first app launch after install, when the token is invalidated
     * by Firebase, or when the user restores the app on a new device. The token must be
     * sent to your server to target push notifications to this device.
     *
     * @param token The new FCM registration token.
     * @since 0.0.1
     */
    fun onNewToken(token: String)

    /**
     * Called when a push notification is received while the app is in the foreground.
     *
     * Background and terminated-state delivery is handled by the OS and does not trigger
     * this callback. Use this to display a local notification or update the in-app UI.
     *
     * @param title The notification title, or `null` if not set by the sender.
     * @param body The notification body text, or `null` if not set by the sender.
     * @param data Key-value data payload attached to the message (may be empty).
     * @since 0.0.1
     */
    fun onMessageReceived(
        title: String?,
        body: String?,
        data: Map<String, String>
    )
}
