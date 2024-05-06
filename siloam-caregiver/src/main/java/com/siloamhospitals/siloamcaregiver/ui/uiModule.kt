package com.siloamhospitals.siloamcaregiver.ui

import android.content.Context
import com.siloamhospitals.siloamcaregiver.ui.chatroom.ChatRoomCaregiverViewModel
import com.siloamhospitals.siloamcaregiver.ui.patientlist.CaregiverPatientListViewModel
import com.siloamhospitals.siloamcaregiver.ui.roomtype.RoomTypeViewModel
import com.siloamhospitals.siloamcaregiver.ui.testconnection.TestConnectionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Context.uiModule
    get() = module {
        viewModel { TestConnectionViewModel(get(), get()) }
        viewModel { CaregiverPatientListViewModel(get(), get()) }
        viewModel { RoomTypeViewModel(get(), get()) }
        viewModel { ChatRoomCaregiverViewModel(get(), get())}
    }
