package uz.yalla.platform.otp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

/**
 * Observes incoming SMS messages for OTP codes using the Google SMS Retriever API.
 *
 * Starts the SMS Retriever client and registers a [BroadcastReceiver] for
 * [SmsRetriever.SMS_RETRIEVED_ACTION]. When an SMS matching the app's hash is received,
 * the raw message body is forwarded to [onCodeReceived]. The caller is responsible for
 * extracting the numeric code from the message.
 *
 * This composable is **Android-only**. On iOS, SMS autofill is handled natively by the
 * system keyboard when a `UITextField` has `textContentType = .oneTimeCode` — no SDK
 * composable is needed. See ADR-015c.
 *
 * The receiver is registered with [ContextCompat.RECEIVER_EXPORTED] and unregistered
 * in the `onDispose` callback to avoid leaks.
 *
 * @param onCodeReceived Called with the raw SMS message body when an OTP SMS is received.
 * @since 0.0.6-alpha05
 */
@Composable
fun ObserveSmsCode(onCodeReceived: (String) -> Unit) {
    val context = LocalContext.current
    val currentCallback = rememberUpdatedState(onCodeReceived)

    DisposableEffect(Unit) {
        val client = SmsRetriever.getClient(context)
        client.startSmsRetriever()

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action != SmsRetriever.SMS_RETRIEVED_ACTION) return
                val extras = intent.extras ?: return
                val status = extras.get(SmsRetriever.EXTRA_STATUS) as? Status ?: return

                if (status.statusCode == CommonStatusCodes.SUCCESS) {
                    val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE).orEmpty()
                    if (message.isNotBlank()) {
                        currentCallback.value(message)
                    }
                }
            }
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
            ContextCompat.RECEIVER_EXPORTED
        )

        onDispose {
            runCatching { context.unregisterReceiver(receiver) }
        }
    }
}
