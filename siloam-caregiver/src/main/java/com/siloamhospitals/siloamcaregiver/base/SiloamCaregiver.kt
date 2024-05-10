package com.siloamhospitals.siloamcaregiver.base

import android.content.Context
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.BuildConfig
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.chatroom.ChatroomCaregiverActivity

class SiloamCaregiver(
    appContexts: Context
) {
    companion object {
        var appContext: Context? = null

        var instance: SiloamCaregiver? = null

        fun init(context: Context) {
            if (instance == null) {
                instance = SiloamCaregiver(context)

                Logger.addLogAdapter(object : AndroidLogAdapter(
                    PrettyFormatStrategy.newBuilder()
                        .showThreadInfo(false)
                        .tag("CAREGIVER_COMMUNICATION")
                        .build()
                ) {
                    override fun isLoggable(priority: Int, tag: String?) = BuildConfig.DEBUG
                })


            }

        }

        fun checkCaregiverInitialized(): Boolean {
            if (instance == null) {
                return false
            }

            return true
        }

        //todo Add more parameter for fullfill the requirement
        //this function just an example
        fun initUser(userId: Long, organizationId: Long, context: Context, wardId: Long = 0L) {
            val mPreference by lazy {
                AppPreferences(context)
            }
            mPreference.userId = userId
            mPreference.organizationId = organizationId
            mPreference.wardId = wardId
        }

        fun openChatRoom(
            context: Context,
            roomName: String,
            patientName: String,
            caregiverId: String,
            channelId: String,
            doctorHopeId: String,
            icon: String
        ) {
            val mPreference by lazy {
                AppPreferences(context)
            }
            mPreference.userId = doctorHopeId.toLong()
            mPreference.notifChannelId = channelId
            mPreference.notifCaregiverId = caregiverId
            mPreference.notifRoomName = roomName
            mPreference.notifPatientName = patientName
            mPreference.notifIcon = icon
            mPreference.isFromNotif = true
            ChatroomCaregiverActivity.start(context)
        }

    }
}
