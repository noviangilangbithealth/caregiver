package com.siloamhospitals.siloamcaregiver.network.response.rmo

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RmoParticipantsResponse(
    val data: RmoParticipantsDataResponse
)

@JsonClass(generateAdapter = true)
data class RmoParticipantsDataResponse(
    @SerializedName("is_allow_edit")
    val isAllowedit: Boolean = false,
    @SerializedName("rmo_limit")
    val rmoLimit: Int = 0,
    @SerializedName("rmo_list")
    var rmoList: List<RmoParticipantsListResponse> = emptyList()
)

@JsonClass(generateAdapter = true)
data class RmoParticipantsListResponse(
    @SerializedName("doctor_hope_id")
    val doctorHopeId: String = "",
    @SerializedName("doctor_name")
    val doctorName: String = "",
    @SerializedName("role_id")
    val roleId: Int = 0,
    @SerializedName("role_name")
    val roleName: String = "",
    @SerializedName("role_color")
    val roleColor: String = "",
    @SerializedName("image_url")
    val imageUrl: String = "",
)
