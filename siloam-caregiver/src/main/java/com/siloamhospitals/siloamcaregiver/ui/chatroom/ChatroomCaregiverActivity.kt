package com.siloamhospitals.siloamcaregiver.ui.chatroom

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.findNavController
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.ActivityChatroomCaregiverBinding
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.groupdetail.GroupDetailViewModel
import com.siloamhospitals.siloamcaregiver.ui.roomtype.RoomTypeCaregiverActivity

class ChatroomCaregiverActivity : AppCompatActivity() {

    private lateinit var chatRoomViewModel: ChatRoomCaregiverViewModel
//    private lateinit var groupDetailViewModel: GroupDetailViewModel

    private val binding by lazy {
        ActivityChatroomCaregiverBinding.inflate(layoutInflater)
    }

    private val navController by lazy { findNavController(R.id.nav_host_chatroom) }

    private val mPreference by lazy {
        AppPreferences(this)
    }

    companion object {
        const val CAREGIVER_ID = "caregiverId"
        const val CHANNEL_ID = "channelId"
        const val ROOM_NAME = "roomName"
        const val URL_ICON = "urlIcon"
        const val PATIENT_NAME = "patientName"
        const val TAG = "ChatroomCaregiverActivity"

        fun start(context: Context) {
            val intent = Intent(context, ChatroomCaregiverActivity::class.java)
//            intent.putExtra("userId", userId)
            context.startActivity(intent)
        }

        fun start(
            context: Context,
            caregiverId: String,
            channelId: String,
            roomName: String,
            urlIcon: String,
            patientName: String
        ) {
            val intent = Intent(context, ChatroomCaregiverActivity::class.java)
            intent.run {
                putExtra(CAREGIVER_ID, caregiverId)
                putExtra(CHANNEL_ID, channelId)
                putExtra(ROOM_NAME, roomName)
                putExtra(URL_ICON, urlIcon)
                putExtra(PATIENT_NAME, patientName)
            }
            context.startActivity(intent)
        }

        fun getIntent(
            context: Context,
            caregiverId: String,
            channelId: String,
            roomName: String,
            urlIcon: String,
            patientName: String
        ): Intent {
            val intent = Intent(context, ChatroomCaregiverActivity::class.java)
            intent.run {
                putExtra(CAREGIVER_ID, caregiverId)
                putExtra(CHANNEL_ID, channelId)
                putExtra(ROOM_NAME, roomName)
                putExtra(URL_ICON, urlIcon)
                putExtra(PATIENT_NAME, patientName)
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
        chatRoomViewModel.run {
            intent.run {
                if (mPreference.isFromNotif) {
                    caregiverId = mPreference.notifCaregiverId
                    channelId = mPreference.notifChannelId
                    patientName = mPreference.notifPatientName
                    roomName = mPreference.notifRoomName
                    urlIcon = mPreference.notifIcon
                    mPreference.isFromNotif = false
                } else {
                    caregiverId = getStringExtra(CAREGIVER_ID).orEmpty()
                    channelId = getStringExtra(CHANNEL_ID).orEmpty()
                    roomName = getStringExtra(ROOM_NAME).orEmpty()
                    urlIcon = getStringExtra(URL_ICON).orEmpty()
                    patientName = getStringExtra(PATIENT_NAME).orEmpty()
                }
            }
        }
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

//        val groupDetailViewModelFactory = viewModelFactory {
//            initializer {
//                GroupDetailViewModel(repository, mPreference)
//            }
//        }
//
//        groupDetailViewModel =
//            ViewModelProvider(this, groupDetailViewModelFactory)[GroupDetailViewModel::class.java]
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
