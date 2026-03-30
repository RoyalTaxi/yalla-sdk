package uz.yalla.platform.otp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Arrays

private const val HASH_TYPE = "SHA-256"
private const val NUM_HASHED_BYTES = 9
private const val NUM_BASE64_CHAR = 11

/**
 * Computes the app's SMS Retriever API signature hash.
 *
 * This must be included in the server-sent SMS for the Retriever API
 * to automatically detect the message.
 */
actual fun getAppSignature(): String? = null // Requires context, see below

/**
 * Context-aware variant for use via Koin or from Android-specific code.
 */
fun getAppSignature(context: Context): String? {
    return runCatching {
        val packageName = context.packageName
        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            context.packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            ).signingInfo?.apkContentsSigners
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            ).signatures
        }

        signatures?.firstNotNullOfOrNull { signature ->
            val hash = MessageDigest.getInstance(HASH_TYPE).run {
                update("$packageName ${signature.toCharsString()}".toByteArray(StandardCharsets.UTF_8))
                digest()
            }
            val truncatedHash = Arrays.copyOfRange(hash, 0, NUM_HASHED_BYTES)
            Base64.encodeToString(truncatedHash, Base64.NO_PADDING or Base64.NO_WRAP)
                .takeIf { it.length >= NUM_BASE64_CHAR }
                ?.substring(0, NUM_BASE64_CHAR)
        }
    }.getOrNull()
}
