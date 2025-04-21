package com.example.btl_android_project.presentation.log_meal

import android.health.connect.datatypes.MealType
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl_android_project.MainActivity
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentCreateMealBinding
import com.example.btl_android_project.local.entity.Recipe
import com.example.btl_android_project.local.entity.RecipeIngredient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateMealFragment : Fragment() {
    private var _binding: FragmentCreateMealBinding? = null
    private val binding get() = _binding!!
    var mealAdapter : MealAdapter? = null
    companion object {
        fun newInstance() = CreateMealFragment()
    }

    private val viewModel: CreateMealViewModel by viewModels()

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

        binding.etMealName.setText(viewModel.mealName)

        binding.etMealName.doOnTextChanged { text, _, _, _ ->
            viewModel.mealName = text.toString()
        }

        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Recipe>("recipe")
            ?.observe(viewLifecycleOwner) { recipe ->
                Log.d("CreateMealFragment", "Received recipe: $recipe")
                val mealItem  = MealItem.RecipeItem(recipe = recipe)
                viewModel.addMealItem(mealItem)
                navController.currentBackStackEntry?.savedStateHandle?.remove<Recipe>("recipe")
            }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mealItems.collect { mealItems ->
                    Log.d("CreateMealFragment", "Received meal items: $mealItems")
                    mealAdapter?.updateData(mealItems)
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
        val testItems: MutableList<MealItem> = mutableListOf(
            MealItem.RecipeItem(Recipe(id = 1, name = "Test Recipe 1")),
            MealItem.RecipeItem(Recipe(id = 2, name = "Test Recipe 2"))
        )

        mealAdapter = MealAdapter(testItems)
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

}