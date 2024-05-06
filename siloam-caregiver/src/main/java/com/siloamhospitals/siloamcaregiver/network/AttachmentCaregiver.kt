package com.siloamhospitals.siloamcaregiver.network

import com.google.gson.annotations.SerializedName

data class AttachmentCaregiver(
    val name: String = "",
    val uri: String = "",

    @SerializedName("uri_ext")
    val uriEXT: String = "",

    @SerializedName("original_name")
    val originalName: String = ""
)
