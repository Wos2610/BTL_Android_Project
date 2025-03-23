package com.example.btl_android_project.presentation.log_recipe

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentReviewIngredientsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewIngredientsFragment : Fragment() {
    private var _binding: FragmentReviewIngredientsBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = ReviewIngredientsFragment()
    }

    private val viewModel: ReviewIngredientsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewIngredientsBinding.inflate(inflater, container, false)
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