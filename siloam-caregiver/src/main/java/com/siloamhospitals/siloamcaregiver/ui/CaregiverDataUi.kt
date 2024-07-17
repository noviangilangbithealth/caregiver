package com.siloamhospitals.siloamcaregiver.ui

import android.net.Uri

data class CaregiverDataUi(
    val x: String = ""
)

data class ListCaregiverPatient(
    val caregiverId: String = "",
    val patientName: String = "",
    val mrNo: String = "",
    val admissionNo: String = "",
    val ward: String = "",
    val room: String = "",
    val dpjp: String = "",
    val gender: Int = 0,
    val isUrgent: Boolean = false,
    val date: String = "",
    val hospitalCode: String = "",
    val isNew: Boolean = false,
    val isPinned: Boolean = false,
    val isOnHold:Boolean = false,
    val notification: List<NotificationIcon> = emptyList()
)

data class NotificationIcon(
    val url: String = ""
)

data class CaregiverRoomTypeUi(
    val channelId: String = "",
    val icon: String = "",
    val roomName: String = "",
    val countUnread: String = "",
    val isUrgent: Boolean = false,
    val lastMessage: String = "",
    val latestMessageAt: String = "",
    val date: String = "",
    val role: String = "",
    val senderName: String = "",
    val caregiverId: String = "",
    val isAttachment: Boolean = false,
    val isActive:Boolean = true,
    val hopeId: String = "",
    val isEmptyMessage: Boolean = false,
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
    val isVoiceNote: Boolean = false,
    val isVideo: Boolean = false,
    val isActive: Boolean = false,
)

