package com.siloamhospitals.siloamcaregiver.network.response

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WardResponse(
    val data: WardDataResponse = WardDataResponse()
)

@JsonClass(generateAdapter = true)
data class WardDataResponse(
    val data: List<WardDataDataResponse> = emptyList()
)

@JsonClass(generateAdapter = true)
data class WardDataDataResponse(
    @SerializedName("ward_list")
    val wardList: List<WardListResponse> = emptyList(),
    @SerializedName("hospital_code")
    val hospitalCode: String? = null,
    @SerializedName("organization_name")
    val orgName: String? = null,
    @SerializedName("organization_id")
    val orgId: Long? = null

)

@JsonClass(generateAdapter = true)
data class WardListResponse(
    @SerializedName("ward_id")
    val wardId: Long = 0,
    @SerializedName("ward_code")
    val wardCode: String = "",
    @SerializedName("ward_name")
    val wardName: String = "",
){
    override fun toString() = wardName
}
