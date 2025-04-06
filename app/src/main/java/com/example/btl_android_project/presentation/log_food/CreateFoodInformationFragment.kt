// Update java\com\example\btl_android_project\presentation\log_food\CreateFoodInformationFragment.kt
package com.example.btl_android_project.presentation.log_food

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.btl_android_project.databinding.FragmentCreateFoodInformationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateFoodInformationFragment : Fragment() {
    private var _binding: FragmentCreateFoodInformationBinding? = null
    private val binding get() = _binding!!

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

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnNext.setOnClickListener {
            if (validateInput()) {
                val name = binding.etBrandName.text.toString().trim()
                val description = binding.etDescription.text.toString().trim()
                val servingsSizeStr = binding.etServingSize.text.toString().trim()
                val servingsUnit = binding.etUnit.text.toString().trim()
                val servingsPerContainerStr = binding.etServingPerContainer.text.toString().trim()
                viewModel.setName(name)
                viewModel.setDescription(description)
                servingsSizeStr.toIntOrNull()?.let { it1 -> viewModel.setServingsSize(it1) }
                viewModel.setServingsUnit(servingsUnit)
                servingsPerContainerStr.toIntOrNull()
                    ?.let { it1 -> viewModel.setServingsPerContainer(it1) }


                if (viewModel.saveBasicFoodInfo()) {
                    val action = CreateFoodInformationFragmentDirections
                        .actionCreateFoodInformationFragmentToCreateFoodNutritionFragment(
                            foodName = viewModel.foodName.value,
                            description = viewModel.description.value,
                            servingsSize = viewModel.servingsSize.value,
                            servingsUnit = viewModel.servingsUnit.value,
                            servingsPerContainer = viewModel.servingsPerContainer.value
                        )
                    findNavController().navigate(action)
                } else {
                    Toast.makeText(
                        context,
                        "Please fill in all required fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun validateInput(): Boolean {
        val name = binding.etBrandName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val servingsSizeStr = binding.etServingSize.text.toString().trim()
        val servingsUnit = binding.etUnit.text.toString().trim()
        val servingsPerContainerStr = binding.etServingPerContainer.text.toString().trim()


        if (name.isEmpty()) {
            binding.etBrandName.error = "Name is required"
            return false
        }
        if (description.isEmpty()) {
            binding.etDescription.error = "Description is required"
            return false
        }
        val servingsSize = servingsSizeStr.toIntOrNull()
        if (servingsSize == null || servingsSize <= 0) {
            binding.etServingSize.error = "Invalid serving size"
            return false
        }
        if (servingsUnit.isEmpty()) {
            binding.etUnit.error = "Unit is required"
            return false
        }
        val servingsPerContainer = servingsPerContainerStr.toIntOrNull()
        if (servingsPerContainer == null || servingsPerContainer <= 0) {
            binding.etServingPerContainer.error = "Invalid servings per container"
            return false
        }

        return true
    }

    private fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isSaved.collectLatest { isSaved ->
                if (isSaved) {
                    Toast.makeText(context, "Food saved successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}