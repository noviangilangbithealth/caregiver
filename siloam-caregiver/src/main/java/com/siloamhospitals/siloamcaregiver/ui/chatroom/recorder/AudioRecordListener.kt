package com.siloamhospitals.siloamcaregiver.ui.chatroom.recorder

import java.io.File

interface AudioRecordListener {
    fun onAudioReady(audioUri: String?, file: File?)
    fun onRecordFailed(errorMessage: String?)
    fun onReadyForRecord()
}