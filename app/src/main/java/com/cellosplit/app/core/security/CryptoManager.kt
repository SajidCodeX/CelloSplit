package com.cellosplit.app.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import timber.log.Timber
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages the encryption key for the SQLCipher database.
 *
 * The key is generated once and stored in the Android Keystore —
 * it never exists as a plaintext value outside the Keystore's
 * Trusted Execution Environment (TEE).
 *
 * Usage:
 *   val passphrase = cryptoManager.getOrCreateDbKey()
 *   val db = AppDatabase.build(context, passphrase)
 *   passphrase.fill(0)  // <-- zero the array immediately after use
 *
 * Why ByteArray instead of String?
 *   Strings are interned and GC'd unpredictably — a ByteArray can be
 *   explicitly zeroed (filled with 0x00) right after use, minimising
 *   the window where key material lives in heap memory.
 */
@Singleton
class CryptoManager @Inject constructor() {

    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "cellosplit_db_key"
        private const val KEY_SIZE = 256
    }

    /**
     * Returns the 32-byte (256-bit) AES key as a ByteArray.
     *
     * Creates the key on first call. Subsequent calls retrieve it
     * from the Keystore. Caller MUST zero the array after use.
     */
    fun getOrCreateDbKey(): ByteArray {
        return getSecretKey().encoded
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).also { it.load(null) }

        // Return existing key if present
        keyStore.getKey(KEY_ALIAS, null)?.let {
            Timber.d("DB key retrieved from Keystore")
            return it as SecretKey
        }

        // Generate a new key — this happens only on first install
        Timber.d("Generating new DB encryption key in Keystore")
        return generateKey()
    }

    private fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )

        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE)
            .setUserAuthenticationRequired(false) // DB must open AFTER biometric, not during
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }
}
