package com.siloamhospitals.siloamcaregiver.network

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import java.io.IOException

class ListAdapter<T>(private val clazz: Class<T>, private val gson: Gson) : TypeAdapter<List<T>>() {

    @Throws(IOException::class)
    override fun read(reader: JsonReader): List<T>? = when (reader.peek()) {
        JsonToken.BEGIN_OBJECT -> {
            val item = gson.fromJson<T>(reader, clazz)
            listOf(item)
        }
        JsonToken.BEGIN_ARRAY -> {
            val list = ArrayList<T>()
            reader.beginArray()
            while (reader.hasNext()) {
                val item = gson.fromJson<T>(reader, clazz)
                list.add(item)
            }
            reader.endArray()
            list
        }
        else -> null
    }

    @Throws(IOException::class)
    override fun write(out: com.google.gson.stream.JsonWriter?, collection: List<T>?) {
        if (collection == null) {
            out?.nullValue()
            return
        }
        val elementTypeAdapter = gson.getAdapter(object : TypeToken<T>() {
        })
        out?.beginArray()
        for (element in collection) {
            elementTypeAdapter.write(out, element)
        }
        out?.endArray()
    }
}
