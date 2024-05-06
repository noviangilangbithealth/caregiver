package com.siloamhospitals.siloamcaregiver.network.response

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)

data class PatientListNotification(
    val data:String = ""
)

@JsonClass(generateAdapter = true)
data class PatientListNotificationData(
    @SerializedName("is_unread_message")
    val isUnreadMessage: Boolean = false,
)
