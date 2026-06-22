package uz.yalla.datastore

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import kotlinx.coroutines.withContext
import org.koin.core.scope.Scope
import uz.yalla.datastore.AndroidKeystoreSecureStore.Companion.KEY_ALIAS
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

internal actual fun createSecureStore(scope: Scope): SecureStore = AndroidKeystoreSecureStore(scope.get<Context>())

private class AndroidKeystoreSecureStore(
    context: Context
) : SecureStore {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(CIPHERTEXT_FILE, Context.MODE_PRIVATE)

    override suspend fun get(key: String): String? =
        withContext(ioDispatcher) {
            val stored = prefs.getString(key, null) ?: return@withContext null
            try {
                decrypt(stored)
            } catch (expected: Exception) {
                null
            }
        }

    override suspend fun put(
        key: String,
        value: String
    ) {
        withContext(ioDispatcher) {
            prefs.edit().putString(key, encrypt(value)).apply()
        }
    }

    override suspend fun remove(key: String) {
        withContext(ioDispatcher) {
            prefs.edit().remove(key).apply()
        }
    }

    private fun encrypt(plaintext: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(plaintext.encodeToByteArray())
        return Base64.getEncoder().encodeToString(iv + ciphertext)
    }

    private fun decrypt(encoded: String): String {
        val blob = Base64.getDecoder().decode(encoded)
        val iv = blob.copyOfRange(0, IV_LENGTH)
        val ciphertext = blob.copyOfRange(IV_LENGTH, blob.size)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey(), GCMParameterSpec(GCM_TAG_BITS, iv))
        return cipher.doFinal(ciphertext).decodeToString()
    }

    private fun secretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }

        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
        val spec =
            KeyGenParameterSpec
                .Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(AES_KEY_BITS)
                .setRandomizedEncryptionRequired(false)
                .build()
        generator.init(spec)
        return generator.generateKey()
    }

    private companion object {
        const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        const val KEY_ALIAS = "uz.yalla.datastore.secure"
        const val CIPHERTEXT_FILE = "yalla_secure_store"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val AES_KEY_BITS = 256
        const val GCM_TAG_BITS = 128
        const val IV_LENGTH = 12
    }
}
