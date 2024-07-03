package com.siloamhospitals.siloamcaregiver.ui.chatroom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siloamhospitals.siloamcaregiver.ext.datetime.NOW
import com.siloamhospitals.siloamcaregiver.ext.datetime.toLocalDateTime
import com.siloamhospitals.siloamcaregiver.ext.datetime.toLocalDateTimeOrNow
import com.siloamhospitals.siloamcaregiver.ext.datetime.withFormat
import com.siloamhospitals.siloamcaregiver.network.AttachmentCaregiver
import com.siloamhospitals.siloamcaregiver.network.ConnectivityLiveData
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.network.response.AttachmentCaregiverResponse
import com.siloamhospitals.siloamcaregiver.network.response.BaseDataResponse
import com.siloamhospitals.siloamcaregiver.network.response.BaseHandleResponse
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChatData
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChatListData
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.CaregiverChatRoomUi
import com.siloamhospitals.siloamcaregiver.ui.Event
import kotlinx.coroutines.launch
import java.io.File

class ChatRoomCaregiverViewModel(
    private val repository: Repository,
    private val preferences: AppPreferences
) : ViewModel() {

    var urlEmrIpd = ""

    val _sendMessage = MutableLiveData<BaseHandleResponse<BaseDataResponse<*>>>()
    val sendMessage: LiveData<BaseHandleResponse<BaseDataResponse<*>>> = _sendMessage

    val _deleteMessage = MutableLiveData<BaseHandleResponse<BaseDataResponse<*>>>()
    val deleteMessage: LiveData<BaseHandleResponse<BaseDataResponse<*>>> = _deleteMessage

    val _messageList = MutableLiveData<Event<CaregiverChatListData>>()
    val messageList: LiveData<Event<CaregiverChatListData>> = _messageList
    val _newMessage = MutableLiveData<Event<CaregiverChatData>>()
    val newMessage: LiveData<Event<CaregiverChatData>> = _newMessage
    val _errorMessageList = MutableLiveData<Event<String>>()
    val errorMessageList: LiveData<Event<String>> = _errorMessageList
    val _errorNewMessage = MutableLiveData<Event<String>>()
    val errorNewMessage: LiveData<Event<String>> = _errorNewMessage

    var caregiverId = ""
    var channelId = ""
    var patientName = ""
    var roomName = ""
    var urlIcon = ""

    var currentPage = 1
    var tempMessage = 0
    var isLastPage = false

    val isConnected: LiveData<Boolean> by lazy {
        ConnectivityLiveData(preferences.context)
    }

    val doctorHopeId get() = preferences.userId.toString()

//    val _uploadFiles = MutableLiveData<ApiEvent<List<ImageDoctorFeedback>>?>()
//    val uploadFiles: LiveData<ApiEvent<List<ImageDoctorFeedback>>?> = _uploadFiles

    val _uploadFiles = MutableLiveData<BaseHandleResponse<AttachmentCaregiverResponse>>()
    val uploadFiles: LiveData<BaseHandleResponse<AttachmentCaregiverResponse>> = _uploadFiles

    fun deleteMessage(messageId: String) =
        viewModelScope.launch {
            try {
                _deleteMessage.postValue(BaseHandleResponse.LOADING())
                val response = repository.deleteMessage(messageId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _deleteMessage.postValue(BaseHandleResponse.SUCCESS(it))
                    }
                } else {
                    _deleteMessage.postValue(BaseHandleResponse.ERROR(response.message()))
                }
            } catch (e: Exception) {
                _deleteMessage.postValue(BaseHandleResponse.ERROR(e.message.orEmpty()))
            }
        }

    fun uploadFiles(documentFiles: List<File>, isVoiceNote: Boolean = false) =
        viewModelScope.launch {
            try {
                _uploadFiles.postValue(BaseHandleResponse.LOADING())
                val response = repository.postUploadAttachment(documentFiles, isVoiceNote)
                if (response.isSuccessful) {
                    response.body()?.let {
                        com.orhanobut.logger.Logger.d(it)
                        _uploadFiles.postValue(BaseHandleResponse.SUCCESS(it))
                    }
                } else {
                    val message = response.message()
                    val error = response.errorBody()?.string()
                    _uploadFiles.postValue(BaseHandleResponse.ERROR(response.message()))
                }
            } catch (e: Exception) {
                _uploadFiles.postValue(BaseHandleResponse.ERROR(e.message.orEmpty()))
            }
        }


//    fun uploadFiles(documentFiles: List<File>, isVoiceNote: Boolean = false) {
//        viewModelScope.launch {
//            caregiverRepository.postUploadPhoto(documentFiles, isVoiceNote).onStart { emit(ApiEvent.OnProgress()) }.collect {
//                _uploadFiles.value = it
//            }
//        }
//    }


    fun emitGetMessage(loadMore: Boolean = false, action: (() -> Unit)? = null) {
        if (currentPage > tempMessage) {
            if (loadMore) {
                tempMessage = currentPage
                currentPage++
            }
            repository.emitGetMessage(
                page = currentPage,
                limit = 20,
                caregiverId = caregiverId,
                channelId = channelId,
                user = preferences.userId.toString()
            )

            if (!loadMore) action?.invoke()
        }
    }

    fun setReadMessage() {
        repository.setReadMessage(caregiverId = caregiverId, channelId = channelId)
    }

    fun resetCurrentPage() {
        currentPage = 1
    }

