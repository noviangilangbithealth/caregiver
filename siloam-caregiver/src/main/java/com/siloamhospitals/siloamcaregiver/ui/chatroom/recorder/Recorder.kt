package com.siloamhospitals.siloamcaregiver.ui.chatroom.recorder

import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.media.MediaRecorder
import android.media.RingtoneManager
import android.os.Environment
import android.widget.Toast
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.orhanobut.logger.Logger
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class Recorder(audioRecordListener: AudioRecordListener?, private var context: Context?) {

    private var recorder: MediaRecorder? = null
    private var audioRecordListener: AudioRecordListener? = null
    private var fileName: String? = null
    private var localPath = ""
    private var recordFile: File? = null

    private var isRecording = false

    fun setFileName(fileName: String?) {
        this.fileName = fileName
    }

    fun setContext(context: Context) {
        this.context = context
    }

    fun startRecord() {
        if (context == null) {
            throw IllegalStateException("Context cannot be null")
        }
        recorder = MediaRecorder()
        recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        recorder?.setAudioSamplingRate(12000) // Sample rate in Hz
        recorder?.setAudioChannels(1) // Number of audio channels (mono)
        recorder?.setAudioEncodingBitRate(128000) //
        recordFile = getOutputMediaFile()
        localPath = recordFile?.absolutePath.orEmpty()
        recorder?.setOutputFile(localPath)

        try {
            recorder?.prepare()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            reflectError(e.toString())
            return
        }
        recorder?.start()
        isRecording = true
        audioRecordListener?.onReadyForRecord()
    }

    fun reset() {
        if (recorder != null) {
            recorder?.release()
            recorder = null
            isRecording = false
        }
    }

    fun stopRecording() {
        try {
            recorder?.stop()
            recorder?.release()
            recorder = null
            reflectRecord(localPath)
        } catch (e: Exception) {
            e.printStackTrace()
            reflectError(e.toString())
        }
    }

    private fun reflectError(error: String?) {
        audioRecordListener?.onRecordFailed(error)
        isRecording = false
    }

    private fun reflectRecord(uri: String?) {
        convertMp4ToM4a(uri.orEmpty(), fileName + ".m4a") {
            deleteFileMp4()
            audioRecordListener?.onAudioReady(fileName + ".m4a", File(fileName + ".m4a"))
            isRecording = false
        }

    }

    //delete file
    fun deleteFileMp4() {
        if (recordFile != null) {
            recordFile?.delete()
        }
    }

    init {
        this.audioRecordListener = audioRecordListener
    }

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
        fileName = "${mediaStorageDir.path}${File.separator}AUDIO_$timeStamp"
        val file = File(fileName + ".mp4")
//        Toast.makeText(requireContext(), file.extension, Toast.LENGTH_SHORT).show()
        return file
    }

    fun convertMp4ToM4a(inputFilePath: String, outputFilePath: String, action: (()-> Unit)) {
        val cmd = arrayOf(
            "-i", inputFilePath,
            "-vn",
            "-acodec", "copy",
            outputFilePath
        )

        val result = FFmpeg.execute(cmd)
        if (result == Config.RETURN_CODE_SUCCESS) {
           action.invoke()
        } else {

        }
    }








}