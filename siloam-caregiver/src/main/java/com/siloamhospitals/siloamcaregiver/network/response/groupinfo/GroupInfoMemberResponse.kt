package com.siloamhospitals.siloamcaregiver.network.response.groupinfo

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupInfoMemberResponse(
    val id: String = "",
    @SerializedName("caregiver_id")
    val caregiverId: String = "",
    @SerializedName("user_id")
    val userId: String = "",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("role_id")
    val roleId: Int = 0,
    val role: MemberRoleResponse = MemberRoleResponse(),
    val user: MemberUserResponse = MemberUserResponse()
)

@JsonClass(generateAdapter = true)
data class MemberRoleResponse(
    val id: Int = 0,
    val name: String = "",
    val color: String = ""
)

@JsonClass(generateAdapter = true)
data class MemberUserResponse(
    val id: String = "",
    val name: String = "",
    @SerializedName("hope_user_id")
    val hopeUserId: String = "",
    @SerializedName("last_online_at")
    val lastOnlineAt: String = "",
)
