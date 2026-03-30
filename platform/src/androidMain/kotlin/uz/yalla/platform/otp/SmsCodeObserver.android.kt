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

@Composable
actual fun ObserveSmsCode(onCodeReceived: (String) -> Unit) {
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
