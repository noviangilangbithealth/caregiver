package com.siloamhospitals.siloamcaregiver.ui.roomtype

import android.app.Activity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.bumptech.glide.Glide
import com.fondesa.recyclerviewdivider.addDivider
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.FragmentRoomTypeCaregiverBinding
import com.siloamhospitals.siloamcaregiver.ui.CaregiverRoomTypeUi
import com.siloamhospitals.siloamcaregiver.ext.datetime.TODAY
import com.siloamhospitals.siloamcaregiver.ext.datetime.YESTERDAY
import com.siloamhospitals.siloamcaregiver.ext.datetime.toLocalDateTime
import com.siloamhospitals.siloamcaregiver.ext.datetime.withFormat
import com.siloamhospitals.siloamcaregiver.ext.text.ellipsizeText
import com.siloamhospitals.siloamcaregiver.ext.view.gone
import com.siloamhospitals.siloamcaregiver.ext.view.visible
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.chatroom.ChatRoomCaregiverViewModel
import com.siloamhospitals.siloamcaregiver.ui.chatroom.ChatroomCaregiverActivity
import com.siloamhospitals.siloamcaregiver.ui.groupdetail.GroupDetailActivity
import com.siloamhospitals.siloamcaregiver.ui.groupdetail.GroupDetailViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class RoomTypeCaregiverFragment : Fragment() {

    private var _binding: FragmentRoomTypeCaregiverBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RoomTypeViewModel by activityViewModels()

//    private val chatRoomViewModel: ChatRoomCaregiverViewModel by activityViewModels()
//    private val detailViewModel: GroupDetailViewModel by activityViewModels()

    private lateinit var preferences: AppPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomTypeCaregiverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences = AppPreferences(requireContext())
        callSocket()
        initHeader()
        setupCheckRecent()
        observeConnection()
    }

    private fun observeConnection() {
        viewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if(isConnected) {
                viewModel.listenRoom()
            } else {
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCheckRecent() {
        if (preferences.recentTag.isNotEmpty() && preferences.recentTag == RoomTypeCaregiverActivity.TAG) {
            preferences.recentTag = ""
            preferences.isFromRecent = true
        } else if (preferences.recentTag.isNotEmpty()) {
            chatRoomLauncher.launch(
                ChatroomCaregiverActivity.getIntent(
                    requireContext(),
                    preferences.caregiverId,
                    preferences.channelId,
                    preferences.roomName,
                    preferences.urlIcon,
                    preferences.patientName
                )
            )
        }
    }

    private fun initHeader() {
        with(binding.topBarRoomType) {
            ibBack.setOnClickListener {
                findNavController().navigateUp()
                requireActivity().finish()
            }
            ivCloseRoomType.setOnClickListener {
                requireActivity().run {
                    preferences.run {
                        recentTag = RoomTypeCaregiverActivity.TAG
                        caregiverId = viewModel.caregiverId
                        patientName = viewModel.patientName
                        description = viewModel.description
                        gender = viewModel.gender
                    }
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
            tvAppBarPatientNameCaregiver.text = ellipsizeText(viewModel.patientName, 25)
            tvAppBarPatientInfoCaregiver.text = viewModel.description
            ivAppBarGender.setImageDrawable(
                if (viewModel.gender == 1) {
                    resources.getDrawable(R.drawable.ic_label_male)
                } else {
                    resources.getDrawable(R.drawable.ic_label_female)
                }
            )
        }
    }

    private fun callSocket() {
        viewModel.emitRoom()
        binding.lottieLoadingRoomtype.visible()
        viewModel.listenRoom()
        viewModel.listenNewRoom()

        observerRoomType()
        observerNewRoom()
    }

    private fun observerNewRoom() {
        viewModel.newRoom.observe(viewLifecycleOwner) { data ->
            viewModel.emitRoom()
        }
    }

    private fun observerRoomType() {
        viewModel.roomTypeList.observe(viewLifecycleOwner) { data ->
            binding.lottieLoadingRoomtype.gone()
            viewModel.listRoomType.clear()
            data.map {
                viewModel.listRoomType.add(
                    CaregiverRoomTypeUi(
                        isAttachment = it.message.firstOrNull()?.attachment?.isNotEmpty() ?: false,
                        channelId = it.id ?: "",
                        caregiverId = viewModel.caregiverId,
                        roomName = it.name ?: "",
                        countUnread = it.countUnreadMessage ?: "",
                        isUrgent = it.isUrgentMessage,
                        lastMessage = it.message.firstOrNull()?.message ?: "",
                        latestMessageAt = it.latestMessageAt ?:"",
                        date = it.message.firstOrNull()?.createAt ?: "",
                        icon = it.icon.urlExt ?: "",
                        role = it.message.firstOrNull()?.user?.role?.name ?: "",
                        senderName = it.message.firstOrNull()?.user?.name ?: ""
                    )
                )
            }
            viewModel.listRoomType.sortByDescending { it.latestMessageAt.toLocalDateTime() }
            onDataLoaded()
        }
    }

    private fun onDataLoaded() {
        with(binding) {
            val dataSource = dataSourceTypedOf(viewModel.listRoomType)
            rvRoom.setup {
                withDataSource(dataSource)
                withItem<CaregiverRoomTypeUi, RoomTypeViewHolder>(R.layout.item_room_type) {
                    onBind(::RoomTypeViewHolder) { _, item ->
                        Glide.with(requireContext()).load(item.icon).into(ivRoomType)
                        tvRoomName.text = item.roomName

                        var lastMessage = when (item.role) {
                            "Doctor" -> "${item.senderName}: ${item.lastMessage}"
                            else -> "${item.role} - ${item.senderName}: ${item.lastMessage}"
                        }

                        if(item.isAttachment) {
                            lastMessage = when (item.role) {
                                "Doctor" -> "${item.senderName}: Send Attachment"
                                else -> "${item.role} - ${item.senderName}: Send Attachment"
                            }
                        }

                        val draftMessage = preferences.findPreference("key_${item.caregiverId}_${item.channelId}", "")
                        val spannableDraftMessage = SpannableStringBuilder("Draft: $draftMessage")
                        spannableDraftMessage.setSpan(
                            ForegroundColorSpan(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorPrimary
                                )
                            ),
                            0,
                            6,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        tvlastMessage.text =
                            if (draftMessage.isNotEmpty()) spannableDraftMessage else if (item.lastMessage.isNotEmpty() || item.isAttachment) lastMessage else "no messages"

                        tvBadgeRoomType.text = item.countUnread
                        tvBadgeRoomType.isVisible = item.countUnread != "0"

                        layoutItemRoom.setBackgroundColor(
                            if (item.isUrgent) {
                                resources.getColor(R.color.colorYellowLight)
                            } else {
                                resources.getColor(R.color.colorWhite)
                            }
                        )

                        if (item.date.isNotEmpty()) {
                            val date = when (item.date.toLocalDateTime().toLocalDate()) {
                                TODAY -> item.date.toLocalDateTime() withFormat "HH:mm"
                                YESTERDAY -> "Yesterday"
                                else -> item.date.toLocalDateTime()
                                    .toLocalDate() withFormat "dd/MM/yyyy"
                            }
                            tvTimeRoom.text = date
                        } else {
                            tvTimeRoom.text = ""
                        }

                        layoutItemRoom.setBackgroundColor(
                            if (item.isUrgent) {
                                resources.getColor(R.color.colorYellowLight)
                            } else {
                                resources.getColor(R.color.colorWhite)
                            }
                        )
                        ivUrgent.isVisible = item.isUrgent
                    }
                    onClick {
                        chatRoomLauncher.launch(
                            ChatroomCaregiverActivity.getIntent(
                                requireContext(),
                                item.caregiverId,
                                item.channelId,
                                item.roomName,
                                item.icon,
                                viewModel.patientName
                            )
                        )
                        /*
                        chatRoomViewModel.run {
                            caregiverId = item.caregiverId
                            channelId = item.channelId
                            roomName = item.roomName
                            urlIcon = item.icon
                            patientName = viewModel.patientName
                        }
                        findNavController().navigate(R.id.action_roomTypeCaregiverFragment_to_chatRoomCaregiverFragment)
                         */
                    }
                }
            }
        }
        binding.rvRoom.addDivider()
    }

    override fun onResume() {
        super.onResume()
        viewModel.emitRoom()
        viewModel.listenRoom()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    val chatRoomLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                requireActivity().run {
                    preferences.run {
                        description = viewModel.description
                        gender = viewModel.gender
                    }
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }

}
