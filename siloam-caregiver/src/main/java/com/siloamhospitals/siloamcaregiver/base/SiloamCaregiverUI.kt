package com.siloamhospitals.siloamcaregiver.base

import android.content.Context
import android.util.Log
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.CaregiverActivity

class SiloamCaregiverUI {


    companion object {
        private var instance: SiloamCaregiverUI? = null

        fun getInstances() : SiloamCaregiverUI {
            if (instance == null) {
                instance = SiloamCaregiverUI()
                return instance as SiloamCaregiverUI
            }

            return instance as SiloamCaregiverUI
        }


    }

    fun openCaregiver(context: Context) {
        CaregiverActivity.start(context)
    }

}
