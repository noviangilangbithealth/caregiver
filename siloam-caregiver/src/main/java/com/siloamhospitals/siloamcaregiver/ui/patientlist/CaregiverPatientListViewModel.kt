package com.siloamhospitals.siloamcaregiver.ui.patientlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.network.response.BaseHandleResponse
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverListData
import com.siloamhospitals.siloamcaregiver.network.response.PatientListNotificationData
import com.siloamhospitals.siloamcaregiver.network.response.UserShowHospitalResponse
import com.siloamhospitals.siloamcaregiver.network.response.UserShowResponse
import com.siloamhospitals.siloamcaregiver.network.response.WardListResponse
import com.siloamhospitals.siloamcaregiver.network.response.WardResponse
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.ListCaregiverPatient
import kotlinx.coroutines.launch

class CaregiverPatientListViewModel(
    private val repository: Repository,
    preferences: AppPreferences
) : ViewModel() {

    val doctorId = preferences.userId
    var orgId = preferences.organizationId
    var wardId = preferences.wardId
    var orgCode = ""
    var wardName = ""
    var isSpecialist = true

    var currentPage = 1
    var keyword = ""
    var scrollPosition = 0
    var firsLoadFilter = true

    val roomPatientList = mutableListOf<ListCaregiverPatient>()

    init {
        getUserShow()
    }

    val hospitals = mutableListOf<UserShowHospitalResponse>()
    val wards = mutableListOf<WardListResponse>()

    fun emitGetCaregiver(action: (()->Unit)? = null) {
        repository.emitGetCaregiver(
            page = currentPage,
            keyword = keyword,
            user = doctorId.toString(),
            organizationId = orgId,
            wardId = wardId
        )
        action?.invoke()
    }

    private var _caregiverList = MutableLiveData<CaregiverListData>()
    var caregiverList: LiveData<CaregiverListData> = _caregiverList

    private var _error = MutableLiveData<String>()
    var error: LiveData<String> = _error
    var errorHasBeenConsumed = false

    fun listenCaregiverList() {
        viewModelScope.launch {
            repository.listenCaregiverList() { data, error ->
                if (error.isEmpty()) {
                    Logger.d(data)
                    _caregiverList.postValue(data)
                    errorHasBeenConsumed = false
                } else {
                    errorHasBeenConsumed = false
                    _error.postValue(error)
                }
            }
        }
    }

    private var _badgeNotif = MutableLiveData<PatientListNotificationData>()
    var badgeNotif: LiveData<PatientListNotificationData> = _badgeNotif

    fun emitGetBadgeNotif() {
        repository.emitNotifNewMessage()
    }

    fun listenBadgeNotif() {
        viewModelScope.launch {
            repository.listenNotifNewMessage { data, error ->
                if (error.isEmpty()) {
                    Logger.d(data)
                    _badgeNotif.postValue(data)
                    errorHasBeenConsumed = false
                } else {
                    errorHasBeenConsumed = false
                    _error.postValue(error)
                }
            }
        }
    }


    private val _userShow = MutableLiveData<BaseHandleResponse<UserShowResponse>>()
    val userShow: LiveData<BaseHandleResponse<UserShowResponse>> = _userShow

    fun getUserShow() {
        viewModelScope.launch {
            val response = repository.getUserShow(doctorId)
            if (response.isSuccessful) {
                response.body()?.let {
                    _userShow.postValue(BaseHandleResponse.SUCCESS(it))
                }
            } else {
                _userShow.postValue(BaseHandleResponse.ERROR(response.message()))
            }
        }
    }

    private val _ward = MutableLiveData<BaseHandleResponse<WardResponse>>()
    val ward: LiveData<BaseHandleResponse<WardResponse>> = _ward

    fun getWard(hospitalId: Long) {
        viewModelScope.launch {
            val response = repository.getWard(hospitalId)
            if (response.isSuccessful) {
                response.body()?.let {
                    _ward.postValue(BaseHandleResponse.SUCCESS(it))
                }
            } else {
                _ward.postValue(BaseHandleResponse.ERROR(response.message()))
            }
        }
    }

}
