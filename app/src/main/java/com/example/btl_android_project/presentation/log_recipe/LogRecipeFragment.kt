package com.example.btl_android_project.presentation.log_recipe

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl_android_project.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                val action = LogAllFragmentDirections.actionLogAllFragmentToDetailRecipeFragment(
                    recipeId = recipe.id,
                    recipeName = recipe.name,
                    servings = recipe.servings,
                    ingredients = recipe.ingredients.toTypedArray()
                )
                findNavController().navigate(action)
            }
        )

        binding.rvRecipes.apply {
            layoutManager = linearLayoutManager
            adapter = recipeAdapter
        }
    }

    companion object{
        fun newInstance() = LogRecipeFragment()
    }
}