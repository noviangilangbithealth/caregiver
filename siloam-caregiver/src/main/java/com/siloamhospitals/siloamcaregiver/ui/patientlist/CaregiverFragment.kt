package com.siloamhospitals.siloamcaregiver.ui.patientlist

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.recyclical.datasource.dataSourceOf
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.itemdefinition.onChildViewClick
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.FragmentCaregiverBinding
import com.siloamhospitals.siloamcaregiver.ext.datetime.TODAY
import com.siloamhospitals.siloamcaregiver.ext.datetime.YESTERDAY
import com.siloamhospitals.siloamcaregiver.ext.datetime.toLocalDateTime
import com.siloamhospitals.siloamcaregiver.ext.datetime.withFormat
import com.siloamhospitals.siloamcaregiver.ext.view.gone
import com.siloamhospitals.siloamcaregiver.ext.view.setSingleOnClickListener
import com.siloamhospitals.siloamcaregiver.ext.view.visible
import com.siloamhospitals.siloamcaregiver.network.response.BaseHandleResponse
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.CaregiverActivity
import com.siloamhospitals.siloamcaregiver.ui.ListCaregiverPatient
import com.siloamhospitals.siloamcaregiver.ui.NotificationIcon
import com.siloamhospitals.siloamcaregiver.ui.roomtype.RoomTypeCaregiverActivity


class CaregiverFragment : Fragment() {

    private var _binding: FragmentCaregiverBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CaregiverPatientListViewModel by activityViewModels()

    private val preferences by lazy {
        AppPreferences(requireContext())
    }

    override fun onResume() {
        super.onResume()
        if (preferences.isFromRecent) {
            viewModel.run {
                selectedHospital = preferences.recentOrgId
                orgCode = preferences.recentOrgCode
                selectedWard = preferences.recentWardId
                wardName = preferences.recentWardName
                emitGetCaregiver {
                    binding.run {
                        lottieLoadingPatientList.visible()
                        rvPatientListCaregiver.gone()
                    }
                }
                emitGetBadgeNotif()
                listenCaregiverList()
                listenDeleteCaregiver()
                listenBadgeNotif()
            }
            preferences.isFromRecent = false
        } else {
            viewModel.emitGetCaregiver()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCaregiverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(
                (activity as CaregiverActivity),
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (viewModel.isOnHold) {
                            viewModel.isOnHold = false
                            binding.topBarCaregiver.setNavigationIcon(resources.getDrawable(R.drawable.ic_close_caregiver))
                            binding.topBarCaregiver.menu.setGroupVisible(0, false)
                            onDataLoaded()
                        } else {
                            requireActivity().finish()
                        }
                    }
                }
            )

        viewModel.run {
//            emitGetCaregiver()
            listenCaregiverList()
            listenNewCaregiver()
            listenBadgeNotif()
            listenDeleteCaregiver()
        }

        childFragmentManager.setFragmentResultListener(
            SelectUnitDialogFragment.KEY_RESULT,
            this@CaregiverFragment
        ) { _, bundle ->
            val result = bundle.getBoolean(SelectUnitDialogFragment.KEY_BUNDLE)
            if (result) {
                setupChip()
                viewModel.emitGetCaregiver {
                    binding.run {
                        lottieLoadingPatientList.visible()
                        rvPatientListCaregiver.gone()
                    }
                }
                viewModel.emitGetBadgeNotif()
            }
        }


