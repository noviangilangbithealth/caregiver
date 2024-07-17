package com.siloamhospitals.siloamcaregiver.shared

import android.content.Context
import android.content.SharedPreferences
import com.siloamhospitals.siloamcaregiver.ext.gson.fromJsonTypedOrNull
import com.siloamhospitals.siloamcaregiver.ext.gson.toJson

class AppPreferences(val context: Context) {

    companion object {
        private const val ID_MEMBER = "member_id"

        private const val TOKEN_FIREBASE = "firebase_token"
        private const val TOKEN_REFRESH = "refresh_token"
        private const val TOKEN_SESSION = "session_token"

        private const val ON_BOARDING = "on_boarding"
        private const val IS_UPDATED = "is_updated"
        private const val IS_PHONE_NUMBER_CHANGED = "is_phone_number_changed"

        private const val COUNTRY_CODE = "country_code"
        private const val HOME_WIZARD = "home_wizards"
        private const val PROFILE_USER = "user_profile"
        private const val UNREAD_NOTIFICATION = "unread_notification"

        private const val USER_FIRST_NAME = "user_first_name"
        private const val USER_LAST_NAME = "user_last_name"
        private const val USER_PHONE_NUMBER = "user_phone_number"
        private const val USER_EMAIL_ADDRESS = "user_email_address"

        private const val SETTING_LANGUAGE = "setting_language"

        private const val SAVED_AUTH = "saved_auth"
        private const val SAVED_TIME_VERIFICATION = "saved_time_verification"

        private const val LIB_ID = "com.siloamhospitals.siloamcaregiver"


        private const val USER_ID = "userId"
        private const val ROLE_CAREGIVER = "roleCaregiver"
        private const val ORG_ID = "orgId"
        private const val WARD_ID = "wardId"

        private const val CAREGIVER_ID = "caregiverId"
        private const val HISTORY_CAREGIVER_ID = "historyCaregiverId"
        private const val HISTORY_HOSPITAL_ID = "historyHospitalId"
        private const val HISTORY_HOSPITAL_UNIT = "historyHospitalUnit"
        private const val HISTORY_ROOM = "historyRoom"
        private const val HISTORY_WARD = "historyWard"
        private const val HISTORY_LOCAL_MR_NUMBER = "historyLocalMrNumber"
        private const val HISTORY_GENDER = "historyGender"
        private const val HISTORY_PATIENT_NAME = "historyPatientName"
        private const val IS_HISTORY_CHAT_ROOM = "historyChatRoom"
        private const val CHANNEL_ID = "channelId"
        private const val ROOM_NAME = "roomName"
        private const val URL_ICON = "urlIcon"
        private const val PATIENT_NAME = "patientName"
        private const val DESCRIPTION = "description"
        private const val GENDER = "gender"
        private const val RECENT_TAG = "recentTag"
        private const val ORG_ID_RECENT = "recentOrgIg"
        private const val ORG_CODE_RECENT = "recentOrgCode"
        private const val WARD_ID_RECENT = "recentWardid"
        private const val WARD_NAME_RECENT = "recentWardName"
        private const val IS_FROM_RECENT = "isFromRecent"

        private const val NOTIF_CAREGIVER_ID = "notifCaregiverId"
        private const val NOTIF_CHANNEL_ID = "notifChannelId"
        private const val NOTIF_PATIENT_NAME = "notifPatientName"
        private const val NOTIF_ROOM_NAME = "notifRoomName"
        private const val NOTIF_ICON = "notifIcon"
        private const val NOTIF = "isFromNotif"
        private const val CHAT_HISTORY = "isChatHistory"
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    var isFromRecent: Boolean
        get() = findPreference(IS_FROM_RECENT, false)
        set(value) {
            putPreference(IS_FROM_RECENT, value)
        }


    var caregiverId: String
        get() = findPreference(CAREGIVER_ID, "")
        set(value) {
            putPreference(CAREGIVER_ID, value)
        }

    var channelId: String
        get() = findPreference(CHANNEL_ID, "")
        set(value) {
            putPreference(CHANNEL_ID, value)
        }

    var roomName: String
        get() = findPreference(ROOM_NAME, "")
        set(value) {
            putPreference(ROOM_NAME, value)
        }

    var urlIcon: String
        get() = findPreference(URL_ICON, "")
        set(value) {
            putPreference(URL_ICON, value)
        }

    var patientName: String
        get() = findPreference(PATIENT_NAME, "")
        set(value) {
            putPreference(PATIENT_NAME, value)
        }

    var description: String
        get() = findPreference(DESCRIPTION, "")
        set(value) {
            putPreference(DESCRIPTION, value)
        }

    var gender: Int
        get() = findPreference(GENDER, 0)
        set(value) {
            putPreference(GENDER, value)
        }

    var recentTag: String
        get() = findPreference(RECENT_TAG, "")
        set(value) {
            putPreference(RECENT_TAG, value)
        }

    var recentOrgCode: String
        get() = findPreference(ORG_CODE_RECENT, "")
        set(value) {
            putPreference(ORG_CODE_RECENT, value)
        }

    var recentWardName: String
        get() = findPreference(WARD_NAME_RECENT, "")
        set(value) {
            putPreference(WARD_NAME_RECENT, value)
        }

    var recentOrgId: Long
        get() = findPreference(ORG_ID_RECENT, 0)
        set(value) {
            putPreference(ORG_ID_RECENT, value)
        }

    var recentWardId: Long
        get() = findPreference(WARD_ID_RECENT, 0)
        set(value) {
            putPreference(WARD_ID_RECENT, value)
        }


    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(LIB_ID, Context.MODE_PRIVATE)
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

//    var auth: Auth?
//        get() = findPreference(SAVED_AUTH, "").fromJsonOrNull()
//        set(value) {
//            putPreference(SAVED_AUTH, value?.toJson())
//        }
//
//    private var userProfile: Profile?
//        get() = findPreference(PROFILE_USER, "").fromJsonOrNull()
//        set(value) {
//            putPreference(PROFILE_USER, value?.toJson())
//        }

    private var homeWizard: List<String>
        get() = findPreference(HOME_WIZARD, "").fromJsonTypedOrNull() ?: emptyList()
        set(value) {
            putPreference(HOME_WIZARD, value.toJson())
        }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    var isHistoryChatRoom: Boolean
        get() = findPreference(IS_HISTORY_CHAT_ROOM, false)
        set(value) {
            putPreference(IS_HISTORY_CHAT_ROOM, value)
        }



    var isChatHistory: Boolean
        get() = findPreference(NOTIF, false)
        set(value) {
            putPreference(NOTIF, value)
        }

    var historyCaregiverId: String
        get() = findPreference(HISTORY_CAREGIVER_ID, "")
        set(value) {
            putPreference(HISTORY_CAREGIVER_ID, value)
        }

    var historyHospitalId: String
        get() = findPreference(HISTORY_HOSPITAL_ID, "")
        set(value) {
            putPreference(HISTORY_HOSPITAL_ID, value)
        }

    var historyWard: String
        get() = findPreference(HISTORY_WARD, "")
        set(value) {
            putPreference(HISTORY_WARD, value)
        }

    var historyHospitalUnit: String
        get() = findPreference(HISTORY_HOSPITAL_UNIT, "")
        set(value) {
            putPreference(HISTORY_HOSPITAL_UNIT, value)
        }

    var historyRoom: String
        get() = findPreference(HISTORY_ROOM, "")
        set(value) {
            putPreference(HISTORY_ROOM, value)
        }

    var historyLocalMrNumber: String
        get() = findPreference(HISTORY_LOCAL_MR_NUMBER, "")
        set(value) {
            putPreference(HISTORY_LOCAL_MR_NUMBER, value)
        }

    var historyGender: String
        get() = findPreference(HISTORY_GENDER, "")
        set(value) {
            putPreference(HISTORY_GENDER, value)
        }

    var historyPatientName: String
        get() = findPreference(HISTORY_PATIENT_NAME, "")
        set(value) {
            putPreference(HISTORY_PATIENT_NAME, value)
        }


    var isFromNotif: Boolean
        get() = findPreference(NOTIF, false)
        set(value) {
            putPreference(NOTIF, value)
        }

    var notifCaregiverId: String
        get() = findPreference(NOTIF_CAREGIVER_ID, "")
        set(value) {
            putPreference(NOTIF_CAREGIVER_ID, value)
        }

    var notifChannelId: String
        get() = findPreference(NOTIF_CHANNEL_ID, "")
        set(value) {
            putPreference(NOTIF_CHANNEL_ID, value)
        }

    var notifRoomName: String
        get() = findPreference(NOTIF_ROOM_NAME, "")
        set(value) {
            putPreference(NOTIF_ROOM_NAME, value)
        }

    var notifIcon: String
        get() = findPreference(NOTIF_ICON, "")
        set(value) {
            putPreference(NOTIF_ICON, value)
        }

    var notifPatientName: String
        get() = findPreference(NOTIF_PATIENT_NAME, "")
        set(value) {
            putPreference(NOTIF_PATIENT_NAME, value)
        }

    var userId: Long
        get() = findPreference(USER_ID, 0)
        set(value) {
            putPreference(USER_ID, value)
        }

    var role: Int
        get() = findPreference(ROLE_CAREGIVER, 0)
        set(value) {
            putPreference(ROLE_CAREGIVER, value)
        }


    var organizationId: Long
        get() = findPreference(ORG_ID, 0)
        set(value) {
            putPreference(ORG_ID, value)
        }

    var wardId: Long
        get() = findPreference(WARD_ID, 0)
        set(value) {
            putPreference(WARD_ID, value)
        }

    var memberId: Long
        get() = findPreference(ID_MEMBER, 0)
        set(value) {
            putPreference(ID_MEMBER, value)
        }

//    var firstName: String
//        get() = findPreference(USER_FIRST_NAME, "").fromHtmlFormat()
//        set(value) {
//            putPreference(USER_FIRST_NAME, value)
//        }
//
//    var lastName: String
//        get() = findPreference(USER_LAST_NAME, "").fromHtmlFormat()
//        set(value) {
//            putPreference(USER_LAST_NAME, value)
//        }

    var phoneNumber: String
        get() = findPreference(USER_PHONE_NUMBER, "")
        set(value) {
            putPreference(USER_PHONE_NUMBER, value)
        }

    var email: String
        get() = findPreference(USER_EMAIL_ADDRESS, "")
        set(value) {
            putPreference(USER_EMAIL_ADDRESS, value)
        }

    var countryCode: String
        get() = findPreference(COUNTRY_CODE, "")
        set(value) {
            putPreference(COUNTRY_CODE, value)
        }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    var language: String
        get() = findPreference(SETTING_LANGUAGE, "")
        set(value) {
            putPreference(SETTING_LANGUAGE, value)
        }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    var firebaseToken: String
        get() = findPreference(TOKEN_FIREBASE, "")
        set(value) {
            putPreference(TOKEN_FIREBASE, value)
        }

    var refreshToken: String
        get() = findPreference(TOKEN_REFRESH, "")
        set(value) {
            putPreference(TOKEN_REFRESH, value)
        }

    var sessionToken: String
        get() = findPreference(TOKEN_SESSION, "")
        set(value) {
            putPreference(TOKEN_SESSION, value)
        }

    fun removeCredentials() {
        prefs.edit().run {
            remove(TOKEN_FIREBASE)
            remove(TOKEN_REFRESH)
            remove(TOKEN_SESSION)
            remove(PROFILE_USER)

            apply()
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    var isUpdated: Boolean
        get() = findPreference(IS_UPDATED, false)
        set(value) {
            putPreference(IS_UPDATED, value)
        }

    var isOnBoardingSeen: Boolean
        get() = findPreference(ON_BOARDING, false)
        set(value) {
            putPreference(ON_BOARDING, value)
        }

    var isPhoneNumberChanged: Boolean
        get() = findPreference(IS_PHONE_NUMBER_CHANGED, false)
        set(value) {
            putPreference(IS_PHONE_NUMBER_CHANGED, value)
        }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    var unreadNotification: Int
        get() = findPreference(UNREAD_NOTIFICATION, 0)
        set(value) {
            putPreference(UNREAD_NOTIFICATION, value)
        }

    fun decreaseUnreadNotification() {
        unreadNotification -= 1
    }

    fun clearNotification() {
        unreadNotification = 0
    }

    fun showNotification(data: Int): Boolean {
        return data != 0 && unreadNotification != data
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    var savedTimeVerification: Long
        get() = findPreference(SAVED_TIME_VERIFICATION, 0)
        set(value) {
            putPreference(SAVED_TIME_VERIFICATION, value)
        }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

//    fun storeSession(data: Auth?) {
//        if (data == null) return
//
//        auth = data.also {
//            memberId = it.memberId.orEmpty()
//            refreshToken = it.sessionRefresh.orEmpty()
//            sessionToken = it.sessionValue.orEmpty()
//        }
//    }
//
//    fun storeUserProfile(response: Profile?) {
//        if (response == null) return
//
//        userProfile = response.also {
//            memberId = it.memberId.orEmpty()
//            firstName = it.firstName
//            lastName = it.lastName
//            phoneNumber = it.memberPhone.orEmpty()
//            email = it.memberEmail.orEmpty()
//            firebaseToken = it.memberToken.orEmpty()
//        }
//    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

//    fun requireUserProfile(): Profile {
//        return userProfile ?: Profile(auth?.memberId, auth?.memberName.fromHtmlFormat(), auth?.memberEmail, auth?.memberPhone, auth?.memberPhoto)
//    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    fun initializeOnBoarding() {
        isOnBoardingSeen = false
    }

    fun onBoardingSeen() {
        if (!isOnBoardingSeen) isOnBoardingSeen = true
    }

//    fun isHomeWizardSeen(): Boolean {
//        return homeWizard.contains(memberId)
//    }
//
//    fun homeWizardSeen() {
//        val modified = homeWizard.toMutableList().apply { add(memberId) }.toList()
//        homeWizard = modified
//    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    @Suppress("UNCHECKED_CAST")
    fun <T> findPreference(name: String, default: T?): T = with(prefs) {
        val res: Any? = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("Type is unknown")
        }
        res as T
    }

    fun <T> putPreference(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can't be saved into Preferences")
        }.apply()
    }

}
