package com.example.btl_android_project.presentation.log_recipe

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl_android_project.MainActivity
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentDetailRecipeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailRecipeFragment : Fragment() {
    private var _binding: FragmentDetailRecipeBinding? = null
    private val binding get() = _binding!!
    private val args: DetailRecipeFragmentArgs by navArgs()
    private lateinit var ingredientAdapter: IngredientAdapter
    private val viewModel: DetailRecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.recipeName = args.recipeName.toString()
        viewModel.servings = args.servings
        viewModel.setIngredients(args.ingredients.toList())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setSaveButtonToolBarOnClickListener()

        binding.etRecipeName.setText(viewModel.recipeName)
        binding.etServings.setText(viewModel.servings.toString())

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.ingredients.collect { list ->
                        ingredientAdapter.updateData(list)
                        viewModel.calculateTotalNutrition()
                    }
                }

                launch {
                    viewModel.totalCalories.collect { totalCalories ->
                        binding.tvCalories.text = totalCalories.toString()
                    }
                }

                launch {
                    viewModel.totalCarbs.collect { totalCarbs ->
                        binding.tvCarbs.text = totalCarbs.toString()
                        binding.tvCarbsAmount.text = viewModel.carbsAmount.toString() + "%"
                    }
                }

                launch {
                    viewModel.totalProteinFlow.collect { totalProtein ->
                        binding.tvProtein.text = totalProtein.toString()
                        binding.tvProteinAmount.text = viewModel.proteinAmount.toString() + "%"
                    }
                }

                launch {
                    viewModel.totalFatFlow.collect { totalFat ->
                        binding.tvFat.text = totalFat.toString()
                        binding.tvFatAmount.text = viewModel.fatAmount.toString() + "%"
                    }
                }


            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        ingredientAdapter = IngredientAdapter(
            ingredients = emptyList(),
            onItemClick = { ingredient ->

            },
        )

        binding.rvIngredients.apply {
            layoutManager = linearLayoutManager
            adapter = ingredientAdapter
        }
    }

    private fun setSaveButtonToolBarOnClickListener() {
        (requireActivity() as? MainActivity)?.setSaveButtonClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Save Recipe")
                .setMessage("Do you want to add this recipe to your diary as well?")
                .setPositiveButton("Add to Diary") { _, _ ->
                    findNavController().popBackStack(R.id.logAllFragment, false)
                }
                .setNegativeButton("Just Create") { _, _ ->
                    viewModel.insertRecipe {
                        findNavController().popBackStack(R.id.logAllFragment, false)
                    }
                }
                .setNeutralButton("Cancel", null)
                .show()
        }
    }
}