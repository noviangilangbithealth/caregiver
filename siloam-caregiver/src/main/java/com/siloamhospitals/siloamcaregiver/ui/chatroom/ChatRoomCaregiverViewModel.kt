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
import com.siloamhospitals.siloamcaregiver.network.entity.CaregiverChatEntity
import com.siloamhospitals.siloamcaregiver.network.entity.FailedChatEntity
import com.siloamhospitals.siloamcaregiver.network.entity.toEntity
import com.siloamhospitals.siloamcaregiver.network.response.AttachmentCaregiverResponse
import com.siloamhospitals.siloamcaregiver.network.response.BaseDataResponse
import com.siloamhospitals.siloamcaregiver.network.response.BaseHandleResponse
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChatData
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChatListData
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.CaregiverChatRoomUi
import com.siloamhospitals.siloamcaregiver.ui.Event
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.io.File

class ChatRoomCaregiverViewModel(
    private val repository: Repository,
    private val preferences: AppPreferences
) : ViewModel() {

    var urlEmrIpd = ""

    val _sendMessage = MutableLiveData<BaseHandleResponse<CaregiverChatData>>()
    val sendMessage: LiveData<BaseHandleResponse<CaregiverChatData>> = _sendMessage

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
    val failedMessageSize = MutableLiveData<Int>()
    val _pinChatMessage = MutableLiveData<BaseHandleResponse<List<CaregiverChatData>>>()
    val pinChatMessage: LiveData<BaseHandleResponse<List<CaregiverChatData>>> = _pinChatMessage

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

    private val _chatMessages = MutableLiveData<Event<BaseHandleResponse<TripleChats>>>()
    val chatMessages: LiveData<Event<BaseHandleResponse<TripleChats>>> = _chatMessages

    private val _pinChat = MutableLiveData<BaseHandleResponse<BaseDataResponse<*>>>()
    val pinChat: LiveData<BaseHandleResponse<BaseDataResponse<*>>> = _pinChat

    fun pinChat(messageId: String, pin: Boolean) {
        viewModelScope.launch {
            try {
                _pinChat.postValue(BaseHandleResponse.LOADING())
                val response = repository.pinChatMessage(messageId, pin)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _pinChat.postValue(BaseHandleResponse.SUCCESS(it))
                    }
                } else {
                    _pinChat.postValue(BaseHandleResponse.ERROR(response.message()))
                }
            } catch (e: Exception) {
                _pinChat.postValue(BaseHandleResponse.ERROR(e.message.orEmpty()))
            }
        }
    }

    fun emitGetPinChat() {
        repository.emitGetPinMessage(caregiverId, channelId)
    }

    fun listenPinChat() {
        viewModelScope.launch {
            _pinChatMessage.postValue(BaseHandleResponse.LOADING())
            repository.listenPinChat { data, error ->
                if (error.isEmpty()) {
                    _pinChatMessage.postValue(BaseHandleResponse.SUCCESS(data))
                } else {
                    _pinChatMessage.postValue(BaseHandleResponse.ERROR(error))
                }
            }
        }
    }

    fun getCaregiverChat() {
        viewModelScope.launch {
            repository.getChatMessagesFlow(channelId, caregiverId)
                .onStart { emit(BaseHandleResponse.LOADING()) }
                .collect { messages ->
                    _chatMessages.postValue(Event(messages))
                }
        }
    }

    fun insertFailedMessage(
        uuid: String,
        message: String,
        localuri: String = "",
        isVideo: Boolean = false
    ) {
        viewModelScope.launch {
            repository.insertFailedMessage(
                FailedChatEntity(
                    channelId = channelId,
                    caregiverId = caregiverId,
                    sentId = uuid,
                    message = message,
                    localuri = localuri,
                    isVideo = isVideo
                )
            )
        }
    }

    fun deleteFailedMessage(sentId: String) {
        viewModelScope.launch {
            repository.deleteFailedMessage(sentId)
        }
    }

    // get failed message size return integer with coroutine
    fun getFailedMessagesSize() = viewModelScope.launch {
        failedMessageSize.postValue(repository.getFailedMessagesSize(channelId, caregiverId))
    }


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

    fun uploadFiles(
        documentFiles: List<File>,
        isVoiceNote: Boolean = false,
        isVideo: Boolean = false
    ) =
        viewModelScope.launch {
            try {
                _uploadFiles.postValue(BaseHandleResponse.LOADING())
                val response = repository.postUploadAttachment(documentFiles, isVoiceNote, isVideo)
                if (response.isSuccessful) {
                    response.body()?.let {
                        com.orhanobut.logger.Logger.d(it)
                        _uploadFiles.postValue(BaseHandleResponse.SUCCESS(it))
                    }
                } else {
                    val message = response.message()
                    val error = response.errorBody()?.string()
                    _uploadFiles.postValue(
                        BaseHandleResponse.ERROR(
                            "message: $message, error; $error",
                            data = AttachmentCaregiverResponse(
                                listOf(
                                    AttachmentCaregiver(
                                        uri = documentFiles.first().path,
                                        name = if (isVideo) "video" else "image"
                                    )
                                )
                            )
                        )
                    )
                }
            } catch (e: Exception) {
                _uploadFiles.postValue(
                    BaseHandleResponse.ERROR(
                        e.message.orEmpty(), data = AttachmentCaregiverResponse(
                            listOf(
                                AttachmentCaregiver(
                                    uri = documentFiles.first().path,
                                    name = if (isVideo) "video" else "image"
                                )
                            )
                        )
                    )
                )
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
        attachments: List<AttachmentCaregiver> = emptyList(),
        sentId: String = ""
    ) {
        viewModelScope.launch {

            _sendMessage.postValue(BaseHandleResponse.LOADING())
            repository.sendChatCaregiver(
                sentID = sentId,
                caregiverID = caregiverId,
                channelID = channelId,
                senderID = doctorHopeId,
                sentAt = NOW.withFormat("yyyy-MM-dd HH:mm:ss"),
                message = message,
                type = type.toString(),
                attachment = attachments
            )
                .onStart { emit(BaseHandleResponse.LOADING()) }
                .collect {
                    _sendMessage.value = it
                }
        }
    }


//    fun listenMessageList() {
//        viewModelScope.launch {
//            repository.listenMessageList(preferences.userId.toString()) { data, error ->
//                if (error.isEmpty()) {
//                    _messageList.postValue(Event(data))
//                } else {
//                    _errorMessageList.postValue(Event(error))
//                }
//            }
//        }
//    }

    fun listenNewMessageList() {
        viewModelScope.launch {
            var newData: CaregiverChatData? = null
            repository.listenNewMessageList { data, error ->
                if (error.isEmpty()) {
                    newData = data
                    _newMessage.postValue(Event(data))
                } else {
                    _errorNewMessage.postValue(Event(error))
                }
            }
            newData?.let { repository.insertChatMessage(it.toEntity()) }
        }
    }

    fun insertChatMessage(message: CaregiverChatData) {
        viewModelScope.launch {
            repository.insertChatMessage(message.toEntity())
        }
    }

    fun insertChatMessageWithoutCheck(message: CaregiverChatData) {
        viewModelScope.launch {
            repository.insertChatMessageWithoutExist(message.toEntity())
        }
    }

    var sizeChat = 0
//    fun List<CaregiverChatData>.generateChatListUI(lastData: CaregiverChatRoomUi?, action: ((isSameDate: Boolean) -> Unit)?): List<CaregiverChatRoomUi> {
//        com.orhanobut.logger.Logger.d(this)
//        var isSameDate = false
//        val dataUi = arrayListOf<CaregiverChatRoomUi>()
//        val dataGroup =
//            this.groupBy { it.createdAt?.toLocalDateTime()?.withFormat("EEEE, dd MMM") ?: "" }
//        dataGroup.forEach { dataGrouped ->
//            dataGrouped.value.map {
//                val roleId = it.user?.role?.id?.toInt() ?: 1
//                dataUi.add(
//                    CaregiverChatRoomUi(
//                        id = it.id.orEmpty(),
//                        name = if (roleId == 1) it.user?.name.orEmpty() else it.user?.role?.name.orEmpty() + " - " + it.user?.name.orEmpty(),
//                        message = it.message.orEmpty(),
//                        time = it.createdAt?.toLocalDateTimeOrNow()?.withFormat("HH:mm") ?: "",
//                        url = if (it.attachment.isNullOrEmpty()) "" else it.attachment.get(0)?.uriExt.orEmpty(),
//                        color = it.user?.role?.color.orEmpty(),
//                        isRead = it.isReaded ?: false,
//                        isSelfSender = it.user?.hopeUserID == doctorHopeId,
//                        isUrgent = (it.type ?: 1).toInt() == 2,
//                        isVoiceNote = if (it.attachment.isNullOrEmpty()) false else it.attachment.get(
//                            0
//                        )?.uriExt.orEmpty()
//                            .last() == 'a' || it.attachment.get(0)?.uriExt.orEmpty().last() == 'c',
//                        isActive = it.isActive ?: false,
//                        isVideo = if (it.attachment.isNullOrEmpty()) false else it.attachment.get(0)?.uriExt.orEmpty().endsWith("mp4") || it.attachment.get(0)?.uriExt.orEmpty().endsWith("mov")
//                    )
//                )
//            }
//
//            dataUi.add(
//                CaregiverChatRoomUi(
//                    isDateLimit = true, time = dataGrouped.key
//                )
//            )
//
//            if(lastData?.isDateLimit ?: false && lastData?.time.orEmpty() == dataGrouped.key){
//                isSameDate = true
//            }
//        }
//        sizeChat = dataUi.size
//
//        action?.invoke(isSameDate)
//        return dataUi
//    }

    fun CaregiverChatData.generateNewChatUI(): CaregiverChatRoomUi {
        val roleId = user?.role?.id?.toInt() ?: 1
        return CaregiverChatRoomUi(
            id = this.id.orEmpty(),
            name = if (roleId == 1) user?.name.orEmpty() else user?.role?.name.orEmpty() + " - " + user?.name.orEmpty(),
            message = message.orEmpty(),
            time = createdAt?.toLocalDateTimeOrNow()?.withFormat("HH:mm") ?: "",
            date = createdAt?.toLocalDateTime()?.withFormat("EEEE, dd MMM") ?: "",
            url = if (attachment.isNullOrEmpty()) "" else attachment.get(0)?.uriExt.orEmpty(),
            color = user?.role?.color.orEmpty(),
            isRead = isReaded ?: false,
            isSelfSender = user?.hopeUserID == doctorHopeId,
            isUrgent = (type ?: 1).toInt() == 2,
            isVoiceNote = if (this.attachment.isNullOrEmpty()) false else this.attachment.get(0)?.uriExt.orEmpty()
                .last() == 'a' || this.attachment.get(0)?.uriExt.orEmpty().last() == 'c',
            isActive = this.isActive ?: false,
            isVideo = if (this.attachment.isNullOrEmpty()) false else this.attachment.get(0)?.uriExt.orEmpty()
                .endsWith("mp4") || this.attachment.get(0)?.uriExt.orEmpty().endsWith("mov")
        )
    }

    fun List<CaregiverChatEntity>.generateChatListUI(
        lastData: CaregiverChatRoomUi?,
        action: ((isSameDate: Boolean) -> Unit)?
    ): List<CaregiverChatRoomUi> {
        com.orhanobut.logger.Logger.d(this)
        var isSameDate = false
        val dataUi = arrayListOf<CaregiverChatRoomUi>()
        val dataGroup =
            this.groupBy { it.createdAt.toLocalDateTime().withFormat("EEEE, dd MMM") ?: "" }
        dataGroup.forEach { dataGrouped ->

            dataGrouped.value.map {
                val roleId = it.user.role.id
                dataUi.add(
                    CaregiverChatRoomUi(
                        id = it.id.orEmpty(),
                        name = if (roleId == 1) it.user.name else it.user.role.name + " - " + it.user.name,
                        message = it.message.orEmpty(),
                        time = it.createdAt.toLocalDateTimeOrNow().withFormat("HH:mm"),
                        date = it.createdAt.toLocalDateTime().withFormat("EEEE, dd MMM"),
                        url = it.attachment?.uriExt.orEmpty(),
                        color = it.user.role.color,
                        isRead = it.isReaded,
                        isSelfSender = it.user.hopeUserId == doctorHopeId,
                        isUrgent = it.type == 2,
                        isVoiceNote = if (it.attachment?.uriExt.isNullOrEmpty()) false else it.attachment?.uriExt.orEmpty()
                            .last() == 'a' || it.attachment?.uriExt.orEmpty().last() == 'c',
                        isActive = it.isActive,
                        isVideo = it.attachment?.uriExt.orEmpty()
                            .endsWith("mp4") || it.attachment?.uriExt.orEmpty().endsWith("mov")
                    )
                )
            }

            if (lastData?.date != dataGrouped.key) {
                dataUi.add(
                    CaregiverChatRoomUi(
                        isDateLimit = true, time = dataGrouped.key
                    )
                )
            }


            if (lastData?.isDateLimit ?: false && lastData?.time.orEmpty() == dataGrouped.key) {
                isSameDate = true
            }
        }
        sizeChat = dataUi.size

        action?.invoke(isSameDate)
        return dataUi
    }

    fun CaregiverChatEntity.generateNewChatUIfromEntity(): CaregiverChatRoomUi {
        val roleId = user.role.id
        return CaregiverChatRoomUi(
            id = this.id,
            name = if (roleId == 1) user.name else user.role.name + " - " + user.name,
            message = message,
            time = createdAt.toLocalDateTimeOrNow().withFormat("HH:mm"),
            url = if (attachment == null) "" else attachment.uriExt,
            color = user.role.color,
            isRead = isReaded,
            isSelfSender = user.hopeUserId == doctorHopeId,
            isUrgent = type == 2,
            isVoiceNote = if (this.attachment?.uriExt.orEmpty()
                    .isEmpty()
            ) false else this.attachment?.uriExt
                ?.last() == 'a' || this.attachment?.uriExt?.last() == 'c',
            isActive = this.isActive,
            isVideo = if (this.attachment?.uriExt.orEmpty()
                    .isEmpty()
            ) false else this.attachment?.uriExt?.endsWith("mp4") ?: false || this.attachment?.uriExt?.endsWith(
                "mov"
            ) ?: false
        )
    }


}

typealias TripleChats = Triple<List<CaregiverChatEntity>, List<CaregiverChatEntity>, List<FailedChatEntity>>
