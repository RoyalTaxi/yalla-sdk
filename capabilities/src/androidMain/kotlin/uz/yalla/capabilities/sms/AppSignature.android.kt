package uz.yalla.capabilities.sms

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import uz.yalla.capabilities.capabilitiesContextOrNull
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Arrays

private const val HASH_TYPE = "SHA-256"

private const val NUM_HASHED_BYTES = 9

private const val NUM_BASE64_CHAR = 11

private val cachedSignature by lazy {
    capabilitiesContextOrNull?.let(::getAppSignature)
}

public actual fun getAppSignature(): String? = cachedSignature

internal fun getAppSignature(context: Context): String? =
    runCatching {
        val packageName = context.packageName
        val signatures =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager
                    .getPackageInfo(
                        packageName,
                        PackageManager.GET_SIGNING_CERTIFICATES
                    ).signingInfo
                    ?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                context.packageManager
                    .getPackageInfo(
                        packageName,
                        PackageManager.GET_SIGNATURES
                    ).signatures
            }

        signatures?.firstNotNullOfOrNull { signature ->
            val hash =
                MessageDigest.getInstance(HASH_TYPE).run {
                    update("$packageName ${signature.toCharsString()}".toByteArray(StandardCharsets.UTF_8))
                    digest()
                }
            val truncatedHash = Arrays.copyOfRange(hash, 0, NUM_HASHED_BYTES)
            Base64
                .encodeToString(truncatedHash, Base64.NO_PADDING or Base64.NO_WRAP)
                .takeIf { it.length >= NUM_BASE64_CHAR }
                ?.substring(0, NUM_BASE64_CHAR)
        }
    }.getOrNull()
