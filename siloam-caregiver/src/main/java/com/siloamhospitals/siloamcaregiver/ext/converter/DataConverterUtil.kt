package com.siloamhospitals.siloamcaregiver.ext.converter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime

internal inline val String.toByteArrayFromHexString: ByteArray
    get() {
        val data = ByteArray(length / 2)
        var i = 0
        while (i < length) {
            data[i / 2] = ((Character.digit(get(i), 16) shl 4) + Character.digit(get(i + 1), 16)).toByte()
            i += 2
        }
        return data
    }

internal inline val ByteArray.toHexString: String
    get() = StringBuilder(size * 2).let { builder ->
        forEach { builder.append(String.format("%02x", it)) }
        builder.toString()
    }

class LocalDateTimeConverter {
    @ToJson
    fun toJson(dateTime: LocalDateTime): String {
        return dateTime.toString()
    }

    @FromJson
    fun fromJson(dateTime: String): LocalDateTime {
        return LocalDateTime.parse(dateTime)
    }
}
