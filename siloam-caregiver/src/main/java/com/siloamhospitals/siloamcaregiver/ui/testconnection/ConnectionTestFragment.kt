package com.siloamhospitals.siloamcaregiver.ui.testconnection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.siloamhospitals.siloamcaregiver.databinding.FragmentConnectionTestBinding
import com.siloamhospitals.siloamcaregiver.shared.AppPreferences
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ConnectionTestFragment : Fragment() {

    private var _binding: FragmentConnectionTestBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModel<TestConnectionViewModel>()

    val mPreference by lazy {
        AppPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConnectionTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel.emitGetCaregiver()
        viewModel.listenCaregiverList()

        observeError()
        observerCaregiverList()
    }

    private fun observeError() {
        viewModel.error.observe(viewLifecycleOwner) {
            if (!viewModel.errorHasBeenConsumed) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.errorHasBeenConsumed = true
            } else {
                Unit
            }
        }
    }

    private fun observerCaregiverList() {
        viewModel.caregiverList.observe(viewLifecycleOwner) { data ->
            Logger.d(data)
            binding.tvResponse.text = data.data.toString()
        }
    }

}
