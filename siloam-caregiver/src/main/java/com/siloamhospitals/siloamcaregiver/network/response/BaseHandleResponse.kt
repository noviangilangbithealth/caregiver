package com.siloamhospitals.siloamcaregiver.network.response

sealed class BaseHandleResponse<T>(
    val data: T? = null,
    val message: String? = null,
    val sentId: String? = null
) {
    class SUCCESS<T> (data: T, sentId: String? = null) : BaseHandleResponse<T>(data, sentId = sentId)
    class ERROR<T> (message: String, data: T? = null, sentId: String? = null) : BaseHandleResponse<T> (data, message, sentId)
    class LOADING<T>(data: T? = null, sentId: String? = null) : BaseHandleResponse<T>(data, sentId = sentId)
}