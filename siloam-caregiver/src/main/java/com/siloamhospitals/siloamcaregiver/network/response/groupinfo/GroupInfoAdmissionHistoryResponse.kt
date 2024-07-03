package com.siloamhospitals.siloamcaregiver.network.response.groupinfo

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupInfoAdmissionHistoryResponse(
    val data: List<GroupInfoAdmissionHistoryDataResponse> = emptyList()
)

@JsonClass(generateAdapter = true)
data class GroupInfoAdmissionHistoryDataResponse(
    val id: String = "",
    @SerializedName("organization_id")
    val orgId: String = "",
    @SerializedName("organization_code")
    val orgCode: String = "",
    @SerializedName("admission_no")
    val admissionNo: String = "",
    @SerializedName("created_at")
    val createAt: String = "",
    @SerializedName("local_mr_no")
    val localMrNo: String = "",
    @SerializedName("patient_id")
    val patientId: String = "",
    @SerializedName("dpjp_name")
    val doctorName: String = "",
    @SerializedName("room_no")
    val roomNo: String = "",
    @SerializedName("ward_name")
    val wardName: String = "",
)
