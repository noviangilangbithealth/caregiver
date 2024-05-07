package com.siloamhospitals.siloamcaregiver.ui.patientlist

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.fondesa.recyclerviewdivider.addDivider
import com.orhanobut.logger.Logger
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.FragmentCaregiverBinding
import com.siloamhospitals.siloamcaregiver.ext.datetime.TODAY
import com.siloamhospitals.siloamcaregiver.ext.datetime.YESTERDAY
import com.siloamhospitals.siloamcaregiver.ext.datetime.toLocalDateTime
import com.siloamhospitals.siloamcaregiver.ext.datetime.withFormat
import com.siloamhospitals.siloamcaregiver.ext.view.gone
import com.siloamhospitals.siloamcaregiver.ext.view.invisible
import com.siloamhospitals.siloamcaregiver.ext.view.visible
import com.siloamhospitals.siloamcaregiver.network.response.BaseHandleResponse
import com.siloamhospitals.siloamcaregiver.network.response.UserShowHospitalResponse
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.ListCaregiverPatient
import com.siloamhospitals.siloamcaregiver.ui.NotificationIcon
import com.siloamhospitals.siloamcaregiver.ui.roomtype.RoomTypeCaregiverActivity
import com.siloamhospitals.siloamcaregiver.ui.roomtype.RoomTypeViewModel


class CaregiverFragment : Fragment() {

    private var _binding: FragmentCaregiverBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CaregiverPatientListViewModel by activityViewModels()
    private val roomViewModel: RoomTypeViewModel by activityViewModels()

    private val preferences by lazy {
        AppPreferences(requireContext())
    }

    override fun onResume() {
        super.onResume()
        binding.chipHospital.text = viewModel.orgCode
        binding.chipWard.text = viewModel.wardName
        binding.chipWard.isVisible = !viewModel.isSpecialist

        if (preferences.isFromRecent) {
            viewModel.run {
                orgId = preferences.recentOrgId
                orgCode = preferences.recentOrgCode
                wardId = preferences.recentWardId
                wardName = preferences.recentWardName
                emitGetCaregiver {
                    binding.run {
                        lottieLoadingPatientList.visible()
                        rvPatientListCaregiver.gone()
                    }
                }
                emitGetBadgeNotif()
                listenCaregiverList()
                emitGetBadgeNotif()
                binding.chipHospital.text = viewModel.orgCode
                binding.chipWard.text = viewModel.wardName
            }
            preferences.isFromRecent = false
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

        viewModel.run {
            emitGetCaregiver()
            listenCaregiverList()
            listenBadgeNotif()
        }

        childFragmentManager.setFragmentResultListener(
            SelectUnitDialogFragment.KEY_RESULT,
            this@CaregiverFragment
        ) { _, bundle ->
            val result = bundle.getBoolean(SelectUnitDialogFragment.KEY_BUNDLE)
            if (result) {
                viewModel.emitGetCaregiver {
                    binding.run {
                        lottieLoadingPatientList.visible()
                        rvPatientListCaregiver.gone()
                    }
                }
                viewModel.emitGetBadgeNotif()
                binding.chipHospital.text = viewModel.orgCode
                binding.chipWard.text = viewModel.wardName
            }
            binding.chipHospital.visible()
            binding.chipWard.isVisible = !viewModel.isSpecialist
        }

        observeError()
        observerCaregiverList()
        observeBadgeNotif()
        setupListener()
        onDataLoaded()
        observeUserShow()
        setupCheckRecent()
        viewModel.listenBadgeNotif()
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
            topBarCaregiver.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.filter -> {
                        binding.chipHospital.invisible()
                        binding.chipWard.invisible()
                        SelectUnitDialogFragment().show(
                            childFragmentManager,
                            "SelectUnitDialogFragment"
                        )
                        edtSearchPatientNameCaregiver.clearFocus()
                        true
                    }

                    else -> false
                }
            }

            topBarCaregiver.setNavigationOnClickListener {
                requireActivity().finish()
            }

