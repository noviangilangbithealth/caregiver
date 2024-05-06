package com.siloamhospitals.siloamcaregiver.network.response.groupinfo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupInfoResponse(
    val data: GroupInfoDataResponse = GroupInfoDataResponse()
)

@JsonClass(generateAdapter = true)
data class GroupInfoDataResponse(
    val result: GroupInfoResultResponse = GroupInfoResultResponse(),
    val attachment: List<GroupInfoAttachmentResponse> = emptyList()
)
