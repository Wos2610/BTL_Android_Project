package com.example.btl_android_project.presentation.log_water

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentLogWaterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogWaterFragment : Fragment() {
    private var _binding: FragmentLogWaterBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = LogWaterFragment()
    }

    private val viewModel: LogWaterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogWaterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}