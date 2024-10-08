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
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChatData
import com.siloamhospitals.siloamcaregiver.network.response.EmrIpdWebViewResponse
import com.siloamhospitals.siloamcaregiver.network.response.groupinfo.GroupInfoAdmissionHistoryDataResponse
import com.siloamhospitals.siloamcaregiver.network.response.groupinfo.GroupInfoAdmissionHistoryResponse
import com.siloamhospitals.siloamcaregiver.network.response.groupinfo.GroupInfoAttachmentResponse
import com.siloamhospitals.siloamcaregiver.network.response.groupinfo.GroupInfoResponse
import com.siloamhospitals.siloamcaregiver.network.response.groupinfo.GroupInfoResultResponse
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class GroupDetailViewModel(
    private val repository: Repository,
    private val preferences: AppPreferences
) : ViewModel() {


    var patientDetail = GroupInfoResultResponse()
    var caregiverId = ""
    val attachment = mutableListOf<GroupInfoAttachmentResponse>()
    var urlWebView = ""


    private val _groupInfo = MutableLiveData<BaseHandleResponse<GroupInfoResponse>>()
    val groupInfo: LiveData<BaseHandleResponse<GroupInfoResponse>> = _groupInfo

    private val _emrIpdWebView = MutableLiveData<BaseHandleResponse<EmrIpdWebViewResponse>>()
    val emrIpdWebView: LiveData<BaseHandleResponse<EmrIpdWebViewResponse>> = _emrIpdWebView

    private val _admissionHistory =
        MutableLiveData<BaseHandleResponse<GroupInfoAdmissionHistoryResponse>>()
    val admissionHistory: LiveData<BaseHandleResponse<GroupInfoAdmissionHistoryResponse>> =
        _admissionHistory

    var savedAdmissionHistory = mutableListOf<GroupInfoAdmissionHistoryDataResponse>()
    var emptyState = false
    var admissionHistoryMrUrl = ""
    var fromAdmissionHistory = false
    fun getAdmissionHistory() {
        viewModelScope.launch {
            try {
                val response = repository.getAdmissionHistory(
                    patientDetail.organizationId,
                    patientDetail.patientId
                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        _admissionHistory.postValue(BaseHandleResponse.SUCCESS(it))
                    }
                } else {
                    _admissionHistory.postValue(BaseHandleResponse.ERROR(response.message()))
                }
            } catch (e: Exception) {
                _admissionHistory.postValue(BaseHandleResponse.ERROR(e.toString()))
            }
        }
    }

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

    val _sendMessage = MutableLiveData<BaseHandleResponse<CaregiverChatData>>()
    val sendMessage: LiveData<BaseHandleResponse<CaregiverChatData>> = _sendMessage

    val doctorHopeId get() = preferences.userId.toString()
    var channelId = ""

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

}
