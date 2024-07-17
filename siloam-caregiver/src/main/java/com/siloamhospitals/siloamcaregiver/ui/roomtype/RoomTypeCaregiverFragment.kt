package com.siloamhospitals.siloamcaregiver.ui.roomtype

import android.app.Activity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
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
import com.google.gson.Gson
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.FragmentRoomTypeCaregiverBinding
import com.siloamhospitals.siloamcaregiver.ext.datetime.TODAY
import com.siloamhospitals.siloamcaregiver.ext.datetime.YESTERDAY
import com.siloamhospitals.siloamcaregiver.ext.datetime.toLocalDateTime
import com.siloamhospitals.siloamcaregiver.ext.datetime.withFormat
import com.siloamhospitals.siloamcaregiver.ext.text.ellipsizeText
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.CaregiverRoomTypeUi
import com.siloamhospitals.siloamcaregiver.ui.chatroom.ChatroomCaregiverActivity

class RoomTypeCaregiverFragment : Fragment() {

    private var _binding: FragmentRoomTypeCaregiverBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RoomTypeViewModel by activityViewModels()


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
            if (isConnected) {
                viewModel.listenRoom()
            } else {
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT)
                    .show()
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
        binding.tvRoomDescription.text = viewModel.description
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
                        latestMessageAt = if (it.message.isNotEmpty()) it.latestMessageAt
                            ?: "" else "",
                        date = it.message.firstOrNull()?.createAt ?: "",
                        icon = it.icon.urlExt ?: "",
                        role = it.message.firstOrNull()?.user?.role?.name ?: "",
                        senderName = it.message.firstOrNull()?.user?.name ?: "",
                        isActive = it.message.firstOrNull()?.isActive ?: false,
                        hopeId = it.message.firstOrNull()?.user?.hopeId ?: "",
                        isEmptyMessage = it.message.isEmpty()
                    )
                )
            }
            viewModel.listRoomType.sortByDescending { it.latestMessageAt }
            val emptyRoom = viewModel.listRoomType.filter { it.latestMessageAt.isEmpty() }
            val notEmptyRoom = viewModel.listRoomType.filter { it.latestMessageAt.isNotEmpty() }
            viewModel.listRoomType.clear()
            viewModel.listRoomType.addAll(notEmptyRoom)
            viewModel.listRoomType.addAll(emptyRoom)
            Log.e("ROOOOOOOOOOOOOOOM", Gson().toJson(viewModel.listRoomType))
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


                        // Get the draft message
                        val draftMessage = preferences.findPreference(
                            "key_${item.caregiverId}_${item.channelId}",
                            ""
                        )

                        // Check and set the draft message
                        if (draftMessage.isNotEmpty()) {
                            val spannableDraftMessage =
                                SpannableStringBuilder("Draft: $draftMessage")
                            spannableDraftMessage.setSpan(
                                ForegroundColorSpan(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.colorPrimaryCaregiver
                                    )
                                ),
                                0,
                                6,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            tvlastMessage.text = spannableDraftMessage
                        } else {
                            // Check if the message is deleted and empty
                            if (!item.isActive && !item.isEmptyMessage) {
                                val deletedMessage =
                                    if (item.hopeId == preferences.userId.toString()) {
                                        getString(R.string.own_message_deleted)
                                    } else {
                                        when (item.role) {
                                            "Doctor" -> "${item.senderName}: ${getString(R.string.sender_delete)}"
                                            else -> "${item.role} - ${item.senderName}: ${
                                                getString(
                                                    R.string.sender_delete
                                                )
                                            }"
                                        }
                                    }
                                tvlastMessage.text = deletedMessage
                            } else if (item.isEmptyMessage) {
                                // Check if the message is empty
                                tvlastMessage.text = getString(R.string.no_messages)
                            } else if (item.isAttachment) {
                                // Check if there is an attachment
                                val attachmentMessage = when (item.role) {
                                    "Doctor" -> "${item.senderName}: Send Attachment"
                                    else -> "${item.role} - ${item.senderName}: Send Attachment"
                                }
                                tvlastMessage.text = attachmentMessage
                            } else {
                                // Set the last message
                                val lastMessage = when (item.role) {
                                    "Doctor" -> "${item.senderName}: ${item.lastMessage}"
                                    else -> "${item.role} - ${item.senderName}: ${item.lastMessage}"
                                }
                                tvlastMessage.text = lastMessage
                            }
                        }





                        tvBadgeRoomType.text = item.countUnread
                        tvBadgeRoomType.isVisible = item.countUnread != "0"

                        layoutItemRoom.setBackgroundColor(
                            if (item.isUrgent) {
                                resources.getColor(R.color.colorYellowLightCaregiver)
                            } else {
                                resources.getColor(R.color.colorWhiteCaregiver)
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
                                resources.getColor(R.color.colorYellowLightCaregiver)
                            } else {
                                resources.getColor(R.color.colorWhiteCaregiver)
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
