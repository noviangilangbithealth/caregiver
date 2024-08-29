package com.siloamhospitals.siloamcaregiver.network.request

import com.google.gson.annotations.SerializedName
import com.siloamhospitals.siloamcaregiver.network.AttachmentCaregiver

data class SendChatCaregiverRequest(

    @SerializedName("caregiver_id")
    val caregiverID: String,

    @SerializedName("channel_id")
    val channelID: String,

    @SerializedName("sender_id")
    val senderID: String,

    @SerializedName("sent_at")
    val sentAt: String,

    val message: String,
    val type: String,
    val attachment: List<AttachmentCaregiver>,

    @SerializedName("send_id")
    val sentID: String
)
