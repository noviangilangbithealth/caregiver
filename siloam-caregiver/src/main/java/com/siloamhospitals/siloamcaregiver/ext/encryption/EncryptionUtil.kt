package com.siloamhospitals.siloamcaregiver.ext.encryption

import com.siloamhospitals.siloamcaregiver.BuildConfig.*
import com.siloamhospitals.siloamcaregiver.ext.converter.toByteArrayFromHexString
import com.siloamhospitals.siloamcaregiver.ext.converter.toHexString
import java.security.MessageDigest
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal fun String.encrypt(
    phone: String,
    init: Int,
    aid: String,
    key: String
): String {
    val sha256 = Mac.getInstance(ALGORITHM_HASH).run {
        init(SecretKeySpec(phone.toByteArray(), ALGORITHM_HASH))
        doFinal(aid.toByteArray())
    }.toHexString

    @Suppress("MagicNumber")
    return encrypt(iv = sha256.substring(init, init + 16), key = key)
}

internal fun String.encrypt(iv: String, key: String) =
    with(Cipher.getInstance(CHIPER_MODE)) {
        init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(key.toByteArray(), ALGORITHM_ENCRYPT),
            IvParameterSpec(iv.toByteArray())
        )
        doFinal(toByteArray())
            .toHexString
            .uppercase(Locale.getDefault())
    }

internal fun String.encrypt(key: String) =
    with(Cipher.getInstance(CHIPER_MODE)) {
        init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(key.toByteArray(), ALGORITHM_ENCRYPT)
        )
        doFinal(toByteArray())
            .toHexString
            .uppercase(Locale.getDefault())
    }

internal fun String.decrypt(iv: String, key: String) =
    with(Cipher.getInstance(CHIPER_MODE)) {
        init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(key.toByteArray(), ALGORITHM_ENCRYPT),
            IvParameterSpec(iv.toByteArray())
        )
        doFinal(toByteArrayFromHexString)
    }.let { String(it) }

internal fun String.getMd5(): String?{
    return try {
        val digest = MessageDigest.getInstance("md5")
        digest.update(this.toByteArray())
        val bytes = digest.digest()
        val sb = StringBuilder()
        for (i in bytes.indices) {
            sb.append(String.format("%02X", bytes[i]))
        }
        sb.toString().lowercase(Locale.getDefault())
    } catch (exc: Exception) {
        null
    }
}
