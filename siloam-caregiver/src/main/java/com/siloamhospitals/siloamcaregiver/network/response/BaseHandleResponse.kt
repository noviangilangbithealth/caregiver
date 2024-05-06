package com.siloamhospitals.siloamcaregiver.network.response

sealed class BaseHandleResponse<T>(
    val data: T? = null,
    val message: String? = null
) {
    class SUCCESS<T> (data: T) : BaseHandleResponse<T>(data)
    class ERROR<T> (message: String, data: T? = null) : BaseHandleResponse<T> (data, message)
    class LOADING<T> : BaseHandleResponse<T>()
}