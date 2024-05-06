package com.siloamhospitals.siloamcaregiver.ui.chatroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.siloamhospitals.siloamcaregiver.databinding.FragmentImageDetailBinding

class ImageDetailFragment : DialogFragment() {

    private var _binding: FragmentImageDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var pickImage: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding.layoutImageViewNpwp.setOnClickListener {
            dialog?.dismiss()
        }

        if (arguments != null) {
            try {
                pickImage = arguments?.getString(DATA)!!
                Glide.with(requireActivity())
                    .load(pickImage)
                    .fitCenter()
                    .into(binding.detailImage)

            } catch (e: Exception) {
//                Timber.d(e)
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        const val DATA = "IMAGE_DATA"
    }
}
