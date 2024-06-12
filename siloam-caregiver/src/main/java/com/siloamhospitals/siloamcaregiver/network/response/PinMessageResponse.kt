package com.siloamhospitals.siloamcaregiver.network.response

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PinMessageResponse(
    val data: List<PinMessageDataResponse> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PinMessageDataResponse(
    @SerializedName("is_pinned") val isPinned: Boolean = false,
)
