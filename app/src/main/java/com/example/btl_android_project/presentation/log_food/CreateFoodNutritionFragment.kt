package com.example.btl_android_project.presentation.log_food

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl_android_project.MainActivity
import com.example.btl_android_project.databinding.FragmentCreateFoodNutritionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateFoodNutritionFragment : Fragment() {
    private var _binding: FragmentCreateFoodNutritionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateFoodNutritionViewModel by viewModels()
    private lateinit var nutritionAdapter: NutritionAdapter

    private var foodId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateFoodNutritionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.foodName = arguments?.let {
            CreateFoodNutritionFragmentArgs.fromBundle(it).foodName
        } ?: ""

        val args = CreateFoodNutritionFragmentArgs.fromBundle(requireArguments())

        viewModel.foodName = args.foodName
        viewModel.description = args.description
        viewModel.servingsSize = args.servingsSize
        viewModel.servingsUnit = args.servingsUnit
        viewModel.servingsPerContainer = args.servingsPerContainer


        foodId = arguments?.getString("foodId") ?: ""
        if (foodId != "") {
            viewModel.loadNutrition(foodId)
        }

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewNutritions.layoutManager = LinearLayoutManager(context)
        nutritionAdapter = NutritionAdapter(viewModel.nutritions.value) { index, amount ->
            viewModel.updateNutrition(index, amount)
        }

        binding.recyclerViewNutritions.adapter = nutritionAdapter
    }


    private fun setupListeners() {
        setSaveButtonToolBarOnClickListener()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isSaved.collectLatest { isSaved ->
                if (isSaved) {
                    Toast.makeText(context, "Food saved successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                    findNavController().navigateUp() // quay lai man log all
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.nutritionLoaded.collect {
                binding.recyclerViewNutritions.post {
                    nutritionAdapter.updateList(viewModel.nutritions.value)
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.nutritions.collect { nutritions ->

                val isValid = nutritions.all { it.amount >= 0 }

                if (!isValid) {
                    Toast.makeText(
                        context,
                        "Nutrition values must be non-negative",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setSaveButtonToolBarOnClickListener() {
        (requireActivity() as? MainActivity)?.setSaveButtonClickListener {
            if (foodId != "") {
                viewModel.updateFood(foodId)
            } else {
                viewModel.saveFood()
            }
        }
    }
}
