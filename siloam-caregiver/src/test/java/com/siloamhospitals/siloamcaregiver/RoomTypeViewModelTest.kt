package com.siloamhospitals.siloamcaregiver

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverRoomTypeData
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.roomtype.RoomTypeViewModel
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import androidx.lifecycle.Observer
import com.siloamhospitals.siloamcaregiver.network.ConnectivityLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After

@ExperimentalCoroutinesApi
class RoomTypeViewModelTest {

    // Allows testing of LiveData
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private val dispatchers = TestCoroutineDispatcher()

    // Mocked dependencies
    private lateinit var repository: Repository
    private lateinit var preferences: AppPreferences

    // ViewModel instance under test
    private lateinit var viewModel: RoomTypeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatchers)
        repository = mockk(relaxed = true)
        preferences = mockk(relaxed = true)

        // Mock userId property in AppPreferences
        every { preferences.userId } returns 2000000886

        viewModel = RoomTypeViewModel(repository, preferences)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test emitRoom calls repository with correct params`() = runBlockingTest {
        // Set the caregiverId
        viewModel.caregiverId = mockk()

        // Call the method
        viewModel.emitRoom()

        // Verify the repository was called with the correct params
        verify { repository.emitGetRoom(any(), any()) }
    }

    @Test
    fun `test listenRoom posts value to roomTypeList on success`() = runBlockingTest {
        // Mock repository's callback
        val roomList = listOf(CaregiverRoomTypeData())
        every {
            repository.listenRoomList(any(), any())
        } answers {
            val callback = arg<(List<CaregiverRoomTypeData>, String) -> Unit>(1)
            callback.invoke(roomList, "") // Simulate success
        }

        // Observe LiveData
        val observer: Observer<List<CaregiverRoomTypeData>> = mockk(relaxed = true)
        viewModel.roomTypeList.observeForever(observer)

        // Call the method
        viewModel.listenRoom()

        // Verify that LiveData was updated
        verify { observer.onChanged(roomList) }
        assertEquals(viewModel.roomTypeList.value, roomList)
    }

    @Test
    fun `test listenRoom posts error to LiveData on failure`() = runBlockingTest {
        // Mock repository's callback
        val errorMessage = "An error occurred"
        every {
            repository.listenRoomList(any(), any())
        } answers {
            val callback = arg<(List<CaregiverRoomTypeData>, String) -> Unit>(1)
            callback.invoke(emptyList(), errorMessage) // Simulate failure
        }

        // Observe LiveData
        val observer: Observer<String> = mockk(relaxed = true)
        viewModel.error.observeForever(observer)

        // Call the method
        viewModel.listenRoom()

        // Verify that error LiveData was updated
        verify { observer.onChanged(errorMessage) }
        assertEquals(viewModel.error.value, errorMessage)
    }

    @Test
    fun `test listenNewRoom posts value to newRoom on success`() = runBlockingTest {
        // Mock repository's callback
        val newRoom = CaregiverRoomTypeData()
        every {
            repository.listenNewRoom(any())
        } answers {
            val callback = arg<(CaregiverRoomTypeData, String) -> Unit>(0)
            callback.invoke(newRoom, "") // Simulate success
        }

        // Observe LiveData
        val observer: Observer<CaregiverRoomTypeData> = mockk(relaxed = true)
        viewModel.newRoom.observeForever(observer)

        // Call the method
        viewModel.listenNewRoom()

        // Verify that LiveData was updated
        verify { observer.onChanged(newRoom) }
        assertEquals(viewModel.newRoom.value, newRoom)
    }

    @Test
    fun `test listenNewRoom posts error to LiveData on failure`() = runBlockingTest {
        // Mock repository's callback
        val errorMessage = "An error occurred"
        every {
            repository.listenNewRoom(any())
        } answers {
            val callback = arg<(CaregiverRoomTypeData, String) -> Unit>(0)
            callback.invoke(CaregiverRoomTypeData(), errorMessage) // Simulate failure
        }

        // Observe LiveData
        val observer: Observer<String> = mockk(relaxed = true)
        viewModel.error.observeForever(observer)

        // Call the method
        viewModel.listenNewRoom()

        // Verify that error LiveData was updated
        verify { observer.onChanged(errorMessage) }
        assertEquals(viewModel.error.value, errorMessage)
    }

    @Test
    fun `test ConnectivityLiveData returns correct value`() = runBlockingTest {
        // Mock the connectivity status
        val connectivityLiveData = mockk<ConnectivityLiveData>(relaxed = true)
        every { preferences.context } returns mockk()
        every { ConnectivityLiveData(preferences.context) } returns connectivityLiveData

        // Initialize the ViewModel
        val viewModel = RoomTypeViewModel(repository, preferences)

        // Assert the isConnected LiveData is being set properly
        assertEquals(viewModel.isConnected, connectivityLiveData)
    }
}
