package com.siloamhospitals.siloamcaregiver.ui.patientlist

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.SheetSelectUnitBinding
import com.siloamhospitals.siloamcaregiver.ext.view.gone
import com.siloamhospitals.siloamcaregiver.ext.view.setSingleOnClickListener
import com.siloamhospitals.siloamcaregiver.ext.view.visible

class SelectUnitDialogFragment() : BottomSheetDialogFragment() {

    private var _binding: SheetSelectUnitBinding? = null

    private val binding get() = _binding!!

    override fun getTheme() = R.style.BaseTheme_BottomSheet

    private val viewModel: CaregiverPatientListViewModel by activityViewModels()

    private var selectedHospitalId: Long = 0L
    private var selectedAlias: String = ""
    private var wardId: Long = 0L
    private var wardName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SheetSelectUnitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedHospitalId = viewModel.orgId
        selectedAlias = viewModel.orgCode
        wardId = viewModel.wardId
        wardName = viewModel.wardName
        setAtvPlaceholder()
        setupFilterHospitals()
        setupFilterWard()
        binding.btnApplyFilter.setSingleOnClickListener {
            viewModel.orgId = selectedHospitalId
            viewModel.orgCode = selectedAlias
            viewModel.wardId = wardId
            viewModel.wardName = wardName
            setFragmentResult(KEY_RESULT, bundleOf(KEY_BUNDLE to true))
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        setFragmentResult(KEY_RESULT, bundleOf(KEY_BUNDLE to false))
    }

    private fun setupFilterHospitals() {
        val adapter =
            ArrayAdapter(requireContext(), R.layout.item_drop_down, viewModel.hospitals)
        binding.atvHospitalUnit.setAdapter(adapter)
        binding.atvHospitalUnit.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, i, _ ->
                val selected = adapter.getItem(i)
                if (selected != null) {
                    binding.atvWard.setText("")
                    selectedHospitalId = selected.hospitalHopeId.toLong()
                    selectedAlias = selected.alias
                    if (!viewModel.isSpecialist) {
                        viewModel.getWard(selected.hospitalHopeId.toLong())
                        observeWard()
                    }
                }
            }
    }

    private fun setAtvPlaceholder() {
        with(binding) {
            atvHospitalUnit.setText(viewModel.orgCode)
            if (viewModel.isSpecialist) {
                atvWard.gone()
                tilWard.gone()
            } else {
                atvWard.visible()
                tilWard.visible()
                atvWard.setText(viewModel.wardName)
            }
        }
    }

    private fun observeWard() {
        viewModel.ward.observe(viewLifecycleOwner) {
            val data = it.data?.data?.data?.first()?.wardList
            if (!data.isNullOrEmpty()) {
                viewModel.wards.clear()
                viewModel.wards.addAll(data)
                setupFilterWard()
            }
        }
    }

    private fun setupFilterWard() {
        val adapter =
            ArrayAdapter(requireContext(), R.layout.item_drop_down, viewModel.wards)
        binding.atvWard.setAdapter(adapter)
        binding.atvWard.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, i, _ ->
                val selected = adapter.getItem(i)
                if (selected != null) {
                    wardId = selected.wardId
                    wardName = selected.wardName
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val KEY_RESULT = "key_result"
        const val KEY_BUNDLE = "bundle"
    }
}
