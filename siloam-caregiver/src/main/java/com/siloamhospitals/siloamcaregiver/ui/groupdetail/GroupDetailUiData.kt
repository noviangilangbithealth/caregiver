package com.siloamhospitals.siloamcaregiver.ui.groupdetail

data class Participan(
    val hopeId: String,
    val name: String,
    val color: String,
    val role: String
)


data class PictureDate(
    val date: String = ""
)

data class PictureItem(
    val url: String = "",
)
