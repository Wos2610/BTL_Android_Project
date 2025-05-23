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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.MainActivity
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentIngredientsBinding
import com.example.btl_android_project.local.entity.Recipe
import com.example.btl_android_project.local.entity.RecipeIngredient
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

        setItemTouchHelper()

        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<RecipeIngredient>("ingredient")
            ?.observe(viewLifecycleOwner) { ingredient ->
                Log.d("IngredientsFragment", "Received ingredient: $ingredient")
                viewModel.addIngredient(ingredient)
                navController.currentBackStackEntry?.savedStateHandle?.remove<RecipeIngredient>("ingredient")
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
            mutableListOf(),
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
                val ingredientToRemove = viewModel.ingredients.value?.getOrNull(position)

                if (ingredientToRemove != null) {
                    viewModel.removeIngredient(ingredientToRemove)
                } else {
                    searchAdapter.notifyItemChanged(position)
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.ingredientsRecyclerView)
    }
}