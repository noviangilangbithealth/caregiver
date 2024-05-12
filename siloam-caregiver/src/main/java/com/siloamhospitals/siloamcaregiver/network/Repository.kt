package com.siloamhospitals.siloamcaregiver.network

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.siloamhospitals.siloamcaregiver.ext.encryption.decrypt
import com.siloamhospitals.siloamcaregiver.network.request.SendChatCaregiverRequest
import com.siloamhospitals.siloamcaregiver.network.response.AttachmentCaregiverResponse
import com.siloamhospitals.siloamcaregiver.network.response.BaseDataResponse
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChat
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChatData
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChatListData
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverList
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverListData
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverPatientListData
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverRoomTypeData
import com.siloamhospitals.siloamcaregiver.network.response.EmrIpdWebViewResponse
import com.siloamhospitals.siloamcaregiver.network.response.PatientListNotification
import com.siloamhospitals.siloamcaregiver.network.response.PatientListNotificationData
import com.siloamhospitals.siloamcaregiver.network.response.UserShowResponse
import com.siloamhospitals.siloamcaregiver.network.response.WardResponse
import com.siloamhospitals.siloamcaregiver.network.response.groupinfo.GroupInfoResponse
import com.siloamhospitals.siloamcaregiver.network.service.RetrofitInstance
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import okhttp3.MediaType
import okhttp3.MediaType.Companion.parse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File

