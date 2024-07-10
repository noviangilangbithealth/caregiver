package com.siloamhospitals.siloamcaregiver.ui.patientlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.siloamhospitals.siloamcaregiver.network.Repository
import com.siloamhospitals.siloamcaregiver.network.response.BaseHandleResponse
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverListData
import com.siloamhospitals.siloamcaregiver.network.response.CaregiverPatientListData
import com.siloamhospitals.siloamcaregiver.network.response.HospitalFilter
import com.siloamhospitals.siloamcaregiver.network.response.PatientListNotificationData
import com.siloamhospitals.siloamcaregiver.network.response.UserShowResponse
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import retrofit2.Response

@ExperimentalCoroutinesApi
class CaregiverPatientListViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private val dispatchers = TestCoroutineDispatcher()

    private val preferencesMock = mockk<AppPreferences> {
        every { wardId } returns 123445
        every { organizationId } returns 2
        every { userId } returns 200037847
        every { role } returns 1
    }

    private val repositoryMock = mockk<Repository>()
    private lateinit var viewModel: CaregiverPatientListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatchers)
        viewModel = CaregiverPatientListViewModel(repositoryMock, preferencesMock)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        dispatchers.cleanupTestCoroutines()
    }

    @Test
    fun `When fetching user summary succeeds`() = dispatchers.runBlockingTest {
        coEvery { repositoryMock.getUserShow(any()) } returns Response.success(UserShowResponse())

        viewModel.getUserShow()

        assert(viewModel.userShow.value != null)
        assert(viewModel.userShow.value is BaseHandleResponse.SUCCESS)
    }


    @Test
    fun `When listenCaregiverList succeeds`() = dispatchers.runBlockingTest {
        val mockCaregiverListData = CaregiverListData(/* initialize with test data */)
        val slot = slot<(CaregiverListData, String) -> Unit>()

        coEvery { repositoryMock.listenCaregiverList(capture(slot)) } answers {
            slot.captured.invoke(mockCaregiverListData, "")
        }

        val caregiverListObserver: Observer<CaregiverListData> = mockk(relaxed = true)
        val errorObserver: Observer<String> = mockk(relaxed = true)

        viewModel.caregiverList.observeForever(caregiverListObserver)
        viewModel.error.observeForever(errorObserver)

        viewModel.listenCaregiverList()

        verify { caregiverListObserver.onChanged(mockCaregiverListData) }
        verify(exactly = 0) { errorObserver.onChanged(any()) }

        viewModel.caregiverList.removeObserver(caregiverListObserver)
        viewModel.error.removeObserver(errorObserver)
    }

    @Test
    fun `When listenCaregiverList fails`() = dispatchers.runBlockingTest {
        val errorMessage = "An error occurred"
        val slot = slot<(CaregiverListData, String) -> Unit>()

        coEvery { repositoryMock.listenCaregiverList(capture(slot)) } answers {
            slot.captured.invoke(CaregiverListData(), errorMessage)
        }

        val caregiverListObserver: Observer<CaregiverListData> = mockk(relaxed = true)
        val errorObserver: Observer<String> = mockk(relaxed = true)

        viewModel.caregiverList.observeForever(caregiverListObserver)
        viewModel.error.observeForever(errorObserver)

        viewModel.listenCaregiverList()

        verify(exactly = 0) { caregiverListObserver.onChanged(any()) }
        verify { errorObserver.onChanged(errorMessage) }

        viewModel.caregiverList.removeObserver(caregiverListObserver)
        viewModel.error.removeObserver(errorObserver)
    }

    @Test
    fun `When listenNewCaregiver succeeds`() = dispatchers.runBlockingTest {
        val mockNewCaregiverData = CaregiverPatientListData(/* initialize with test data */)
        val slot = slot<(CaregiverPatientListData, String) -> Unit>()

        coEvery { repositoryMock.listenNewCaregiver(capture(slot)) } answers {
            slot.captured.invoke(mockNewCaregiverData, "")
        }

        val newCaregiverObserver: Observer<CaregiverPatientListData> = mockk(relaxed = true)
        val errorObserver: Observer<String> = mockk(relaxed = true)

        viewModel.newCaregiver.observeForever(newCaregiverObserver)
        viewModel.error.observeForever(errorObserver)

        viewModel.listenNewCaregiver()

        verify { newCaregiverObserver.onChanged(mockNewCaregiverData) }
        verify(exactly = 0) { errorObserver.onChanged(any()) }

        viewModel.newCaregiver.removeObserver(newCaregiverObserver)
        viewModel.error.removeObserver(errorObserver)
    }

    @Test
    fun `When listenNewCaregiver fails`() = dispatchers.runBlockingTest {
        val errorMessage = "An error occurred"
        val slot = slot<(CaregiverPatientListData, String) -> Unit>()

        coEvery { repositoryMock.listenNewCaregiver(capture(slot)) } answers {
            slot.captured.invoke(CaregiverPatientListData(), errorMessage)
        }

        val newCaregiverObserver: Observer<CaregiverPatientListData> = mockk(relaxed = true)
        val errorObserver: Observer<String> = mockk(relaxed = true)

        viewModel.newCaregiver.observeForever(newCaregiverObserver)
        viewModel.error.observeForever(errorObserver)

        viewModel.listenNewCaregiver()

        verify(exactly = 0) { newCaregiverObserver.onChanged(any()) }
        verify { errorObserver.onChanged(errorMessage) }

        viewModel.newCaregiver.removeObserver(newCaregiverObserver)
        viewModel.error.removeObserver(errorObserver)
    }

    @Test
    fun `When listenDeleteCaregiver succeeds`() = dispatchers.runBlockingTest {
        val mockDeleteCaregiverData = CaregiverPatientListData(/* initialize with test data */)
        val slot = slot<(CaregiverPatientListData, String) -> Unit>()

        coEvery { repositoryMock.listenDeleteCaregiver(capture(slot)) } answers {
            slot.captured.invoke(mockDeleteCaregiverData, "")
        }

        val deleteCaregiverObserver: Observer<CaregiverPatientListData> = mockk(relaxed = true)
        val errorObserver: Observer<String> = mockk(relaxed = true)

        viewModel.deleteCaregiver.observeForever(deleteCaregiverObserver)
        viewModel.error.observeForever(errorObserver)

        viewModel.listenDeleteCaregiver()

        verify { deleteCaregiverObserver.onChanged(mockDeleteCaregiverData) }
        verify(exactly = 0) { errorObserver.onChanged(any()) }

        viewModel.deleteCaregiver.removeObserver(deleteCaregiverObserver)
        viewModel.error.removeObserver(errorObserver)
    }

    @Test
    fun `When listenDeleteCaregiver fails`() = dispatchers.runBlockingTest {
        val errorMessage = "An error occurred"
        val slot = slot<(CaregiverPatientListData, String) -> Unit>()

        coEvery { repositoryMock.listenDeleteCaregiver(capture(slot)) } answers {
            slot.captured.invoke(CaregiverPatientListData(), errorMessage)
        }

        val deleteCaregiverObserver: Observer<CaregiverPatientListData> = mockk(relaxed = true)
        val errorObserver: Observer<String> = mockk(relaxed = true)

        viewModel.deleteCaregiver.observeForever(deleteCaregiverObserver)
        viewModel.error.observeForever(errorObserver)

        viewModel.listenDeleteCaregiver()

        verify(exactly = 0) { deleteCaregiverObserver.onChanged(any()) }
        verify { errorObserver.onChanged(errorMessage) }

        viewModel.deleteCaregiver.removeObserver(deleteCaregiverObserver)
        viewModel.error.removeObserver(errorObserver)
    }

    @Test
    fun `listenBadgeNotif success updates badgeNotif LiveData`() = dispatchers.runBlockingTest {
        val mockData = PatientListNotificationData(/* initialize with test data */)
        val slot = slot<(PatientListNotificationData, String) -> Unit>()

        coEvery { repositoryMock.listenNotifNewMessage(capture(slot)) } answers {
            slot.captured.invoke(mockData, "")
        }

        val observer: Observer<PatientListNotificationData> = mockk(relaxed = true)
        viewModel.badgeNotif.observeForever(observer)

        viewModel.listenBadgeNotif()

        verify { observer.onChanged(mockData) }
        viewModel.badgeNotif.removeObserver(observer)
    }

    @Test
    fun `listenBadgeNotif failure updates error LiveData`() = dispatchers.runBlockingTest {
        val errorMessage = "An error occurred"
        val slot = slot<(PatientListNotificationData, String) -> Unit>()

        coEvery { repositoryMock.listenNotifNewMessage(capture(slot)) } answers {
            slot.captured.invoke(PatientListNotificationData(), errorMessage)
        }

        val observer: Observer<String> = mockk(relaxed = true)
        viewModel.error.observeForever(observer)

        viewModel.listenBadgeNotif()

        verify { observer.onChanged(errorMessage) }
        viewModel.error.removeObserver(observer)
    }

    @Test
    fun `When listenHospitalWardFilter succeeds`() = dispatchers.runBlockingTest {
        val mockHospitalFilterData = listOf(HospitalFilter(/* initialize with test data */))
        val slot = slot<(List<HospitalFilter>, String) -> Unit>()

        coEvery { repositoryMock.listenHospitalWardFilter(capture(slot)) } answers {
            slot.captured.invoke(mockHospitalFilterData, "")
        }

        val hospitalWardObserver: Observer<List<HospitalFilter>> = mockk(relaxed = true)
        val errorObserver: Observer<String> = mockk(relaxed = true)

        viewModel.hospitalWard.observeForever(hospitalWardObserver)
        viewModel.error.observeForever(errorObserver)

        viewModel.listenHospitalWardFilter()

        verify { hospitalWardObserver.onChanged(mockHospitalFilterData) }
        verify(exactly = 0) { errorObserver.onChanged(any()) }

        viewModel.hospitalWard.removeObserver(hospitalWardObserver)
        viewModel.error.removeObserver(errorObserver)
    }

    @Test
    fun `When listenHospitalWardFilter fails`() = dispatchers.runBlockingTest {
        val errorMessage = "An error occurred"
        val slot = slot<(List<HospitalFilter>, String) -> Unit>()

        coEvery { repositoryMock.listenHospitalWardFilter(capture(slot)) } answers {
            slot.captured.invoke(emptyList(), errorMessage)
        }

        val hospitalWardObserver: Observer<List<HospitalFilter>> = mockk(relaxed = true)
        val errorObserver: Observer<String> = mockk(relaxed = true)

        viewModel.hospitalWard.observeForever(hospitalWardObserver)
        viewModel.error.observeForever(errorObserver)

        viewModel.listenHospitalWardFilter()

        verify(exactly = 0) { hospitalWardObserver.onChanged(any()) }
        verify { errorObserver.onChanged(errorMessage) }

        viewModel.hospitalWard.removeObserver(hospitalWardObserver)
        viewModel.error.removeObserver(errorObserver)
    }
}

