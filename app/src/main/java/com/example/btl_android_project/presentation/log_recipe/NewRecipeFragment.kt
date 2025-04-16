package com.example.btl_android_project.presentation.log_recipe

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.btl_android_project.MainActivity
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentNewRecipeBinding
import com.example.btl_android_project.local.entity.StaticRecipeIngredient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewRecipeFragment : Fragment() {
    private var _binding: FragmentNewRecipeBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = NewRecipeFragment()
    }

    private val viewModel: NewRecipeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNextTextToolBarOnClickListener()

        binding.etRecipeName.doOnTextChanged {
            text, _, _, _ ->
            viewModel.recipeName = text.toString()
        }

        binding.etServings.doOnTextChanged {
            text, _, _, _ ->
            viewModel.servings = text.toString().toIntOrNull() ?: 1
        }

//        val navController = findNavController()
//        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<List<StaticRecipeIngredient>>("ingredients")
//            ?.observe(viewLifecycleOwner) { ingredients ->
//                navController.currentBackStackEntry?.savedStateHandle?.remove<List<StaticRecipeIngredient>>("ingredients")
//                Log.d("New Recipe Fragment", "Received ingredients: ${ingredients.size}")
//                val action = NewRecipeFragmentDirections.actionNewRecipeFragmentToDetailRecipeFragment(
//                    recipeName = viewModel.recipeName,
//                    servings = viewModel.servings,
//                    ingredients = ingredients.toTypedArray()
//                )
//                navController.navigate(action)
//            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setNextTextToolBarOnClickListener() {
        (requireActivity() as? MainActivity)?.setEndTextClickListener {
            val action = NewRecipeFragmentDirections.actionNewRecipeFragmentToIngredientsFragment(
                recipeName = viewModel.recipeName,
                servings = viewModel.servings
            )
            findNavController().navigate(action)
        }
    }
}