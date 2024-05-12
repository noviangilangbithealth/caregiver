package com.siloamhospitals.siloamcaregiver.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.findNavController
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.ActivityCaregiverBinding
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.patientlist.CaregiverPatientListViewModel


class CaregiverActivity : AppCompatActivity() {

    private lateinit var caregiverPatientListViewModel: CaregiverPatientListViewModel

    private val binding by lazy {
        ActivityCaregiverBinding.inflate(layoutInflater)
    }

    private val navController by lazy { findNavController(R.id.nav_host_caregiver) }

    lateinit var mPreference: AppPreferences

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, CaregiverActivity::class.java)
//            intent.putExtra("userId", userId)
            context.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPreference = AppPreferences(this)
        setContentView(binding.root)
        initViewModel()
    }

    private fun initViewModel() {
        val repository = Repository(mPreference)

        val caregiverPatientListViewModelFactory = viewModelFactory {
            initializer {
                CaregiverPatientListViewModel(repository, mPreference)
            }
        }
        caregiverPatientListViewModel = ViewModelProvider(
            this,
            caregiverPatientListViewModelFactory
        )[CaregiverPatientListViewModel::class.java]

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}
