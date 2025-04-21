package com.example.btl_android_project.presentation.log_recipe

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.MainActivity
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentLogRecipeDiaryBinding
import com.example.btl_android_project.local.entity.RecipeIngredient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogRecipeDiaryFragment : Fragment() {
    private var _binding: FragmentLogRecipeDiaryBinding? = null
    private val binding get() = _binding!!
    private val args: LogRecipeDiaryFragmentArgs by navArgs()
    private lateinit var ingredientAdapter: IngredientAdapter
    private val viewModel: LogRecipeDiaryViewModel by viewModels()
    private var isFromCreateMeal: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFromCreateMeal = args.isFromCreateMeal

        if (args.recipeId != 0) {
            viewModel.getRecipeById(args.recipeId)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogRecipeDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setUpSpinner()
        setSaveButtonToolBarOnClickListener()
        binding.etServings.setText(viewModel.servings.toString())

        binding.etServings.doOnTextChanged { text, _, _, _ ->
            viewModel.servings = text.toString().toIntOrNull() ?: 1
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.recipe.collect { recipe ->
                        recipe?.let {
                            binding.tvTitle.text = it.name
                            binding.etServings.setText(it.servings.toString())
                            ingredientAdapter.updateData(recipe.ingredients)
                        }
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
            ingredients = mutableListOf(),
            onItemClick = { ingredient ->

            },
        )

        binding.rvIngredients.apply {
            layoutManager = linearLayoutManager
            adapter = ingredientAdapter
        }
    }

    private fun setUpSpinner() {
//        val mealOptions = listOf(
//            R.string.select_a_meal,
//            R.string.breakfast,
//            R.string.lunch,
//            R.string.dinner,
//            R.string.snack
//        ).map { getString(it) }
//
//        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, mealOptions)
//        binding.spinnerMeal.adapter = adapter
//
//        binding.spinnerMeal.setSelection(0)
//
//        binding.spinnerMeal.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View,
//                position: Int,
//                id: Long
//            ) {
//                val selectedMeal = mealOptions[position]
//                when (selectedMeal) {
//                    getString(R.string.breakfast) -> viewModel.mealType = "Breakfast"
//                    getString(R.string.lunch) -> viewModel.mealType = "Lunch"
//                    getString(R.string.dinner) -> viewModel.mealType = "Dinner"
//                    getString(R.string.snack) -> viewModel.mealType = "Snack"
//                    else -> viewModel.mealType = ""
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {}
//        })
    }

    private fun setSaveButtonToolBarOnClickListener() {
        if(isFromCreateMeal){
            (requireActivity() as? MainActivity)?.setSaveButtonClickListener {
                val sendRecipe = viewModel.sendRecipe()
                findNavController().previousBackStackEntry?.savedStateHandle?.set("recipe", sendRecipe)
                findNavController().popBackStack()
            }

        }else{
            (requireActivity() as? MainActivity)?.setSaveButtonClickListener {

            }
        }
    }

}