package com.example.btl_android_project.presentation.log_meal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.btl_android_project.databinding.FragmentCreateMealBinding
import com.example.btl_android_project.local.entity.Food
import com.example.btl_android_project.local.entity.Recipe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateMealFragment : Fragment() {
    private var _binding: FragmentCreateMealBinding? = null
    private val binding get() = _binding!!
    var mealAdapter: MealAdapter? = null

    companion object {
        fun newInstance() = CreateMealFragment()
    }

    private val viewModel: CreateMealViewModel by viewModels()
    private val args : CreateMealFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAddButtonToolBarOnClickListener()
        setSaveButtonToolBarOnClickListener()
        setupRecyclerView()
        setItemTouchHelper()

        binding.etMealName.doOnTextChanged {
            text, _, _, _ ->
            viewModel.mealName = text.toString()
        }

        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Recipe>("recipe")
            ?.observe(viewLifecycleOwner) { recipe ->
                Log.d("CreateMealFragment", "Received recipe: $recipe")
                val mealItem = MealItem.RecipeItem(recipe = recipe)
                viewModel.addMealItem(mealItem)
                navController.currentBackStackEntry?.savedStateHandle?.remove<Recipe>("recipe")
            }

        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Food>("food")
            ?.observe(viewLifecycleOwner) { food ->
                Log.d("CreateMealFragment", "Received food: $food")
                val mealItem = MealItem.FoodItem(food = food)
                viewModel.addMealItem(mealItem)
                navController.currentBackStackEntry?.savedStateHandle?.remove<Food>("food")
            }

        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<List<MealItem>>("mealItems")
            ?.observe(viewLifecycleOwner) { mealItems ->
                Log.d("LogAllFragment", "Received meal items: $mealItems")
                mealItems.forEach { mealItem ->
                    viewModel.addMealItem(mealItem)
                }
                navController.currentBackStackEntry?.savedStateHandle?.remove<List<MealItem>>("mealItems")
            }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.mealItems.collect { mealItems ->
                        Log.d("CreateMealFragment", "Received meal items: $mealItems")
                        if (mealItems.isEmpty()) binding.tvMealItems.visibility = View.INVISIBLE
                        else binding.tvMealItems.visibility = View.VISIBLE
                        mealAdapter?.updateData(mealItems)
                        viewModel.calculateTotalNutrition()
                    }
                }

                launch {
                    viewModel.meal.collect { meal ->
                        meal?.let {
                            binding.etMealName.setText(it.name)
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
        binding.rvMealItems.layoutManager = LinearLayoutManager(requireContext())
        mealAdapter = MealAdapter(mutableListOf<MealItem>())
        binding.rvMealItems.adapter = mealAdapter
    }

    private fun setAddButtonToolBarOnClickListener() {
        (activity as MainActivity).setAddButtonClickListener {
            val action = CreateMealFragmentDirections
                .actionCreateMealFragmentToLogAllFragment(isFromCreateMeal = true)

            findNavController().navigate(action)
        }
    }

    private fun setSaveButtonToolBarOnClickListener() {
        (activity as MainActivity).setSaveButtonClickListener {
            viewModel.saveMeal(
                onSaveSuccess = {
                    findNavController().popBackStack()
                }
            )
        }
    }

    private fun setItemTouchHelper() {
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val mealItemToRemove = viewModel.mealItems.value.getOrNull(position)

                if (mealItemToRemove != null) {
                    viewModel.removeMealItem(mealItemToRemove)
                } else {
                    mealAdapter?.notifyItemChanged(position)
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvMealItems)
    }

}