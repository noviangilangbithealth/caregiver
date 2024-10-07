package com.siloamhospitals.siloamcaregiver.ui.patientlist.rmo

import android.view.View
import com.afollestad.recyclical.ViewHolder
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textview.MaterialTextView
import com.siloamhospitals.siloamcaregiver.R

class AddRmoViewHolder(itemView: View) : ViewHolder(itemView) {

    val cbRmo = itemView.findViewById<MaterialCheckBox>(R.id.cb_rmo)

}