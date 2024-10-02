package com.siloamhospitals.siloamcaregiver

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.siloamhospitals.siloamcaregiver.network.AttachmentCaregiver
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.network.response.*
import com.siloamhospitals.siloamcaregiver.network.response.groupinfo.GroupInfoAdmissionHistoryResponse
import com.siloamhospitals.siloamcaregiver.network.response.groupinfo.GroupInfoResponse
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.groupdetail.GroupDetailViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import retrofit2.Response

@ExperimentalCoroutinesApi
class GroupDetailViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private val dispatchers = TestCoroutineDispatcher()

    private lateinit var repository: Repository
    private lateinit var preferences: AppPreferences
    private lateinit var viewModel: GroupDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatchers)
        repository = mockk(relaxed = true)
        preferences = mockk(relaxed = true)

        every { preferences.userId } returns 2000000886
        viewModel = GroupDetailViewModel(repository, preferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when getGroupInfo Success`() = runBlockingTest {
        val caregiverId = "caregiverId"
        val response = Response.success(GroupInfoResponse())

        coEvery { repository.getGroupInfo(caregiverId) } returns response

        viewModel.getGroupInfo(caregiverId)

        assertTrue(viewModel.groupInfo.value is BaseHandleResponse.SUCCESS)
    }

    @Test
    fun `when getGroupInfo Error`() = runBlockingTest {
        val caregiverId = "caregiverId"
        val errorMessage = "Error retrieving group info"

        val response = Response.error<GroupInfoResponse>(
            400, okhttp3.ResponseBody.create(null, errorMessage)
        )

        coEvery { repository.getGroupInfo(caregiverId) } returns response

        viewModel.getGroupInfo(caregiverId)

        assertTrue(viewModel.groupInfo.value is BaseHandleResponse.ERROR)
    }

    @Test
    fun `when getAdmissionHistory Success`() = runBlockingTest {
        val response = Response.success(GroupInfoAdmissionHistoryResponse())

        coEvery { repository.getAdmissionHistory(any(), any()) } returns response

        viewModel.getAdmissionHistory()

        assertTrue(viewModel.admissionHistory.value is BaseHandleResponse.SUCCESS)
    }

    @Test
    fun `when getAdmissionHistory Error`() = runBlockingTest {
        val errorMessage = "Error retrieving admission history"
        val response = Response.error<GroupInfoAdmissionHistoryResponse>(
            400, okhttp3.ResponseBody.create(null, errorMessage)
        )

        coEvery { repository.getAdmissionHistory(any(), any()) } returns response

        viewModel.getAdmissionHistory()

        assertTrue(viewModel.admissionHistory.value is BaseHandleResponse.ERROR)
    }

    @Test
    fun `when getEmrIpdWebView Success`() = runBlockingTest {
        val caregiverId = "caregiverId"
        val response = Response.success(EmrIpdWebViewResponse())

        coEvery { repository.getEmrIpdWebView(caregiverId) } returns response

        viewModel.getEmrIpdWebView(caregiverId)

        assertTrue(viewModel.emrIpdWebView.value is BaseHandleResponse.SUCCESS)
    }

    @Test
    fun `when getEmrIpdWebView Error`() = runBlockingTest {
        val caregiverId = "caregiverId"
        val errorMessage = "Error retrieving EMR IPD WebView"
        val response = Response.error<EmrIpdWebViewResponse>(
            400, okhttp3.ResponseBody.create(null, errorMessage)
        )

        coEvery { repository.getEmrIpdWebView(caregiverId) } returns response

        viewModel.getEmrIpdWebView(caregiverId)

        assertTrue(viewModel.emrIpdWebView.value is BaseHandleResponse.ERROR)
    }

    @Test
    fun `when sendChat Success`() = runBlockingTest {
        val message = "Test message"
        val response = BaseHandleResponse.SUCCESS(CaregiverChatData())

        coEvery {
            repository.sendChatCaregiver(
                sentID = any(),
                caregiverID = any(),
                channelID = any(),
                senderID = any(),
                sentAt = any(),
                message = any(),
                type = any(),
                attachment = any()
            )
        } returns flowOf(BaseHandleResponse.LOADING(), response)

        viewModel.sendChat(message = message)

        assertTrue(viewModel.sendMessage.value is BaseHandleResponse.SUCCESS)
    }

    @Test
    fun `when sendChat Error`() = runBlockingTest {
        val message = "Test message"
        val response = BaseHandleResponse.ERROR<CaregiverChatData>("Error")

        coEvery {
            repository.sendChatCaregiver(
                sentID = any(),
                caregiverID = any(),
                channelID = any(),
                senderID = any(),
                sentAt = any(),
                message = any(),
                type = any(),
                attachment = any()
            )
        } returns flowOf(BaseHandleResponse.LOADING(), response)

        viewModel.sendChat(message = message)

        assertTrue(viewModel.sendMessage.value is BaseHandleResponse.ERROR)
    }
}
