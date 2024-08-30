package com.siloamhospitals.siloamcaregiver.ui.chatroom

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.recyclical.datasource.DataSource
import com.afollestad.recyclical.datasource.emptyDataSourceTyped
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.FragmentChatRoomCaregiverBinding
import com.siloamhospitals.siloamcaregiver.ext.bitmap.BitmapUtils
import com.siloamhospitals.siloamcaregiver.ext.bitmap.BitmapUtils.getRealPathFromURI
import com.siloamhospitals.siloamcaregiver.ext.view.gone
import com.siloamhospitals.siloamcaregiver.ext.view.invisible
import com.siloamhospitals.siloamcaregiver.ext.view.visible
import com.siloamhospitals.siloamcaregiver.network.response.BaseHandleResponse
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChatData
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.CaregiverChatRoomUi
import com.siloamhospitals.siloamcaregiver.ui.chatroom.adapters.ChatRoomCaregiverAdapter
import com.siloamhospitals.siloamcaregiver.ui.chatroom.adapters.PinChatAdapter
import com.siloamhospitals.siloamcaregiver.ui.chatroom.recorder.AudioRecordListener
import com.siloamhospitals.siloamcaregiver.ui.chatroom.recorder.Recorder
import com.siloamhospitals.siloamcaregiver.ui.decoration.SpaceItemDecoration
import com.siloamhospitals.siloamcaregiver.ui.groupdetail.GroupDetailActivity
import com.siloamhospitals.siloamcaregiver.ui.player.VideoPlayerActivity
import okhttp3.internal.filterList
import java.io.File
import java.util.UUID


class ChatRoomCaregiverFragment : Fragment(), AudioRecordListener {

    private var _binding: FragmentChatRoomCaregiverBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatRoomCaregiverViewModel by activityViewModels()

    private var dataSource: DataSource<CaregiverChatRoomUi> = emptyDataSourceTyped()

    private lateinit var adapterChatRoom: ChatRoomCaregiverAdapter

    private lateinit var preferences: AppPreferences

    private lateinit var recorder: Recorder
    private var positionDelete: Int? = null
    private var fromRetry = false
    private var lastMessagefromInput = ""
    private var currentSentId = ""
    private var chatPinnedList = listOf<CaregiverChatData>()
    private var inActionMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatRoomCaregiverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitleChat.setOnClickListener {
            GroupDetailActivity.start(requireContext(), viewModel.caregiverId, viewModel.channelId)
        }
        binding.tvPatientName.setOnClickListener {
            GroupDetailActivity.start(requireContext(), viewModel.caregiverId, viewModel.channelId)
        }


