package com.siloamhospitals.siloamcaregiver.ui.chatroom.adapters

import com.siloamhospitals.siloamcaregiver.databinding.ItemTextPinChatBinding
import com.siloamhospitals.siloamcaregiver.utils.BaseSingleAdapter

class PinChatAdapter(private val action: (Int) -> Unit): BaseSingleAdapter<String, ItemTextPinChatBinding>(ItemTextPinChatBinding::inflate) {
    override fun onBindViewHolder(
        binding: ItemTextPinChatBinding,
        data: String,
        adapterPosition: Int
    ) {
        binding.run {
            tvPinnedChat.text = data
            binding.root.setOnClickListener {
                action.invoke(adapterPosition)
            }
        }
    }
}