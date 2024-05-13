package com.siloamhospitals.siloamcaregiver.ui.chatroom

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.ItemChatDateBinding
import com.siloamhospitals.siloamcaregiver.databinding.ItemChatLeftBinding
import com.siloamhospitals.siloamcaregiver.databinding.ItemChatRightBinding
import com.siloamhospitals.siloamcaregiver.databinding.ItemChatUrgentRightBinding
import com.siloamhospitals.siloamcaregiver.databinding.ItemChatVoiceNoteLeftBinding
import com.siloamhospitals.siloamcaregiver.databinding.ItemChatVoiceNoteRightBinding
import com.siloamhospitals.siloamcaregiver.ext.view.gone
import com.siloamhospitals.siloamcaregiver.ext.view.visible
import com.siloamhospitals.siloamcaregiver.ui.CaregiverChatRoomUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ChatRoomCaregiverAdapter(
    private val chatRoomUis: MutableList<CaregiverChatRoomUi> = ArrayList(),
    val action: ((url: String, isWeb: Boolean) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), CoroutineScope {

    lateinit var adapterContext: Context
    var activity: AppCompatActivity? = null
    var mediaPlayer: MediaPlayer? = null
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        adapterContext = parent.context
        return when (viewType) {
            DATE_CHAT -> DateChatViewHolder(
                ItemChatDateBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            LEFT_CHAT -> LeftChatViewHolder(
                ItemChatLeftBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            RIGHT_CHAT -> RightChatViewHolder(
                ItemChatRightBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

            URGENT_RIGHT_CHAT -> UrgentRightChatViewHolder(
                ItemChatUrgentRightBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            VN_RIGHT_CHAT -> VoiceNoteRightChatViewHolder(
                ItemChatVoiceNoteRightBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            VN_LEFT_CHAT -> VoiceNoteLeftChatViewHolder(
                ItemChatVoiceNoteLeftBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return chatRoomUis.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = chatRoomUis[position]
        when (holder) {
            is DateChatViewHolder -> {
                holder.binding.tvDateTime.text = item.time
            }

            is LeftChatViewHolder -> holder.binding.run {
                tvName.text = item.name
                if(item.color.isNotEmpty()) tvName.setTextColor(Color.parseColor(item.color))
                if (item.url.isNotEmpty()) {
                    Glide.with(adapterContext).load(item.url).into(imageChat)
                    tvChat.gone()
                    cardImage.visible()
                } else {
                    tvChat.visible()
                    cardImage.gone()
                    tvChat.text = item.message
                }
                tvTime.text = item.time
                imageChat.setOnClickListener {
                    action?.invoke(item.url, false)
                }
                layoutLinkLeft.isVisible = item.message.contains("https://")
                var title = ""
                var urlWeb = ""
                if (item.message.contains("https://")) {
                    var x = item.message.split(" ")
                    title = x.first()
                    urlWeb = x.last()
                }
                tvLink.text = "Go to link - $title"
                layoutLinkLeft.setOnClickListener {
                    action?.invoke(urlWeb, true)
                }
            }

            is RightChatViewHolder -> holder.binding.run {
                tvDate.text = item.time
                if (item.url.isNotEmpty()) {
                    Glide.with(adapterContext).load(item.url).into(imageChat)
                    tvChat.gone()
                    cardImage.visible()
                } else {
                    tvChat.visible()
                    cardImage.gone()
                    tvChat.text = item.message
                }
                if (item.isRead) {
                    ivRead.setColorFilter(
                        ContextCompat.getColor(
                            adapterContext,
                            R.color.colorBlueBase
                        )
                    )
                } else {
                    ivRead.setColorFilter(
                        ContextCompat.getColor(
                            adapterContext,
                            R.color.colorBlack38
                        )
                    )
                }
                imageChat.setOnClickListener {
                    action?.invoke(item.url, false)
                }
                layoutLinkRight.isVisible = item.message.contains("https://")
                var title = ""
                var urlWeb = ""
                if (item.message.contains("https://")) {
                    var x = item.message.split(" ")
                    title = x.first()
                    urlWeb = x.last()
                }
                tvLink.text = "Go to link - $title"
                layoutLinkRight.setOnClickListener {
                    action?.invoke(urlWeb, true)
                }
//                tvChat.isVisible = !item.message.contains("https://")
            }

            is UrgentRightChatViewHolder -> holder.binding.run {
                tvDate.text = item.time
                tvChat.text = item.message
                if (item.isRead) {
                    ivRead.setColorFilter(
                        ContextCompat.getColor(
                            adapterContext,
                            R.color.colorBlueBase
                        )
                    )
                } else {
                    ivRead.setColorFilter(
                        ContextCompat.getColor(
                            adapterContext,
                            R.color.colorBlack38
                        )
                    )
                }
            }

            is VoiceNoteRightChatViewHolder -> holder.binding.run {
                tvDate.text = item.time
                mediaPlayer = MediaPlayer()
                try {
                    mediaPlayer?.setDataSource(item.url)
                    mediaPlayer?.prepare()
                    mediaPlayer?.isLooping = false
                    seekbarVoiceNote.max = mediaPlayer?.duration ?: 0
                } catch (e: Exception) {
                    mediaPlayer = null
                }

                ivPlay.setOnClickListener {
                    if (mediaPlayer?.isPlaying == true) {
                        mediaPlayer?.pause()
                        root.post {
                            ivPlay.setImageResource(R.drawable.ic_play_media)
                        }
                    } else {
                        mediaPlayer?.start()
                        runSeeker()
                        root.post {
                            ivPlay.setImageResource(R.drawable.ic_stop_media)
                        }
                    }
                }

                seekbarVoiceNote.setOnSeekBarChangeListener(object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        val x = Math.ceil((progress / 1000).toDouble()).toInt()
                        if (x == 0 && mediaPlayer != null && mediaPlayer?.isPlaying == false) {
                            ivPlay.setImageResource(R.drawable.ic_play_media)
                            root.post {
                                seekBar?.progress = 0
                            }
                        }

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
                            mediaPlayer?.seekTo(seekBar?.progress ?: 0)
                        }
                    }

                })

            }

            is VoiceNoteLeftChatViewHolder -> holder.binding.run {
                tvName.text = item.name
                tvDate.text = item.time
                if(item.color.isNotEmpty()) tvName.setTextColor(Color.parseColor(item.color))
                mediaPlayer = MediaPlayer()
                try {
                    mediaPlayer?.setDataSource(item.url)
                    mediaPlayer?.prepare()
                    mediaPlayer?.isLooping = false
                    seekbarVoiceNote.max = mediaPlayer?.duration ?: 0
                } catch (e: Exception) {
                    mediaPlayer = null
                }

                ivPlay.setOnClickListener {
                    if (mediaPlayer?.isPlaying == true) {
                        mediaPlayer?.pause()
                        root.post {
                            ivPlay.setImageResource(R.drawable.ic_play_media)
                        }
                    } else {
                        mediaPlayer = MediaPlayer()
                        mediaPlayer?.setDataSource(item.url)
                        mediaPlayer?.prepare()
                        mediaPlayer?.start()
                        runSeeker()
                        root.post {
                            ivPlay.setImageResource(R.drawable.ic_stop_media)
                        }
                    }
                }

                seekbarVoiceNote.setOnSeekBarChangeListener(object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        val x = Math.ceil((progress / 1000).toDouble()).toInt()
                        if (x == 0 && mediaPlayer != null && mediaPlayer?.isPlaying == false) {
                            ivPlay.setImageResource(R.drawable.ic_play_media)
                            root.post {
                                seekBar?.progress = 0
                            }
                        }

                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
                            mediaPlayer?.seekTo(seekBar?.progress ?: 0)
                        }
                    }

                })
            }
        }
    }

    fun ItemChatVoiceNoteRightBinding.runSeeker() {
        launch(coroutineContext) {
            var currentPosition = mediaPlayer?.currentPosition ?: 0
            val total = mediaPlayer?.duration ?: 0
            while (mediaPlayer != null && mediaPlayer?.isPlaying == true && currentPosition < total) {
                try {
                    delay(1000)
                    currentPosition = mediaPlayer?.currentPosition ?: 0
                } catch (e: InterruptedException) {
                    Logger.d(e)
                } catch (e: Exception) {
                    Logger.d(e)
                }
                root.post {
                    seekbarVoiceNote.progress = currentPosition
                }
            }
            root.post {
                ivPlay.setImageResource(R.drawable.ic_play_media)
                seekbarVoiceNote.progress = 0
            }
        }
    }

    fun ItemChatVoiceNoteLeftBinding.runSeeker() {
        launch(coroutineContext) {
            var currentPosition = mediaPlayer?.currentPosition ?: 0
            val total = mediaPlayer?.duration ?: 0
            while (mediaPlayer != null && mediaPlayer?.isPlaying == true && currentPosition < total) {
                try {
                    delay(1000)
                    currentPosition = mediaPlayer?.currentPosition ?: 0
                } catch (e: InterruptedException) {
                    Logger.d(e)
                } catch (e: Exception) {
                    Logger.d(e)
                }
                root.post {
                    seekbarVoiceNote.progress = currentPosition
                }
            }
            root.post {
                ivPlay.setImageResource(R.drawable.ic_play_media)
                seekbarVoiceNote.progress = 0
                mediaPlayer?.release()
                mediaPlayer = null
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        val data = chatRoomUis[position]
        return when {
            data.isVoiceNote && data.isSelfSender -> VN_RIGHT_CHAT
            data.isVoiceNote -> VN_LEFT_CHAT
            data.isDateLimit -> DATE_CHAT
            data.isSelfSender -> RIGHT_CHAT
            data.isUrgent -> URGENT_RIGHT_CHAT
            else -> LEFT_CHAT
        }
    }

    companion object {
        private const val DATE_CHAT = 0
        private const val LEFT_CHAT = 1
        private const val RIGHT_CHAT = 2
        private const val URGENT_RIGHT_CHAT = 3
        private const val VN_RIGHT_CHAT = 4
        private const val VN_LEFT_CHAT = 5
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    fun initialize(contents: List<CaregiverChatRoomUi>) {
        val size = itemCount

        chatRoomUis.clear()
        notifyItemRangeRemoved(0, size)

        chatRoomUis.addAll(contents)
        notifyItemRangeInserted(0, itemCount)
    }

    fun setup(activity: Activity?) {
        this.activity = activity as AppCompatActivity
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    fun add(content: CaregiverChatRoomUi) {
        chatRoomUis.add(content)
        notifyItemInserted(itemCount)
    }

    fun add(index: Int, content: CaregiverChatRoomUi) {
        chatRoomUis.add(index, content)
        notifyItemInserted(index)
    }

    fun addAll(contents: List<CaregiverChatRoomUi>) {
        val size = itemCount
        chatRoomUis.addAll(contents)
        notifyItemRangeInserted(size, itemCount)
    }

    fun remove(position: Int, action: (() -> Unit)? = null) {
        chatRoomUis.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)

        action?.invoke()
    }

    fun update(position: Int, content: CaregiverChatRoomUi) {
        chatRoomUis[position] = content
        notifyItemChanged(position)
    }

    fun clear() {
        val size = itemCount
        chatRoomUis.clear()
        notifyItemRangeRemoved(0, size)
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    fun getIndexOf(item: CaregiverChatRoomUi?): Int {
        if (item == null) return 0
        return chatRoomUis.indexOf(item)
    }

    private fun getData(position: Int = 0): CaregiverChatRoomUi {
        return try {
            chatRoomUis[position]
        } catch (e: Exception) {
            chatRoomUis.last()
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    fun firstItem() = chatRoomUis.firstOrNull()
    fun lastItem() = chatRoomUis.lastOrNull()

    fun lastIndex() = chatRoomUis.lastIndex
    fun isEmpty() = chatRoomUis.isEmpty()
    fun isNotEmpty() = chatRoomUis.isNotEmpty()

    fun getList() = chatRoomUis.toList()
    fun getSize() = chatRoomUis.size

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --


}
