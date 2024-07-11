package com.siloamhospitals.siloamcaregiver.ui.groupdetail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.orhanobut.logger.Logger
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.FragmentAdmissionHistoryBinding
import com.siloamhospitals.siloamcaregiver.ext.datetime.toLocalDateTime
import com.siloamhospitals.siloamcaregiver.ext.datetime.withFormat
import com.siloamhospitals.siloamcaregiver.ext.view.gone
import com.siloamhospitals.siloamcaregiver.ext.view.visible
import com.siloamhospitals.siloamcaregiver.network.response.BaseHandleResponse
import com.siloamhospitals.siloamcaregiver.network.response.groupinfo.GroupInfoAdmissionHistoryDataResponse
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.roomtype.RoomTypeCaregiverActivity

class AdmissionHistoryFragment : Fragment() {

    private var _binding: FragmentAdmissionHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GroupDetailViewModel by activityViewModels()

    private val preferences by lazy {
        AppPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdmissionHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topBarAdmissionHistory.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        if (viewModel.emptyState) {
            with(binding) {
                tvEmptyAdmissionHistory.visible()
                ivEmptyAdmissionHistory.visible()
                rvAdmissionHistory.gone()
            }
        } else {
            with(binding) {
                tvEmptyAdmissionHistory.gone()
                ivEmptyAdmissionHistory.gone()
                rvAdmissionHistory.visible()
            }
            val dataSource = dataSourceTypedOf(viewModel.savedAdmissionHistory)
            binding.rvAdmissionHistory.setup {
                withDataSource(dataSource)
                withItem<GroupInfoAdmissionHistoryDataResponse, AdmissionHistoryViewHolder>(R.layout.item_admission_history) {
                    onBind(::AdmissionHistoryViewHolder) { _, item ->
                        val hospitalMrAdmission =
                            "${item.orgCode}.${item.localMrNo} • ${item.admissionNo}"
                        tvHospitalMrAdmission.text = hospitalMrAdmission
                        tvDoctorName.text = item.doctorName
                        tvDateTime.text =
                            item.createAt.take(19).toLocalDateTime() withFormat "dd MMM yyyy, HH:mm"
                        btnChatHistory.setOnClickListener {
                            val intent =
                                Intent(requireContext(), RoomTypeCaregiverActivity::class.java)
                            preferences.historyCaregiverId = item.id
                            preferences.isChatHistory = true
                            preferences.historyHospitalId = item.orgId
                            preferences.historyHospitalUnit = item.orgCode
                            preferences.historyRoom = item.roomNo
                            preferences.historyWard = item.wardName
                            preferences.historyLocalMrNumber = item.localMrNo
                            preferences.historyPatientName = viewModel.patientDetail.name
                            preferences.historyGender =
                                if (viewModel.patientDetail.gender == 1) "M" else "F"
                            startActivity(intent)
                        }
                        btnMedicalRecord.setOnClickListener {
                            viewModel.getEmrIpdWebView(item.id)
                            observeUrlWebView()
                        }
                    }
                }
            }
        }

    }

    private fun observeUrlWebView() {
        viewModel.emrIpdWebView.observe(viewLifecycleOwner) { response ->
            when (response) {
                is BaseHandleResponse.ERROR -> {
                    Logger.d(response.message)
                }

                is BaseHandleResponse.LOADING -> {}
                is BaseHandleResponse.SUCCESS -> {
                    val data = response.data?.url
                    if (!data.isNullOrEmpty()) {
                        viewModel.admissionHistoryMrUrl = data
                        viewModel.fromAdmissionHistory = true
                        findNavController().navigate(R.id.action_admissionHistoryFragment_to_emrIpdWebViewFragment)
                    }
                }
            }

        }
    }


}
