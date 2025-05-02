package com.example.btl_android_project.presentation.log_recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl_android_project.databinding.FragmentLogRecipeBinding
import com.example.btl_android_project.presentation.log_all.LogAllFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogRecipeFragment : Fragment() {
    private var _binding: FragmentLogRecipeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LogRecipeViewModel by viewModels()

    private lateinit var recipeAdapter: RecipeAdapter

    private var isFromCreateMeal: Boolean = false

    val onSearchQueryChanged : (String) -> Unit = { query ->
        viewModel.searchRecipes(query = query)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFromCreateMeal = arguments?.getBoolean(ARG_IS_FROM_CREATE_MEAL, false) ?: false
        Log.d("LogRecipeFragment", "isFromCreateMeal: $isFromCreateMeal")

        viewModel.pullRecipesFromFireStore()
        viewModel.loadRecipes()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCreateRecipe.tvLogItem.text = "Create New Recipe"

        setupRecyclerView()

        binding.btnCreateRecipe.cvLogItem.setOnClickListener {
            val action = LogAllFragmentDirections.actionLogAllFragmentToNewRecipeFragment()
            findNavController().navigate(action)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.recipes.collect { list ->
                        recipeAdapter.updateData(list)
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
        recipeAdapter = RecipeAdapter(
            recipes = emptyList(),
            onItemClick = {recipe ->
                if(isFromCreateMeal){
                    val action = LogAllFragmentDirections.actionLogAllFragmentToLogRecipeDiaryFragment(
                        recipeId = recipe.id,
                        isFromCreateMeal = isFromCreateMeal
                    )
                    findNavController().navigate(action)
                }
                else{
                    val action = LogAllFragmentDirections.actionLogAllFragmentToEditRecipeFragment(
                        recipeId = recipe.id,
                        recipeName = recipe.name,
                        servings = recipe.servings,
                        ingredients = recipe.ingredients.toTypedArray(),
                        isFromCreateMeal = isFromCreateMeal
                    )
                    findNavController().navigate(action)
                }

            },
            onAddToDiaryClick = { recipe ->
                if(isFromCreateMeal) {

                }else{
                    viewModel.addRecipeToDiary(
                        recipeId = recipe.id,
                        onSuccess = {
                            Toast.makeText(requireContext(), "Recipe added to diary", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        )

        binding.rvRecipes.apply {
            layoutManager = linearLayoutManager
            adapter = recipeAdapter
        }
    }


    companion object {
        private const val ARG_IS_FROM_CREATE_MEAL = "isFromCreateMeal"

        fun newInstance(isFromCreateMeal: Boolean = false): LogRecipeFragment {
            val fragment = LogRecipeFragment()
            val args = Bundle()
            args.putBoolean(ARG_IS_FROM_CREATE_MEAL, isFromCreateMeal)
            fragment.arguments = args
            return fragment
        }
    }
}