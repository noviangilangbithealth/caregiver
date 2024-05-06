package com.siloamhospitals.siloamcaregiver.ui.groupdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.findNavController
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.ActivityGroupDetailBinding
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences

class GroupDetailActivity : AppCompatActivity() {

    private lateinit var groupDetailViewModel: GroupDetailViewModel

    private val binding by lazy {
        ActivityGroupDetailBinding.inflate(layoutInflater)
    }

    private val mPreference by lazy {
        AppPreferences(this)
    }

    private val navController by lazy { findNavController(R.id.nav_host_groupdetail) }

    companion object {
        const val CAREGIVER_ID = "caregiverId"
        const val CHANNEL_ID = "channelId"

        fun start(context: Context) {
            val intent = Intent(context, GroupDetailActivity::class.java)
//            intent.putExtra("userId", userId)
            context.startActivity(intent)
        }

        fun start(context: Context, caregiverId: String, channelId: String) {
            val intent = Intent(context, GroupDetailActivity::class.java)
            intent.putExtra(CAREGIVER_ID, caregiverId)
            intent.putExtra(CHANNEL_ID, channelId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViewModel()
        initData()
    }

    private fun initData() {
        groupDetailViewModel.caregiverId = intent.getStringExtra(CAREGIVER_ID).orEmpty()
        groupDetailViewModel.channelId = intent.getStringExtra(CHANNEL_ID).orEmpty()
    }

    private fun initViewModel() {
        val repository = Repository(mPreference)
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
