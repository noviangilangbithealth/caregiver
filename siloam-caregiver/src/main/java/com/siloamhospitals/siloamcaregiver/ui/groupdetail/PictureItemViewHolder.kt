package com.siloamhospitals.siloamcaregiver.ui.groupdetail

import android.view.View
import android.widget.ImageView
import com.afollestad.recyclical.ViewHolder
import com.google.android.material.textview.MaterialTextView
import com.siloamhospitals.siloamcaregiver.R

class PictureItemViewHolder(itemView: View) : ViewHolder(itemView) {
    val ivPictureItem = itemView.findViewById<ImageView>(R.id.iv_picture_item)
    val ivPlayButton = itemView.findViewById<ImageView>(R.id.iv_play_button)
}
