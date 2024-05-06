package com.siloamhospitals.siloamcaregiver.network.response

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserShowResponse(
    val data: UserShowDataResponse = UserShowDataResponse()
)

@JsonClass(generateAdapter = true)
data class UserShowDataResponse(
    val name: String = "",
    @SerializedName("specialization_hope_id")
    val specializationId: String = "",
    val hospital: List<UserShowHospitalResponse> = emptyList()
)

@JsonClass(generateAdapter = true)
data class UserShowHospitalResponse(
    @SerializedName("hospital_hope_id")
    val hospitalHopeId: Int = 0,
    @SerializedName("hospital_id")
    val hospitalId: String = "",
    val alias: String = "",
    val name: String = "",
    val timeZone: Int = 0
){
    override fun toString() = alias
}
