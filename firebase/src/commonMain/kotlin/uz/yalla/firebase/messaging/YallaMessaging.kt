package uz.yalla.firebase.messaging

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.FirebaseMessaging
import dev.gitlive.firebase.messaging.messaging
import uz.yalla.firebase.YallaFirebase

/**
 * Wrapper for Firebase Cloud Messaging.
 */
class YallaMessaging {
    private val messaging: FirebaseMessaging by lazy { Firebase.messaging }

    /**
     * Get the FCM registration token.
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
     * Subscribe to a topic for receiving targeted messages.
     */
    suspend fun subscribeToTopic(topic: String) {
        try {
            messaging.subscribeToTopic(topic)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Messaging", "Failed to subscribe to topic '$topic': ${e.message}")
        }
    }

    /**
     * Unsubscribe from a topic.
     */
    suspend fun unsubscribeFromTopic(topic: String) {
        try {
            messaging.unsubscribeFromTopic(topic)
        } catch (e: Exception) {
            YallaFirebase.logger.log("Messaging", "Failed to unsubscribe from topic '$topic': ${e.message}")
        }
    }

    /**
     * Delete the FCM instance ID and data.
     */
    suspend fun deleteToken() {
        try {
            messaging.deleteToken()
        } catch (e: Exception) {
            YallaFirebase.logger.log("Messaging", "Failed to delete token: ${e.message}")
        }
    }
}
