package com.siloamhospitals.siloamcaregiver.network.service

import com.siloamhospitals.siloamcaregiver.network.request.PinMessageRequest
import com.siloamhospitals.siloamcaregiver.network.request.SendChatCaregiverRequest
import com.siloamhospitals.siloamcaregiver.network.response.AttachmentCaregiverResponse
import com.siloamhospitals.siloamcaregiver.network.response.BaseDataResponse
import com.siloamhospitals.siloamcaregiver.network.response.EmrIpdWebViewResponse
import com.siloamhospitals.siloamcaregiver.network.response.UserShowResponse
import com.siloamhospitals.siloamcaregiver.network.response.WardResponse
import com.siloamhospitals.siloamcaregiver.network.response.groupinfo.GroupInfoResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @POST(PostSendMessage)
    suspend fun sendMessage(
        @Body sendChatRequestBody: SendChatCaregiverRequest
    ): Response<BaseDataResponse<*>>

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


    companion object {
        const val PathCaregiverId = "caregiver_id"
        const val PathUserId = "doctor_hope_id"
        const val PathHospitalId = "hospital_id"
        const val PathMessageId = "message_id"

        const val PostUpload = "/caregiver/api/v1/messages/upload"
        const val PostSendMessage = "/caregiver/api/v1/messages"
        const val GetGroupInfo = "/caregiver/api/v1/caregivers/detail/{$PathCaregiverId}"
        const val GetUserShow = "/caregiver/api/v1/user/show/{$PathUserId}"
        const val GetWard = "/caregiver/api/v1/ward/{$PathHospitalId}"
        const val GetEmrIpdWebView = "/caregiver/api/v1/caregivers/summary/{$PathCaregiverId}"
        const val PostPinMessage = "/caregiver/api/v1/caregivers/pin"
        const val DeleteMessage = "/caregiver/api/v1/messages/{message_id}"
    }

}
