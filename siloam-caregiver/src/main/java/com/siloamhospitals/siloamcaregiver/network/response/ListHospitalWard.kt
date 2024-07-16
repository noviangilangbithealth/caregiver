package com.siloamhospitals.siloamcaregiver.network.response

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ListHospitalWard(
    val data:String = ""
)

@JsonClass(generateAdapter = true)
data class HospitalFilter(
    @SerializedName("id") val hospitalHopeId: Long? = null,
    @SerializedName("code") val hospitalCode: String? = null,
    @SerializedName("name") val hospitalName: String? = null,
    @SerializedName("wards") val wardList: List<WardFilter> = emptyList(),
    @SerializedName("is_urgent") val isUrgent: Boolean? = null,
    @SerializedName("is_unread") val isUnread: Boolean = false
)

@JsonClass(generateAdapter = true)
data class WardFilter(
    @SerializedName("ward_id") val wardId: Long? = null,
    @SerializedName("name") val wardName: String? = null,
    @SerializedName("is_urgent") val isUrgent: Boolean? = null,
    @SerializedName("is_unread") val isUnread: Boolean? = null
)
