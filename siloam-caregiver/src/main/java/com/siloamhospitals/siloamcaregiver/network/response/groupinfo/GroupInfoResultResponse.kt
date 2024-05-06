package com.siloamhospitals.siloamcaregiver.network.response.groupinfo

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupInfoResultResponse(
    val id: String = "",
    val name: String = "",
    @SerializedName("organization_id")
    val organizationId: String = "",
    @SerializedName("local_mr_no")
    val localMrNo: String = "",
    @SerializedName("admission_no")
    val admissionNo: String = "",
    @SerializedName("phone_number")
    val phoneNumber: String = "",
    @SerializedName("ward_id")
    val wardId: String = "",
    @SerializedName("ward_name")
    val wardName: String = "",
    @SerializedName("patient_id")
    val patientId: String = "",
    @SerializedName("room_no")
    val roomNo: String = "",
    val gender: Int = 0,
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("organization_code")
    val organizationCode: String = "",
    @SerializedName("is_active")
    val isActive: Boolean = false,
    val payer: String = "",
    val members: List<GroupInfoMemberResponse> = emptyList()
)
