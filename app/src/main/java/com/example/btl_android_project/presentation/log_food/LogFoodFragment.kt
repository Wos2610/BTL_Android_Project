package com.example.btl_android_project.presentation.log_food

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentLogFoodBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogFoodFragment : Fragment() {
    private var _binding: FragmentLogFoodBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = LogFoodFragment()
    }

    private val viewModel: LogFoodViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFoods.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = FoodAdapter(viewModel.foods)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}