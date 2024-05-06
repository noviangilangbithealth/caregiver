package com.siloamhospitals.siloamcaregiver.network.response

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CaregiverList(
    val status: Boolean = false,
    val data: String = ""
)

@JsonClass(generateAdapter = true)
data class CaregiverListData(
    val meta: CaregiverListMeta = CaregiverListMeta(),
    val data: List<CaregiverPatientListData> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CaregiverListMeta(
    val total: Int? = 0,
    @SerializedName("per_page")
    val perPage: Int? = 0,
    @SerializedName("current_page")
    val currentPage: Int? = 0,
    @SerializedName("last_page")
    val lastPage: Int? = 0,
    @SerializedName("first_page")
    val firstPage: Int? = 0,
    @SerializedName("first_page_url")
    val firstPageUrl: String? = "",
    @SerializedName("last_page_url")
    val lastPageUrl: String? = "",
    @SerializedName("next_page_url")
    val nextPageUrl: String? = "",
)

@JsonClass(generateAdapter = true)
data class CaregiverPatientListData(
    val id: String? = "",
    val name: String? = "",
    @SerializedName("organization_id")
    val orgId: String? = "",
    @SerializedName("local_mr_no")
    val localMrNo: String? = "",
    @SerializedName("admission_no")
    val admissionNo: String? = "",
    @SerializedName("ward_id")
    val wardId: String? = "",
    @SerializedName("ward_name")
    val wardName: String? = "",
    @SerializedName("patient_id")
    val patientId: String? = "",
    @SerializedName("room_no")
    val roomNo: String? = "",
    val gender: Int? = 1,
    @SerializedName("created_at")
    val createAt: String? = "",
    @SerializedName("updated_at")
    val updatedAt: String? = "",
    @SerializedName("organization_code")
    val orgCode: String? = "",
    @SerializedName("unread_message")
    val unreadMessage: String? = "",
    @SerializedName("is_urgent_message")
    val isUrgent: Boolean? = false,
    val notifications: List<CaregiverPatientNotification> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CaregiverPatientNotification(
    val type: String? = "",
    @SerializedName("is_unread")
    val unread: Boolean? = false,
    val icon: CaregiverIconNotification = CaregiverIconNotification()
)

@JsonClass(generateAdapter = true)
data class CaregiverIconNotification(
    val name: String? = "",
    val uri: String? = "",
    @SerializedName("uri_ext")
    val uriExt: String? = ""
)
