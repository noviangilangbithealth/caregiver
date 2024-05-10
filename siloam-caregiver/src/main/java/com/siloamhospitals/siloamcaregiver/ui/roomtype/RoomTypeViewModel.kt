package com.siloamhospitals.siloamcaregiver.ui.roomtype

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siloamhospitals.siloamcaregiver.network.ConnectivityLiveData
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverRoomType
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverRoomTypeData
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.CaregiverRoomTypeUi
import kotlinx.coroutines.launch

class RoomTypeViewModel(
    private val repository: Repository,
    private val preferences: AppPreferences,
) : ViewModel() {

    var patientName = ""
    var gender = 0
    var description = ""
    var caregiverId = ""

    val doctorId = preferences.userId

    var listRoomType = mutableListOf<CaregiverRoomTypeUi>()

    val isConnected: LiveData<Boolean> by lazy {
        ConnectivityLiveData(preferences.context)
    }


    fun emitRoom() {
        repository.emitGetRoom(caregiverId, doctorId.toString())
    }

    private var _roomTypeList = MutableLiveData<List<CaregiverRoomTypeData>>()
    var roomTypeList: LiveData<List<CaregiverRoomTypeData>> = _roomTypeList

    private var _error = MutableLiveData<String>()
    var error: LiveData<String> = _error
    var errorHasBeenConsumed = false

    fun listenRoom() {
        viewModelScope.launch {
            repository.listenRoomList(doctorId.toString()) { data, error ->
                if (error.isEmpty()) {
                    _roomTypeList.postValue(data)
                } else {
                    _error.postValue(error)
                }
            }
        }
    }


}
