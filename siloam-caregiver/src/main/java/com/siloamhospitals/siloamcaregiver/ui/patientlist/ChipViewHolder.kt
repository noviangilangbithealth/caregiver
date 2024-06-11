package com.siloamhospitals.siloamcaregiver.ui.patientlist

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.afollestad.recyclical.ViewHolder
import com.google.android.material.textview.MaterialTextView
import com.siloamhospitals.siloamcaregiver.R

class ChipViewHolder(itemView: View) : ViewHolder(itemView) {
    val chip = itemView.findViewById<LinearLayout>(R.id.layout_chip_filter_patient)
    val tvText = itemView.findViewById<MaterialTextView>(R.id.chip_text)
    val ivChip = itemView.findViewById<ImageView>(R.id.iv_chip)
}
