package com.siloamhospitals.siloamcaregiver.network.request

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PinChatRequest (
    @SerializedName("message_id")
    val messageId: String,
    @SerializedName("user_hope_id")
    val userHopeId: String,
    val pin: Boolean
)