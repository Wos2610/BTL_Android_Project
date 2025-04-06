package com.example.btl_android_project.presentation.log_food

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentLogFoodBinding
import com.example.btl_android_project.local.entity.Food
import com.example.btl_android_project.presentation.log_all.LogAllFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogFoodFragment : Fragment() {
    private var _binding: FragmentLogFoodBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LogFoodViewModel by viewModels()
    private lateinit var foodAdapter: FoodAdapter

    companion object {
        fun newInstance() = LogFoodFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupListeners()
    }

    private fun setupRecyclerView() {
        foodAdapter = FoodAdapter(emptyList())
        binding.rvFoods.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = foodAdapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.foods.collectLatest { foods ->
                foodAdapter = FoodAdapter(foods)
                binding.rvFoods.adapter = foodAdapter
            }
        }
    }

    private fun setupListeners() {
        binding.btnCreateAFood.cvLogItem.setOnClickListener {
            val action =
                LogAllFragmentDirections.actionLogAllFragmentToCreateFoodInformationFragment();
            findNavController().navigate(action)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}