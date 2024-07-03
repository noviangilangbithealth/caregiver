package com.siloamhospitals.siloamcaregiver.ui.chatroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.SheetDeleteMessageBinding

class DeleteMessageBottomSheetDialog(val action: ()->Unit): BottomSheetDialogFragment() {

    private var _binding: SheetDeleteMessageBinding? = null

    override fun getTheme() = R.style.BaseTheme_BottomSheet

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = SheetDeleteMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            buttonDelete.setOnClickListener {
                action.invoke()
                dismiss()
            }
            buttonCancel.setOnClickListener {
                dismiss()
            }
        }
    }
}