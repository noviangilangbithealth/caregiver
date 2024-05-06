package com.siloamhospitals.siloamcaregiver.ui.roomtype

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.findNavController
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.ActivityRoomTypeCaregiverBinding
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.chatroom.ChatroomCaregiverActivity
import com.siloamhospitals.siloamcaregiver.ui.groupdetail.GroupDetailViewModel

class RoomTypeCaregiverActivity : AppCompatActivity() {

    private lateinit var roomTypeViewModel: RoomTypeViewModel
//    private lateinit var groupDetailViewModel: GroupDetailViewModel

    private val binding by lazy {
        ActivityRoomTypeCaregiverBinding.inflate(layoutInflater)
    }

    val mPreference by lazy {
        AppPreferences(this)
    }

    private val navController by lazy { findNavController(R.id.nav_host_roomtype) }

    companion object {
        const val CAREGIVER_ID = "caregiverId"
        const val PATIENT_NAME = "patientName"
        const val DESCRIPTION = "description"
        const val GENDER = "gender"
        const val TAG = "RoomTypeCaregiverActivity"

        fun start(context: Context) {
            val intent = Intent(context, RoomTypeCaregiverActivity::class.java)
//            intent.putExtra("userId", userId)
            context.startActivity(intent)
        }

        fun start(
            context: Context,
            caregiverId: String,
            patientName: String,
            description: String,
            gender: Int
        ) {
            val intent = Intent(context, RoomTypeCaregiverActivity::class.java)
            intent.run {
                putExtra(CAREGIVER_ID, caregiverId)
                putExtra(PATIENT_NAME, patientName)
                putExtra(DESCRIPTION, description)
                putExtra(GENDER, gender)
            }
            context.startActivity(intent)
        }

        fun getIntent(
            context: Context,
            caregiverId: String,
            patientName: String,
            description: String,
            gender: Int
        ): Intent {
            val intent = Intent(context, RoomTypeCaregiverActivity::class.java)
            intent.run {
                putExtra(CAREGIVER_ID, caregiverId)
                putExtra(PATIENT_NAME, patientName)
                putExtra(DESCRIPTION, description)
                putExtra(GENDER, gender)
            }
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViewModel()
        initDataExtra()
    }

    private fun initDataExtra() {
        roomTypeViewModel.run {
            intent.run {
                caregiverId = getStringExtra(CAREGIVER_ID).orEmpty()
                patientName = getStringExtra(PATIENT_NAME).orEmpty()
                description = getStringExtra(DESCRIPTION).orEmpty()
                gender = getIntExtra(GENDER, 0)
            }
        }
    }

    private fun initViewModel() {
        val repository = Repository(mPreference)
        val roomTypeViewModelFactory = viewModelFactory {
            initializer {
                RoomTypeViewModel(repository, mPreference)
            }
        }
        roomTypeViewModel =
            ViewModelProvider(this, roomTypeViewModelFactory)[RoomTypeViewModel::class.java]

//        val groupDetailViewModelFactory = viewModelFactory {
//            initializer {
//                GroupDetailViewModel(repository, mPreference)
//            }
//        }
//        groupDetailViewModel =
//            ViewModelProvider(this, groupDetailViewModelFactory)[GroupDetailViewModel::class.java]
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}