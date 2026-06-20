package uz.yalla.datastore

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import kotlinx.coroutines.withContext
import org.koin.core.scope.Scope
import org.koin.core.scope.get
import java.security.KeyStore
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

internal actual fun createSecureStore(scope: Scope): SecureStore = AndroidKeystoreSecureStore(scope.get<Context>())

/**
 * [SecureStore] backed by AES-256-GCM with a key held in the **Android Keystore** (TEE/StrongBox-backed
 * where available), under a fixed alias generated on first use. The key never leaves the Keystore — only
 * `Base64(IV || ciphertext)` is persisted (in a dedicated `SharedPreferences` file, distinct from the
 * plain DataStore). A fresh random 12-byte IV is generated per write (GCM's nonce-reuse requirement), so
 * `setRandomizedEncryptionRequired(false)` is set deliberately: WE supply the IV, the Keystore must not.
 *
 * Deliberately NOT `EncryptedSharedPreferences` (deprecated). This is plain `SharedPreferences` used only
 * as opaque ciphertext storage; all confidentiality comes from the Keystore key + GCM here.
 */
private class AndroidKeystoreSecureStore(context: Context) : SecureStore {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(CIPHERTEXT_FILE, Context.MODE_PRIVATE)

    override suspend fun get(key: String): String? =
        withContext(ioDispatcher) {
            val stored = prefs.getString(key, null) ?: return@withContext null
            try {
                decrypt(stored)
            } catch (expected: Exception) {
                // A value that no longer decrypts (key invalidated by a credential/biometric change, or a
                // corrupt blob) is treated as absent rather than crashing every read — the caller re-auths.
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
        val iv = cipher.iv // GCM init produces a fresh random 12-byte IV.
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

    /** Loads the Keystore AES key, generating it under [KEY_ALIAS] on first use. */
    private fun secretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }
        (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let { return it.secretKey }

        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(AES_KEY_BITS)
                // Decryption supplies the stored IV via GCMParameterSpec; the default randomized-encryption
                // requirement forbids a caller-provided IV, so it must be relaxed. Encryption still uses a
                // fresh GCM-generated random IV per value (read back via cipher.iv) — no nonce reuse.
                .setRandomizedEncryptionRequired(false)
                .build()
        )
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
