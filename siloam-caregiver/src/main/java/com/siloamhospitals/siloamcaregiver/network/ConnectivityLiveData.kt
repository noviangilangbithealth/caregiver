package com.siloamhospitals.siloamcaregiver.network

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData

class ConnectivityLiveData(private val context: Context) : LiveData<Boolean>() {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val connectivityCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: android.net.Network) {
            postValue(true)
        }

        override fun onLost(network: android.net.Network) {
            postValue(false)
        }
    }

    override fun onActive() {
        super.onActive()
        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected = networkInfo != null && networkInfo.isConnected
        postValue(isConnected)
        connectivityManager.registerDefaultNetworkCallback(connectivityCallback)
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(connectivityCallback)
    }
}
