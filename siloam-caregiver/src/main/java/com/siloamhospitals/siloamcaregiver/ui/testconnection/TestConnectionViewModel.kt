package com.siloamhospitals.siloamcaregiver.ui.testconnection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverList
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverListData
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import kotlinx.coroutines.launch

class TestConnectionViewModel(
    private val repository: Repository,
    preferences: AppPreferences
) : ViewModel() {

    val doctorId = preferences.userId
    val orgId = preferences.organizationId

//    fun emitGetCaregiver(keyword: String = "") {
//        repository.emitGetCaregiver(
//            page = 1,
//            keyword = keyword,
//            user = doctorId.toString(),
//            organizationId = orgId,
//            wardId = ward
//        )
//    }

    private var _caregiverList = MutableLiveData<CaregiverListData>()
    var caregiverList: LiveData<CaregiverListData> = _caregiverList

    private var _error = MutableLiveData<String>()
    var error: LiveData<String> = _error
    var errorHasBeenConsumed = false

    fun listenCaregiverList() {
        viewModelScope.launch {
            repository.listenCaregiverList() { data, error ->
                if (error.isEmpty()) {
                    _caregiverList.postValue(data)
                } else {
                    _error.postValue(error)
                }
            }
        }
    }

}
