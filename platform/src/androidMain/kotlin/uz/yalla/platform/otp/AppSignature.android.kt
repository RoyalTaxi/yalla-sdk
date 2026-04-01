package uz.yalla.platform.otp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Arrays

/** SHA-256 digest algorithm used for the SMS Retriever hash computation. */
private const val HASH_TYPE = "SHA-256"

/** Number of bytes to keep from the SHA-256 digest before Base64 encoding. */
private const val NUM_HASHED_BYTES = 9

/** Number of Base64 characters in the final app signature string. */
private const val NUM_BASE64_CHAR = 11

/**
 * Android actual for [getAppSignature].
 *
 * Returns `null` because the context-free expect signature cannot access
 * [android.content.Context]. Use the [getAppSignature] overload that accepts
 * a [Context] parameter for the real hash.
 *
 * @see getAppSignature
 */
actual fun getAppSignature(): String? = null // Requires context, see below

/**
 * Computes the app's SMS Retriever API hash using the package signing certificate.
 *
 * The server must append this hash to the SMS body for the SMS Retriever API
 * to automatically intercept the message without requesting `READ_SMS` permission.
 *
 * On API 28+ uses `PackageManager.GET_SIGNING_CERTIFICATES`; on older APIs falls back
 * to the deprecated `GET_SIGNATURES`.
 *
 * @param context Android application or activity context.
 * @return The 11-character Base64 hash string, or `null` if computation fails.
 * @since 0.0.6-alpha05
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