class Repository(
    private val preferences: AppPreferences
) {
    companion object {
        private const val GET_CAREGIVER_EMIT_EVENT = "get-caregiver"
        private const val CAREGIVER_LIST_ON_EVENT = "caregiver-listener"
        private const val NEW_CAREGIVER_LISTENER = "new-caregiver-listener"
        private const val DELETE_CAREGIVER_LISTENER = "delete-caregiver-listener"
        private const val GET_ROOM_EMIT_EVENT = "get-room"
        private const val ROOM_LIST_ON_EVENT = "room-listener"
        private const val NEW_ROOM_LISTENER = "new-room-listener"
        private const val GET_MESSAGE_EMIT_EVENT = "get-message"
        private const val MESSAGE_LIST_ON_EVENT = "message-listener"
        private const val NEW_MESSAGE_LIST_ON_EVENT = "new-message-listener"
        private const val SET_READ_MESSAGE = "set-read-message"
        private const val GET_PATIENT_LIST_NOTIFICATION = "get-notif-new-message"
        private const val PATIENT_LIST_NOTIFICATIONT_ON_EVENT = "notif-new-message-listener"
        private const val KEY = "YoDnqnDe-y42HklO0W2awXO4Rsnooic0"
        private const val IV = "24594253911bc137"
    }

    private var mSocket = SocketIoManager(preferences)

    fun emitGetCaregiver(
        page: Int,
        keyword: String,
        user: String,
        organizationId: Long,
        wardId: Long
    ) {
        try {
            val data = JSONObject()
            data.put("page", page)
            data.put("keyword", keyword)
            data.put("user", user)
            data.put("organization_id", organizationId)
            data.put("ward_id", wardId)
            mSocket.emitEvent(GET_CAREGIVER_EMIT_EVENT, data)
        } catch (e: Exception) {
            Logger.d(e)
        }

    }

    fun listenCaregiverList(action: ((CaregiverListData, String) -> Unit)) {
        try {
            mSocket.onEvent(CAREGIVER_LIST_ON_EVENT) { data, error ->
                if (error.isEmpty()) {
                    val caregiverList =
                        Gson().getAdapter(CaregiverList::class.java).fromJson(data.toString())
                    val decryptedData = caregiverList.data.decrypt(IV, KEY)
                    val adapter = Gson().getAdapter(CaregiverListData::class.java)
                    val newData = adapter.fromJson(decryptedData)
                    if (newData != null) {
                        action.invoke(newData, "")
                    } else {
                        action.invoke(CaregiverListData(), "Empty Data")
                    }
                } else {
                    action.invoke(CaregiverListData(), error)
                }
            }
        } catch (e: Exception) {
            Logger.d(e.toString())
            action.invoke(CaregiverListData(), e.toString())
        }

    }

    fun listenNewCaregiver(action: ((CaregiverPatientListData, String) -> Unit)) {
        try {
            mSocket.onEvent(NEW_CAREGIVER_LISTENER) { data, error ->
                if (error.isEmpty()) {
                    val caregiverList =
                        Gson().getAdapter(CaregiverList::class.java).fromJson(data.toString())
                    val decryptedData = caregiverList.data.decrypt(IV, KEY)
                    val adapter = Gson().getAdapter(CaregiverPatientListData::class.java)
                    val newData = adapter.fromJson(decryptedData)
                    if (newData != null) {
                        action.invoke(newData, "")
                    } else {
                        action.invoke(CaregiverPatientListData(), "Empty Data")
                    }
                } else {
                    action.invoke(CaregiverPatientListData(), error)
                }
            }
        } catch (e: Exception) {
            Logger.d(e.toString())
            action.invoke(CaregiverPatientListData(), e.toString())
        }

    }

    fun listenDeleteCaregiver(action: ((CaregiverPatientListData, String) -> Unit)) {
        try {
            mSocket.onEvent(DELETE_CAREGIVER_LISTENER) { data, error ->
                if (error.isEmpty()) {
                    val caregiverList =
                        Gson().getAdapter(CaregiverList::class.java).fromJson(data.toString())
                    val decryptedData = caregiverList.data.decrypt(IV, KEY)
                    val adapter = Gson().getAdapter(CaregiverPatientListData::class.java)
                    val newData = adapter.fromJson(decryptedData)
                    if (newData != null) {
                        action.invoke(newData, "")
                    } else {
                        action.invoke(CaregiverPatientListData(), "Empty Data")
                    }
                } else {
                    action.invoke(CaregiverPatientListData(), error)
                }
            }
        } catch (e: Exception) {
            Logger.d(e.toString())
            action.invoke(CaregiverPatientListData(), e.toString())
        }

    }

    fun emitGetRoom(caregiverId: String, user: String) {
        val data = JSONObject()
        data.put("caregiverID", caregiverId)
        data.put("user", user)
        mSocket.emitEvent(GET_ROOM_EMIT_EVENT, data)
    }

    fun listenRoomList(
        doctorHopeid: String,
        action: ((List<CaregiverRoomTypeData>, String) -> Unit)
    ) {
        try {
            mSocket.onEvent(ROOM_LIST_ON_EVENT) { data, error ->
                if (error.isEmpty()) {
                    val encryptedData = data as JSONObject
                    val decryptedData = encryptedData.getString("data").decrypt(IV, KEY)
                    val type = object : TypeToken<List<CaregiverRoomTypeData>>() {}.type
                    val newData = Gson().fromJson<List<CaregiverRoomTypeData>>(decryptedData, type)
                    if (newData != null) {
                        action.invoke(newData, "")
                    } else {
                        action.invoke(emptyList(), "Empty Data")
                    }
                } else {
                    action.invoke(emptyList(), error)
                }
            }
        } catch (e: Exception) {
            action.invoke(emptyList(), e.toString())
        }
    }

    fun listenNewRoom( action: ((CaregiverRoomTypeData, String) -> Unit)) {
        try {
            mSocket.onEvent(NEW_ROOM_LISTENER) { data, error ->
                if (error.isEmpty()) {
                    val encryptedData = data as JSONObject
                    val decryptedData = encryptedData.getString("data").decrypt(IV, KEY)
                    val type = object : TypeToken<CaregiverRoomTypeData>() {}.type
                    val newData = Gson().fromJson<CaregiverRoomTypeData>(decryptedData, type)
                    if (newData != null) {
                        action.invoke(newData, "")
                    } else {
                        action.invoke(CaregiverRoomTypeData(), "Empty Data")
                    }
                } else {
                    action.invoke(CaregiverRoomTypeData(), error)
                }
            }
        } catch (e: Exception) {
            action.invoke(CaregiverRoomTypeData(), e.toString())
        }
    }

    fun emitGetMessage(
        page: Int,
        limit: Int,
        caregiverId: String,
        channelId: String,
        user: String
    ) {
        val data = JSONObject()
        data.put("page", page)
        data.put("limit", limit)
        data.put("caregiverID", caregiverId)
        data.put("channelID", channelId)
        data.put("user", user)
        mSocket.emitEvent(GET_MESSAGE_EMIT_EVENT, data)
    }

    fun setReadMessage(caregiverId: String, channelId: String,) {
        val data = JSONObject()
        data.put("caregiverID", caregiverId)
        data.put("channelID", channelId)
        mSocket.emitEvent(SET_READ_MESSAGE, data)
    }

    fun listenMessageList(doctorHopeid: String, action: ((CaregiverChatListData, String) -> Unit)) {
        try {
            mSocket.onEvent(MESSAGE_LIST_ON_EVENT) { data, error ->
                if (error.isEmpty()) {
                    val encryptedData =
                        Gson().getAdapter(CaregiverChat::class.java).fromJson(data.toString())
                    val decryptedData = encryptedData.data.decrypt(IV, KEY)
                    val adapter = Gson().getAdapter(CaregiverChatListData::class.java)
                    val newData = adapter.fromJson(decryptedData)
                    if (newData != null) {
                        action.invoke(newData, "")
                    } else {
                        action.invoke(CaregiverChatListData(), error)
                    }
                } else {
                    action.invoke(CaregiverChatListData(), error)
                }
            }
        } catch (e: Exception) {
            action.invoke(CaregiverChatListData(), e.toString())
        }
    }

    fun listenNewMessageList(action: ((CaregiverChatData, String) -> Unit)) {
        try {
            mSocket.onEvent(NEW_MESSAGE_LIST_ON_EVENT) { data, error ->
                if (error.isEmpty()) {
                    val encryptedData =
                        Gson().getAdapter(CaregiverChat::class.java).fromJson(data.toString())
                    val decryptedData = encryptedData.data.decrypt(IV, KEY)
                    val adapter = Gson().getAdapter(CaregiverChatData::class.java)
                    val newData = adapter.fromJson(decryptedData)
                    if (newData != null) {
                        action.invoke(newData, "")
                    } else {
                        action.invoke(CaregiverChatData(), error)
                    }
                } else {
                    action.invoke(CaregiverChatData(), error)
                }
            }
        } catch (e: Exception) {
            action.invoke(CaregiverChatData(), e.toString())
        }
    }

    fun emitNotifNewMessage() {
        mSocket.emitEvent(GET_PATIENT_LIST_NOTIFICATION, null)
    }

    fun listenNotifNewMessage(action: (PatientListNotificationData, String) -> Unit) {
        try {
            mSocket.onEvent(PATIENT_LIST_NOTIFICATIONT_ON_EVENT) { data, error ->
                if (error.isEmpty()) {
                    val encryptedData =
                        Gson().getAdapter(PatientListNotification::class.java)
                            .fromJson(data.toString())
                    val decryptionData = encryptedData.data.decrypt(IV, KEY)
                    val adapter = Gson().getAdapter(PatientListNotificationData::class.java)
                    val newData = adapter.fromJson(decryptionData)
                    if (newData != null) {
                        action.invoke(newData, "")
                    } else {
                        action.invoke(PatientListNotificationData(), error)
                    }
                }
            }
        } catch (e: Exception) {
            action.invoke(PatientListNotificationData(), e.toString())
        }
    }

    suspend fun sendChatCaregiver(
        caregiverID: String,
        channelID: String,
        senderID: String,
        sentAt: String,
        message: String,
        type: String,
        attachment: List<AttachmentCaregiver>
    ): Response<BaseDataResponse<*>> {
        val request = SendChatCaregiverRequest(
            caregiverID,
            channelID,
            senderID,
            sentAt,
            message,
            type,
            attachment
        )
        return RetrofitInstance.getInstance.sendMessage(request)
    }

    suspend fun postUploadAttachment(
        files: List<File>,
        isVoiceNote: Boolean = false
    ): Response<AttachmentCaregiverResponse> {
        val attachment = files.map {
            val fbody = if (isVoiceNote) it.asRequestBody("audio/m4a".toMediaType()) else it.asRequestBody(
                    "image/jpeg".toMediaTypeOrNull()
                )
            MultipartBody.Part.createFormData("attachment", it.name, fbody)
        }
        return RetrofitInstance.getInstance.postUpload(attachment)
    }

    suspend fun getGroupInfo(
        caregiverId: String
    ): Response<GroupInfoResponse> {
        return RetrofitInstance.getInstance.getGroupInfo(caregiverId)
    }

    suspend fun getUserShow(
        userId: Long
    ): Response<UserShowResponse> {
        return RetrofitInstance.getInstance.getUserShow(userId)
    }

    suspend fun getWard(
        hospitalId: Long
    ): Response<WardResponse> {
        return RetrofitInstance.getInstance.getWard(hospitalId)
    }

    suspend fun getEmrIpdWebView(
        caregiverId: String
    ): Response<EmrIpdWebViewResponse> {
        return RetrofitInstance.getInstance.getEmrIpdWebView(caregiverId)
    }

}
