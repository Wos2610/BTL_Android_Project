package com.example.btl_android_project.presentation.log_recipe

import android.app.AlertDialog
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
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentEditRecipeBinding
import com.example.btl_android_project.local.entity.RecipeIngredient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditRecipeFragment : Fragment() {

    private var _binding: FragmentEditRecipeBinding? = null
    private val binding get() = _binding!!
    private val args: EditRecipeFragmentArgs by navArgs()
    private lateinit var ingredientAdapter: IngredientAdapter
    private val viewModel: EditRecipeViewModel by viewModels()
    private var isFromCreateMeal: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.recipeName = args.recipeName.toString()
        viewModel.servings = args.servings
        viewModel.setIngredients(args.ingredients.toList())
        viewModel.recipeId = args.recipeId.toString()
        isFromCreateMeal = args.isFromCreateMeal

        if (args.recipeId != "") {
            viewModel.getRecipeById(args.recipeId.toString())
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setSaveButtonToolBarOnClickListener()
        setAddButtonToolBarOnClickListener()
        setDeleteButtonToolBarOnClickListener()

        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<RecipeIngredient>("ingredient")
            ?.observe(viewLifecycleOwner) { ingredient ->
                Log.d("IngredientsFragment", "Received ingredient: $ingredient")
                viewModel.addIngredient(ingredient)
                navController.currentBackStackEntry?.savedStateHandle?.remove<RecipeIngredient>("ingredient")
            }

        binding.etRecipeName.setText(viewModel.recipeName)
        binding.etServings.setText(viewModel.servings.toString())

        binding.etRecipeName.doOnTextChanged { text, _, _, _ ->
            viewModel.recipeName = text.toString()
        }

        binding.etServings.doOnTextChanged { text, _, _, _ ->
            viewModel.servings = text.toString().toIntOrNull() ?: 1
        }

        setItemTouchHelper()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.ingredients.collect { list ->
                        ingredientAdapter.updateData(list)
                        viewModel.calculateTotalNutrition()
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

    private fun setSaveButtonToolBarOnClickListener() {
        if(isFromCreateMeal){

        }else{
            (requireActivity() as? MainActivity)?.setSaveButtonClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Save Recipe")
                    .setMessage("Do you want to add this recipe to your diary as well?")
                    .setPositiveButton("Add to Diary") { _, _ ->
                        viewModel.addRecipeToDiary(
                            onSuccess = {
                                findNavController().popBackStack(R.id.logAllFragment, false)
                            },
                        )
                    }
                    .setNegativeButton("Just Update") { _, _ ->
                        viewModel.updateRecipe {
                            findNavController().popBackStack(R.id.logAllFragment, false)
                        }
                    }
                    .setNeutralButton("Cancel", null)
                    .show()
            }
        }
    }

    private fun setAddButtonToolBarOnClickListener() {
        (requireActivity() as? MainActivity)?.setAddButtonClickListener {
            val action = EditRecipeFragmentDirections.actionEditRecipeFragmentToSearchIngredientFragment()
            findNavController().navigate(action)
        }
    }

    private fun setDeleteButtonToolBarOnClickListener() {
        (requireActivity() as? MainActivity)?.setDeleteButtonClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Recipe")
                .setMessage("Are you sure you want to delete this recipe?")
                .setPositiveButton("Delete") { _, _ ->
//                    viewModel.deleteRecipe(
//                        recipeId = viewModel.recipeId,
//                        navigateToLogAllFragment = {
//                            findNavController().popBackStack(R.id.logAllFragment, false)
//                        },
//                    )
                }
                .setNegativeButton("Cancel", null)
                .show()
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
                    ingredientAdapter.notifyItemChanged(position)
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvIngredients)
    }
}