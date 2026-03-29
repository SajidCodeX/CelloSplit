package com.cellosplit.app.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import timber.log.Timber
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.Mac
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoManager @Inject constructor() {

    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "cellosplit_db_key"
        private const val KEY_SIZE = 256
        private const val FIXED_DATA = "cellosplit_db_passphrase"
    }

    fun getOrCreateDbKey(): ByteArray {
        val secretKey = getSecretKey()
        // Use HMAC to derive exportable bytes from the Keystore key
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKey)
        return mac.doFinal(FIXED_DATA.toByteArray(Charsets.UTF_8))
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).also { it.load(null) }
        keyStore.getKey(KEY_ALIAS, null)?.let {
            Timber.d("DB key retrieved from Keystore")
            return it as SecretKey
        }
        Timber.d("Generating new DB encryption key in Keystore")
        return generateKey()
    }

    private fun generateKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_HMAC_SHA256,
            KEYSTORE_PROVIDER
        )
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_SIGN
        )
            .setUserAuthenticationRequired(false)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }
}