        viewModel.getUserShow()
        viewModel.emitHospitalWard()
        viewModel.listenHospitalWardFilter()
        observeError()
        observerCaregiverList()
        observeBadgeNotif()
        setupListener()
        onDataLoaded()
        observeUserShow()
        setupCheckRecent()
        observeConnection()
        observeNewCaregiver()
        observeDeleteCaregiver()
        observePinPatient()
        viewModel.listenBadgeNotif()
    }

    private fun observeDeleteCaregiver() {
        viewModel.deleteCaregiver.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.emitGetCaregiver()
            }
        }
    }

    private fun observeNewCaregiver() {
        viewModel.newCaregiver.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.emitGetCaregiver()
            }
        }
    }

    private fun observeConnection() {
        viewModel.isConnected.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.run {
                    listenCaregiverList()
                    listenBadgeNotif()
                }

            } else {
                Toast.makeText(requireContext(), "No Internet Connection", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setupCheckRecent() {
        if (preferences.recentTag.isNotEmpty()) {
            roomTypeLauncher.launch(
                RoomTypeCaregiverActivity.getIntent(
                    requireContext(),
                    preferences.caregiverId,
                    preferences.patientName,
                    preferences.description,
                    preferences.gender
                )
            )
        }
    }

    private fun setupListener() {
        binding.run {
            topBarCaregiver.menu.setGroupVisible(0, false)
            topBarCaregiver.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_pin -> {
                        topBarCaregiver.menu.setGroupVisible(0, false)
                        viewModel.pinMessage()
                        true
                    }

                    else -> false
                }
            }

            topBarCaregiver.setNavigationOnClickListener {
                if (viewModel.isOnHold) {
                    binding.topBarCaregiver.menu.setGroupVisible(0, false)
                    binding.topBarCaregiver.setNavigationIcon(resources.getDrawable(R.drawable.ic_close_caregiver))
                    viewModel.isOnHold = false
                    onDataLoaded()
                } else {
                    requireActivity().finish()
                }
            }

            edtSearchPatientNameCaregiver.addTextChangedListener {
                val newText = it.toString()
                if (newText != viewModel.keyword) {
                    viewModel.keyword = newText
                    viewModel.emitGetCaregiver {
                        binding.run {
                            lottieLoadingPatientList.visible()
                            rvPatientListCaregiver.gone()
                        }
                    }
                    viewModel.emitGetBadgeNotif()
                }

            }


        }
    }

    private fun observeUserShow() {
        viewModel.userShow.observe(viewLifecycleOwner) {
            when (it) {
                is BaseHandleResponse.ERROR -> {
                    Logger.e(it.message.orEmpty(), it.message)
                }

                is BaseHandleResponse.LOADING -> {}
                is BaseHandleResponse.SUCCESS -> {
                    val data = it.data?.data
                    if (viewModel.firsLoadFilter) {
                        if (viewModel.hospitals.isEmpty()) {
                            viewModel.hospitals.addAll(it.data?.data?.hospital?.sortedBy { e -> e.hospitalHopeId }
                                ?: emptyList())
                        }
                        if (viewModel.hospitals.isNotEmpty()) {
                            val hospital = viewModel.hospitals.first()
                            viewModel.selectedHospital = hospital.hospitalHopeId.toLong()
                            viewModel.orgCode = hospital.alias
                            if (data?.specializationId != "7") {
                                viewModel.emitGetCaregiver {
                                    binding.run {
                                        lottieLoadingPatientList.visible()
                                        rvPatientListCaregiver.gone()
                                    }
                                }
                                viewModel.emitGetBadgeNotif()
                                viewModel.listenCaregiverList()
                            } else {
                                viewModel.isSpecialist = false
                                viewModel.selectedWard = hospital.hospitalHopeId.toLong()
                                viewModel.getWard(hospital.hospitalHopeId.toLong())
                            }
                        }
                    }

                    if (data == null) {
                        viewModel.isSpecialist = false
                        viewModel.getWard()
                    }
                    observeHospitalWard()

                }
            }

        }
    }

    private fun observeError() {
        viewModel.error.observe(viewLifecycleOwner) {
            if (!viewModel.errorHasBeenConsumed) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.errorHasBeenConsumed = true
            } else {
                Unit
            }
        }
    }


    private fun observeBadgeNotif() {
        viewModel.badgeNotif.observe(viewLifecycleOwner) { data ->
            if (data.isUnreadMessage) {
                binding.ibFilterPatient
                    .setImageDrawable(requireContext().getDrawable(R.drawable.ic_filter_badge))
            } else {
                binding.ibFilterPatient
                    .setImageDrawable(requireContext().getDrawable(R.drawable.ic_filter_patient_list))
            }
        }
    }

    private fun observePinPatient() {
        viewModel.pinnedMessage.observe(viewLifecycleOwner) { response ->
            when (response) {
                is BaseHandleResponse.ERROR -> {
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }

                is BaseHandleResponse.LOADING -> {}
                is BaseHandleResponse.SUCCESS -> {
                    binding.topBarCaregiver.menu.setGroupVisible(0, false)
                    binding.topBarCaregiver.setNavigationIcon(resources.getDrawable(R.drawable.ic_close_caregiver))
                    viewModel.isOnHold = false
                    Snackbar.make(
                        requireView(),
                        "${viewModel.pinPatientName} ${if (viewModel.isPinned) "pinned" else "unpinned"}",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    private fun observerCaregiverList() {
        viewModel.caregiverList.observe(viewLifecycleOwner) { data ->
            binding.run {
                lottieLoadingPatientList.gone()
                rvPatientListCaregiver.visible()
            }
            if (data.data.isNotEmpty()) {
                Logger.d(data.data)
                val newData = data.data.map {
                    ListCaregiverPatient(
                        caregiverId = it.id ?: "",
                        patientName = it.name ?: "",
                        mrNo = it.localMrNo ?: "",
                        admissionNo = it.admissionNo ?: "",
                        dpjp = it.dpjp ?: "",
                        ward = it.wardName ?: "",
                        room = it.roomNo ?: "",
                        gender = it.gender ?: 5,
                        isUrgent = it.isUrgent ?: false,
                        date = it.latestMessageAt ?: "",
                        hospitalCode = it.orgCode ?: "",
                        isNew = it.isNew,
                        isPinned = it.isPinned,
                        notification = it.notifications.map { e ->
                            NotificationIcon(
                                url = e.icon.uriExt ?: ""
                            )
                        }
                    )
                }
                viewModel.roomPatientList.clear()
                viewModel.roomPatientList.addAll(newData)
                onDataLoaded()
                binding.tvEmpty.gone()
                binding.ivEmpty.gone()
                binding.rvPatientListCaregiver.visible()
            } else {
                binding.tvEmpty.visible()
                binding.ivEmpty.visible()
                binding.rvPatientListCaregiver.gone()
            }
        }
    }


    private fun onDataLoaded() {
        val data = viewModel.roomPatientList
        val newPatient = data.filter { it.isNew }.sortedByDescending { it.date.toLocalDateTime() }
        val pinnedPatient =
            data.filter { it.isPinned }.sortedByDescending { it.date.toLocalDateTime() }
        val otherPatient = data.filter { !it.isPinned && !it.isNew }
            .sortedByDescending { it.date.toLocalDateTime() }

        viewModel.roomPatientList.clear()
        viewModel.roomPatientList.addAll(pinnedPatient)
        viewModel.roomPatientList.addAll(newPatient)
        viewModel.roomPatientList.addAll(otherPatient)
        val dataSource = dataSourceTypedOf(viewModel.roomPatientList)
        binding.rvPatientListCaregiver.setup {
            withDataSource(dataSource)
            withItem<ListCaregiverPatient, CaregiverPatientListViewHolder>(R.layout.item_patient_list_caregiver) {
                onBind(::CaregiverPatientListViewHolder) { _, item ->
                    tvPatientName.text = item.patientName
                    ivGender.setImageDrawable(
                        if (item.gender == 1) {
                            resources.getDrawable(R.drawable.ic_label_male)
                        } else {
                            resources.getDrawable(R.drawable.ic_label_female)
                        }
                    )
                    val stringInfo =
                        "${item.dpjp}\n${item.hospitalCode.toUpperCase()}.${item.mrNo.toUpperCase()} • " +
                                "${item.ward.toUpperCase()} - ${item.room.toUpperCase()}"
                    tvInfoPatient.text = stringInfo

                    val date = when (item.date.toLocalDateTime().toLocalDate()) {
                        TODAY -> item.date.toLocalDateTime() withFormat "HH:mm"
                        YESTERDAY -> "Yesterday"
                        else -> item.date.toLocalDateTime().toLocalDate() withFormat "dd/MMM/yyyy"
                    }
                    tvDate.text = date

                    layoutCard.setBackgroundColor(
                        if (item.isUrgent) {
                            resources.getColor(R.color.colorYellowLightCaregiver)
                        } else {
                            if (!item.isOnHold) {
                                resources.getColor(R.color.colorWhiteCaregiver)
                            } else {
                                resources.getColor(R.color.colorBlueLightCaregiver)
                            }
                        }
                    )

                    rvTag.setup {
                        withDataSource(dataSourceOf(item.notification))
                        withLayoutManager(
                            LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                        )
                        withItem<NotificationIcon, TagNotificationViewHolder>(R.layout.item_tag_chat) {
                            onBind(::TagNotificationViewHolder) { _, item ->
                                Glide.with(requireContext()).load(item.url).into(ivTag)
                            }
                        }
                    }
                    rvTag.isVisible = item.notification.isNotEmpty()
                    ivPinned.isVisible = item.isPinned
                    ivNew.isVisible = item.isNew
//                    vPatientList.isVisible = !item.isOnHold
                }
                onChildViewClick(CaregiverPatientListViewHolder::layoutCard) { index, _ ->
                    roomTypeLauncher.launch(
                        RoomTypeCaregiverActivity.getIntent(
                            requireContext(),
                            caregiverId = item.caregiverId,
                            patientName = item.patientName,
                            description = "${item.hospitalCode}.${item.mrNo} • " +
                                    "${item.ward} - ${item.room}",
                            gender = item.gender
                        )
                    )
//                    roomViewModel.patientName = item.patientName
//                    roomViewModel.caregiverId = item.caregiverId
//                    roomViewModel.description = "MR NO: ${item.hospitalCode}.${item.mrNo} | " +
//                            "ADMISSION NO: ${item.admissionNo} | " +
//                            "WARD: ${item.ward} | ROOM NO: ${item.room}"
//                    roomViewModel.gender = item.gender
//                    findNavController().navigate(R.id.action_caregiverFragment_to_roomTypeCaregiverFragment)
                }
                onLongClick {
                    // TODO: logic on pin
                    binding.topBarCaregiver.menu.findItem(R.id.menu_pin).setIcon(
                        if (!item.isPinned) {
                            R.drawable.ic_pin
                        } else {
                            R.drawable.ic_unpin
                        }
                    )
                    viewModel.isOnHold = true
                    viewModel.pinnedCaregiverId = item.caregiverId
                    viewModel.isPinned = !item.isPinned
                    viewModel.pinPatientName = item.patientName

                    val newData = dataSource.toList().map {
                        if (it.caregiverId == item.caregiverId) {
                            it.copy(isOnHold = !item.isOnHold)
                        } else {
                            it.copy(isOnHold = false)
                        }
                    }
//                    val index = dataSource.toList().indexOf(item)
                    dataSource.clear()
                    dataSource.set(newData)
                    binding.topBarCaregiver.setNavigationIcon(resources.getDrawable(R.drawable.ic_close))

                    binding.topBarCaregiver.menu.setGroupVisible(0, true)
                }
            }
        }

        binding.ibFilterPatient.isVisible = !viewModel.isSpecialist && viewModel.role == ROLE_DOCTOR
        binding.ibFilterPatient.setSingleOnClickListener {
            SelectUnitDialogFragment().show(childFragmentManager, "FilterUnitDialog")
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    val roomTypeLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                preferences.run {
                    recentOrgId = viewModel.selectedHospital
                    recentOrgCode = viewModel.orgCode
                    recentWardId = viewModel.selectedWard
                    recentWardName = viewModel.wardName
                }
                requireActivity().finish()
            }
        }

    /*
    * Chip dibuat statik karena max 3 hospital per dokter
    * CHIP FILTER RULES :
    * - DPJP hanya filter hospital
    * - RMO ada filter hospital dan ward
    * - Nurse biasa hanya show ward dan hospital
    *  */

    private fun observeHospitalWard() {
        viewModel.hospitalWard.observe(viewLifecycleOwner) { data ->
            Log.e("Ward", Gson().toJson(data))
            if (data.isNotEmpty()) {
                val mapData = data.map {
                    ChipHospitalData(
                        hospitalId = it.hospitalHopeId ?: 0L,
                        hospitalAlias = it.hospitalCode ?: "",
                        isSelected = it.hospitalHopeId == viewModel.selectedHospital,
                        isUrgent = it.isUrgent ?: false,
                        showBadge = it.isUnread ?: false,
                        hospitalName = it.hospitalName ?: "",
                        wards = it.wardList.map { w ->
                            ChipWardData(
                                wardId = w.wardId ?: 0L,
                                wardName = w.wardName ?: "",
                                isSelected = w.wardId == viewModel.selectedWard,
                                isUrgent = w.isUrgent ?: false,
                                showBadge = w.isUnread ?: false
                            )
                        }
                    )
                }
                viewModel.dialogFilterData.clear()
                viewModel.dialogFilterData.addAll(mapData)
                setupChip()
            }

            Toast.makeText(requireContext(), "Data Hospital Ward Empty", Toast.LENGTH_SHORT).show()

        }
    }

    fun setupChip() {
        val ward = mutableListOf<ChipFilterPatientData>()
        viewModel.dialogFilterData.map { chipHospitalData ->
            viewModel.chipData.add(
                ChipFilterPatientData(
                    name = chipHospitalData.hospitalAlias,
                    isSelected = chipHospitalData.isSelected,
                    isHospital = viewModel.isSpecialist,
                    isUrgent = chipHospitalData.isUrgent,
                    showBadge = chipHospitalData.showBadge,
                    hospitalId = chipHospitalData.hospitalId,
                    wardId = -1
                )
            )
            chipHospitalData.wards.map { chipWardData ->
                ward.add(
                    ChipFilterPatientData(
                        name = chipWardData.wardName,
                        isSelected = chipWardData.isSelected,
                        isHospital = viewModel.isSpecialist,
                        isUrgent = chipWardData.isUrgent,
                        showBadge = chipWardData.showBadge,
                        hospitalId = chipHospitalData.hospitalId,
                        wardId = chipWardData.wardId
                    )
                )
            }
        }
        val dataMap = mutableListOf<ChipFilterPatientData>()
        when {
            viewModel.isSpecialist -> {
                val sortedData = viewModel.chipData.sortedBy { it.hospitalId }
                val x =
                    sortedData.first { it.hospitalId == viewModel.selectedHospital }
                val y = sortedData.filter { it.hospitalId != x.hospitalId }
                dataMap.add(x)
                dataMap.addAll(y)
                viewModel.selectedHospital = x.hospitalId
            }

            !viewModel.isSpecialist -> {
                val wardSorted = ward.sortedBy { it.hospitalId }.sortedBy { it.wardId }
                if (!viewModel.isFiltered) {
                    try {
                        val x =
                            viewModel.chipData.first { it.hospitalId == viewModel.selectedHospital }
                                .copy(isSelected = true, isHospital = true)
                        val y =
                            if (viewModel.role == ROLE_DOCTOR) {
                                wardSorted.first { it.hospitalId == x.hospitalId }
                                    .copy(isSelected = true, isHospital = false)
                            } else {
                                wardSorted.first { it.wardId == viewModel.selectedWard }
                                    .copy(isSelected = true, isHospital = false)
                            }
                        dataMap.add(x)
                        dataMap.add(y)
                        viewModel.selectedHospital = x.hospitalId
                        viewModel.selectedWard = y.wardId
                        viewModel.bufferHospital = x.hospitalId
                        viewModel.bufferWard = y.wardId
                        viewModel.emitGetCaregiver()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    val x = viewModel.chipData.first { it.hospitalId == viewModel.selectedHospital }
                        .copy(isSelected = true, isHospital = true)
                    val y =
                        wardSorted.first { it.hospitalId == viewModel.selectedHospital && it.wardId == viewModel.selectedWard }
                            .copy(isSelected = true, isHospital = false)
                    dataMap.add(x)
                    dataMap.add(y)
                    viewModel.selectedHospital = x.hospitalId
                    viewModel.selectedWard = y.wardId
                    viewModel.bufferHospital = x.hospitalId
                    viewModel.bufferWard = y.wardId
                    viewModel.emitGetCaregiver()
                }
            }

            else -> Unit
        }
        setupRvChip(dataMap.distinct().toMutableList())
    }

    private fun setupRvChip(data: MutableList<ChipFilterPatientData>) {
        val dataSource = dataSourceTypedOf(data)
        binding.rvChipPatientList.setup {
            withDataSource(dataSource)
            withLayoutManager(
                LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            )
            withItem<ChipFilterPatientData, ChipViewHolder>(R.layout.chip_filter) {
                onBind(::ChipViewHolder) { _, item ->
                    chip.setSingleOnClickListener {
                        if (item.isHospital && viewModel.isSpecialist) {
                            refreshDataChip(item.hospitalId, data)
                            viewModel.selectedHospital = item.hospitalId
                            viewModel.emitGetCaregiver()
                        }
                    }
                    tvText.text = item.name
                    tvText.setTextColor(
                        if (item.isSelected) {
                            resources.getColor(R.color.colorWhiteCaregiver)
                        } else {
                            if (item.isHospital) {
                                resources.getColor(R.color.colorPrimaryCaregiver)
                            } else {
                                resources.getColor(R.color.colorSecondaryBaseCaregiver)
                            }
                        }
                    )
                    if (item.isUrgent) {
                        ivChip.setImageDrawable(resources.getDrawable(R.drawable.ic_urgent_caregiver_16))
                    } else {
                        if (item.showBadge) {
                            ivChip.setImageDrawable(resources.getDrawable(R.drawable.ic_badge))
                        } else {
                            ivChip.gone()
                        }
                    }
                    if (item.isHospital) {
                        if (item.isSelected) {
                            chip.setBackgroundResource(R.drawable.background_chip_primary)
                        } else {
                            chip.setBackgroundResource(R.drawable.background_chip_outline_primary)
                        }
                    } else {
                        chip.setBackgroundResource(R.drawable.background_chip_secondary)
                    }

                }
            }
        }
    }

    private fun refreshDataChip(hospitalId: Long, chipData: MutableList<ChipFilterPatientData>) {
        val newData = chipData.map {
            if (it.hospitalId == hospitalId) {
                it.copy(isSelected = true)
            } else {
                it.copy(isSelected = false)
            }
        }
        setupRvChip(newData.toMutableList())
    }

}

const val ROLE_DOCTOR = 1
const val ROLE_NURSE = 2
