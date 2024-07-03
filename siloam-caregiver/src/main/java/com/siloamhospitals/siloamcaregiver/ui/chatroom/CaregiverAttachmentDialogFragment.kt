package com.siloamhospitals.siloamcaregiver.ui.chatroom

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.orhanobut.logger.Logger
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.SheetCaregiverChatBinding
import com.siloamhospitals.siloamcaregiver.ext.bitmap.BitmapUtils
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.io.File

class CaregiverAttachmentDialogFragment : BottomSheetDialogFragment() {

    private var _binding: SheetCaregiverChatBinding? = null
    private val binding get() = _binding!!

    override fun getTheme() = R.style.BaseTheme_BottomSheet

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    private val launcherIntentPhotoCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val file = File(currentPhotoPath).also { getFile = it }
            val bundleCam = Bundle()
            bundleCam.putSerializable(KEY_CAMERA, file)
            sendParent(bundleCam)
        }
    }

    private val launcherIntentVideoCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val videoUri: Uri? = result.data?.data
            // Use videoUri here
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val clipData = result.data?.clipData

            if (clipData != null) {
                // Multiple images were selected
                val selectedImagesCount = clipData.itemCount
                val maxImages = 3
                val imagesToHandle = if (selectedImagesCount > maxImages) maxImages else selectedImagesCount
                val photos = arrayListOf<String>()
                for (i in 0 until imagesToHandle) {
                    val uri = clipData.getItemAt(i).uri
                    photos.add(uri.toString())
                }
                val bundleGal = Bundle()
                bundleGal.putStringArrayList(KEY_GALLERY, photos)
                sendParent(bundleGal)
            } else if (result.data?.data != null) {
                // Single image was selected
                val selectedImg: Uri = result.data?.data as Uri
                val bundleGal = Bundle()
                bundleGal.putString(KEY_GALLERY, selectedImg.toString())
                sendParent(bundleGal)
            }

        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
//                Timber.tag("Permission: ").i("Granted")
            } else {
                Logger.i("Permission: ", "Denied")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SheetCaregiverChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {

            tbFromCamera.setOnClickListener {
//                viewModel.insertDoctorLog(
//                    screen_name = "image_picker_doctor_feedback",
//                    view = "button_camera",
//                    message = "Button Camera Clicked"
//                )

                checkPermissionCamera {
                    startIntentPhotoCamera()
                }

            }

            tbVideoFromCamera.setOnClickListener {
                checkPermissionCamera {
                    startIntentVideoCamera()
                }
            }

            tbFromGallery.setOnClickListener {
//                viewModel.insertDoctorLog(
//                    screen_name = "image_picker_doctor_feedback",
//                    view = "button_gallery",
//                    message = "Button Gallery Clicked"
//                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!hasManageExternalStoragePermission()) {
                        openPermissionAllAccessFile()
                    } else {
                        startIntentGallery()
                    }
                } else {
                    if (checkPermission()) {
                        startIntentGallery()
                    } else {
                        requestPermission()
                    }
                }
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startIntentVideoCamera() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (intent.resolveActivity(binding.root.context.packageManager) != null) {
            launcherIntentVideoCamera.launch(intent)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startIntentPhotoCamera() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(binding.root.context.packageManager)

        BitmapUtils.createTempFile(requireActivity()).also {
            val photoUri = FileProvider.getUriForFile(
                requireActivity(),
                binding.root.context.packageName + ".provider",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentPhotoCamera.launch(intent)
        }

    }

    private fun checkPermissionCamera(action: () -> Unit) {
        when {
            ContextCompat.checkSelfPermission(
                binding.root.context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                action.invoke()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            ) -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun checkPermission(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )

        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(activity, getString(R.string.permission_to_gallery), Toast.LENGTH_SHORT)
                .show()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
        }
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

    private fun startIntentGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/* video/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        val chooser = Intent.createChooser(intent, "Choose a Picture or Video")
        launcherIntentGallery.launch(chooser)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendParent(bundle: Bundle) {
        setFragmentResult(
            KEY_CONFIRM_UPLOAD, bundle
        )
        dismiss()
    }

    companion object {
        const val KEY_CONFIRM_UPLOAD = "res_confirm_upload"
        const val KEY_CAMERA = "key_camera"
        const val KEY_GALLERY = "key_gallery"
    }


}