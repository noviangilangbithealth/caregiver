package com.siloamhospitals.siloamcaregiver.ui.button

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.network.response.FloatingNotificationData
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.Event
import kotlinx.coroutines.launch

class CaregiverButtonViewModel(
    private val repository: Repository
): ViewModel() {

    private val _floatingNotification = MutableLiveData<Event<FloatingNotificationData>>()
    val floatingNotification: LiveData<Event<FloatingNotificationData>> = _floatingNotification

    private val _errorFloatingNotification = MutableLiveData<Event<String>>()
    val errorFloatingNotification: LiveData<Event<String>> = _errorFloatingNotification

    fun emitFloatingNotification() {
        repository.emitFloatingNotification()
    }

    fun listenFloatingNotification() {
        viewModelScope.launch {
            repository.listenFloatingNotification { data, error ->
                if (error.isEmpty()) {
                    _floatingNotification.postValue(Event(data))
                } else {
                    _errorFloatingNotification.postValue(Event(error))
                }
            }
        }
    }

}