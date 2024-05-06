package com.siloamhospitals.siloamcaregiver.network.response

import com.siloamhospitals.siloamcaregiver.network.AttachmentCaregiver
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AttachmentCaregiverResponse(
    val data: List<AttachmentCaregiver> = emptyList()
)
