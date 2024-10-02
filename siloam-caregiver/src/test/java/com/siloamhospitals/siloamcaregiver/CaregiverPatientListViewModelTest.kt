package com.siloamhospitals.siloamcaregiver

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.siloamhospitals.siloamcaregiver.base.SiloamCaregiver
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.network.response.*
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.patientlist.CaregiverPatientListViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class CaregiverPatientListViewModelTest {

    // Allows testing of LiveData
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private val dispatchers = TestCoroutineDispatcher()

    // Mocked dependencies
    private lateinit var repository: Repository
    private lateinit var preferences: AppPreferences

    // ViewModel instance under test
    private lateinit var viewModel: CaregiverPatientListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatchers)
        repository = mockk(relaxed = true)
        preferences = mockk(relaxed = true)

        // Mock wardId, organizationId, and userId properties in AppPreferences
        every { preferences.wardId } returns 0
        every { preferences.organizationId } returns 0
        every { preferences.userId } returns 2000000886
        every { preferences.role } returns SiloamCaregiver.ROLE_DOCTOR

        viewModel = CaregiverPatientListViewModel(repository, preferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test emitGetCaregiver calls repository with correct params`() = runBlockingTest {
        // Call the method
        viewModel.emitGetCaregiver()

        // Verify the repository was called with the correct params
        verify {
            repository.emitGetCaregiver(
                page = 1,
                keyword = "",
                user = "2000000886", // Mocked doctorId
                organizationId = 0,
                wardId = 0
            )
        }
    }

    @Test
    fun `test listenCaregiverList posts value to LiveData`() = runBlockingTest {
        // Mock repository's callback
        every {
            repository.listenCaregiverList(any())
        } answers {
            val callback = arg<(CaregiverListData, String) -> Unit>(0)
            callback.invoke(CaregiverListData(), "") // Simulate success
        }

        // Call the method
        viewModel.listenCaregiverList()

        // Verify that the LiveData was updated
        assertNotNull(viewModel.caregiverList.value)
    }

    @Test
    fun `test setFirebaseToken calls repository`() = runBlockingTest {
        // Call the method
        viewModel.setFirebaseToken("test_token")

        // Verify the repository was called with the correct token
        verify { repository.setFirebaseToken("test_token") }
    }

    @Test
    fun `test listenNewCaregiver posts value to LiveData`() = runBlockingTest {
        every { repository.listenNewCaregiver(any()) } answers {
            val callback = arg<(CaregiverPatientListData, String) -> Unit>(0)
            callback.invoke(CaregiverPatientListData(), "") // Simulate success
        }

        viewModel.listenNewCaregiver()

        assertNotNull(viewModel.newCaregiver.value)
    }

    @Test
    fun `test listenDeleteCaregiver posts value to LiveData`() = runBlockingTest {
        every { repository.listenDeleteCaregiver(any()) } answers {
            val callback = arg<(CaregiverPatientListData, String) -> Unit>(0)
            callback.invoke(CaregiverPatientListData(), "") // Simulate success
        }

        viewModel.listenDeleteCaregiver()

        assertNotNull(viewModel.deleteCaregiver.value)
    }

    @Test
    fun `test getUserShow success`() = runBlockingTest {
        // Mock the repository response
        val userShowResponse = UserShowResponse()
        coEvery { repository.getUserShow(789L) } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns userShowResponse
        }

        // Call the method
        viewModel.getUserShow()

        // Verify that LiveData was updated
        assertTrue(viewModel.userShow.value is BaseHandleResponse.SUCCESS)
    }

    @Test
    fun `test getWard success`() = runBlockingTest {
        // Mock the repository response
        val wardResponse = WardResponse()
        coEvery { repository.getWard(123L) } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns wardResponse
        }

        // Call the method
        viewModel.getWard()

        // Verify that LiveData was updated
        assertTrue(viewModel.ward.value is BaseHandleResponse.SUCCESS)
    }

    @Test
    fun `test pinMessage success`() = runBlockingTest {
        // Mock the repository response
        val baseDataResponse = BaseDataResponse<Any>(
            status = "success",
            statusCode = 200,
            message = "Message pinned successfully",
            data = Any()
        )
        coEvery {
            repository.pinMessage(any(), any(), any())
        } returns mockk {
            every { isSuccessful } returns true
            every { body() } returns baseDataResponse
        }

        // Call the method
        viewModel.pinnedCaregiverId = "caregiverId"
        viewModel.isPinned = true
        viewModel.pinMessage()

        // Verify LiveData update
        assertTrue(viewModel.pinnedMessage.value is BaseHandleResponse.SUCCESS)
    }

    @Test
    fun `test emitGetBadgeNotif calls repository`() = runBlockingTest {
        // Call the method
        viewModel.emitGetBadgeNotif()

        // Verify that the repository method was called
        verify { repository.emitNotifNewMessage() }
    }

    @Test
    fun `test listenBadgeNotif posts value to LiveData`() = runBlockingTest {
        every { repository.listenNotifNewMessage(any()) } answers {
            val callback = arg<(PatientListNotificationData, String) -> Unit>(0)
            callback.invoke(PatientListNotificationData(), "") // Simulate success
        }

        viewModel.listenBadgeNotif()

        assertNotNull(viewModel.badgeNotif.value)
    }

    @Test
    fun `test listenHospitalWardFilter posts value to LiveData`() = runBlockingTest {
        every { repository.listenHospitalWardFilter(any()) } answers {
            val callback = arg<(List<HospitalFilter>, String) -> Unit>(0)
            callback.invoke(listOf(), "") // Simulate success
        }

        viewModel.listenHospitalWardFilter()

        assertNotNull(viewModel.hospitalWard.value)
    }
}
