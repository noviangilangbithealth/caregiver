package com.siloamhospitals.siloamcaregiver.ui.groupdetail

import android.view.View
import com.afollestad.recyclical.ViewHolder
import com.google.android.material.textview.MaterialTextView
import com.siloamhospitals.siloamcaregiver.R
import de.hdodenhof.circleimageview.CircleImageView

class ParticipanViewHolder(itemView: View) : ViewHolder(itemView) {
    val ivParticipan = itemView.findViewById<CircleImageView>(R.id.iv_partisipan)
    val tvPartcipanName = itemView.findViewById<MaterialTextView>(R.id.tv_partisipan_name)
    val tvRole = itemView.findViewById<MaterialTextView>(R.id.tv_role)

}
