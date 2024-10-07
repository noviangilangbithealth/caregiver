package com.siloamhospitals.siloamcaregiver.network.request.rmo

import com.google.gson.annotations.SerializedName

data class SubmitRmoRequest(

    @SerializedName("organization_id")
    val organizationId: String,
    @SerializedName("ward_id")
    val wardId: String,
    @SerializedName("admission_id")
    val admissionId: String = "",
    val members: List<SubmitRmoMembersRequest> = emptyList()

)

data class SubmitRmoMembersRequest(
    @SerializedName("hope_user_id")
    val hopeUserId: Long,
    val name: String,
    @SerializedName("role_id")
    val roleId: Int = 0,

    )
