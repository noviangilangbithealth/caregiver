package com.siloamhospitals.siloamcaregiver.ui.chatroom

import androidx.recyclerview.widget.RecyclerView
import com.siloamhospitals.siloamcaregiver.databinding.ItemChatDateBinding
import com.siloamhospitals.siloamcaregiver.databinding.ItemChatLeftBinding
import com.siloamhospitals.siloamcaregiver.databinding.ItemChatRightBinding
import com.siloamhospitals.siloamcaregiver.databinding.ItemChatUrgentRightBinding
import com.siloamhospitals.siloamcaregiver.databinding.ItemChatVoiceNoteLeftBinding
import com.siloamhospitals.siloamcaregiver.databinding.ItemChatVoiceNoteRightBinding

class DateChatViewHolder(val binding: ItemChatDateBinding): RecyclerView.ViewHolder(binding.root)

class LeftChatViewHolder(val binding: ItemChatLeftBinding): RecyclerView.ViewHolder(binding.root)

class RightChatViewHolder(val binding: ItemChatRightBinding): RecyclerView.ViewHolder(binding.root)

class UrgentRightChatViewHolder(val binding: ItemChatUrgentRightBinding): RecyclerView.ViewHolder(binding.root)

class VoiceNoteRightChatViewHolder(val binding: ItemChatVoiceNoteRightBinding): RecyclerView.ViewHolder(binding.root)

class VoiceNoteLeftChatViewHolder(val binding: ItemChatVoiceNoteLeftBinding): RecyclerView.ViewHolder(binding.root)