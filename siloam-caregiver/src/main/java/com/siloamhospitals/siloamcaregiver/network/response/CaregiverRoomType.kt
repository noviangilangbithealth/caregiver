package com.siloamhospitals.siloamcaregiver.network.response
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CaregiverRoomType(
    val data: List<CaregiverRoomTypeData> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CaregiverRoomTypeData(
    val id: String? = "",
    val name: String? = "",
    val icon: CaregiverRoomTypeIcon = CaregiverRoomTypeIcon(),
    @SerializedName("count")
    val countUnreadMessage: String? = "",
    @SerializedName("is_urgent_message")
    val isUrgentMessage: Boolean = false,
    val message: List<CaregiverRoomTypeLastMessage> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CaregiverRoomTypeLastMessage(
    val message: String? = "",
    val user: CaregiverRoomTypeLastMessageUser = CaregiverRoomTypeLastMessageUser(),
    @SerializedName("created_at")
    val createAt: String? = "",
)

@JsonClass(generateAdapter = true)
data class CaregiverRoomTypeLastMessageUser(
    val name: String? = "",
    val role: CaregiverRoomTypeLastMessageUserRole = CaregiverRoomTypeLastMessageUserRole()
)

@JsonClass(generateAdapter = true)
data class CaregiverRoomTypeLastMessageUserRole(
    val name: String? = ""
)


@JsonClass(generateAdapter = true)
data class CaregiverRoomTypeIcon(
    @SerializedName("uri")
    val url: String? = "",
    @SerializedName("uri_ext")
    val urlExt: String? = "",
)

