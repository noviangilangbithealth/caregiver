package com.siloamhospitals.siloamcaregiver.ui.roomtype

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverRoomTypeData
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class RoomTypeViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private val dispatchers = TestCoroutineDispatcher()

    private val preferencesMock = mockk<AppPreferences> {
        every { userId } returns 200037847
    }

    private val repositoryMock = mockk<Repository>()
    private lateinit var viewModel: RoomTypeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatchers)
        viewModel = RoomTypeViewModel(repositoryMock, preferencesMock)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        dispatchers.cleanupTestCoroutines()
    }

    @Test
    fun `When listen room list success`() = dispatchers.runBlockingTest {
        val data = listOf(CaregiverRoomTypeData())
        every { repositoryMock.listenRoomList("200037847", any()) } answers {
            val callback = secondArg<(List<CaregiverRoomTypeData>, String) -> Unit>()
            callback(data, "")
        }
        viewModel.listenRoom()

        assertEquals(data, viewModel.roomTypeList.value)

    }

    @Test
    fun `When listen room list failed`() = dispatchers.runBlockingTest {
        val error = "Error"
        every { repositoryMock.listenRoomList("200037847", any()) } answers {
            val callback = secondArg<(List<CaregiverRoomTypeData>, String) -> Unit>()
            callback(emptyList(), error)
        }
        viewModel.listenRoom()

        assertEquals(error, viewModel.error.value)
    }

    @Test
    fun `When listen new room success`() = dispatchers.runBlockingTest {
        val data = CaregiverRoomTypeData()
        every { repositoryMock.listenNewRoom(any()) } answers {
            val callback = firstArg<(CaregiverRoomTypeData, String) -> Unit>()
            callback(data, "")
        }
        viewModel.listenNewRoom()

        assertEquals(data, viewModel.newRoom.value)
    }

    @Test
    fun `When listen new room failed`() = dispatchers.runBlockingTest {
        val error = "Error"
        every { repositoryMock.listenNewRoom(any()) } answers {
            val callback = firstArg<(CaregiverRoomTypeData, String) -> Unit>()
            callback(CaregiverRoomTypeData(), error)
        }
        viewModel.listenNewRoom()

        assertEquals(error, viewModel.error.value)
    }


}
