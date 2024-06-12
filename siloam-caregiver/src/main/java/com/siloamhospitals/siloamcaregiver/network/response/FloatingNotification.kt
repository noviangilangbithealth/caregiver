package com.siloamhospitals.siloamcaregiver.network.response

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)

data class FloatingNotification(
    val data:String = ""
)

@JsonClass(generateAdapter = true)
data class FloatingNotificationData(
    val count: Int = 0,
    @SerializedName("is_urgent_message")
    val isUrgentMessage: Boolean = false,
)
