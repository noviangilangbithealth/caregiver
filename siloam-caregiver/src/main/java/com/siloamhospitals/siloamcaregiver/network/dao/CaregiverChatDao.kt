package com.siloamhospitals.siloamcaregiver.network.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.siloamhospitals.siloamcaregiver.network.entity.CaregiverChatEntities
import com.siloamhospitals.siloamcaregiver.network.entity.CaregiverChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CaregiverChatDao {
    @Query("SELECT * FROM CaregiverChatEntity WHERE channel_id = :channelId AND caregiver_id = :caregiverId ORDER BY created_at DESC")
    abstract fun getChatMessages(channelId: String, caregiverId: String): Flow<CaregiverChatEntities>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertChatMessage(message: CaregiverChatEntity): Long

    @Query("SELECT EXISTS(SELECT 1 FROM CaregiverChatEntity WHERE id = :messageId)")
    abstract suspend fun exists(messageId: String): Boolean
}