        initRecorder()
        setupPreference()
        setupAdapter()
        callSocket()
        setupInit()
        setupListener()
        setupObserver()
        setupView()
        setupCheckRecent()
//        setupPinChat()
    }

    private var targetPosition = 0

    private fun initRecorder() {
        recorder = Recorder(this, requireContext())
    }

    private fun setupCheckRecent() {
        if (preferences.recentTag.isNotEmpty() && preferences.recentTag == ChatroomCaregiverActivity.TAG) {
            preferences.recentTag = ""
        }
    }

    private fun setupPreference() {
        preferences = AppPreferences(requireContext())
    }

    private fun setupAdapter() {
        adapterChatRoom = ChatRoomCaregiverAdapter(
            action = { clicktype, item, position ->
                when (clicktype) {
                    ChatRoomCaregiverAdapter.ClickType.MEDiA -> {
                        if (item.isVideo) {
                            VideoPlayerActivity.openPlayer(requireContext(), item.url)
                        } else {
                            viewDetailImage(item.url)
                        }
                    }

                    ChatRoomCaregiverAdapter.ClickType.LINK -> {
                        viewModel.urlEmrIpd = item.url
                        findNavController().navigate(R.id.action_chatRoomCaregiverFragment2_to_chatRoomWebViewFragment)
                    }

                    ChatRoomCaregiverAdapter.ClickType.DElETE_OR_PIN -> {
                        showDeleteOrPinChatConfirmationDialog(item, position)
                    }

                    ChatRoomCaregiverAdapter.ClickType.RETRY -> {
                        showResendMessageConfirmDialog(item, position)
                    }

                    ChatRoomCaregiverAdapter.ClickType.PIN -> {
                        if (chatPinnedList.find { it.id == item.id } == null) {
                            showPinChatConfirmationDialog(item)
                        } else {
                            showUnpinChatConfirmationDialog(item)
                        }
                    }
                }
            }
        )

        binding.rvChatCaregiver.run {
            adapter = adapterChatRoom
            addItemDecoration(SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.medium)))
        }
    }

    private fun startActionMode() {
        inActionMode = true
        binding.run {
            toolbarChatRoom.menu.clear()
            toolbarChatRoom.inflateMenu(R.menu.action_mode_chat_room)
            layoutToolbarChatRoom.invisible()
            toolbarChatRoom.setNavigationIcon(R.drawable.ic_close)
        }
    }

    private fun endActionMode() {
        inActionMode = false
        binding.run {
            toolbarChatRoom.menu.clear()
            layoutToolbarChatRoom.visible()
            toolbarChatRoom.setNavigationIcon(R.drawable.ic_arrow_back_primary)
        }
    }

    private fun showDeleteOrPinChatConfirmationDialog(item: CaregiverChatRoomUi, position: Int) {

        startActionMode()

        val isNotPin = chatPinnedList.find { it.id == item.id } == null
        val itemMenu = binding.toolbarChatRoom.menu.findItem(R.id.action_pin)
        if (itemMenu != null) {
            itemMenu.icon = ContextCompat.getDrawable(
                requireContext(),
                if (isNotPin) R.drawable.ic_pin else R.drawable.ic_unpin_primary
            )
        } else {
            Toast.makeText(requireContext(), "Item not found", Toast.LENGTH_SHORT).show()
        }

        binding.toolbarChatRoom.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_delete -> {
                    positionDelete = position
                    DeleteMessageBottomSheetDialog {
                        viewModel.deleteMessage(item.id)
                    }.show(childFragmentManager, "DeleteMessageBottomSheetDialog")
                    endActionMode()
                    true
                }

                R.id.action_pin -> {
                    if (isNotPin) {
                        showPinChatConfirmationDialog(item)
                    } else {
                        showUnpinChatConfirmationDialog(item)
                    }
                    endActionMode()
                    true
                }

                else -> false
            }

        }

    }

    private fun showResendMessageConfirmDialog(item: CaregiverChatRoomUi, itemPosition: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pesan Gagal Dikirim")
        builder.setMessage("Silahkan pilih aksi untuk pesan ini")
        builder.setPositiveButton("Kirim Ulang") { dialog, _ ->
            if (item.url.isEmpty()) {
                viewModel.sendChat(sentId = item.sentId, message = item.message)
            } else {
                viewModel.uploadFiles(listOf(File(item.url)))
            }
            fromRetry = true
            currentSentId = item.sentId
            positionDelete = itemPosition
            dialog.dismiss()
        }
        builder.setNegativeButton("Hapus Pesan") { dialog, _ ->
            adapterChatRoom.remove(itemPosition)
            viewModel.deleteFailedMessage(item.sentId)
            dialog.dismiss()
        }
        builder.show()
    }

    private fun showPinChatConfirmationDialog(item: CaregiverChatRoomUi) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pin Chat")
        builder.setMessage("Apakah anda yakin ingin mem-pin chat ini?")
        builder.setPositiveButton("Ya") { dialog, _ ->
            viewModel.pinChat(item.id, true)
            dialog.dismiss()
        }
        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun showUnpinChatConfirmationDialog(item: CaregiverChatRoomUi) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Unpin Chat")
        builder.setMessage("Apakah anda yakin ingin mem-unpin chat ini?")
        builder.setPositiveButton("Ya") { dialog, _ ->
            viewModel.pinChat(item.id, false)
            dialog.dismiss()
        }
        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun setupInit() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                if (seconds < (5 * 60)) {
                    updateTimerText()
                    if (isTimerRunning) {
                        handler.postDelayed(this, 1000)
                    }
                } else {
                    handler.removeCallbacks(runnable)
                }
            }
        }
    }

    private fun setupView() {
        binding.run {
            etInputChat.setText(
                preferences.findPreference(
                    "key_${viewModel.caregiverId}_${viewModel.channelId}",
                    ""
                )
            )
            tvTitleChat.text = viewModel.roomName
            tvPatientName.text = viewModel.patientName
            Glide.with(requireContext()).load(viewModel.urlIcon).into(ivRoomChat)
        }
    }

    private fun callSocket() {
        viewModel.emitGetPinChat()
        viewModel.getCaregiverChat()
        viewModel.listenNewMessageList()
        viewModel.listenPinChat()
    }

    private fun setupObserver() {
        observeSendChat()
        observeNewMessage()
        observeUploadPhotos()
        observeConnection()
        observeDeleteMessage()
        observeChatMessages()
        obsservePinMessage()
        observeGetPinMessages()
    }

    private fun observeGetPinMessages() {
        viewModel.pinChatMessage.observe(viewLifecycleOwner) { response ->
            when (response) {
                is BaseHandleResponse.ERROR -> {
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }

                is BaseHandleResponse.LOADING -> {}
                is BaseHandleResponse.SUCCESS -> {
                    chatPinnedList = response.data.orEmpty()
                    if (chatPinnedList.isNotEmpty()) {
                        binding.cardPinChat.visible()
                        val adapter = PinChatAdapter { position ->
                            adapterChatRoom.getList().find { it.id == chatPinnedList[position].id }
                                ?.let {
                                    binding.rvChatCaregiver.smoothScrollToPosition(
                                        adapterChatRoom.getIndexOf(it)
                                    )
                                }
                        }
                        binding.viewpagerPinChat.adapter = adapter
                        adapter.initialize(chatPinnedList.map { if(it.attachment.orEmpty().isEmpty()) it.message.orEmpty() else "attachment" })
                        binding.viewpagerPinChat.registerOnPageChangeCallback(object :
                            ViewPager2.OnPageChangeCallback() {
                            override fun onPageSelected(position: Int) {
                                targetPosition = position
                                binding.tvPinChatCount.text =
                                    "${position + 1} of ${chatPinnedList.size}"
                            }
                        }
                        )
                    } else {
                        binding.cardPinChat.gone()
                    }
                }
            }
        }
    }

    private fun obsservePinMessage() {
        viewModel.pinChat.observe(viewLifecycleOwner) { response ->
            when (response) {
                is BaseHandleResponse.ERROR -> {
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }

                is BaseHandleResponse.LOADING -> {}
                is BaseHandleResponse.SUCCESS -> {
                    Toast.makeText(requireContext(), "Message Pinned", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeDeleteMessage() {
        viewModel.deleteMessage.observe(viewLifecycleOwner) { response ->
            when (response) {
                is BaseHandleResponse.ERROR -> {
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }

                is BaseHandleResponse.LOADING -> {}
                is BaseHandleResponse.SUCCESS -> {
//                    Toast.makeText(requireContext(), "Message Deleted", Toast.LENGTH_SHORT).show()
                    viewModel.listenNewMessageList()
                }
            }
        }
    }

    private fun observeConnection() {
        viewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                viewModel.listenNewMessageList()
            } else {
                Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT)
                    .show()
//                viewModel.getCaregiverChat()
            }
        }

    }

    private fun observeChatMessages() {
        viewModel.run {
            chatMessages.observe(viewLifecycleOwner) { it ->
                it.getContentIfNotHandled()?.let { data ->

                    adapterChatRoom.initialize(data.first.generateChatListUI(adapterChatRoom.firstItem()) {
                        if (it) adapterChatRoom.remove(adapterChatRoom.getSize() - 1)
                    })


                    if (data.second.isNotEmpty()) {
                        adapterChatRoom.add(
                            0,
                            CaregiverChatRoomUi(
                                isUnread = true,
                                unreadCount = data.second.size
                            )
                        )
                        data.second.generateChatListUI(adapterChatRoom.secondItem()) {
                            if (it) adapterChatRoom.remove(adapterChatRoom.getSize() - 1)
                        }.reversed().map {
                            adapterChatRoom.add(0, it)
                        }
                    }

                    if (data.third.isNotEmpty()) {
                        data.third.map {
                            adapterChatRoom.add(
                                0, CaregiverChatRoomUi(
                                    sentId = it.sentId,
                                    message = it.message,
                                    isFailed = true,
                                    isSelfSender = true,
                                    isActive = true,
                                    url = it.localuri,
                                    isVideo = it.isVideo
                                )
                            )
                        }
                    }

                    setReadMessage()
                }
            }
        }
    }

    private fun observeNewMessage() {
        viewModel.run {

            newMessage.observe(viewLifecycleOwner) { it ->

                it.getContentIfNotHandled()?.let { data ->

                    val size = adapterChatRoom.getList().filterList { isFailed }.size
                    if (data.channelId == viewModel.channelId && data.caregiverId == viewModel.caregiverId) {
                        val result = adapterChatRoom.getList().find { it.id == data.id }
                        if (result == null) {
                            adapterChatRoom.add(size, data.generateNewChatUI())
                            binding.rvChatCaregiver.smoothScrollToPosition(0)
                            setReadMessage()
                        } else if (data.isActive!!.not() && positionDelete != null && data.senderID == viewModel.doctorHopeId) {
                            adapterChatRoom.update(positionDelete!!, data.generateNewChatUI())
                        } else if (data.isActive.not()) {
                            adapterChatRoom.update(
                                adapterChatRoom.getIndexOf(result),
                                data.generateNewChatUI()
                            )
                        }
                        viewModel.insertChatMessageWithoutCheck(data)
                    }
                }
            }
        }
    }

    private fun observeUploadPhotos() {
        viewModel.uploadFiles.observe(viewLifecycleOwner) { response ->
            when (response) {
                is BaseHandleResponse.ERROR -> {
                    createFailedMessage(pathsFileWanttoUpload)
                }

                is BaseHandleResponse.LOADING -> {}
                is BaseHandleResponse.SUCCESS -> {
                    Logger.d(response.data)
                    viewModel.sendChat(attachments = response.data?.data.orEmpty())
                }

            }
        }
    }

    private fun observeSendChat() {
        viewModel.sendMessage.observe(viewLifecycleOwner) { response ->
            when (response) {
                is BaseHandleResponse.ERROR -> createFailedMessage()
                is BaseHandleResponse.LOADING -> {}
                is BaseHandleResponse.SUCCESS -> {
                    if (fromRetry) {
                        viewModel.deleteFailedMessage(currentSentId)
                        adapterChatRoom.remove(positionDelete!!)
                    }
                    currentSentId = ""
                    positionDelete = null
                    fromRetry = false
                }
            }

        }
    }

    fun createFailedMessage(pathsAttachment: Pair<Boolean, List<String>> = Pair(false, listOf())) {
        if (!fromRetry) {
            val randomUUID = UUID.randomUUID().toString()
            adapterChatRoom.add(
                0,
                CaregiverChatRoomUi(
                    sentId = randomUUID,
                    message = lastMessagefromInput,
                    isFailed = true,
                    isSelfSender = true,
                    isActive = true,
                    url = if (pathsAttachment.second.size == 1) pathsAttachment.second[0] else "",
                    isVideo = pathsAttachment.first
                )
            )
            viewModel.insertFailedMessage(
                randomUUID,
                lastMessagefromInput,
                if (pathsAttachment.second.size == 1) pathsAttachment.second[0] else "",
                isVideo = pathsAttachment.first
            )
        }
        fromRetry = false
        lastMessagefromInput = ""

        binding.rvChatCaregiver.smoothScrollToPosition(0)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListener() {
        binding.run {

            requireActivity()
                .onBackPressedDispatcher
                .addCallback(
                    (activity as ChatroomCaregiverActivity),
                    object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            if (inActionMode) {
                                endActionMode()
                            } else {
                                requireActivity().finish()
                            }
                        }
                    }
                )

            toolbarChatRoom.setNavigationOnClickListener {
                Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
                if (inActionMode) {
                    endActionMode()
                } else {
//                    findNavController().navigateUp()
                    requireActivity().finish()
                }
            }

            rvChatCaregiver.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    // Check if the scroll has stopped
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        // Remove the scroll listener
                        recyclerView.removeOnScrollListener(this)

                        // Find the target item view
                        val targetView =
                            recyclerView.layoutManager?.findViewByPosition(targetPosition)
                        targetView?.performClick()
                    }
                }
            })

            ivCloseChatRoom.setOnClickListener {
                requireActivity().run {
                    preferences.run {
                        recentTag = ChatroomCaregiverActivity.TAG
                        caregiverId = viewModel.caregiverId
                        channelId = viewModel.channelId
                        roomName = viewModel.roomName
                        urlIcon = viewModel.urlIcon
                        patientName = viewModel.patientName
                    }

                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }

            etInputChat.addTextChangedListener(afterTextChanged = { text ->
                preferences.putPreference(
                    "key_${viewModel.caregiverId}_${viewModel.channelId}",
                    text.toString()
                )
            })

            ivBtnSend.setOnClickListener {
                if (etInputChat.text.toString().isNotEmpty()) {
                    viewModel.sendChat(etInputChat.text.toString())
                    lastMessagefromInput = etInputChat.text.toString()
                    etInputChat.setText("")
                    binding.rvChatCaregiver.smoothScrollToPosition(0)
                }
            }

            addAttach.setOnClickListener {
                CaregiverAttachmentDialogFragment().show(
                    childFragmentManager,
                    "CaregiverAttachmentDialogFragment"
                )
            }

            ivMic.setOnLongClickListener {
                if (superCheckPermission()) {
//                    Toast.makeText(requireContext(), "Recording Start", Toast.LENGTH_SHORT).show()
                    recordMode(true)
                    startTimer()
                    recorder.startRecord()
                }
                true
            }


            ivMic.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        if (superCheckPermission()) {
                            stopTimer()
                            recordMode(false)
                            recorder.stopRecording()
                        }
                    }
                }

                false // Return false to allow the touch event to continue
            }

        }

        childFragmentManager.setFragmentResultListener(
            CaregiverAttachmentDialogFragment.KEY_CONFIRM_UPLOAD, this
        ) { _, bundle ->

            var file: File?
            val resCamera = bundle.getSerializable(CaregiverAttachmentDialogFragment.KEY_CAMERA)
            val resGallery = bundle.getString(CaregiverAttachmentDialogFragment.KEY_GALLERY)
            val resGalleryPhotos =
                bundle.getStringArrayList(CaregiverAttachmentDialogFragment.KEY_GALLERY)
            val recordVideoPath = bundle.getString(CaregiverAttachmentDialogFragment.KEY_VIDEO)

            if (recordVideoPath != null) {
                uploadFilesHandler(listOf(File(recordVideoPath)), isVideo = true)
            } else if (resCamera != null) {
                File(resCamera.toString()).also { file = it }
                val bitmap = BitmapFactory.decodeFile(file?.path)
                BitmapUtils.getFileFromBitmap(bitmap, requireContext()).also {
                    uploadFilesHandler(listOfNotNull(it))
                }

            } else if (resGallery.orEmpty().isNotEmpty()) {
                val fileUri: Uri = Uri.parse(resGallery)
                BitmapUtils.uriToFile(fileUri, requireContext()).also {
                    uploadFilesHandler(listOf(it))
                }
            } else if (resGalleryPhotos.orEmpty().isNotEmpty()) {
//                Toast.makeText(requireContext(), "YIha", Toast.LENGTH_SHORT).show()
                resGalleryPhotos.orEmpty().forEach { it ->
                    val fileUri: Uri = Uri.parse(it)
                    if (isVideoUri(fileUri)) {
                        getRealPathFromURI(requireContext(), fileUri)?.let { path ->
                            viewModel.uploadFiles(listOf(File(path)), isVideo = true)
                        }
                    } else {
                        BitmapUtils.uriToFile(fileUri, requireContext()).also {
                            viewModel.uploadFiles(listOf(it))
                        }
                    }
                }
            }

            //show photo from gallery using glide


        }


        if (preferences.isHistoryChatRoom) {
            binding.llChatRoomClosed.visible()
            binding.clInputText.gone()
        } else {
            binding.llChatRoomClosed.gone()
            binding.clInputText.visible()
        }
    }

    var pathsFileWanttoUpload = Pair(false, listOf(""))
    private fun uploadFilesHandler(files: List<File>, isVideo: Boolean = false) {
        pathsFileWanttoUpload = Pair(isVideo, files.map { it.path })
        viewModel.uploadFiles(files)
    }

    private fun isVideoUri(uri: Uri): Boolean {
        val mimeType = requireContext().contentResolver.getType(uri)
        return mimeType?.startsWith("video") == true
    }

    private fun recordMode(isRecordMode: Boolean) {
        binding.run {
            if (isRecordMode) {
                groupSendChat.invisible()
                tvTimerRecord.visible()
            } else {
                tvTimerRecord.invisible()
                groupSendChat.visible()
            }
        }
    }

