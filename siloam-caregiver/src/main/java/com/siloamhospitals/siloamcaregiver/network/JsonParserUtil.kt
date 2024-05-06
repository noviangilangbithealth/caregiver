package com.siloamhospitals.siloamcaregiver.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

internal inline fun <reified T> Moshi.getAdapter() =
    adapter(T::class.java)

internal inline fun <reified T> Moshi.getLisAdapter() =
    adapter<List<T>>(Types.newParameterizedType(List::class.java, T::class.java))
