package com.siloamhospitals.siloamcaregiver.network

import android.util.Log
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URI
import java.net.URISyntaxException

class SocketIoManager(
    preferences: AppPreferences
) {
    lateinit var socket: Socket
    private val uri = URI.create(URLS)

    init {
        try {
            val options = IO.Options()
            options.forceNew = true
            options.query = "userId=${preferences.userId}"
            options.path = "/caregiver-ws/"
            socket = IO.socket(uri, options)
            socket.connect()
        } catch (e: URISyntaxException) {
            Logger.d(e)
        }
    }

    fun disconnect() {
        socket.disconnect()
    }

    fun isConnected(): Boolean {
        return socket.connected()
    }
    // Add other Socket.IO event handling methods as needed

    // Example method to emit an event
    fun emitEvent(eventName: String, data: Any?) {
        Log.i("SOCKET","------------------------------------SOCKET LOG------------------------------------")
        Log.i("SOCKET","EVENT NAME : $eventName")
        Log.i("SOCKET","REQUEST: $data")
        Log.i("SOCKET","------------------------------------END SOCKET LOG------------------------------------")
       if (data != null) {
           socket.emit(eventName, data)
       }else{
           socket.emit(eventName)
       }
    }

    // Example method to listen for events
    fun onEvent(eventName: String, listener: (Any, String) -> Unit) {
        socket.on(eventName) { args ->
            try {
                if (args[0].toString().isNotEmpty()) {
                    val data = args[0]
                    listener.invoke(data, "")
                    Log.i("SOCKET","------------------------------------SOCKET LOG------------------------------------")
                    Log.i("SOCKET","EVENT NAME : $eventName")
                    Log.i("SOCKET","RESPONSE: $data")
                    Log.i("SOCKET","------------------------------------END SOCKET LOG------------------------------------")
                } else {
                    Logger.d("Empty")
                    Log.i("SOCKET","------------------------------------SOCKET LOG------------------------------------")
                    Log.i("SOCKET","EVENT NAME : $eventName")
                    Log.i("SOCKET","RESPONSE: EMPTY DATA")
                    Log.i("SOCKET","------------------------------------END SOCKET LOG------------------------------------")
                }
            } catch (e: Exception) {
                listener.invoke("", e.toString())
                Log.i("SOCKET","------------------------------------SOCKET LOG------------------------------------")
                Log.i("SOCKET","EVENT NAME : $eventName")
                Log.i("SOCKET","ERROR: $e")
                Log.i("SOCKET","------------------------------------END SOCKET LOG------------------------------------")
            }
        }
    }

}

const val URLS = "wss://mysiloam-api-staging.siloamhospitals.com/"
