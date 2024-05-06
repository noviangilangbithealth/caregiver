package com.siloamhospitals.siloamcaregiver.ui.groupdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.siloamhospitals.siloamcaregiver.R
import com.siloamhospitals.siloamcaregiver.databinding.FragmentPictureDocumentBinding

/*
* Fragment ini disedian karena plan awal ada tab bar buat picture dan dokumen
* Berhubung dokumen masih belum di handle
* jadi hanya ada picture saja
* tab barnya udah di siapin tinggal uncomment xml sama function setupUi() aja.
* Jangan lupa ubah title top barnya
* drop line 37
* */

class PictureDocumentFragment : Fragment() {

    private var _binding: FragmentPictureDocumentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPictureDocumentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.topBarPictureDocument.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.f_layout_tab_bar, PictureFragment()).commit()

//        setupUi()

    }
//
//    private fun setupUi() {
//        with(binding) {
//            topBarPictureDocument.setNavigationOnClickListener {
//                findNavController().navigateUp()
//            }
//            childFragmentManager.beginTransaction()
//                .replace(R.id.f_layout_tab_bar, PictureFragment()).commit()
//
//            tlPictureDocument.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//
//                override fun onTabSelected(tab: TabLayout.Tab?) {
//                    Logger.d(tab?.text)
//                    if (tab?.text == "Document") {
//                        childFragmentManager.beginTransaction()
//                            .replace(R.id.f_layout_tab_bar, DocumentFragment()).commit()
//                    } else {
//                        childFragmentManager.beginTransaction()
//                            .replace(R.id.f_layout_tab_bar, PictureFragment()).commit()
//                    }
//                }
//
//                override fun onTabReselected(tab: TabLayout.Tab?) {
//                    // Handle tab reselect
//                }
//
//                override fun onTabUnselected(tab: TabLayout.Tab?) {
//                    // Handle tab unselect
//                }
//            })
//        }
//
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
