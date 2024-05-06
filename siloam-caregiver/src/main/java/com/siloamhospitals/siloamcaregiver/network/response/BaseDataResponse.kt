package com.siloamhospitals.siloamcaregiver.network.response

import com.google.gson.annotations.SerializedName

data class BaseDataResponse<T>(
    @SerializedName("status")
    var status: String?,
    @SerializedName("statusCode")
    var statusCode: Int,
    @SerializedName("message")
    var message: String?,
    @SerializedName("data")
    var data: T?
)