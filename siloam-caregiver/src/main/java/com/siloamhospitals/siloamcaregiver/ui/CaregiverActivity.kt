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
import com.siloamhospitals.siloamcaregiver.ui.chatroom.ChatRoomCaregiverViewModel
import com.siloamhospitals.siloamcaregiver.ui.groupdetail.GroupDetailViewModel
import com.siloamhospitals.siloamcaregiver.ui.patientlist.CaregiverPatientListViewModel
import com.siloamhospitals.siloamcaregiver.ui.roomtype.RoomTypeCaregiverActivity
import com.siloamhospitals.siloamcaregiver.ui.roomtype.RoomTypeViewModel

class CaregiverActivity : AppCompatActivity() {

    private lateinit var chatRoomViewModel: ChatRoomCaregiverViewModel
    private lateinit var roomTypeViewModel: RoomTypeViewModel
    private lateinit var caregiverPatientListViewModel: CaregiverPatientListViewModel
    private lateinit var groupDetailViewModel: GroupDetailViewModel

    private val binding by lazy {
        ActivityCaregiverBinding.inflate(layoutInflater)
    }

    private val navController by lazy { findNavController(R.id.nav_host_caregiver) }

    val mPreference by lazy {
        AppPreferences(this)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, CaregiverActivity::class.java)
//            intent.putExtra("userId", userId)
            context.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViewModel()
    }

    private fun initViewModel() {
        val repository = Repository(mPreference)
        val chatRoomCaregiverViewModelFactory = viewModelFactory {
            initializer {
                ChatRoomCaregiverViewModel(repository, mPreference)
            }
        }
        chatRoomViewModel = ViewModelProvider(
            this,
            chatRoomCaregiverViewModelFactory
        )[ChatRoomCaregiverViewModel::class.java]

        val roomTypeViewModelFactory = viewModelFactory {
            initializer {
                RoomTypeViewModel(repository, mPreference)
            }
        }
        roomTypeViewModel =
            ViewModelProvider(this, roomTypeViewModelFactory)[RoomTypeViewModel::class.java]

        val caregiverPatientListViewModelFactory = viewModelFactory {
            initializer {
                CaregiverPatientListViewModel(repository, mPreference)
            }
        }
        caregiverPatientListViewModel = ViewModelProvider(
            this,
            caregiverPatientListViewModelFactory
        )[CaregiverPatientListViewModel::class.java]

        val groupDetailViewModelFactory = viewModelFactory {
            initializer {
                GroupDetailViewModel(repository, mPreference)
            }
        }
        groupDetailViewModel =
            ViewModelProvider(this, groupDetailViewModelFactory)[GroupDetailViewModel::class.java]
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}
