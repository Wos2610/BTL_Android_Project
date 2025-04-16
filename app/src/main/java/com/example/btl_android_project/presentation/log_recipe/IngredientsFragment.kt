package com.example.btl_android_project.presentation.log_recipe

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
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
import com.example.btl_android_project.databinding.FragmentIngredientsBinding
import com.example.btl_android_project.local.entity.Recipe
import com.example.btl_android_project.local.entity.StaticRecipeIngredient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IngredientsFragment : Fragment() {
    private var _binding: FragmentIngredientsBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchAdapter: IngredientAdapter

    private val viewModel: IngredientsViewModel by viewModels()
    private val args: IngredientsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.recipeName = args.recipeName.toString()
        viewModel.servings = args.servings
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setAddButtonToolBarOnClickListener()
        setNextTextToolBarOnClickListener()

        binding.btnAddIngredients.setOnClickListener {
            val action = IngredientsFragmentDirections.actionIngredientsFragmentToSearchIngredientFragment()
            findNavController().navigate(action)
        }

        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<StaticRecipeIngredient>("ingredient")
            ?.observe(viewLifecycleOwner) { recipe ->
                Log.d("IngredientsFragment", "Received ingredient: $recipe")
                viewModel.addIngredient(recipe)
                navController.currentBackStackEntry?.savedStateHandle?.remove<StaticRecipeIngredient>("ingredient")
            }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.ingredients.collect { list ->
                        if (list.isEmpty()) {
                            binding.emptyStateContainer.visibility = View.VISIBLE
                            binding.ingredientsContainer.visibility = View.GONE
                        } else {
                            binding.emptyStateContainer.visibility = View.GONE
                            binding.ingredientsContainer.visibility = View.VISIBLE
                        }

                        binding.ingredientsRecyclerView.visibility = View.VISIBLE
                        searchAdapter.updateData(list)
                        viewModel.calculateTotalCalories()
                    }
                }

                launch {
                    viewModel.totalCalories.collect { totalCalories ->
                        if (totalCalories > 0) {
                            binding.tvTotalCalories.text = getString(R.string.total_calories, totalCalories)
                        }
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
        searchAdapter = IngredientAdapter(
            emptyList(),
            onItemClick = { ingredient ->

            }
        )

        binding.ingredientsRecyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = searchAdapter
        }
    }

    private fun setAddButtonToolBarOnClickListener() {
        (requireActivity() as? MainActivity)?.setAddButtonClickListener {
            val action = IngredientsFragmentDirections.actionIngredientsFragmentToSearchIngredientFragment()
            findNavController().navigate(action)
        }
    }

    private fun setNextTextToolBarOnClickListener() {
//        (requireActivity() as? MainActivity)?.setEndTextClickListener {
//            val controller = findNavController()
//            controller.previousBackStackEntry?.savedStateHandle?.set(
//                "ingredients",
//                viewModel.ingredients.value
//            )
//            controller.popBackStack()
//        }
        (requireActivity() as? MainActivity)?.setEndTextClickListener {
            val action = IngredientsFragmentDirections.actionIngredientsFragmentToDetailRecipeFragment(
                recipeName = viewModel.recipeName,
                servings = viewModel.servings,
                ingredients = viewModel.ingredients.value.toTypedArray()
            )
            findNavController().navigate(action)
        }
    }
}