package com.siloamhospitals.siloamcaregiver.ui.patientlist

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
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


    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
                behavior.peekHeight = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }

        return dialog
    }

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

        setupFilterHospitals()
        binding.btnCloseDialogFilter.setSingleOnClickListener {
            dismiss()
        }

        binding.btnApplyFilter.setSingleOnClickListener {
            viewModel.selectedHospital = viewModel.bufferHospital
            viewModel.selectedWard = viewModel.bufferWard
            viewModel.isFiltered = true
            setFragmentResult(KEY_RESULT, bundleOf(KEY_BUNDLE to true))
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.bufferWard = viewModel.selectedWard
        viewModel.bufferHospital = viewModel.selectedHospital
        setFragmentResult(KEY_RESULT, bundleOf(KEY_BUNDLE to false))
    }

    private fun setupFilterHospitals() {
        with(binding) {
            val hospitals = viewModel.dialogFilterData.sortedBy { it.hospitalId }
            val dataHospital = hospitals.map {
                it.copy(isSelected = it.hospitalId == viewModel.bufferHospital)
            }
            val hospitalDataSource = dataSourceTypedOf(dataHospital)
            rvChipHospital.setup {
                withDataSource(hospitalDataSource)
                withLayoutManager(
                    LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                )
                withItem<ChipHospitalData, ChipViewHolder>(R.layout.chip_filter) {
                    onBind(::ChipViewHolder) { _, item ->
                        chip.visible()
                        tvText.text = item.hospitalAlias
                        if (item.isUrgent) {
                            ivChip.setImageDrawable(resources.getDrawable(R.drawable.ic_urgent_caregiver_16))
                        } else {
                            if (item.showBadge) {
                                ivChip.setImageDrawable(resources.getDrawable(R.drawable.ic_badge))
                            } else {
                                ivChip.gone()
                            }
                        }
                        if (item.isSelected) {
                            chip.setBackgroundResource(R.drawable.background_chip_primary)
                            tvText.setTextColor(resources.getColor(R.color.white))
                        } else {
                            chip.setBackgroundResource(R.drawable.background_chip_outline_primary)
                            tvText.setTextColor(resources.getColor(R.color.colorPrimary))
                        }
                    }
                    onClick {
                        handlingDataHospital(item.hospitalId, dataHospital)
                    }
                }
            }

            setupFilterWards(dataHospital)
        }
    }

    private fun handlingDataHospital(hospitalId: Long, dataHospital: List<ChipHospitalData>) {
        viewModel.bufferHospital = hospitalId
        viewModel.bufferWard =
            dataHospital.first { it.hospitalId == hospitalId }.wards.first().wardId
        setupFilterHospitals()
    }


    private fun setupFilterWards(dataHospital: List<ChipHospitalData>) {
        val dataWard = dataHospital.first { it.isSelected }.wards.map {
            it.copy(isSelected = it.wardId == viewModel.bufferWard)
        }
        val dataSource = dataSourceTypedOf(dataWard)
        val layoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
        }

        binding.rvChipWard.setup {
            withDataSource(dataSource)
            withLayoutManager(
                layoutManager
            )
            withItem<ChipWardData, ChipViewHolder>(R.layout.chip_filter) {
                onBind(::ChipViewHolder) { _, item ->
                    chip.visible()
                    tvText.text = item.wardName
                    if (item.isUrgent) {
                        ivChip.setImageDrawable(resources.getDrawable(R.drawable.ic_urgent_caregiver_16))
                    } else {
                        if (item.showBadge) {
                            ivChip.setImageDrawable(resources.getDrawable(R.drawable.ic_badge))
                        } else {
                            ivChip.gone()
                        }
                    }
                    if (item.isSelected) {
                        chip.setBackgroundResource(R.drawable.background_chip_secondary)
                        tvText.setTextColor(resources.getColor(R.color.white))
                    } else {
                        chip.setBackgroundResource(R.drawable.background_chip_outline_secondary)
                        tvText.setTextColor(resources.getColor(R.color.colorSecondaryBase))
                    }
                }
                onClick {
                    handlingDataWard(item.wardId, dataHospital)
                }
            }
        }
    }

    private fun handlingDataWard(wardId: Long, dataHospital: List<ChipHospitalData>) {
        viewModel.bufferWard = wardId
        setupFilterWards(dataHospital)
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
