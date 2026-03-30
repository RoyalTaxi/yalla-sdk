package uz.yalla.platform.browser

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberInAppBrowser(): InAppBrowserLauncher {
    val context = LocalContext.current
    return remember(context) {
        object : InAppBrowserLauncher {
            override fun open(url: String) {
                val uri = Uri.parse(url)
                if (uri.scheme.isNullOrBlank()) return
                try {
                    CustomTabsIntent.Builder()
                        .setShowTitle(true)
                        .build()
                        .launchUrl(context, uri)
                } catch (_: ActivityNotFoundException) {
                    val fallback = Intent(Intent.ACTION_VIEW, uri)
                    fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(fallback)
                }
            }
        }
    }
}
