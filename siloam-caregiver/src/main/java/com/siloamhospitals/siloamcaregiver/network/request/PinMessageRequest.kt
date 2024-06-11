package com.siloamhospitals.siloamcaregiver.network.request

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PinMessageRequest(
    @SerializedName("caregiver_id")
    val caregiverId: String,
    @SerializedName("hope_user_id")
    val hopeUserId: String,
    @SerializedName("is_pinned")
    val isPinned: Boolean,
)
