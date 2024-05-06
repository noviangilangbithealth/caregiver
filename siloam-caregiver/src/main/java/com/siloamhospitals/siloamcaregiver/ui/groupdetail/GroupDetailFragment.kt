package com.siloamhospitals.siloamcaregiver.ui.groupdetail

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.FragmentGroupDetailBinding
import com.siloamhospitals.siloamcaregiver.ext.view.setSingleOnClickListener
import com.siloamhospitals.siloamcaregiver.network.response.BaseHandleResponse
import com.siloamhospitals.siloamcaregiver.network.response.groupinfo.GroupInfoDataResponse

class GroupDetailFragment : Fragment() {

    private var _binding: FragmentGroupDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GroupDetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getGroupInfo(viewModel.caregiverId)
        viewModel.getEmrIpdWebView(viewModel.caregiverId)

        binding.topBarInfoGroup.setNavigationOnClickListener {
            requireActivity().finish()
        }
        observeGroupInfo()
        observeUrlWebView()
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
                        viewModel.urlWebView = data
                    }
                }
            }

        }
    }

    private fun observeGroupInfo() {
        viewModel.groupInfo.observe(viewLifecycleOwner) { response ->
            when (response) {
                is BaseHandleResponse.ERROR -> {
                    Logger.d(response.message)
                }

                is BaseHandleResponse.LOADING -> {}
                is BaseHandleResponse.SUCCESS -> {
                    if (response.data?.data != null) {
                        onDataLoaded(response.data.data)
                    }
                }
            }

        }
    }

    private fun onDataLoaded(data: GroupInfoDataResponse) {
        Logger.d(Gson().toJson(data))
        //map response to data class for ui
        val participan = data.result.members.map {
            Participan(
                hopeId = it.user.hopeUserId,
                name = it.user.name,
                color = it.role.color,
                role = it.role.name
            )
        }.sortedBy { it.hopeId == viewModel.doctorHopeId }
        val patientResult = data.result

        with(binding) {

            //region region patient detail
            with(groupDetailPatient) {
                tvGdPatientName.text = patientResult.name

                if (patientResult.gender == GENDER_MALE) ivGender.setImageResource(R.drawable.ic_label_male)
                else ivGender.setImageResource(R.drawable.ic_label_female)

                tvAdm.text = getString(
                    R.string.sub_header_group_detail,
                    patientResult.organizationCode,
                    patientResult.admissionNo
                )

                val room = "${patientResult.wardName} - ${patientResult.roomNo}"
                tvRoom.text = room
                tvPayer.text = patientResult.payer
                tvPhone.text = patientResult.phoneNumber
                btnDetailViewMedicalRecord.setSingleOnClickListener {
                    if (viewModel.urlWebView.isNotEmpty()) {
                        findNavController().navigate(R.id.action_groupDetailFragment2_to_emrIpdWebViewFragment)
                    } else {
                        Toast.makeText(requireContext(), "EMR IPD is empty", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            //endregion

            //region partisipan
            with(groupDetailPartisipan) {
                tvCountPartisipan.text = getString(R.string.partisipan, participan.size.toString())
                val dataSource = dataSourceTypedOf(participan)
                rvPartisipan.setup {
                    withDataSource(dataSource)
                    withItem<Participan, ParticipanViewHolder>(R.layout.item_group_detail_partisipan) {
                        onBind(::ParticipanViewHolder) { _, item ->
                            tvPartcipanName.text = item.name
                            tvRole.text = changeBulletColor(
                                getString(
                                    R.string.group_role,
                                    item.role,
                                ),
                                item.color
                            )
                        }
                    }
                }
            }
            //endregion

            with(groupDetailGambarDocument) {
                layoutGroupDetailGambarDocument.setSingleOnClickListener {
                    viewModel.attachment.clear()
                    viewModel.attachment.addAll(data.attachment)
                    findNavController().navigate(R.id.action_groupDetailFragment2_to_pictureDocumentFragment2)
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

const val GENDER_MALE = 1
