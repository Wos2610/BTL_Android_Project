package com.example.btl_android_project.presentation.log_recipe

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
import com.example.btl_android_project.databinding.FragmentSearchIngredientBinding
import com.example.btl_android_project.local.entity.RecipeIngredient
import com.example.btl_android_project.local.entity.StaticRecipeIngredient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchIngredientFragment : Fragment() {
    private var _binding: FragmentSearchIngredientBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchAdapter: StaticIngredientAdapter

    companion object {
        fun newInstance() = SearchIngredientFragment()
    }

    private val viewModel: SearchIngredientViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchIngredientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<RecipeIngredient>("ingredient")
            ?.observe(viewLifecycleOwner) { ingredient ->
                Log.d("SearchIngredientFragment", "Received ingredient: $ingredient")
                val controller = findNavController()
                controller.previousBackStackEntry?.savedStateHandle?.set(
                    "ingredient",
                    ingredient
                )
                controller.popBackStack()
            }

        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.setSearchQuery(text.toString())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ingredientSearchResults.collect {
                    searchAdapter.updateData(it)
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
        searchAdapter = StaticIngredientAdapter(
            emptyList(),
            onItemClick = { ingredient ->
                val action = SearchIngredientFragmentDirections.actionSearchIngredientFragmentToDetailIngredientFragment(ingredient.fdcId)
                findNavController().navigate(action)
            }
        )

        binding.rvResults.apply {
            layoutManager = linearLayoutManager
            adapter = searchAdapter
        }
    }
}