//    private fun onMessageListLoadedBaseRv(data: List<CaregiverChatRoomUi>) {
//        if (viewModel.currentPage == 1) {
//            adapterChatRoom.initialize(data)
//        } else if (adapterChatRoom.isNotEmpty() && !viewModel.isLastPage && viewModel.currentPage != 1) {
//            adapterChatRoom.addAll(data)
//        }
//    }


    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var seconds = 0
    private var isTimerRunning = false
    private fun startTimer() {
        isTimerRunning = true
        handler.postDelayed(runnable, 1000)
    }

    private fun stopTimer() {
        isTimerRunning = false
        handler.removeCallbacks(runnable)
        seconds = 0
    }

    private fun updateTimerText() {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        binding.tvTimerRecord.text = String.format("%02d:%02d", minutes, remainingSeconds)
        seconds++
    }


    private val RECORD_AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
    private val WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val REQUEST_PERMISSION_CODE = 123

    private fun superCheckPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!hasManageExternalStoragePermission()) {
                openPermissionAllAccessFile()
            } else {
                if (checkPermissionRecord()) {
                    return true
                } else {
                    requestPermissions()
                }
            }
        } else {
            if (checkPermissions()) {
                return true
            } else {
                requestPermissions()
            }
        }
        return false
    }

    private fun hasManageExternalStoragePermission(): Boolean {
        var result = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                result = true
            } else {
                if (Environment.isExternalStorageLegacy()) {
                    result = true
                }
            }
        }
        return result
    }

    private fun openPermissionAllAccessFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:${binding.root.context.packageName}")
                    )
                )
            } catch (e: Exception) {
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    )
                )
            }
        }
    }

    private fun checkPermissions(): Boolean {

        val recordAudioPermission =
            ContextCompat.checkSelfPermission(requireContext(), RECORD_AUDIO_PERMISSION)
        val writeExternalStoragePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            WRITE_EXTERNAL_STORAGE_PERMISSION
        )

        return recordAudioPermission == PackageManager.PERMISSION_GRANTED && writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissionRecord(): Boolean {

        val recordAudioPermission =
            ContextCompat.checkSelfPermission(requireContext(), RECORD_AUDIO_PERMISSION)

        return recordAudioPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(RECORD_AUDIO_PERMISSION, WRITE_EXTERNAL_STORAGE_PERMISSION),
            REQUEST_PERMISSION_CODE
        )
    }

    private fun createAlertDialog(
        title: String,
        description: String,
        action: (() -> Unit),
        actionCancel: (() -> Unit)
    ) {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(title)
        builder.setMessage(description)

        builder.setPositiveButton("OK") { dialog, _ ->
            action.invoke()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            actionCancel.invoke()
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


    private fun deleteFile(path: String) {
        val file = File(path)

        if (file.exists()) {
            val delete = file.delete()
            if (delete) {
//                Toast.makeText(requireContext(), "Delete record successfully", Toast.LENGTH_SHORT)
//                    .show()
            }
        }
    }

    private fun viewDetailImage(imageDetail: String) {
        val bundle = Bundle()
        bundle.putString(
            ImageDetailFragment.DATA,
            imageDetail
        )
        val viewDialogFragment = ImageDetailFragment()
        viewDialogFragment.arguments = bundle
        val mFragmentManager = parentFragmentManager
        viewDialogFragment.show(
            mFragmentManager,
            ImageDetailFragment::class.java.simpleName
        )
    }

    override fun onResume() {
        super.onResume()
//        if (viewModel.currentPage > 1) {
//            viewModel.emitGetMessage {
//                binding.run {
//                    lottieLoadingChatRoom.visible()
//                    rvChatCaregiver.gone()
//                }
//            }
//        }
        viewModel.listenNewMessageList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetCurrentPage()
        viewModel.isLastPage = false
        adapterChatRoom.clear()
    }

    override fun onAudioReady(audioUri: String?, file: File?) {
        createAlertDialog(
            title = "Send Voice Note",
            description = "Would you like to send this ${binding.tvTimerRecord.text} voice note?",
            action = {
                file?.let {
                    viewModel.uploadFiles(listOf(it), true)
                }
                binding.rvChatCaregiver.smoothScrollToPosition(0)
                dataSource.invalidateAll()
            },
            actionCancel = {
                deleteFile(audioUri.orEmpty())
            }
        )
    }

    override fun onRecordFailed(errorMessage: String?) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onReadyForRecord() {

    }


}
