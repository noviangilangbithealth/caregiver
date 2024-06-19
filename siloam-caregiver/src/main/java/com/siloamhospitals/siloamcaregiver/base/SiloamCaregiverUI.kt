package com.siloamhospitals.siloamcaregiver.base

import android.content.Context
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.CaregiverActivity
import com.siloamhospitals.siloamcaregiver.ui.chatroom.ChatroomCaregiverActivity
import com.siloamhospitals.siloamcaregiver.ui.roomtype.RoomTypeCaregiverActivity

class SiloamCaregiverUI {


    companion object {
        private var instance: SiloamCaregiverUI? = null

        fun getInstances(): SiloamCaregiverUI {
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

    fun openChatHistory(
        context: Context,
        hospitalHopeId: String,
        hospitalCode: String,
        doctorHopeId: Long,
        caregiverId: String,
        patientName: String,
        localMrNumber: String,
        wardName: String,
        roomName: String,
        gender: String,
    ) {
        val mPreference by lazy {
            AppPreferences(context)
        }
        mPreference.userId = doctorHopeId
        mPreference.historyHospitalId = hospitalHopeId
        mPreference.historyHospitalUnit = hospitalCode
        mPreference.historyCaregiverId = caregiverId
        mPreference.historyPatientName = patientName
        mPreference.historyLocalMrNumber = localMrNumber
        mPreference.historyWard = wardName
        mPreference.historyRoom = roomName
        mPreference.historyGender = gender
        mPreference.isChatHistory = true
        RoomTypeCaregiverActivity.start(context)
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

    fun setChatRoom(
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
    }

}
