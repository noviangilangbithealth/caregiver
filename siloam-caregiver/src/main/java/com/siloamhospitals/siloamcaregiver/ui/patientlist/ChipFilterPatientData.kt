package com.siloamhospitals.siloamcaregiver.ui.patientlist

data class ChipFilterPatientData(
    val isHospital: Boolean = true,
    val name: String,
    val isUrgent: Boolean = false,
    val showBadge: Boolean = false,
    val isSelected: Boolean,
    val hospitalId:Long,
    val wardId:Long
)

data class ChipHospitalData(
    val hospitalId: Long,
    val hospitalName: String,
    val hospitalAlias: String,
    val isUrgent: Boolean = false,
    val showBadge: Boolean = false,
    val isSelected: Boolean = false,
    val wards: List<ChipWardData> = emptyList()
)

data class ChipWardData(
    val wardId: Long,
    val wardName: String,
    val isSelected: Boolean = false,
    val isUrgent: Boolean = false,
    val showBadge: Boolean = false
)