            edtSearchPatientNameCaregiver.addTextChangedListener {
                val newText = it.toString()
                if (newText != viewModel.keyword) {
                    viewModel.keyword = newText
                    viewModel.scrollPosition = 0
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
            when(it) {
                is BaseHandleResponse.ERROR -> {
                    Logger.e(it.message.orEmpty(), it.message)
                }
                is BaseHandleResponse.LOADING -> { }
                is BaseHandleResponse.SUCCESS -> {
                    val data = it.data?.data
                    if (viewModel.firsLoadFilter) {
                        if (viewModel.hospitals.isEmpty()) {
                            viewModel.hospitals.addAll(it.data?.data?.hospital?.sortedBy { e -> e.hospitalHopeId }
                                ?: emptyList())
                        }
                        if (viewModel.hospitals.isNotEmpty()) {
                            val hospital = viewModel.hospitals.first()
                            viewModel.orgId = hospital.hospitalHopeId.toLong()
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
                                binding.chipHospital.text = hospital.alias
                                binding.chipWard.gone()
                            } else {
                                viewModel.isSpecialist = false
                                viewModel.orgId = hospital.hospitalHopeId.toLong()
                                viewModel.getWard(hospital.hospitalHopeId.toLong())
                                binding.chipHospital.text = hospital.alias
                                observeWard()
                            }
                        }
                    }

                    if(data == null) {
                        viewModel.isSpecialist = false
                        viewModel.getWard()
                        observeWard()
                    }
                }
            }

        }
    }

    private fun observeWard() {
        viewModel.ward.observe(viewLifecycleOwner) {
            if (viewModel.firsLoadFilter) {
                val data = it.data?.data?.data?.first()?.wardList
                val dataHospital = it.data?.data?.data?.first()
                if(viewModel.hospitals.isEmpty()) {
                    viewModel.hospitals.add(UserShowHospitalResponse(
                        hospitalHopeId = dataHospital?.orgId?.toInt() ?: 0,
                        alias = dataHospital?.hospitalCode.orEmpty(),
                        name = dataHospital?.orgName.orEmpty()
                    ))
                    viewModel.orgId = dataHospital?.orgId ?: 0
                    viewModel.orgCode = dataHospital?.hospitalCode.orEmpty()
                    binding.chipHospital.text = dataHospital?.hospitalCode.orEmpty()
                }
                viewModel.firsLoadFilter = false
                if (!data.isNullOrEmpty()) {
                    viewModel.wards.clear()
                    viewModel.wards.addAll(data)
                    if(data.find { it.wardId == viewModel.wardId} != null) {
                        viewModel.wardName = data.find { it.wardId == viewModel.wardId}?.wardName.orEmpty()
                        binding.chipWard.text = data.find { it.wardId == viewModel.wardId}?.wardName.orEmpty()
                    } else {
                        viewModel.wardId = data.first().wardId
                        viewModel.wardName = data.first().wardName
                        binding.chipWard.text = data.first().wardName
                    }
                    binding.chipWard.isVisible = !viewModel.isSpecialist
                    viewModel.emitGetCaregiver {
                        binding.run {
                            lottieLoadingPatientList.visible()
                            rvPatientListCaregiver.gone()
                        }
                    }
                    viewModel.emitGetBadgeNotif()
                    viewModel.listenCaregiverList()
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
                binding.topBarCaregiver.menu.findItem(R.id.filter)
                    .setIcon(R.drawable.ic_filter_badge)
            } else {
                binding.topBarCaregiver.menu.findItem(R.id.filter)
                    .setIcon(R.drawable.ic_filter_patient_list)
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
                        ward = it.wardName ?: "",
                        room = it.roomNo ?: "",
                        gender = it.gender ?: 5,
                        isUrgent = it.isUrgent ?: false,
                        date = it.createAt ?: "",
                        hospitalCode = it.orgCode ?: "",
                        notification = it.notifications.map { e ->
                            NotificationIcon(
                                url = e.icon.uriExt ?: ""
                            )
                        }
                    )
                }
                Logger.d(newData)
                viewModel.roomPatientList.clear()
                viewModel.roomPatientList.addAll(newData)
                binding.rvPatientListCaregiver.scrollToPosition(viewModel.scrollPosition)
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
                        "MR NO: ${item.hospitalCode}.${item.mrNo} | " +
                                "ADMISSION NO: ${item.admissionNo} | " +
                                "WARD: ${item.ward} | ROOM NO: ${item.room}"
                    tvInfoPatient.text = stringInfo

                    val date = when (item.date.toLocalDateTime().toLocalDate()) {
                        TODAY -> item.date.toLocalDateTime() withFormat "HH:mm"
                        YESTERDAY -> "Yesterday"
                        else -> item.date.toLocalDateTime().toLocalDate() withFormat "dd/MM/yyyy"
                    }
                    tvDate.text = date

                    layoutCard.setBackgroundColor(
                        if (item.isUrgent) {
                            resources.getColor(R.color.colorYellowLight)
                        } else {
                            resources.getColor(R.color.colorWhite)
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

                }
                onChildViewClick(CaregiverPatientListViewHolder::layoutCard) { index, _ ->
                    val layoutManager =
                        binding.rvPatientListCaregiver.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition =
                        layoutManager.findFirstCompletelyVisibleItemPosition()
                    viewModel.scrollPosition = firstVisibleItemPosition
                    roomTypeLauncher.launch(
                        RoomTypeCaregiverActivity.getIntent(
                            requireContext(),
                            caregiverId = item.caregiverId,
                            patientName = item.patientName,
                            description = "MR NO: ${item.hospitalCode}.${item.mrNo} | " +
                                    "ADMISSION NO: ${item.admissionNo} | " +
                                    "WARD: ${item.ward} | ROOM NO: ${item.room}",
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
            }
        }

        binding.rvPatientListCaregiver.addDivider()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    val roomTypeLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                preferences.run {
                    recentOrgId = viewModel.orgId
                    recentOrgCode = viewModel.orgCode
                    recentWardId = viewModel.wardId
                    recentWardName = viewModel.wardName
                }
                requireActivity().finish()
            }
        }
}

const val URL = "ws://10.85.139.102:3344/"
