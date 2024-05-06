package com.siloamhospitals.siloamcaregiver.ui.roomtype

import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.afollestad.recyclical.ViewHolder
import com.google.android.material.textview.MaterialTextView
import com.siloamhospitals.siloamcaregiver.R

class RoomTypeViewHolder(itemView: View) : ViewHolder(itemView) {

    val layoutItemRoom = itemView.findViewById<ConstraintLayout>(R.id.layout_item_room_type)
    val ivRoomType = itemView.findViewById<ImageView>(R.id.iv_room_type)
    val ivUrgent = itemView.findViewById<ImageView>(R.id.iv_urgent_room_type)
    val tvRoomName = itemView.findViewById<MaterialTextView>(R.id.tv_room_type_name)
    val tvlastMessage = itemView.findViewById<MaterialTextView>(R.id.tv_last_message_room_type)
    val tvBadgeRoomType = itemView.findViewById<MaterialTextView>(R.id.badge_room_type)
    val tvTimeRoom = itemView.findViewById<MaterialTextView>(R.id.tv_time_room_type)

}
