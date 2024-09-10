package com.siloamhospitals.siloamcaregiver.network.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FailedChatEntity(
    @PrimaryKey val sentId : String,
    @ColumnInfo(name = "caregiver_id") val caregiverId: String,
    @ColumnInfo(name = "channel_id") val channelId: String,
    val message : String,
    val localuri: String,
    val isVideo: Boolean,
)
