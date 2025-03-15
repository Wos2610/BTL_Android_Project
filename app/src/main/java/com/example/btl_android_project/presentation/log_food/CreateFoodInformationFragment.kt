package com.example.btl_android_project.presentation.log_food

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentCreateFoodInformationBinding

class CreateFoodInformationFragment : Fragment() {
    private var _binding: FragmentCreateFoodInformationBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = CreateFoodInformationFragment()
    }

    private val viewModel: CreateFoodInformationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateFoodInformationBinding.inflate(inflater, container, false)
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