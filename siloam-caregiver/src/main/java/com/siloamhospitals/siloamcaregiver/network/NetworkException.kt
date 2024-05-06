package com.siloamhospitals.siloamcaregiver.network

import android.content.Context
import com.siloamhospitals.siloamcaregiver.R
import java.io.IOException

class NetworkException(private val context: Context) : IOException() {

    override val message: String
        get() = context.getString(R.string.no_internet_connection)

}