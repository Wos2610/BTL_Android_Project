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
import com.example.btl_android_project.MainActivity
import com.example.btl_android_project.databinding.FragmentCreateFoodInformationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateFoodInformationFragment : Fragment() {
    private var _binding: FragmentCreateFoodInformationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreateFoodInformationViewModel by viewModels()
    private var foodId = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateFoodInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        foodId = arguments?.getInt("foodId")!!
        if (foodId != -1) {
            viewModel.loadFood(foodId)
        }
        observeViewModel()
        setupListeners()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.food.collectLatest { food ->
                food?.let {
                    binding.etBrandName.setText(it.name)
                    binding.etDescription.setText(it.description)
                    binding.etServingSize.setText(it.servingsSize.toString())
                    binding.etUnit.setText(it.servingsUnit)
                    binding.etServingPerContainer.setText(it.servingsPerContainer.toString())
                }
            }
        }
    }

    private fun setupListeners() {
        setEndTextClickListener()
    }

    private fun validateInput(): Boolean {
        val name = binding.etBrandName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val servingsSizeStr = binding.etServingSize.text.toString().trim()
        val servingsUnit = binding.etUnit.text.toString().trim()
        val servingsPerContainerStr = binding.etServingPerContainer.text.toString().trim()

        if (name.isEmpty() || description.isEmpty() || servingsUnit.isEmpty()) return false
        val servingsSize = servingsSizeStr.toIntOrNull() ?: return false
        val servingsPerContainer = servingsPerContainerStr.toIntOrNull() ?: return false
        if (servingsSize <= 0 || servingsPerContainer <= 0) return false
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setEndTextClickListener() {
        (requireActivity() as? MainActivity)?.setEndTextClickListener {
            if (validateInput()) {
                val name = binding.etBrandName.text.toString().trim()
                val description = binding.etDescription.text.toString().trim()
                val servingsSize = binding.etServingSize.text.toString().toIntOrNull() ?: 0
                val servingsUnit = binding.etUnit.text.toString().trim()
                val servingsPerContainer = binding.etServingPerContainer.text.toString().toIntOrNull() ?: 0

                if (foodId != -1) {
                    val action =
                        CreateFoodInformationFragmentDirections.actionCreateFoodInformationFragmentToCreateFoodNutritionFragment(
                            foodId = foodId,
                            foodName = name,
                            description = description,
                            servingsSize = servingsSize,
                            servingsUnit = servingsUnit,
                            servingsPerContainer = servingsPerContainer
                        )
                    findNavController().navigate(action)
                } else {
                    val action =
                        CreateFoodInformationFragmentDirections.actionCreateFoodInformationFragmentToCreateFoodNutritionFragment(
                            foodName = name,
                            description = description,
                            servingsSize = servingsSize,
                            servingsUnit = servingsUnit,
                            servingsPerContainer = servingsPerContainer
                        )
                    findNavController().navigate(action)
                }
            } else {
                Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}