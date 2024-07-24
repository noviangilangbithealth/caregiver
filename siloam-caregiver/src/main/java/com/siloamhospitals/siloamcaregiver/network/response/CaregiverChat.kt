package com.siloamhospitals.siloamcaregiver.network.response

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CaregiverChat(
    val data: String = ""
)

@JsonClass(generateAdapter = true)
data class CaregiverChatListData(
    val meta: CaregiverChatMeta? = CaregiverChatMeta(),
    val data: List<CaregiverChatData>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class CaregiverChatData (
    val id: String? = "",

    @SerializedName("sender_id")
    val senderID: String? = "",

    val message: String? = "",
    val type: Long? = 0,
    val attachment: List<CaregiverChatAttachment?>? = emptyList(),

    @SerializedName("action_status")
    val actionStatus: Boolean? = false,

    @SerializedName("created_at")
    val createdAt: String? = "",

    @SerializedName("sent_at")
    val sentAt: String? = "",

    @SerializedName("is_received")
    val isReceived: Boolean? = false,

    @SerializedName("received_at")
    val receivedAt: Any? = null,

    @SerializedName("is_readed")
    val isReaded: Boolean? = false,

    @SerializedName("readed_at")
    val readedAt: Any? = null,

    val user: CaregiverChatUser? = CaregiverChatUser(),

    @SerializedName("caregiver_id")
    val caregiverId: String? = null,

    @SerializedName("channel_id")
    val channelId: String? = null,

    @SerializedName("is_active")
    val isActive: Boolean? = false
)

@JsonClass(generateAdapter = true)
data class CaregiverChatAttachment(
    val name: String? = "",
    val uri: String? = "",

    @SerializedName("uri_ext")
    val uriExt: String? = "",

    @SerializedName("original_name")
    val originalName: String? = ""
)

@JsonClass(generateAdapter = true)
data class CaregiverChatUser (
    val id: String? = "",
    val name: String? = "",

    @SerializedName("role_id")
    val roleID: Long? = 0,

    @SerializedName("hope_user_id")
    val hopeUserID: String? = "",

    val role: CaregiverChatRole? = CaregiverChatRole()
)

@JsonClass(generateAdapter = true)
data class CaregiverChatRole (
    val id: Long? = 0,
    val name: String? = "",
    val color: String? = ""
)

@JsonClass(generateAdapter = true)
data class CaregiverChatMeta (
    val total: Long? = 0,

    @SerializedName("per_page")
    val perPage: Long? = 0,

    @SerializedName("current_page")
    val currentPage: Long? = 0,

    @SerializedName("last_page")
    val lastPage: Long? = 0,

    @SerializedName("first_page")
    val firstPage: Long? = 0,

    @SerializedName("first_page_url")
    val firstPageURL: String? = "",

    @SerializedName("last_page_url")
    val lastPageURL: String? = "",

    @SerializedName("next_page_url")
    val nextPageURL: Any? = null,

    @SerializedName("previous_page_url")
    val previousPageURL: Any? = null
)

