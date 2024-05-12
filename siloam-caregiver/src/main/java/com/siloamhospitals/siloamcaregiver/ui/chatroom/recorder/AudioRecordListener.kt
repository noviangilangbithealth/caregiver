package com.siloamhospitals.siloamcaregiver.ui.chatroom.recorder

interface AudioRecordListener {
    fun onAudioReady(audioUri: String?)
    fun onRecordFailed(errorMessage: String?)
    fun onReadyForRecord()
}