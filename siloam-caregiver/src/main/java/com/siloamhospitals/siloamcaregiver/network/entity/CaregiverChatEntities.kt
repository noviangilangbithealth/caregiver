package com.siloamhospitals.siloamcaregiver.network.entity

import androidx.room.Embedded
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChatAttachment
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChatData
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChatRole
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChatUser

@Entity
data class CaregiverChatEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "sender_id") val senderId: String,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "action_status") val actionStatus: Boolean,
    @ColumnInfo(name = "sent_at")val sentAt: String,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "is_received") val isReceived: Boolean,
    @ColumnInfo(name = "received_at") val receivedAt: String,
    @ColumnInfo(name = "caregiver_id") val caregiverId: String,
    @ColumnInfo(name = "channel_id") val channelId: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
    @ColumnInfo(name = "is_readed") val isReaded: Boolean,
    @ColumnInfo(name = "readed_at") val readedAt: String,
    @Embedded val user: CaregiverChatUserEntity,
    @Embedded val attachment: CaregiverChatAttachmentEntity? = null
)

typealias CaregiverChatEntities = List<CaregiverChatEntity>

fun CaregiverChatData.toEntity() = CaregiverChatEntity(
    id = id.orEmpty(),
    senderId = senderID.orEmpty(),
    message = message.orEmpty(),
    type = type?.toInt() ?: 0,
    actionStatus = actionStatus ?: false,
    sentAt = sentAt.orEmpty(),
    createdAt = createdAt.orEmpty(),
    isReceived = isReceived ?: false,
    receivedAt = receivedAt.toString(),
    caregiverId = caregiverId.orEmpty(),
    channelId = channelId.orEmpty(),
    isActive = isActive ?: false,
    isReaded = isReaded ?: false,
    readedAt = readedAt.toString(),
    user = user?.toEntity() ?: CaregiverChatUserEntity("", "", 0, "", CaregiverChatRoleEntity(0, "", "")),
    attachment = attachment?.firstOrNull()?.toEntity() ?: CaregiverChatAttachmentEntity("", "", "", "")
)

@Entity
data class CaregiverChatUserEntity(
    @ColumnInfo(name = "user_id") val id: String,
    @ColumnInfo(name = "user_name") val name: String,
    @ColumnInfo(name = "role_id") val roleId: Int,
    @ColumnInfo(name = "hope_user_id") val hopeUserId: String,
    @Embedded val role: CaregiverChatRoleEntity
)

fun CaregiverChatUser.toEntity() = CaregiverChatUserEntity(
    id = id.orEmpty(),
    name = name.orEmpty(),
    roleId = roleID?.toInt() ?: 0,
    hopeUserId = hopeUserID.orEmpty(),
    role = role?.toEntity() ?: CaregiverChatRoleEntity(0, "", "")
)

@Entity
data class CaregiverChatRoleEntity(
    @ColumnInfo(name = "role_role_id") val id: Int,
    @ColumnInfo(name = "role_name") val name: String,
    val color:String
)

fun CaregiverChatRole.toEntity() = CaregiverChatRoleEntity(
    id = id?.toInt() ?: 0,
    name = name.orEmpty(),
    color = color.orEmpty()
)

@Entity
data class CaregiverChatAttachmentEntity(
    @ColumnInfo(name = "file_name")val name: String,
    val uri: String,
    @ColumnInfo(name = "uri_ext") val uriExt: String,
    @ColumnInfo(name = "original_name") val originalName: String
)

fun CaregiverChatAttachment.toEntity() = CaregiverChatAttachmentEntity(
    name = name.orEmpty(),
    uri = uri.orEmpty(),
    uriExt = uriExt.orEmpty(),
    originalName = originalName.orEmpty()
)