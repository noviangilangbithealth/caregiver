package com.siloamhospitals.siloamcaregiver.ui.chatroom

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaRecorder
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.recyclical.datasource.DataSource
import com.afollestad.recyclical.datasource.emptyDataSourceTyped
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.FragmentChatRoomCaregiverBinding
import com.siloamhospitals.siloamcaregiver.ext.bitmap.BitmapUtils
import com.siloamhospitals.siloamcaregiver.ext.view.gone
import com.siloamhospitals.siloamcaregiver.ext.view.invisible
import com.siloamhospitals.siloamcaregiver.ext.view.visible
import com.siloamhospitals.siloamcaregiver.network.response.BaseHandleResponse
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.CaregiverChatRoomUi
import com.siloamhospitals.siloamcaregiver.ui.LinearLoadMoreListener
import com.siloamhospitals.siloamcaregiver.ui.chatroom.recorder.AudioRecordListener
import com.siloamhospitals.siloamcaregiver.ui.decoration.SpaceItemDecoration
import com.siloamhospitals.siloamcaregiver.ui.groupdetail.GroupDetailActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ChatRoomCaregiverFragment : Fragment(), AudioRecordListener {

    private var _binding: FragmentChatRoomCaregiverBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatRoomCaregiverViewModel by activityViewModels()

    private var dataSource: DataSource<CaregiverChatRoomUi> = emptyDataSourceTyped()

    private lateinit var adapterChatRoom: ChatRoomCaregiverAdapter

    private var onProcess = false

    private lateinit var preferences: AppPreferences

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

        setupPreference()
        setupAdapter()
        callSocket()
        setupInit()
        setupListener()
        setupObserver()
        setupView()
        setupCheckRecent()
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
        adapterChatRoom = ChatRoomCaregiverAdapter { url, isWeb ->
            if (isWeb) {
                viewModel.urlEmrIpd = url
                findNavController().navigate(R.id.action_chatRoomCaregiverFragment2_to_chatRoomWebViewFragment)
            } else {
                viewDetailImage(url)
            }
        }

        binding.rvChatCaregiver.run {
            adapter = adapterChatRoom
            addItemDecoration(SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.medium)))
            addOnScrollListener(object : LinearLoadMoreListener(layoutManager) {
                override fun isLoading(): Boolean {
                    return onProcess
                }

                override fun loadMoreItems() {
                    viewModel.emitGetMessage(loadMore = true)
                    onProcess = true
                }

            })
        }
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
            etInputChat.setText(preferences.findPreference("key_${viewModel.caregiverId}_${viewModel.channelId}", ""))
            tvTitleChat.text = viewModel.roomName
            tvPatientName.text = viewModel.patientName
            Glide.with(requireContext()).load(viewModel.urlIcon).into(ivRoomChat)
        }
    }

    private fun callSocket() {
        viewModel.emitGetMessage {
            binding.run {
                lottieLoadingChatRoom.visible()
                rvChatCaregiver.gone()
            }
        }
        viewModel.listenMessageList()
        viewModel.listenNewMessageList()
        viewModel.setReadMessage()
    }

    private fun setupObserver() {
        observeSendChat()
        observeMessageList()
        observeNewMessage()
        observeUploadPhotos()
        observeConnection()
    }

    private fun observeConnection() {
        viewModel.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if (!isConnected) {
                Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.listenNewMessageList()
            }
        }

    }

    private fun observeNewMessage() {
        viewModel.run {
            newMessage.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { data ->
                    if (data.channelId == viewModel.channelId && data.caregiverId == viewModel.caregiverId) {
                        val result = adapterChatRoom.getList().find { it.id == data.id }
                        if (result == null) {
                            adapterChatRoom.add(0, data.generateNewChatUI())
                            binding.rvChatCaregiver.smoothScrollToPosition(0)
                            setReadMessage()
                        }
                    }
                }
            }
        }
    }

    private fun observeUploadPhotos() {
        viewModel.uploadFiles.observe(viewLifecycleOwner) { response ->
            when (response) {
                is BaseHandleResponse.ERROR -> {
                    Logger.d(response.message)
                }

                is BaseHandleResponse.LOADING -> {}
                is BaseHandleResponse.SUCCESS -> {
                    Logger.d(response.data)
                    viewModel.sendChat(attachments = response.data?.data.orEmpty())
                }

            }
        }
    }


    private fun observeMessageList() {
        viewModel.run {
            messageList.observe(viewLifecycleOwner) {

                binding.run {
                    lottieLoadingChatRoom.gone()
                    rvChatCaregiver.visible()
                }

                it.getContentIfNotHandled()?.let { data ->
                    onProcess = false
                    if (data.data.orEmpty().isNotEmpty()) {
                        onMessageListLoadedBaseRv(data.data.orEmpty().generateChatListUI())
                    } else {
                        resetCurrentPage()
                        isLastPage = true
                    }
                }
            }

            errorMessageList.observe(viewLifecycleOwner) { error ->
                error.getContentIfNotHandled()?.let {
                    Logger.d(it)
                }
            }
        }
    }

    private fun observeSendChat() {
        viewModel.sendMessage.observe(viewLifecycleOwner) { response ->
            when (response) {
                is BaseHandleResponse.ERROR -> {
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }

                is BaseHandleResponse.LOADING -> {}
                is BaseHandleResponse.SUCCESS -> {}
            }

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListener() {
        binding.run {

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
                preferences.putPreference("key_${viewModel.caregiverId}_${viewModel.channelId}", text.toString())
            })

            ivBtnSend.setOnClickListener {
                if (etInputChat.text.toString().isNotEmpty()) {
                    viewModel.sendChat(etInputChat.text.toString())
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

            toolbarChatRoom.setNavigationOnClickListener {
                findNavController().navigateUp()
                requireActivity().finish()
            }

            ivMic.setOnLongClickListener {
                if (superCheckPermission()) {
//                    Toast.makeText(requireContext(), "Recording Start", Toast.LENGTH_SHORT).show()
                    recordMode(true)
                    startTimer()
                    startRecording()
                }
                true
            }


            ivMic.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP && superCheckPermission()) {
                    // Handle the finger lifted action (optional)
//                    Toast.makeText(requireContext(), "Recording Stop", Toast.LENGTH_SHORT).show()
                    stopTimer()
                    recordMode(false)
                    stopRecording()
                    createAlertDialog(
                        title = "Send Voice Note",
                        description = "Would you like to send this ${binding.tvTimerRecord.text} voice note?",
                        action = {
                            adapterChatRoom.add(
                                0, CaregiverChatRoomUi(
                                    isVoiceNote = true,
                                    isSelfSender = true,
                                    url = outputFile.orEmpty()
                                )
                            )

                            outputFileAudioRecord?.let {
                                viewModel.uploadFiles(listOf(it), true)
                            }
                            outputFile = null
                            binding.rvChatCaregiver.smoothScrollToPosition(0)
                            dataSource.invalidateAll()
                        },
                        actionCancel = {
                            deleteFile(outputFile.orEmpty())
                            outputFile = null
                        }
                    )

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
            if (resCamera != null) {
                val npwp = File(resCamera.toString()).also { file = it }
                val bitmap = BitmapFactory.decodeFile(file?.path)
                BitmapUtils.getFileFromBitmap(bitmap, requireContext()).also {
                    viewModel.uploadFiles(listOfNotNull(it))
                }

            } else if (resGallery.orEmpty().isNotEmpty()) {
                val fileUri: Uri = Uri.parse(resGallery)
                BitmapUtils.uriToFile(fileUri, requireContext()).also {
                    viewModel.uploadFiles(listOf(it))
                }
            } else if (resGalleryPhotos.orEmpty().isNotEmpty()) {
                resGalleryPhotos.orEmpty().forEach {
                    val fileUri: Uri = Uri.parse(it)
                    BitmapUtils.uriToFile(fileUri, requireContext()).also {
                        viewModel.uploadFiles(listOf(it))
                    }
                }
            }

        }
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

    private fun onMessageListLoadedBaseRv(data: List<CaregiverChatRoomUi>) {
        if (viewModel.currentPage == 1) {
            adapterChatRoom.initialize(data)
        } else if (adapterChatRoom.isNotEmpty() && !viewModel.isLastPage && viewModel.currentPage != 1) {
            adapterChatRoom.addAll(data)
        }
    }


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


    private lateinit var mediaRecorder: MediaRecorder

    private var isRecording = false
    private var outputFile: String? = null

    private fun getOutputMediaFile(): File? {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            "Caregiver"
        )

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Logger.d("Caregiver", "failed to create directory")
                return null
            }
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File("${mediaStorageDir.path}${File.separator}AUDIO_$timeStamp.aac")
//        Toast.makeText(requireContext(), file.extension, Toast.LENGTH_SHORT).show()
        return file
    }

    private var outputFileAudioRecord: File? = null

    private fun startRecording() {
        try {
            outputFileAudioRecord = getOutputMediaFile()
            outputFile = outputFileAudioRecord?.absolutePath

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(requireContext())
            } else {
                MediaRecorder()
            }

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder.setOutputFile(outputFileAudioRecord)

            mediaRecorder.prepare()
            mediaRecorder.start()

            isRecording = true

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder.stop()
            mediaRecorder.release()

            isRecording = false

            val renamedFile = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath}/Caregiver/recording.mp3")
            outputFileAudioRecord?.renameTo(renamedFile)

        } catch (e: Exception) {
            e.printStackTrace()
        }
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
        viewModel.emitGetMessage {
            binding.run {
                lottieLoadingChatRoom.visible()
                rvChatCaregiver.gone()
            }
        }
        viewModel.listenNewMessageList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetCurrentPage()
        viewModel.isLastPage = false
        adapterChatRoom.clear()
    }

    override fun onAudioReady(audioUri: String?) {

    }

    override fun onRecordFailed(errorMessage: String?) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onReadyForRecord() {

    }


}
