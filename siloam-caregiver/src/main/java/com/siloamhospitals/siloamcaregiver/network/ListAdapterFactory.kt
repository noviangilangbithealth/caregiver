package com.siloamhospitals.siloamcaregiver.network

import java.lang.reflect.ParameterizedType

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken

class ListAdapterFactory : TypeAdapterFactory {

    @Suppress("UNCHECKED_CAST")
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        return if (type.rawType == List::class.java) {
            try {
                val clazz: Class<T> = (type.type as ParameterizedType).actualTypeArguments[0] as Class<T>
                ListAdapter(clazz, gson) as TypeAdapter<T>
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
}
