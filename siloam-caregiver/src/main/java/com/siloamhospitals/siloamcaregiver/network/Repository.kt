package com.siloamhospitals.siloamcaregiver.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import com.siloamhospitals.siloamcaregiver.ext.encryption.decrypt
import com.siloamhospitals.siloamcaregiver.network.entity.CaregiverChatEntity
import com.siloamhospitals.siloamcaregiver.network.entity.FailedChatEntity
import com.siloamhospitals.siloamcaregiver.network.entity.toEntity
import com.siloamhospitals.siloamcaregiver.network.request.PinChatRequest
import com.siloamhospitals.siloamcaregiver.network.request.PinMessageRequest
import com.siloamhospitals.siloamcaregiver.network.request.SendChatCaregiverRequest
import com.siloamhospitals.siloamcaregiver.network.response.AttachmentCaregiverResponse
import com.siloamhospitals.siloamcaregiver.network.response.BaseDataResponse
import com.siloamhospitals.siloamcaregiver.network.response.BaseHandleResponse
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChat
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChatData
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChatListData
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverList
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverListData
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverPatientListData
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverRoomTypeData
import com.siloamhospitals.siloamcaregiver.network.response.EmrIpdWebViewResponse
import com.siloamhospitals.siloamcaregiver.network.response.FloatingNotificationData
import com.siloamhospitals.siloamcaregiver.network.response.HospitalFilter
import com.siloamhospitals.siloamcaregiver.network.response.ListHospitalWard
import com.siloamhospitals.siloamcaregiver.network.response.PatientListNotification
import com.siloamhospitals.siloamcaregiver.network.response.PatientListNotificationData
import com.siloamhospitals.siloamcaregiver.network.response.UserShowResponse
import com.siloamhospitals.siloamcaregiver.network.response.WardResponse
import com.siloamhospitals.siloamcaregiver.network.response.groupinfo.GroupInfoAdmissionHistoryResponse
import com.siloamhospitals.siloamcaregiver.network.response.groupinfo.GroupInfoResponse
import com.siloamhospitals.siloamcaregiver.network.service.RetrofitInstance
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File

