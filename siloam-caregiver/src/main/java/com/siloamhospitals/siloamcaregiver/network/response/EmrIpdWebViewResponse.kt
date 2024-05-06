package com.siloamhospitals.siloamcaregiver.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EmrIpdWebViewResponse(
    val url: String = ""
)
