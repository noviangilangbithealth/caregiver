package com.siloamhospitals.siloamcaregiver.network.response

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ListHospitalWard(
    val data:String = ""
)

@JsonClass(generateAdapter = true)
data class HospitalFilter(
    @SerializedName("hospital_hope_id") val hospitalHopeId: Long? = null,
    @SerializedName("hospital_code") val hospitalCode: String? = null,
    @SerializedName("hospital_name") val hospitalName: String? = null,
    @SerializedName("ward_list") val wardList: List<WardFilter> = emptyList(),
    @SerializedName("is_urgent") val isUrgent: Boolean? = null,
    @SerializedName("is_unread") val isUnread: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class WardFilter(
    @SerializedName("ward_id") val wardId: Long? = null,
    @SerializedName("ward_name") val wardName: String? = null,
    @SerializedName("is_urgent") val isUrgent: Boolean? = null,
    @SerializedName("is_unread") val isUnread: Boolean? = null
)
