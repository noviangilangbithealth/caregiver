package com.siloamhospitals.siloamcaregiver.ui.groupdetail

import android.view.View
import android.widget.ImageView
import com.afollestad.recyclical.ViewHolder
import com.google.android.material.textview.MaterialTextView
import com.siloamhospitals.siloamcaregiver.R

class ParticipanViewHolder(itemView: View) : ViewHolder(itemView) {
    val ivParticipan = itemView.findViewById<ImageView>(R.id.iv_partisipan)
    val tvPartcipanName = itemView.findViewById<MaterialTextView>(R.id.tv_partisipan_name)
    val tvRole = itemView.findViewById<MaterialTextView>(R.id.tv_role)

}
