package com.siloamhospitals.siloamcaregiver.ui

import android.net.Uri

data class CaregiverDataUi(
    val x: String = ""
)

data class ListCaregiverPatient(
    val caregiverId:String = "",
    val patientName: String = "",
    val mrNo: String = "",
    val admissionNo: String = "",
    val ward: String = "",
    val room: String = "",
    val gender: Int = 0,
    val isUrgent: Boolean = false,
    val date: String = "",
    val hospitalCode: String = "",
    val notification: List<NotificationIcon> = emptyList()
)

data class NotificationIcon(
    val url: String = ""
)

data class CaregiverRoomTypeUi(
    val channelId:String = "",
    val icon:String = "",
    val roomName:String ="",
    val countUnread:String = "",
    val isUrgent: Boolean = false,
    val lastMessage:String = "",
    val date:String = "",
    val role:String ="",
    val senderName:String = "",
    val caregiverId:String =""
)


data class CaregiverChatRoomUi(
    val id: String = "",
    val name: String = "",
    val message: String = "",
    val time: String = "",
    val url: String = "",
    val color: String = "",
    val isRead: Boolean = false,
    val isSelfSender: Boolean = false,
    val isUrgent: Boolean = false,
    val uriPic: Uri? = null,
    val isDateLimit: Boolean = false,
    val isVoiceNote: Boolean = false
)

