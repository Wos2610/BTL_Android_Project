package com.example.btl_android_project.presentation.log_meal

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.MainActivity
import com.example.btl_android_project.databinding.FragmentLogMealDiaryBinding
import com.example.btl_android_project.local.entity.Food
import com.example.btl_android_project.local.entity.Recipe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogMealDiaryFragment : Fragment() {
    private var _binding: FragmentLogMealDiaryBinding? = null
    private val binding get() = _binding!!
    var mealAdapter : MealAdapter? = null
    private val viewModel: LogMealDiaryViewModel by viewModels()
    private val args : LogMealDiaryFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getMealById(args.mealId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogMealDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSaveButtonToolBarOnClickListener()
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.meal.collect { meal ->
                        meal?.let {
                            binding.tvMealName.text = it.name
                        }
                    }
                }

                launch {
                    viewModel.mealItems.collect { mealItems ->
                        mealAdapter?.updateData(mealItems)
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
        binding.rvMealItems.layoutManager = LinearLayoutManager(requireContext())
        mealAdapter = MealAdapter(mutableListOf<MealItem>())
        binding.rvMealItems.adapter = mealAdapter
    }


    private fun setSaveButtonToolBarOnClickListener() {
        (activity as MainActivity).setSaveButtonClickListener {
            val numberOfServings = binding.etServings.text.toString().trim()

            if (numberOfServings.isEmpty()) {
                binding.etServings.error = "Please enter number of servings"
                return@setSaveButtonClickListener
            }


            val servingsValid = numberOfServings.toIntOrNull()

            if (servingsValid == null) {
                binding.etServings.error = "Enter a valid number"
                return@setSaveButtonClickListener
            }

            val updatedMealItems = viewModel.mealItems.value.map { mealItem ->
                when (mealItem) {
                    is MealItem.FoodItem -> {
                        mealItem.copy(food = mealItem.food.copy(servings = mealItem.food.servings * servingsValid))
                    }
                    is MealItem.RecipeItem -> {

                        mealItem.copy(recipe = mealItem.recipe.copy(servings = mealItem.recipe.servings * servingsValid))
                    }
                }
            }

            val controller = findNavController()
            controller.previousBackStackEntry?.savedStateHandle?.set(
                "mealItems",
                updatedMealItems
            )

            controller.popBackStack()
        }
    }

}