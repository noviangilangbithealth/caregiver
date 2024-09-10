package com.siloamhospitals.siloamcaregiver.ui.groupdetail

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.FragmentPictureBinding
import com.siloamhospitals.siloamcaregiver.ext.datetime.toLocalDate
import com.siloamhospitals.siloamcaregiver.ext.datetime.withFormat
import com.siloamhospitals.siloamcaregiver.ext.view.gone
import com.siloamhospitals.siloamcaregiver.ui.chatroom.CaregiverImageDetailFragment
import com.siloamhospitals.siloamcaregiver.ui.player.VideoPlayerActivity
import java.time.LocalDate

class PictureFragment : Fragment() {

    private var _binding: FragmentPictureBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GroupDetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPictureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataList = mutableListOf<Any>()
        val date = viewModel.attachment.distinctBy { it.sentAt.take(10) }.map { it.sentAt.take(10) }
            .sortedByDescending { it.toLocalDate() }
        date.forEach { sendDate ->
            dataList.add(PictureDate(date = sendDate))
            viewModel.attachment.forEach { data ->
                if (data.sentAt.take(10) == sendDate) {
                    dataList.add(PictureItem(url = data.attachment.first().uriText, isVideo = data.attachment.first().uriText.endsWith(".mp4") || data.attachment.first().uriText.endsWith(".3gp") || data.attachment.first().uriText.endsWith(".mov")))
                }
            }
        }

        with(binding) {
            val dataSource = dataSourceTypedOf(dataList)
            rvPicture.setup {
                withDataSource(dataSource)
                withItem<PictureDate, PictureDateViewHolder>(R.layout.item_picture_date) {
                    onBind(::PictureDateViewHolder) { _, item ->
                        tvPictureDate.text = returnDate(item.date.toLocalDate())
                    }
                }
                withLayoutManager(GridLayoutManager(requireContext(), 4))
                withItem<PictureItem, PictureItemViewHolder>(R.layout.item_picture_item) {
                    onBind(::PictureItemViewHolder) { _, item ->
//                        val imageViewWidth = calculateImageViewWidth()
//                        ivPictureItem.layoutParams =
//                            ViewGroup.LayoutParams(imageViewWidth, imageViewWidth)
                        Glide.with(requireContext())
                            .load(item.url)
                            .centerCrop()
                            .addListener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {

                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    ivPlayButton.gone()
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    ivPlayButton.isVisible = item.isVideo
                                    return false
                                }
                            })
                            .into(ivPictureItem)

                    }
                    onClick {
                        if(item.isVideo){
                            VideoPlayerActivity.openPlayer(requireContext(), item.url)
                        }else {
                            viewDetailImage(item.url)
                        }
                    }
                }
                val layoutManager = GridLayoutManager(requireContext(), 4)
                layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        // Assuming your dataSource can provide the type directly or you can infer it from the item
                        // Span full width for PictureDate and default span for PictureItem
                        return when (dataSource[position]) {
                            is PictureDate -> 4 // Span full width
                            is PictureItem -> 1 // Default span
                            else -> 1
                        }
                    }
                }
                withLayoutManager(layoutManager)

                val spaceBetweenItemsPx =
                    resources.getDimensionPixelSize(R.dimen.space_between_items)

// Add ItemDecoration to add space between items
                rvPicture.addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        super.getItemOffsets(outRect, view, parent, state)

                        // Set space between items
                        outRect.set(
                            spaceBetweenItemsPx,
                            spaceBetweenItemsPx,
                            spaceBetweenItemsPx,
                            spaceBetweenItemsPx
                        )
                    }
                })

            }
        }
    }

    fun dpToPx(dp: Int): Int {
        return (dp * requireContext().resources.displayMetrics.density).toInt()
    }

    fun calculateImageViewWidth(): Int {
        val screenWidth = requireContext().resources.displayMetrics.widthPixels
        val padding = dpToPx(8)
        // 3 gaps between 4 images + 2 edges = 5 * 8dp padding in total
        val totalPadding = padding * 5
        return (screenWidth - totalPadding) / 4
    }

    fun returnDate(date: LocalDate): String {
        return when {
            date == LocalDate.now() -> getString(R.string.today)
            date == LocalDate.now().minusDays(1) -> getString(R.string.yesterday)
            else -> date withFormat "dd MMM yyyy"
        }
    }

    private fun viewDetailImage(imageDetail: String) {
        val bundle = Bundle()
        bundle.putString(
            CaregiverImageDetailFragment.DATA,
            imageDetail
        )
        val viewDialogFragment = CaregiverImageDetailFragment()
        viewDialogFragment.arguments = bundle
        val mFragmentManager = parentFragmentManager
        viewDialogFragment.show(
            mFragmentManager,
            CaregiverImageDetailFragment::class.java.simpleName
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
