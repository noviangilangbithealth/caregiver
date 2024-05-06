package com.siloamhospitals.siloamcaregiver.network.response.groupinfo

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupInfoAttachmentResponse(
    val id: String = "",
    @SerializedName("sender_id")
    val senderId: String = "",
    val attachment: List<GroupInfoAttachmentDetailResponse> = emptyList(),
    @SerializedName("sent_at")
    val sentAt: String = "",
    @SerializedName("created_at")
    val createAt: String = ""
)

@JsonClass(generateAdapter = true)
data class GroupInfoAttachmentDetailResponse(
    val name: String = "",
    val uri: String = "",
    @SerializedName("uri_ext")
    val uriText: String = "",
    @SerializedName("original_name")
    val originalName: String = ""
)
