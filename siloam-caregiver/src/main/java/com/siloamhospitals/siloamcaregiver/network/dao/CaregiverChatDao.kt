package com.siloamhospitals.siloamcaregiver.network.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.siloamhospitals.siloamcaregiver.network.entity.CaregiverChatEntities
import com.siloamhospitals.siloamcaregiver.network.entity.CaregiverChatEntity
import com.siloamhospitals.siloamcaregiver.network.entity.FailedChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CaregiverChatDao {
    @Query("SELECT * FROM CaregiverChatEntity WHERE channel_id = :channelId AND caregiver_id = :caregiverId ORDER BY created_at DESC")
    abstract fun getChatMessages(channelId: String, caregiverId: String): Flow<CaregiverChatEntities>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertChatMessage(message: CaregiverChatEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertFailedMessage(message: FailedChatEntity)

    @Query("DELETE FROM FailedChatEntity WHERE sentId = :sentid")
    abstract suspend fun deleteFailedMessage(sentid: String)

    @Query("SELECT * FROM FailedChatEntity WHERE channel_id = :channelId AND caregiver_id = :caregiverId")
    abstract fun getFailedMessages(channelId: String, caregiverId: String): Flow<List<FailedChatEntity>>

    @Query("SELECT COUNT(*) FROM FailedChatEntity WHERE channel_id = :channelId AND caregiver_id = :caregiverId")
    abstract suspend fun getFailedMessagesSize(channelId: String, caregiverId: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM CaregiverChatEntity WHERE id = :messageId)")
    abstract suspend fun exists(messageId: String): Boolean
}