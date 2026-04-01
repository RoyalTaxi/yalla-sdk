package uz.yalla.firebase.messaging

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.FirebaseMessaging
import dev.gitlive.firebase.messaging.messaging
import uz.yalla.firebase.YallaFirebase

/**
 * Wrapper for Firebase Cloud Messaging (FCM) that provides token management and topic
 * subscription with built-in error handling.
 *
 * All operations are `suspend` functions (following the GitLive Firebase SDK) and are
 * wrapped in `try-catch` blocks. Exceptions thrown by the underlying Firebase SDK are
 * swallowed and forwarded to [YallaFirebase.logger] under the `"Messaging"` tag — they
 * do not propagate to the caller.
 *
 * Access this class via [YallaFirebase.messaging]; do not instantiate it directly.
 *
 * @see YallaFirebase.messaging
 * @see MessagingDelegate
 * @since 0.0.1
 */
class YallaMessaging {
    private val messaging: FirebaseMessaging by lazy { Firebase.messaging }

    /**
     * Retrieves the current FCM registration token for this device.
     *
     * The token uniquely identifies this app installation and is used by your server to
     * send targeted push notifications. The token may change over time; use
     * [MessagingDelegate.onNewToken] to receive refresh events.
     *
     * @return The FCM registration token, or `null` if retrieval failed (error is logged
     *   via [YallaFirebase.logger]).
     * @see MessagingDelegate.onNewToken
     * @see deleteToken
     * @since 0.0.1
     */
    suspend fun getToken(): String? {
        return try {
            messaging.getToken()
        } catch (e: Exception) {
            YallaFirebase.logger.log("Messaging", "Failed to get token: ${e.message}")
            null
        }
    }

    /**
     * Subscribes this device to an FCM topic.
     *
     * Topic subscriptions allow your server to send messages to all devices subscribed
     * to that topic (e.g. `"news"`, `"driver_updates"`). The subscription is persistent
     * across app restarts.
     *
     * @param topic The topic name to subscribe to (alphanumeric, dashes, and underscores;
     *   max 900 chars).
     * @see unsubscribeFromTopic
     * @since 0.0.1
     */
    suspend fun subscribeToTopic(topic: String) {
        try {
            messaging.subscribeToTopic(topic)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Messaging", "Failed to subscribe to topic '$topic': ${e.message}")
        }
    }

    /**
     * Unsubscribes this device from an FCM topic.
     *
     * After unsubscribing, the device will no longer receive messages sent to [topic].
     *
     * @param topic The topic name to unsubscribe from.
     * @see subscribeToTopic
     * @since 0.0.1
     */
    suspend fun unsubscribeFromTopic(topic: String) {
        try {
            messaging.unsubscribeFromTopic(topic)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Messaging", "Failed to unsubscribe from topic '$topic': ${e.message}")
        }
    }

    /**
     * Deletes the FCM registration token and all associated data for this app installation.
     *
     * After deletion, a new token will be generated on the next call to [getToken] or
     * on the next [MessagingDelegate.onNewToken] callback. Use this to implement
     * a "reset push notifications" feature or on full account deletion.
     *
     * @see getToken
     * @see MessagingDelegate.onNewToken
     * @since 0.0.1
     */
    suspend fun deleteToken() {
        try {
            messaging.deleteToken()
        } catch (e: Exception) {
            YallaFirebase.logger.log("Messaging", "Failed to delete token: ${e.message}")
        }
    }
}
