package com.siloamhospitals.siloamcaregiver.ui.groupdetail

import android.view.View
import com.afollestad.recyclical.ViewHolder
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.siloamhospitals.siloamcaregiver.R

class AdmissionHistoryViewHolder(itemView: View) : ViewHolder(itemView) {
    var tvHospitalMrAdmission =
        itemView.findViewById<MaterialTextView>(R.id.tv_hospital_mr_no_admission_no)
    var tvDoctorName = itemView.findViewById<MaterialTextView>(R.id.tv_doctor_name)
    var tvDateTime = itemView.findViewById<MaterialTextView>(R.id.tv_date_time)
    var btnChatHistory = itemView.findViewById<MaterialButton>(R.id.btn_history_chat)
    var btnMedicalRecord = itemView.findViewById<MaterialButton>(R.id.btn_medical_record)

}