//    fun sendChat(message: String = "", type: Int = 1, attachments: List<ImageDoctorFeedback> = emptyList()) {
//        viewModelScope.launch {
//            caregiverRepository.postSendMessage(
//                caregiverId = caregiverId,
//                channelId = channelId,
//                senderId = userRepository.getDoctorId(),
//                message = message,
//                type = type.toString(),
//                attachments = attachments
//            ).onStart { emit(ApiEvent.OnProgress()) }.collect { _sendMessage.value = it }
//        }
//    }

    fun sendChat(
        message: String = "",
        type: Int = 1,
        attachments: List<AttachmentCaregiver> = emptyList()
    ) =
        viewModelScope.launch {
            try {
                _sendMessage.postValue(BaseHandleResponse.LOADING())
                val response = repository.sendChatCaregiver(
                    caregiverID = caregiverId,
                    channelID = channelId,
                    senderID = doctorHopeId,
                    sentAt = NOW.withFormat("yyyy-MM-dd HH:mm:ss"),
                    message = message,
                    type = type.toString(),
                    attachment = attachments
                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        _sendMessage.postValue(BaseHandleResponse.SUCCESS(it))
                    }
                } else {
                    _sendMessage.postValue(BaseHandleResponse.ERROR(response.message()))
                }
            } catch (e: Exception) {
                _sendMessage.postValue(BaseHandleResponse.ERROR("No Internet Connection"))
            }
        }


    fun listenMessageList() {
        viewModelScope.launch {
            repository.listenMessageList(preferences.userId.toString()) { data, error ->
                if (error.isEmpty()) {
                    _messageList.postValue(Event(data))
                } else {
                    _errorMessageList.postValue(Event(error))
                }
            }
        }
    }

    fun listenNewMessageList() {
        viewModelScope.launch {
            repository.listenNewMessageList { data, error ->
                if (error.isEmpty()) {
                    _newMessage.postValue(Event(data))
                } else {
                    _errorNewMessage.postValue(Event(error))
                }
            }
        }
    }

    var sizeChat = 0
    fun List<CaregiverChatData>.generateChatListUI(lastData: CaregiverChatRoomUi?, action: ((isSameDate: Boolean) -> Unit)?): List<CaregiverChatRoomUi> {
        com.orhanobut.logger.Logger.d(this)
        var isSameDate = false
        val dataUi = arrayListOf<CaregiverChatRoomUi>()
        val dataGroup =
            this.groupBy { it.createdAt?.toLocalDateTime()?.withFormat("EEEE, dd MMM") ?: "" }
        dataGroup.forEach { dataGrouped ->
            dataGrouped.value.map {
                val roleId = it.user?.role?.id?.toInt() ?: 1
                dataUi.add(
                    CaregiverChatRoomUi(
                        id = it.id.orEmpty(),
                        name = if (roleId == 1) it.user?.name.orEmpty() else it.user?.role?.name.orEmpty() + " - " + it.user?.name.orEmpty(),
                        message = it.message.orEmpty(),
                        time = it.createdAt?.toLocalDateTimeOrNow()?.withFormat("HH:mm") ?: "",
                        url = if (it.attachment.isNullOrEmpty()) "" else it.attachment?.get(0)?.uriExt.orEmpty(),
                        color = it.user?.role?.color.orEmpty(),
                        isRead = it.isReaded ?: false,
                        isSelfSender = it.user?.hopeUserID == doctorHopeId,
                        isUrgent = (it.type ?: 1).toInt() == 2,
                        isVoiceNote = if (it.attachment.isNullOrEmpty()) false else it.attachment.get(
                            0
                        )?.uriExt.orEmpty()
                            .last() == 'a' || it.attachment.get(0)?.uriExt.orEmpty().last() == 'c',
                        isActive = it.isActive ?: false
                    )
                )
            }

            dataUi.add(
                CaregiverChatRoomUi(
                    isDateLimit = true, time = dataGrouped.key
                )
            )

            if(lastData?.isDateLimit ?: false && lastData?.time.orEmpty() == dataGrouped.key){
                isSameDate = true
            }
        }
        sizeChat = dataUi.size

        action?.invoke(isSameDate)
        return dataUi
    }

    fun CaregiverChatData.generateNewChatUI(): CaregiverChatRoomUi {
        val roleId = user?.role?.id?.toInt() ?: 1
        return CaregiverChatRoomUi(
            id = this.id.orEmpty(),
            name = if (roleId == 1) user?.name.orEmpty() else user?.role?.name.orEmpty() + " - " + user?.name.orEmpty(),
            message = message.orEmpty(),
            time = createdAt?.toLocalDateTimeOrNow()?.withFormat("HH:mm") ?: "",
            url = if (attachment.isNullOrEmpty()) "" else attachment.get(0)?.uriExt.orEmpty(),
            color = user?.role?.color.orEmpty(),
            isRead = isReaded ?: false,
            isSelfSender = user?.hopeUserID == doctorHopeId,
            isUrgent = (type ?: 1).toInt() == 2,
            isVoiceNote = if (this.attachment.isNullOrEmpty()) false else this.attachment.get(0)?.uriExt.orEmpty()
                .last() == 'a' || this.attachment.get(0)?.uriExt.orEmpty().last() == 'c',
            isActive = this.isActive ?: false
        )
    }


}
