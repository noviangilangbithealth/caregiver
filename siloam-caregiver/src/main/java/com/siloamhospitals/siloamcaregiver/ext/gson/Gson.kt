package com.siloamhospitals.siloamcaregiver.ext.gson

import android.content.Context
import androidx.annotation.RawRes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T> Context.jsonToClass(@RawRes resourceId: Int): T = Gson().fromJson(
    resources.openRawResource(resourceId).bufferedReader().use { it.readText() }, object : TypeToken<T>() {}.type
)

inline fun <reified T : Any> T.toJson(): String = Gson().toJson(this, T::class.java)

inline fun <reified T : Any> String.fromJson(): T = Gson().fromJson(this, T::class.java)
inline fun <reified T : Any> String.fromJsonOrNull(): T? {
    return try {
        Gson().fromJson(this, T::class.java)
    } catch (e: Exception) {
        null
    }
}

inline fun <reified T> String?.fromJsonTyped(): T = Gson().fromJson(this, object : TypeToken<T>() {}.type)
inline fun <reified T> String?.fromJsonTypedOrNull(): T? {
    return try {
        Gson().fromJson(this, object : TypeToken<T>() {}.type)
    } catch (e: Exception) {
        null
    }
}