package com.siloamhospitals.siloamcaregiver.ui.patientlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.siloamhospitals.siloamcaregiver.network.ConnectivityLiveData
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.network.response.BaseHandleResponse
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverListData
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverPatientListData
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

    val isConnected: LiveData<Boolean> by lazy {
        ConnectivityLiveData(preferences.context)
    }

    val doctorId = preferences.userId
    var orgId = preferences.organizationId
    var wardId = preferences.wardId
    var orgCode = ""
    var wardName = ""
    var isSpecialist = true

    var currentPage = 1
    var keyword = ""
    var firsLoadFilter = true

    val roomPatientList = mutableListOf<ListCaregiverPatient>()

    init {
        getUserShow()
    }

    val hospitals = mutableListOf<UserShowHospitalResponse>()
    val wards = mutableListOf<WardListResponse>()

    fun emitGetCaregiver(action: (() -> Unit)? = null) {
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

    private var _newCaregiver = MutableLiveData<CaregiverPatientListData>()
    var newCaregiver: LiveData<CaregiverPatientListData> = _newCaregiver

    private var _deleteCaregiver = MutableLiveData<CaregiverPatientListData>()
    var deleteCaregiver: LiveData<CaregiverPatientListData> = _deleteCaregiver

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
                    Logger.d(data)
                    errorHasBeenConsumed = false
                    _error.postValue(error)
                }
            }
        }
    }

    fun listenNewCaregiver() {
        viewModelScope.launch {
            repository.listenNewCaregiver() { data, error ->
                if (error.isEmpty()) {
                    Logger.d(data)
                    _newCaregiver.postValue(data)
                    errorHasBeenConsumed = false
                } else {
                    Logger.d(data)
                    errorHasBeenConsumed = false
                    _error.postValue(error)
                }
            }
        }
    }

    fun listenDeleteCaregiver() {
        viewModelScope.launch {
            repository.listenNewCaregiver() { data, error ->
                if (error.isEmpty()) {
                    Logger.d(data)
                    _deleteCaregiver.postValue(data)
                    errorHasBeenConsumed = false
                } else {
                    Logger.d(data)
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
            try {
                val response = repository.getUserShow(doctorId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _userShow.postValue(BaseHandleResponse.SUCCESS(it))
                    }
                } else {
                    _userShow.postValue(BaseHandleResponse.ERROR(response.message()))
                }
            } catch (e: Exception) {
                _userShow.postValue(BaseHandleResponse.ERROR(e.message.orEmpty()))
            }
        }
    }

    private val _ward = MutableLiveData<BaseHandleResponse<WardResponse>>()
    val ward: LiveData<BaseHandleResponse<WardResponse>> = _ward

    fun getWard(hospitalId: Long = orgId) {
        viewModelScope.launch {
            try {
                val response = repository.getWard(hospitalId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _ward.postValue(BaseHandleResponse.SUCCESS(it))
                    }
                } else {
                    _ward.postValue(BaseHandleResponse.ERROR(response.message()))
                }
            } catch (e: Exception) {
                _ward.postValue(BaseHandleResponse.ERROR(e.message.orEmpty()))
            }
        }
    }

}
