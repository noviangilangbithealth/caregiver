package com.siloamhospitals.siloamcaregiver

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.siloamhospitals.siloamcaregiver.network.AttachmentCaregiver
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.network.response.AttachmentCaregiverResponse
import com.siloamhospitals.siloamcaregiver.network.response.BaseDataResponse
import com.siloamhospitals.siloamcaregiver.network.response.BaseHandleResponse
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChatData
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverChatListData
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.Event
import com.siloamhospitals.siloamcaregiver.ui.chatroom.ChatRoomCaregiverViewModel
import com.siloamhospitals.siloamcaregiver.ui.chatroom.TripleChats
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
import java.io.File

@ExperimentalCoroutinesApi
class ChatRoomCaregiverViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val dispatchers = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatchers)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val repository: Repository = mockk()
    private val preferences: AppPreferences = mockk()

    private fun createViewModel(): ChatRoomCaregiverViewModel {
        return ChatRoomCaregiverViewModel(repository, preferences)
    }

    @Test
    fun `when pinChat Success`() = runBlockingTest {
        val messageId = "messageId"
        val isPinned = true

        coEvery { repository.pinChatMessage(messageId, isPinned) } returns Response.success(
            BaseDataResponse(
                status = "success",
                statusCode = 200,
                message = "Message pinned successfully",
                data = Any()
            )
        )

        val viewModel = createViewModel()
        viewModel.pinChat(messageId, isPinned)

        assertTrue(viewModel.pinChat.value is BaseHandleResponse.SUCCESS)
    }

    @Test
    fun `when emitGetPinChat Success`() = runBlockingTest {
        val viewModel = createViewModel()

        coEvery { repository.emitGetPinMessage(any(), any()) } just Runs

        viewModel.emitGetPinChat()

        coVerify { repository.emitGetPinMessage(any(), any()) }
    }

    @Test
    fun `when listenPinChat Success`() = runBlockingTest {
        val data = listOf(CaregiverChatData())
        val error = ""

        coEvery { repository.listenPinChat(any()) } answers {
            firstArg<(List<CaregiverChatData>, String) -> Unit>().invoke(data, error)
        }

        val viewModel = createViewModel()
        viewModel.listenPinChat()

        assertTrue(viewModel.pinChatMessage.value is BaseHandleResponse.SUCCESS)
    }

    @Test
    fun `when getCaregiverChat Success`() = runBlockingTest {
        val messages = TripleChats(emptyList(), emptyList(), emptyList())
        coEvery { repository.getChatMessagesFlow(any(), any()) } returns flowOf(BaseHandleResponse.SUCCESS(
            messages
        ))

        val viewModel = createViewModel()
        viewModel.getCaregiverChat()

        assertTrue(viewModel.chatMessages.value?.getContentIfNotHandled() == messages)
    }

    @Test
    fun `when insertFailedMessage Success`() = runBlockingTest {
        val uuid = "uuid"
        val message = "message"
        val localUri = "localUri"
        val isVideo = false

        coEvery { repository.insertFailedMessage(any()) } just Runs

        val viewModel = createViewModel()
        viewModel.insertFailedMessage(uuid, message, localUri, isVideo)

        coVerify { repository.insertFailedMessage(any()) }
    }

    @Test
    fun `when deleteFailedMessage Success`() = runBlockingTest {
        val sentId = "sentId"

        coEvery { repository.deleteFailedMessage(sentId) } just Runs

        val viewModel = createViewModel()
        viewModel.deleteFailedMessage(sentId)

        coVerify { repository.deleteFailedMessage(sentId) }
    }

    @Test
    fun `when getFailedMessagesSize Success`() = runBlockingTest {
        val size = 5
        coEvery { repository.getFailedMessagesSize(any(), any()) } returns size

        val viewModel = createViewModel()
        viewModel.getFailedMessagesSize()

        assertTrue(viewModel.failedMessageSize.value == size)
    }

    @Test
    fun `when deleteMessage Success`() = runBlockingTest {
        val messageId = "messageId"

        coEvery { repository.deleteMessage(messageId) } returns Response.success(
            BaseDataResponse(
                status = "success",
                statusCode = 200,
                message = "Message deleted successfully",
                data = Any()
            )
        )

        val viewModel = createViewModel()
        viewModel.deleteMessage(messageId)

        assertTrue(viewModel.deleteMessage.value is BaseHandleResponse.SUCCESS)
    }

    @Test
    fun `when uploadFiles Success`() = runBlockingTest {
        val files = listOf<File>(mockk())
        val response = AttachmentCaregiverResponse()

        coEvery { repository.postUploadAttachment(any(), any(), any()) } returns Response.success(response)

        val viewModel = createViewModel()
        viewModel.uploadFiles(files)

        assertTrue(viewModel.uploadFiles.value is BaseHandleResponse.SUCCESS)
    }

    @Test
    fun `when emitGetMessage Success`() = runBlockingTest {
        val viewModel = createViewModel()

        coEvery { repository.emitGetMessage(any(), any(), any(), any(), any()) } just Runs

        viewModel.emitGetMessage()

        coVerify { repository.emitGetMessage(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `when setReadMessage Success`() = runBlockingTest {
        val viewModel = createViewModel()

        coEvery { repository.setReadMessage(any(), any()) } just Runs

        viewModel.setReadMessage()

        coVerify { repository.setReadMessage(any(), any()) }
    }

    @Test
    fun `when resetCurrentPage Success`() {
        val viewModel = createViewModel()
        viewModel.currentPage = 5

        viewModel.resetCurrentPage()

        assertTrue(viewModel.currentPage == 1)
    }

    @Test
    fun `when sendChat Success`() = runBlockingTest {
        val message = "message"
        val type = 1
        val attachments = listOf<AttachmentCaregiver>()
        val sentId = "sentId"

        coEvery { repository.sendChatCaregiver(any(), any(), any(), any(), any(), any(), any(), any()) } returns flowOf(
            BaseHandleResponse.SUCCESS(CaregiverChatData())
        )

        val viewModel = createViewModel()
        viewModel.sendChat(message, type, attachments, sentId)

        assertTrue(viewModel.sendMessage.value is BaseHandleResponse.SUCCESS)
    }

    @Test
    fun `when listenNewMessageList Success`() = runBlockingTest {
        val data = CaregiverChatData()
        val error = ""

        coEvery { repository.listenNewMessageList(any()) } answers {
            firstArg<(CaregiverChatData, String) -> Unit>().invoke(data, error)
        }

        val viewModel = createViewModel()
        viewModel.listenNewMessageList()

        assertTrue(viewModel.newMessage.value?.getContentIfNotHandled() == data)
    }

    @Test
    fun `when insertChatMessage Success`() = runBlockingTest {
        val message = CaregiverChatData()

        coEvery { repository.insertChatMessage(any()) } just Runs

        val viewModel = createViewModel()
        viewModel.insertChatMessage(message)

        coVerify { repository.insertChatMessage(any()) }
    }

    @Test
    fun `when insertChatMessageWithoutCheck Success`() = runBlockingTest {
        val message = CaregiverChatData()

        coEvery { repository.insertChatMessageWithoutExist(any()) } just Runs

        val viewModel = createViewModel()
        viewModel.insertChatMessageWithoutCheck(message)

        coVerify { repository.insertChatMessageWithoutExist(any()) }
    }
}

