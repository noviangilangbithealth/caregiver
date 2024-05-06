package com.siloamhospitals.siloamcaregiver.ui.patientlist

import android.view.View
import android.widget.ImageView
import com.afollestad.recyclical.ViewHolder
import com.siloamhospitals.siloamcaregiver.R

class TagNotificationViewHolder(itemView: View) : ViewHolder(itemView) {
    val ivTag = itemView.findViewById<ImageView>(R.id.iv_tag_chat)
}
