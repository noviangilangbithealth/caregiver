package com.siloamhospitals.siloamcaregiver.ui.chatroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.FragmentChatRoomWebViewBinding


class ChatRoomWebViewFragment : Fragment() {

    private var _binding: FragmentChatRoomWebViewBinding? = null
    private val binding get() = _binding!!

    private val chatRoomViewModel: ChatRoomCaregiverViewModel by activityViewModels()

    private val onBackPressedDispatcher by lazy {
        requireActivity().onBackPressedDispatcher.addCallback(this, false) {
            if (binding.wvEmrIpd.canGoBack()) binding.wvEmrIpd.goBack() else findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatRoomWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        onBackPressedDispatcher.isEnabled = true

        binding.topBarWebView.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.topBarWebView.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.share -> {
                    val currentUrl: String = binding.wvEmrIpd.url ?: ""
                    val pageTitle = binding.wvEmrIpd.title ?: ""
                    var sendUrl = "$pageTitle $currentUrl"
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.share))
                        .setMessage(getString(R.string.share_message))
                        .setPositiveButton(getString(R.string.ok)) { _, _ ->
                            chatRoomViewModel.sendChat(message = sendUrl)
                        }
                        .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                        }
                        .show()

                    true
                }

                else -> false
            }
        }

        loadUrl(chatRoomViewModel.urlEmrIpd)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadUrl(url: String) {
        binding.wvEmrIpd.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            loadUrl(url)
        }
    }


}
