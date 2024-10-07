package com.siloamhospitals.siloamcaregiver.network.response.rmo

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RmoMasterDataResponse(
    val data: RmoMasterDataDataResponse
)

@JsonClass(generateAdapter = true)
data class RmoMasterDataDataResponse(
    @SerializedName("rmo_list")
    val rmoList: List<RmoMasterDataListResponse>
)


@JsonClass(generateAdapter = true)
data class RmoMasterDataListResponse(
    @SerializedName("doctor_hope_id")
    val doctorHopeId: Long = 0,
    @SerializedName("doctor_name")
    val doctorName: String = "",
)