class Repository(
    private val preferences: AppPreferences,
    private val caregiverDatabase: CaregiverDatabase? = null
) {
    companion object {
        private const val EMIT_GET_PIN_MESSAGE = "get-pin-message"
        private const val PIN_MESSAGE_ON_EVENT = "pin-message-listener"
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
        private const val EMIT_HOSPITAL_WARD_FILTER = "get-list-ward-by-id"
        private const val LISTEN_HOSPITAL_WARD_FILTER = "list-ward-by-id-listener"
        private const val GET_NOTIF_FLOATING = "get-notif-floating"
        private const val NOTIF_FLOATING_LISTENER = "notif-floating-listener"
        private const val UPDATE_TOKEN_ID = "update-token-id"
        private const val KEY = "YoDnqnDe-y42HklO0W2awXO4Rsnooic0"
        private const val IV = "24594253911bc137"
    }

    private var mSocket = SocketIoManager(preferences)

    private val caregiverChatDao by lazy { caregiverDatabase?.caregiverChatDao() }

    fun emitGetPinMessage(caregiverId: String, channelId: String) {
        val data = JSONObject()
        data.put("caregiverID", caregiverId)
        data.put("channelID", channelId)
        mSocket.emitEvent(EMIT_GET_PIN_MESSAGE, data)
    }

    fun listenPinChat(action: (List<CaregiverChatData>, String) -> Unit) {
        try {
            mSocket.onEvent(PIN_MESSAGE_ON_EVENT) { data, error ->
                if (error.isEmpty()) {
                    val caregiverList =
                        Gson().getAdapter(CaregiverChat::class.java).fromJson(data.toString())
                    val decryptedData = caregiverList.data.decrypt(IV, KEY)
                    val listType = object : TypeToken<List<CaregiverChatData>>() {}.type
                    val newData = Gson().fromJson<List<CaregiverChatData>>(decryptedData, listType)
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
            data.put("limit", 100)
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
                    Log.e("decryptedData", "listenCaregiverList:$decryptedData")
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
                Log.e("dataaaa", "listenRoomList: $data")
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

    fun listenNewRoom(action: ((CaregiverRoomTypeData, String) -> Unit)) {
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

    fun setReadMessage(caregiverId: String, channelId: String) {
        val data = JSONObject()
        data.put("caregiverID", caregiverId)
        data.put("channelID", channelId)
        mSocket.emitEvent(SET_READ_MESSAGE, data)
    }

    fun setFirebaseToken(tokenId: String) {
        val data = JSONObject()
        data.put("tokenId", tokenId)
        mSocket.emitEvent(UPDATE_TOKEN_ID, data)
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

    fun emitFloatingNotification() {
        mSocket.emitEvent(GET_NOTIF_FLOATING, null)
    }

    fun listenFloatingNotification(action: (FloatingNotificationData, String) -> Unit) {
        try {
            mSocket.onEvent(NOTIF_FLOATING_LISTENER) { data, error ->
                if (error.isEmpty()) {
                    val encryptedData =
                        Gson().getAdapter(PatientListNotification::class.java)
                            .fromJson(data.toString())
                    val decryptionData = encryptedData.data.decrypt(IV, KEY)
                    val adapter = Gson().getAdapter(FloatingNotificationData::class.java)
                    val newData = adapter.fromJson(decryptionData)
                    if (newData != null) {
                        action.invoke(newData, "")
                    } else {
                        action.invoke(FloatingNotificationData(), error)
                    }
                }
            }
        } catch (e: Exception) {
            action.invoke(FloatingNotificationData(), e.toString())
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


    suspend fun deleteMessage(messageId: String): Response<BaseDataResponse<*>> {
        return RetrofitInstance.getInstance.deleteMessage(messageId)
    }

    suspend fun postUploadAttachment(
        files: List<File>,
        isVoiceNote: Boolean = false,
        isVideo: Boolean = false,
    ): Response<AttachmentCaregiverResponse> {
        val attachment = files.map {
            val fbody =
                if (isVoiceNote) it.asRequestBody("audio/m4a".toMediaType())
                else if (isVideo) it.asRequestBody("video/*".toMediaType())
                else it.asRequestBody("image/jpeg".toMediaTypeOrNull())
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

    suspend fun pinMessage(
        caregiverId: String,
        userHopeId: String,
        isPinned: Boolean
    ): Response<BaseDataResponse<*>> {
        val request = PinMessageRequest(caregiverId, userHopeId, isPinned)
        return RetrofitInstance.getInstance.postPinMessage(request)
    }

    fun emitHospitalWardFilter(
        doctorHopeId: Long
    ) {
        try {
            val data = JSONObject()
            data.put("doctor_hope_id", doctorHopeId)
            mSocket.emitEvent(EMIT_HOSPITAL_WARD_FILTER, data)
        } catch (e: Exception) {
            Logger.d(e)
        }
    }

    fun listenHospitalWardFilter(action: ((List<HospitalFilter>, String) -> Unit)) {
        try {
            mSocket.onEvent(LISTEN_HOSPITAL_WARD_FILTER) { data, error ->
                if (error.isEmpty()) {
                    val encryptedData =
                        Gson().getAdapter(ListHospitalWard::class.java).fromJson(data.toString())
                    val decryptedData = encryptedData.data.decrypt(IV, KEY)
                    Logger.d(decryptedData)
                    val builder =
                        GsonBuilder().registerTypeAdapterFactory(ListAdapterFactory()).create()
                    val adapter = ListAdapter(HospitalFilter::class.java, builder)
                    val newData = adapter.fromJson(decryptedData)
                    if (newData != null) {
                        action.invoke(newData, "")
                    }
                } else {
                    action.invoke(emptyList(), error)
                }
            }
        } catch (e: Exception) {
            Logger.d(e.toString())
            action.invoke(emptyList(), e.toString())
        }
    }

    suspend fun getAdmissionHistory(
        hospitalId: String,
        patientId: String
    ): Response<GroupInfoAdmissionHistoryResponse> {
        Log.e("Request", "getAdmissionHistory: $hospitalId ---- $patientId")
        return RetrofitInstance.getInstance.getAdmissionHistory(hospitalId, patientId)
    }

    // Pair first data is local data, second data is unread data
    fun getChatMessagesFlow(
        channelId: String,
        caregiverId: String
    ): Flow<BaseHandleResponse<Triple<List<CaregiverChatEntity>, List<CaregiverChatEntity>, List<FailedChatEntity>>>> {
        return flow {
            var messages: List<CaregiverChatEntity> = emptyList()
            var failedMessages: List<FailedChatEntity> = emptyList()
            try {
                messages =
                    caregiverChatDao?.getChatMessages(channelId, caregiverId)?.first().orEmpty()
                failedMessages =
                    caregiverChatDao?.getFailedMessages(channelId, caregiverId)?.first().orEmpty()
                val isLocalDataEmpty = messages.isEmpty()
                val response = RetrofitInstance.getInstance.getListMessage(
                    preferences.userId.toString(),
                    caregiverId,
                    channelId,
                    if (isLocalDataEmpty) "" else "true"
                )

                Logger.d(response.body())
                if (response.isSuccessful) {
                    if (response.body() == null || response.body()?.data.orEmpty().isEmpty()) {
                        emit(BaseHandleResponse.ERROR("Empty Message"))
                    } else {
                        val decryptedData = response.body()?.data?.decrypt(IV, KEY)
                        Logger.d(decryptedData)
                        val builder =
                            GsonBuilder().registerTypeAdapterFactory(ListAdapterFactory()).create()
                        val adapter = ListAdapter(CaregiverChatData::class.java, builder)
                        val newData = adapter.fromJson(decryptedData)
                        if (newData != null) {
                            if (isLocalDataEmpty) {
                                if(newData.isEmpty()) {
                                    emit(BaseHandleResponse.ERROR("Empty Message"))
                                    return@flow
                                }
                                insertChatMessages(newData.map { it.toEntity() })
                                emit(
                                    BaseHandleResponse.SUCCESS(
                                        Triple(
                                            newData.map { it.toEntity() },
                                            emptyList(),
                                            failedMessages
                                        )
                                    )
                                )
                            } else {
                                val unreadData = newData.map { it.toEntity() }
                                insertChatMessages(unreadData)
                                emit(
                                    BaseHandleResponse.SUCCESS(
                                        Triple(
                                            messages,
                                            unreadData,
                                            failedMessages
                                        )
                                    )
                                )
                            }
                        }
                    }
                } else {
                    Logger.d(response.message())
                    emit(BaseHandleResponse.ERROR("Failed to load message"))
                }

            } catch (e: Exception) {
                Logger.d(e)
                if (messages.isEmpty()) {
                    Logger.d(e.toString())
                    emit(BaseHandleResponse.ERROR("Failed to load message"))
                } else {
                    emit(BaseHandleResponse.SUCCESS(Triple(messages, emptyList(), failedMessages)))
                }
            }
        }
    }

    suspend fun sendChatCaregiver(
        caregiverID: String,
        channelID: String,
        senderID: String,
        sentAt: String,
        message: String,
        type: String,
        attachment: List<AttachmentCaregiver>,
        sentID: String = "",
    ): Flow<BaseHandleResponse<CaregiverChatData>> = flow {
        try {
            val request = SendChatCaregiverRequest(
                caregiverID,
                channelID,
                senderID,
                sentAt,
                message,
                type,
                attachment,
                sentID
            )
            val response = RetrofitInstance.getInstance.sendMessage(request)
            if (response.isSuccessful) {
                val body = response.body()
                if (response.isSuccessful) {
                    val data = body?.data
                    if (data != null) {
                        val decryptedData = data.decrypt(IV, KEY)
                        val builder =
                            GsonBuilder().registerTypeAdapterFactory(ListAdapterFactory()).create()
                        val adapter = ListAdapter(CaregiverChatData::class.java, builder)
                        val newData = adapter.fromJson(decryptedData)
                        if (newData != null) {
                            if (newData.isNotEmpty()) {
                                insertChatMessage(newData.first().toEntity())
                                emit(BaseHandleResponse.SUCCESS(newData.first(), sentId = sentID))
                            } else {
                                emit(BaseHandleResponse.ERROR("Empty Data", sentId = sentID))
                            }
                        } else {
                            emit(BaseHandleResponse.ERROR("Empty Data", sentId = sentID))
                        }
                    } else {
                        emit(BaseHandleResponse.ERROR("Empty Data", sentId = sentID))
                    }
                } else {
                    emit(BaseHandleResponse.ERROR(body?.message ?: "Error", sentId = sentID))
                }
            } else {
                emit(BaseHandleResponse.ERROR(response.message(), sentId = sentID))
            }
        } catch (e: Exception) {
            emit(BaseHandleResponse.ERROR(e.toString(), sentId = sentID))
        }

    }
//        flow<> {  }
//        val request = SendChatCaregiverRequest(
//            caregiverID,
//            channelID,
//            senderID,
//            sentAt,
//            message,
//            type,
//            attachment,
//            sentID
//        )
//        return RetrofitInstance.getInstance.sendMessage(request)
//    }

    suspend fun pinChatMessage(
        messageId: String,
        isPinned: Boolean
    ): Response<BaseDataResponse<*>> {
        val request = PinChatRequest(messageId, preferences.userId.toString(), isPinned)
        return RetrofitInstance.getInstance.putPinMessage(request)
    }

    suspend fun insertFailedMessage(message: FailedChatEntity) {
        caregiverChatDao?.insertFailedMessage(message)
    }

    suspend fun deleteFailedMessage(sentid: String) {
        caregiverChatDao?.deleteFailedMessage(sentid)
    }

    suspend fun getFailedMessagesSize(channelId: String, caregiverId: String): Int {
        return caregiverChatDao?.getFailedMessagesSize(channelId, caregiverId) ?: 0
    }

    private suspend fun insertChatMessages(messages: List<CaregiverChatEntity>) {
        for (message in messages) {
            caregiverChatDao?.let { caregiverChatDao ->
                if (!caregiverChatDao.exists(message.id)) {
                    caregiverChatDao.insertChatMessage(message)
                }
            }
        }
    }

    suspend fun insertChatMessage(message: CaregiverChatEntity) {
        caregiverChatDao?.let { caregiverChatDao ->
            if (!caregiverChatDao.exists(message.id)) {
                caregiverChatDao.insertChatMessage(message)
            }
        }
    }

    suspend fun insertChatMessageWithoutExist(message: CaregiverChatEntity) {
        caregiverChatDao?.insertChatMessage(message)
    }

    suspend fun getUnreadMessages(messages: List<CaregiverChatEntity>): List<CaregiverChatEntity> {
        caregiverChatDao?.let {
            return messages.filter { message -> !it.exists(message.id) }
        }
        return emptyList()
    }

}
