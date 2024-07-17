package com.siloamhospitals.siloamcaregiver.base

import android.content.Context
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.BuildConfig
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences

class SiloamCaregiver(
    appContexts: Context
) {
    companion object {
        const val ROLE_DOCTOR = 1
        const val ROLE_NURSE = 2

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
        fun initUser(
            userId: Long,
            organizationId: Long,
            context: Context,
            wardId: Long = 0L,
            role: Int
        ) {
            val mPreference by lazy {
                AppPreferences(context)
            }
            mPreference.userId = userId
            mPreference.organizationId = organizationId
            mPreference.wardId = wardId
            mPreference.role = role
        }

        fun setWardId(wardID: Long, context: Context) {
            val mPreference by lazy {
                AppPreferences(context)
            }
            mPreference.wardId = wardID
        }

        fun seFirebaseToken(context: Context, token: String) {
            val mPreference by lazy {
                AppPreferences(context)
            }
            mPreference.firebaseToken = token
        }

    }
}
