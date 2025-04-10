package com.example.btl_android_project.presentation.log_meal

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.btl_android_project.MainActivity
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentCreateMealBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateMealFragment : Fragment() {
    private var _binding: FragmentCreateMealBinding? = null
    private val binding get() = _binding!!
    companion object {
        fun newInstance() = CreateMealFragment()
    }

    private val viewModel: CreateMealViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateMealBinding.inflate(inflater, container, false)
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