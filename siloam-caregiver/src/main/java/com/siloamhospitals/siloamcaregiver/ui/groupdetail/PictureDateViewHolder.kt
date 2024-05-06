package com.siloamhospitals.siloamcaregiver.ui.groupdetail

import android.view.View
import com.afollestad.recyclical.ViewHolder
import com.google.android.material.textview.MaterialTextView
import com.siloamhospitals.siloamcaregiver.R

class PictureDateViewHolder(itemView: View) : ViewHolder(itemView) {
    val tvPictureDate = itemView.findViewById<MaterialTextView>(R.id.tv_picture_date)
}
