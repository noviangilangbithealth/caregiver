package com.siloamhospitals.siloamcaregiver.ui.groupdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siloamhospitals.siloamcaregiver.ext.datetime.NOW
import com.siloamhospitals.siloamcaregiver.ext.datetime.withFormat
import com.siloamhospitals.siloamcaregiver.network.AttachmentCaregiver
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.network.response.BaseDataResponse
import com.siloamhospitals.siloamcaregiver.network.response.BaseHandleResponse
import com.siloamhospitals.siloamcaregiver.network.response.EmrIpdWebViewResponse
import com.siloamhospitals.siloamcaregiver.network.response.groupinfo.GroupInfoAttachmentResponse
import com.siloamhospitals.siloamcaregiver.network.response.groupinfo.GroupInfoResponse
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import kotlinx.coroutines.launch

class GroupDetailViewModel(
    private val repository: Repository,
    private val preferences: AppPreferences
) : ViewModel() {

    var caregiverId = ""
    val attachment = mutableListOf<GroupInfoAttachmentResponse>()
    var urlWebView = ""

    private val _groupInfo = MutableLiveData<BaseHandleResponse<GroupInfoResponse>>()
    val groupInfo: LiveData<BaseHandleResponse<GroupInfoResponse>> = _groupInfo

    private val _emrIpdWebView = MutableLiveData<BaseHandleResponse<EmrIpdWebViewResponse>>()
    val emrIpdWebView: LiveData<BaseHandleResponse<EmrIpdWebViewResponse>> = _emrIpdWebView

    fun getGroupInfo(caregiverId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getGroupInfo(caregiverId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _groupInfo.postValue(BaseHandleResponse.SUCCESS(it))
                    }
                } else {
                    _groupInfo.postValue(BaseHandleResponse.ERROR(response.message()))
                }
            } catch (e: Exception) {
                _groupInfo.postValue(BaseHandleResponse.ERROR("No Internet Connection"))
            }
        }
    }

    fun getEmrIpdWebView(caregiverId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getEmrIpdWebView(caregiverId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _emrIpdWebView.postValue(BaseHandleResponse.SUCCESS(it))
                    }
                } else {
                    _emrIpdWebView.postValue(BaseHandleResponse.ERROR(response.message()))
                }
            } catch (e: Exception) {
                _emrIpdWebView.postValue(BaseHandleResponse.ERROR("No Internet Connection"))
            }
        }
    }

    val _sendMessage = MutableLiveData<BaseHandleResponse<BaseDataResponse<*>>>()
    val sendMessage: LiveData<BaseHandleResponse<BaseDataResponse<*>>> = _sendMessage

    val doctorHopeId get() = preferences.userId.toString()
    var channelId = ""

    fun sendChat(
        message: String = "",
        type: Int = 1,
        attachments: List<AttachmentCaregiver> = emptyList()
    ) =
        viewModelScope.launch {
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
        }

}
