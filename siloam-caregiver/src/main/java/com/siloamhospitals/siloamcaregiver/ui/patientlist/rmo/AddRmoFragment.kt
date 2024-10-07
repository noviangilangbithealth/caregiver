package com.siloamhospitals.siloamcaregiver.ui.patientlist.rmo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.itemdefinition.onChildViewClick
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.FragmentAddRmoBinding
import com.siloamhospitals.siloamcaregiver.ext.view.setSingleOnClickListener
import com.siloamhospitals.siloamcaregiver.network.response.BaseHandleResponse
import com.siloamhospitals.siloamcaregiver.ui.patientlist.CaregiverPatientListViewModel

class AddRmoFragment : Fragment() {

    private val caregiverViewModel: CaregiverPatientListViewModel by activityViewModels()
    private val viewModel: RmoViewModel by activityViewModels()


    private var _binding: FragmentAddRmoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRmoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            toolbarAddRmoList.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            edtSearchPatientNameCaregiver.addTextChangedListener {
                viewModel.query = it.toString()
                viewModel.filterRmo()
            }

            btnSaveRmo.setSingleOnClickListener {
                val data = viewModel.rmoUiList.filter { it.isChecked }

                val doctorNameMap = data.joinToString(separator = "\n") { "● ${it.name}" }

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.save_confirmation_rmo_caregiver))
                    .setMessage("Apakah anda yakin ingin menambahkan RMO di ward ${viewModel.wardName} ? \n\n$doctorNameMap")
                    .setPositiveButton(getString(R.string.ok)) { _, _ ->
                        viewModel.addParticipantsRmo(data)
                        observeAddRmo()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                    }
                    .show()
            }
        }

        viewModel.rmoUiList.clear()
        viewModel.getRmoMaster()
        observeRmoMaster()

    }

    private fun observeAddRmo() {
        viewModel.postRmoParticipantsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is BaseHandleResponse.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                is BaseHandleResponse.LOADING -> {}
                is BaseHandleResponse.SUCCESS -> {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.success_add_rmo_caregiver),
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                    viewModel.rmoUiList.clear()
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun observeRmoMaster() {
        viewModel.rmoMaster.observe(viewLifecycleOwner) {
            when (it) {
                is BaseHandleResponse.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }

                is BaseHandleResponse.LOADING -> {}

                is BaseHandleResponse.SUCCESS -> {

                    val data = it.data?.data?.rmoList?.map { e ->
                        RmoList(
                            id = e.doctorHopeId,
                            name = e.doctorName,
                            roleId = RMO_ROLE_ID,
                            isChecked = false
                        )
                    }
                    data?.sortedBy { e -> e.name }
                    data?.sortedByDescending { e -> e.isChecked }
                    if (data != null) {
                        viewModel.rmoUiList.clear()
                        viewModel.rmoUiList.addAll(data)
                        val ids = viewModel.rmoParticipantsList.map { e -> e.id }
                        viewModel.setSelectedRmo(ids)
                    }
                    observeRmoUi()
                }
            }
        }
    }

    private fun observeRmoUi() {
        viewModel.rmoDataUi.observe(viewLifecycleOwner) { data ->
            loadRmo(data)
            setTitle(viewModel.rmoUiList.filter { it.isChecked }.size)
        }
    }

    private fun loadRmo(data: List<RmoList>) {
        val dataSource = dataSourceTypedOf(data)
        binding.rvAddRmo.setup {
            withDataSource(dataSource)
            withItem<RmoList, AddRmoViewHolder>(R.layout.item_add_rmo) {
                onBind(::AddRmoViewHolder) { _, item ->
                    cbRmo.isChecked = item.isChecked
                    cbRmo.text = item.name
                }
                onChildViewClick(AddRmoViewHolder::cbRmo) { index, _ ->
                    if (viewModel.rmoUiList.filter { it.isChecked }.size >= LIMIT_RMO) {
                        Toast.makeText(requireContext(), "RMO LIMIT", Toast.LENGTH_SHORT).show()
                        viewModel.checkedFalseChangeRmo(item, index)
                    } else {
                        viewModel.checkedChangeRmo(item)
                    }
                }
            }
        }
    }


    private fun setTitle(count: Int) {
        with(binding) {
            toolbarAddRmoList.title =
                getString(R.string.title_add_rmo, count.toString(), LIMIT_RMO.toString())
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val RMO_ROLE_ID = 4
    }

}

const val LIMIT_RMO = 4