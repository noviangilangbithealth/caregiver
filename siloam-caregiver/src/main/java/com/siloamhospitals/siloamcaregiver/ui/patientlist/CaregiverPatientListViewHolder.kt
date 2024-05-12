package com.siloamhospitals.siloamcaregiver.ui.patientlist

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.recyclical.ViewHolder
import com.google.android.material.textview.MaterialTextView
import com.siloamhospitals.siloamcaregiver.R

class CaregiverPatientListViewHolder(itemView: View) : ViewHolder(itemView) {

    val layoutCard = itemView.findViewById<LinearLayout>(R.id.layout_item_patient_list_dashboard)

    val tvPatientName =
        itemView.findViewById<MaterialTextView>(R.id.tv_patient_name_caregiver_patient_list)

    val tvDate = itemView.findViewById<MaterialTextView>(R.id.tv_time_caregiver_patient_list)

    val tvLastMessage = itemView.findViewById<MaterialTextView>(R.id.tv_last_message_room_type)

    val tvInfoPatient =
        itemView.findViewById<MaterialTextView>(R.id.tv_patient_info_caregiver_dashboard)

    val ivGender = itemView.findViewById<ImageView>(R.id.iv_gender_caregiver_patient_list)

    val rvTag = itemView.findViewById<RecyclerView>(R.id.rv_tag_caregiver)

}
