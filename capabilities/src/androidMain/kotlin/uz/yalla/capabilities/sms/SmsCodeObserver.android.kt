package uz.yalla.capabilities.sms

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

private const val CONSENT_GRACE_MS = 400L

@Composable
public actual fun ObserveSmsCode(
    enabled: Boolean,
    codeLength: Int,
    alphanumeric: Boolean,
    restartKey: Any?,
    onCode: (String) -> Unit
) {
    if (!enabled) return
    LocalActivityResultRegistryOwner.current ?: return
    val context = LocalContext.current
    val currentOnCode = rememberUpdatedState(onCode)
    val currentLength = rememberUpdatedState(codeLength)
    val currentAlphanumeric = rememberUpdatedState(alphanumeric)
    var armNonce by remember { mutableIntStateOf(0) }
    var delivered by remember(restartKey) { mutableStateOf(false) }
    var seenResume by remember { mutableStateOf(false) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (seenResume) armNonce++ else seenResume = true
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val message = result.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE).orEmpty()
            extractOtp(message, currentLength.value, currentAlphanumeric.value)?.let { code -> currentOnCode.value(code) }
        }
        armNonce++
    }

    DisposableEffect(restartKey, armNonce) {
        runCatching { SmsRetriever.getClient(context).startSmsRetriever() }
        runCatching { SmsRetriever.getClient(context).startSmsUserConsent(null) }
        val handler = Handler(Looper.getMainLooper())

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(
                ctx: Context?,
                intent: Intent?
            ) {
                if (intent?.action != SmsRetriever.SMS_RETRIEVED_ACTION) return
                val extras = intent.extras ?: return
                val status = extras.get(SmsRetriever.EXTRA_STATUS) as? Status ?: return
                when (status.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                        if (message != null) {
                            extractOtp(message, currentLength.value, currentAlphanumeric.value)?.let { code ->
                                delivered = true
                                currentOnCode.value(code)
                            }
                            return
                        }
                        if (delivered) return
                        val consentIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT, Intent::class.java)
                        } else {
                            @Suppress("DEPRECATION")
                            extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT)
                        }
                        consentIntent?.let { consent ->
                            handler.postDelayed({
                                if (!delivered) runCatching { launcher.launch(consent) }.onFailure { armNonce++ }
                            }, CONSENT_GRACE_MS)
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> armNonce++
                }
            }
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
            SmsRetriever.SEND_PERMISSION,
            null,
            ContextCompat.RECEIVER_EXPORTED
        )

        onDispose {
            handler.removeCallbacksAndMessages(null)
            runCatching { context.unregisterReceiver(receiver) }
        }
    }
}
