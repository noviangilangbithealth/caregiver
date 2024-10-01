package com.siloamhospitals.siloamcaregiver.ui.patientlist.rmo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.network.response.BaseHandleResponse
import com.siloamhospitals.siloamcaregiver.network.response.rmo.RmoMasterDataResponse
import com.siloamhospitals.siloamcaregiver.network.response.rmo.RmoParticipantsResponse
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.patientlist.ROLE_NURSE
import kotlinx.coroutines.launch

class RmoViewModel(
    private val repository: Repository,
    preferences: AppPreferences
) : ViewModel() {


    var isSpecialist = false
    var isRmo = false
    var isHospitalist = false
    var isNurse = preferences.role == ROLE_NURSE

    var orgId = ""
    var wardId = ""
    var wardName = ""
    var userId = ""


    var query = ""

    val rmoParticipantsList = mutableListOf<RmoList>()

    val rmoUiList = mutableListOf<RmoList>()

    private val _rmoParticipants = MutableLiveData<BaseHandleResponse<RmoParticipantsResponse>>()
    val rmoParticipants: LiveData<BaseHandleResponse<RmoParticipantsResponse>> =
        _rmoParticipants

    fun getRmoParticipants() {
        viewModelScope.launch {
            try {
                val response = repository.getRmoListParticipants(orgId, wardId, userId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _rmoParticipants.postValue(BaseHandleResponse.SUCCESS(it))
                    }
                } else {
                    _rmoParticipants.postValue(BaseHandleResponse.ERROR(response.message()))
                }
            } catch (e: Exception) {
                _rmoParticipants.postValue(BaseHandleResponse.ERROR(e.message.orEmpty()))
            }
        }
    }


    private val _postRmoParticipantsLiveData = MutableLiveData<BaseHandleResponse<Unit>>()
    val postRmoParticipantsLiveData: LiveData<BaseHandleResponse<Unit>> =
        _postRmoParticipantsLiveData

    fun addParticipantsRmo(members: List<RmoList>) {
        viewModelScope.launch {
            try {
                val response = repository.addRmoParticipants(
                    orgId = orgId,
                    wardId = wardId,
                    members = members
                )
                if (response.isSuccessful) {
                    _postRmoParticipantsLiveData.postValue(BaseHandleResponse.SUCCESS(Unit))
                } else {
                    Logger.d(response.message())
                    _postRmoParticipantsLiveData.postValue(BaseHandleResponse.ERROR(response.message()))
                }
            } catch (e: Exception) {
                Logger.d(e)
                _postRmoParticipantsLiveData.postValue(BaseHandleResponse.ERROR(e.message.orEmpty()))
            }
        }
    }

    private val _rmoMaster = MutableLiveData<BaseHandleResponse<RmoMasterDataResponse>>()
    val rmoMaster: LiveData<BaseHandleResponse<RmoMasterDataResponse>> =
        _rmoMaster

    fun getRmoMaster() {
        viewModelScope.launch {
            try {
                val response = repository.getMasterRMo(orgId, wardId, userId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _rmoMaster.postValue(BaseHandleResponse.SUCCESS(it))
                    }
                } else {
                    _rmoMaster.postValue(BaseHandleResponse.ERROR(response.message()))
                }
            } catch (e: Exception) {
                _rmoMaster.postValue(BaseHandleResponse.ERROR(e.message.orEmpty()))
            }
        }
    }


    private var _rmoDataUi =
        MutableLiveData<List<RmoList>>()
    internal val rmoDataUi: LiveData<List<RmoList>> get() = _rmoDataUi

    fun setSelectedRmo(ids: List<Long>) {
        ids.forEach { e ->
            val rmo = rmoUiList.find { it.id == e }
            if (rmo != null) {
                val newData = rmo.copy(isChecked = true)
                rmoUiList.removeIf { it.id == e }
                rmoUiList.add(newData)
            }
        }
        rmoUiList.sortBy { it.name }
        rmoUiList.sortByDescending { it.isChecked }
        emitRmo()
    }


    fun checkedChangeRmo(rmo: RmoList) {
        val newData = rmo.copy(isChecked = !rmo.isChecked)
        rmoUiList.removeIf { it.id == rmo.id }
        rmoUiList.add(newData)
        rmoUiList.sortBy { it.name }
        rmoUiList.sortByDescending { it.isChecked }
        if (query.isNotEmpty()) {
            filterRmo()
        } else {
            emitRmo()
        }
    }


    fun checkedFalseChangeRmo(rmo: RmoList, index: Int) {
        val newData = rmo.copy(isChecked = false)
        rmoUiList.removeIf { it.id == rmo.id }
        rmoUiList.add(index, newData)
        rmoUiList.sortBy { it.name }
        rmoUiList.sortByDescending { it.isChecked }
        if (query.isNotEmpty()) {
            filterRmo()
        } else {
            emitRmo()
        }
    }

    fun filterRmo() {
        val filteredList =
            rmoUiList.filter { !it.isChecked }.filter { it.name.contains(query, ignoreCase = true) }
        val filterdChecked = rmoUiList.filter { it.isChecked }
        val data = mutableListOf<RmoList>()
        data.addAll(filterdChecked)
        data.addAll(filteredList)
        rmoUiList.sortBy { it.name }
        rmoUiList.sortByDescending { it.isChecked }
        _rmoDataUi.value = data
    }

    fun emitRmo() {
        _rmoDataUi.value = rmoUiList

    }


}