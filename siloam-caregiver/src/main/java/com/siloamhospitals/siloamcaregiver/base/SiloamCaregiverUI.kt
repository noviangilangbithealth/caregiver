package com.siloamhospitals.siloamcaregiver.base

import android.content.Context
import android.util.Log
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.CaregiverActivity
import com.siloamhospitals.siloamcaregiver.ui.chatroom.ChatroomCaregiverActivity

class SiloamCaregiverUI {


    companion object {
        private var instance: SiloamCaregiverUI? = null

        fun getInstances() : SiloamCaregiverUI {
            if (instance == null) {
                instance = SiloamCaregiverUI()
                return instance as SiloamCaregiverUI
            }

            return instance as SiloamCaregiverUI
        }


    }

    fun openCaregiver(context: Context) {
        CaregiverActivity.start(context)
    }

    fun openChatRoom(
        context: Context,
        roomName: String,
        patientName: String,
        caregiverId: String,
        channelId: String,
        doctorHopeId: Long,
        icon: String
    ) {
        val mPreference by lazy {
            AppPreferences(context)
        }
        mPreference.userId = doctorHopeId
        mPreference.notifChannelId = channelId
        mPreference.notifCaregiverId = caregiverId
        mPreference.notifRoomName = roomName
        mPreference.notifPatientName = patientName
        mPreference.notifIcon = icon
        mPreference.isFromNotif = true
        ChatroomCaregiverActivity.start(context)
    }

}
