package com.siloamhospitals.siloamcaregiver.network.service

import com.siloamhospitals.siloamcaregiver.network.request.PinChatRequest
import com.siloamhospitals.siloamcaregiver.network.request.PinMessageRequest
import com.siloamhospitals.siloamcaregiver.network.request.SendChatCaregiverRequest
import com.siloamhospitals.siloamcaregiver.network.request.rmo.SubmitRmoRequest
import com.siloamhospitals.siloamcaregiver.network.response.AttachmentCaregiverResponse
import com.siloamhospitals.siloamcaregiver.network.response.BaseDataResponse
import com.siloamhospitals.siloamcaregiver.network.response.EmrIpdWebViewResponse
import com.siloamhospitals.siloamcaregiver.network.response.UserShowResponse
import com.siloamhospitals.siloamcaregiver.network.response.WardResponse
import com.siloamhospitals.siloamcaregiver.network.response.groupinfo.GroupInfoAdmissionHistoryResponse
import com.siloamhospitals.siloamcaregiver.network.response.groupinfo.GroupInfoResponse
import com.siloamhospitals.siloamcaregiver.network.response.rmo.RmoMasterDataResponse
import com.siloamhospitals.siloamcaregiver.network.response.rmo.RmoParticipantsResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST(PostSendMessage)
    suspend fun sendMessage(
        @Body sendChatRequestBody: SendChatCaregiverRequest
    ): Response<BaseDataResponse<String>>

    @DELETE(DeleteMessage)
    suspend fun deleteMessage(
        @Path(value = PathMessageId) messageId: String
    ): Response<BaseDataResponse<*>>

    @Multipart
    @POST(PostUpload)
    suspend fun postUpload(
        @Part attachment: List<MultipartBody.Part>
    ): Response<AttachmentCaregiverResponse>

    @GET(GetGroupInfo)
    suspend fun getGroupInfo(
        @Path(value = PathCaregiverId) caregiverId: String
    ): Response<GroupInfoResponse>

    @GET(GetUserShow)
    suspend fun getUserShow(
        @Path(value = PathUserId) userId: Long
    ): Response<UserShowResponse>


    @GET(GetWard)
    suspend fun getWard(
        @Path(value = PathHospitalId) hospitalId: Long
    ): Response<WardResponse>

    @GET(GetEmrIpdWebView)
    suspend fun getEmrIpdWebView(
        @Path(value = PathCaregiverId) caregiverId: String
    ): Response<EmrIpdWebViewResponse>


//    @POST("sendmessage")
//    suspend fun sendMessage(
//        @Body sendMessageRequestBody: SendMessageRequestBody
//    ) : Response<BaseDataResponse<*>>
//
//    @POST("createchannel")
//    suspend fun createChannel(
//        @Body createChannelRequestBody: CreateChannelRequestBody
//    ) : Response<BaseDataResponse<*>>
//
//    @GET("getlistchannel/{organizationId}")
//    suspend fun getListChannel(
//        @Path("organizationId") organizationId: Int
//    ) : Response<BaseDataResponse<MutableList<Channel>>>
//
//    @GET("gethistorychatbychannelname/{channelName}/{userId}")
//    suspend fun getHistoryChatByChannelName(
//        @Path("channelName") channelName: String,
//        @Path("userId") userId: String
//    ) : Response<BaseDataResponse<MutableList<Message>>>

    @POST(PostPinMessage)
    suspend fun postPinMessage(
        @Body pinMessageRequest: PinMessageRequest
    ): Response<BaseDataResponse<*>>

    @GET(GetAdmissionHistory)
    suspend fun getAdmissionHistory(
        @Path(value = PathHospitalId) hospitalId: String,
        @Path(value = PathPatientId) patientId: String
    ): Response<GroupInfoAdmissionHistoryResponse>

    @GET(GetListMessage)
    suspend fun getListMessage(
        @Path(value = PathUserId) userId: String,
        @Path(value = PathCaregiverId) caregiverId: String,
        @Path(value = PathChannelId) channelId: String,
        @Query("unread") unread: String
    ): Response<BaseDataResponse<String>>

    @PUT(PutPinMessage)
    suspend fun putPinMessage(
        @Body pinChatRequest: PinChatRequest
    ): Response<BaseDataResponse<*>>

    @POST(PostParticipants)
    suspend fun postParticipantsRmo(
        @Body pinChatRequest: SubmitRmoRequest
    ): Response<BaseDataResponse<*>>

    @GET(GetMasterRmO)
    suspend fun getMasterRmo(
        @Path(value = PathHospitalId) hospitalId: String,
        @Path(value = PathWardId) wardId: String,
        @Path(value = PathUserId) userId: String
    ): Response<RmoMasterDataResponse>

    @GET(GetRmoParticipantsList)
    suspend fun getMasterRmoParticipants(
        @Path(value = PathHospitalId) hospitalId: String,
        @Path(value = PathWardId) wardId: String,
        @Path(value = PathUserId) userId: String
    ): Response<RmoParticipantsResponse>

    companion object {
        const val PathCaregiverId = "caregiver_id"
        const val PathUserId = "doctor_hope_id"
        const val PathHospitalId = "hospital_id"
        const val PathPatientId = "patient_id"
        const val PathMessageId = "message_id"
        const val PathChannelId = "channel_id"
        const val PathWardId = "ward_id"
        const val PathUnread = "unread"

        const val PostUpload = "/caregiver/api/v1/messages/upload"
        const val PostSendMessage = "/caregiver/api/v1/messages"
        const val GetGroupInfo = "/caregiver/api/v1/caregivers/detail/{$PathCaregiverId}"
        const val GetUserShow = "/caregiver/api/v1/user/show/{$PathUserId}"
        const val GetWard = "/caregiver/api/v1/ward/{$PathHospitalId}"
        const val GetEmrIpdWebView = "/caregiver/api/v1/caregivers/summary/{$PathCaregiverId}"
        const val PostPinMessage = "/caregiver/api/v1/caregivers/pin"
        const val GetAdmissionHistory =
            "/caregiver/api/v1/caregiver/list/{$PathHospitalId}/{$PathPatientId}"
        const val DeleteMessage = "/caregiver/api/v1/messages/{message_id}"
        const val GetListMessage =
            "/caregiver/api/v1/message/user/{$PathUserId}/caregiver/{$PathCaregiverId}/channel/{$PathChannelId}"
        const val PutPinMessage = "/caregiver/api/v1/messages/pin"
        const val PostParticipants = "/caregiver/api/v1/participant"
        const val GetMasterRmO = "/caregiver/api/v1/rmo/organization/{$PathHospitalId}/ward/{$PathWardId}/user/{$PathUserId}"
        const val GetRmoParticipantsList =
            "/caregiver/api/v1/roster/organization/{$PathHospitalId}/ward/{$PathWardId}/user/{$PathUserId}"
    }

}
