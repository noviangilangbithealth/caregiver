package com.siloamhospitals.siloamcaregiver.ui.patientlist.rmo

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.FragmentRmoListBinding
import com.siloamhospitals.siloamcaregiver.network.response.BaseHandleResponse
import com.siloamhospitals.siloamcaregiver.network.response.rmo.RmoParticipantsListResponse
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import com.siloamhospitals.siloamcaregiver.ui.CaregiverActivity
import com.siloamhospitals.siloamcaregiver.ui.groupdetail.ParticipanViewHolder
import com.siloamhospitals.siloamcaregiver.ui.patientlist.CaregiverPatientListViewModel


class RmoListFragment : Fragment() {

    private var _binding: FragmentRmoListBinding? = null
    private val binding get() = _binding!!

    private val caregiverViewModel: CaregiverPatientListViewModel by activityViewModels()
    private val viewModel: RmoViewModel by activityViewModels()

    private val preferences by lazy {
        AppPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRmoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        viewModel.getRmoParticipants()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(
                (activity as CaregiverActivity),
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        findNavController().navigateUp()
                    }
                }
            )

        with(binding) {
            toolbarRmoList.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            toolbarRmoList.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_rmo_edit -> {
                        findNavController().navigate(R.id.action_rmoListFragment_to_addRmoFragment)
                        true
                    }

                    else -> false
                }
            }
        }

        observeRmoParticipants()

    }

    private fun observeRmoParticipants() {
        viewModel.rmoParticipants.observe(viewLifecycleOwner) { event ->
            when (event) {
                is BaseHandleResponse.SUCCESS -> {
                    val data = event.data?.data
                    if (data != null) {
                        onDataLoaded(data.rmoList)
                        if (viewModel.isNurse || caregiverViewModel.isSpecialist || !data.isAllowedit) {
                            binding.toolbarRmoList.menu.setGroupVisible(0, false)
                        }
                    }
                    Logger.d(data)
                }

                is BaseHandleResponse.ERROR -> {
                    val message = event.message
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }

                is BaseHandleResponse.LOADING -> {}

            }

        }
    }

    private fun onDataLoaded(rmoList: List<RmoParticipantsListResponse>) {


        viewModel.rmoParticipantsList.clear()
        rmoList.map {
            viewModel.rmoParticipantsList.add(
                RmoList(
                    id = it.doctorHopeId.toLong(),
                    name = it.doctorName,
                    roleId = it.roleId,
                    isChecked = true
                )
            )
        }
        with(binding) {
            tvCountRmoParticipants.text = getString(R.string.participants, rmoList.size.toString())
            val dataSource = dataSourceTypedOf(rmoList)
            rvRmoParticipants.setup {
                withDataSource(dataSource)
                withItem<RmoParticipantsListResponse, ParticipanViewHolder>(R.layout.item_group_detail_partisipan) {
                    onBind(::ParticipanViewHolder) { _, item ->
                        tvPartcipanName.text = item.doctorName
                        tvRole.text = changeBulletColor(
                            getString(
                                R.string.group_role,
                                item.roleName,
                            ),
                            item.roleColor
                        )
                        Glide.with(requireContext()).load(item.imageUrl).into(ivParticipan)
                    }
                }

            }
        }
    }

    private fun changeBulletColor(originalText: String, newColor: String): SpannableString {
        val spannableString = SpannableString(originalText)
        val bulletIndex = originalText.indexOf("●")

        if (bulletIndex != -1) {
            val colorInt = Color.parseColor(newColor)
            spannableString.setSpan(
                ForegroundColorSpan(colorInt),
                bulletIndex,
                bulletIndex + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannableString
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}