package com.example.btl_android_project.presentation.log_recipe

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.btl_android_project.MainActivity
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentDetailIngredientBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class DetailIngredientFragment : Fragment() {
    private var _binding: FragmentDetailIngredientBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = DetailIngredientFragment()
    }

    private val viewModel: DetailIngredientViewModel by viewModels()
    private val args: DetailIngredientFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.setIngredientById(args.ingredientId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailIngredientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSaveButtonToolBarOnClickListener()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.ingredient.collect { ingredient ->
                val calories =
                    ingredient?.foodNutrients?.firstOrNull { it.name.contains("Energy") }?.amount
                val protein =
                    ingredient?.foodNutrients?.firstOrNull { it.name.contains("Protein") }?.amount?.toInt()
                        ?: 0
                val fat =
                    ingredient?.foodNutrients?.firstOrNull { it.name.contains("fat") }?.amount?.toInt()
                        ?: 0
                val carbs =
                    ingredient?.foodNutrients?.firstOrNull { it.name.contains("Carbohydrate") }?.amount?.toInt()
                        ?: 0
                val sum = protein + fat + carbs
                if (sum == 0) return@collect

                val proteinPercentage = (protein.toDouble() / sum) * 100
                val fatPercentage = (fat.toDouble() / sum) * 100
                val carbsPercentage = (carbs.toDouble() / sum) * 100

                binding.apply {
                    tvRecipeIngredientName.text = ingredient?.description
                    tvNumberOfServings.text = "1"
                    tvServingSize.text = "100g"

                    tvCalories.text = "${calories?.toInt() ?: 0}"
                    tvProteinAmount.text = "$protein g"
                    tvFatAmount.text = "$fat g"
                    tvCarbsAmount.text = "$carbs g"

                    tvProteinPercentage.text = "${proteinPercentage.roundToInt()}%"
                    tvFatPercentage.text = "${fatPercentage.roundToInt()}%"
                    tvCarbsPercentage.text = "${carbsPercentage.roundToInt()}%"
                }
            }
        }

//        binding.btnAddToRecipe.setOnClickListener {
//            val action = DetailIngredientFragmentDirections.actionDetailIngredientFragmentToIngredientsFragment()
//            findNavController().navigate(action)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    private fun setSaveButtonToolBarOnClickListener() {
        (requireActivity() as? MainActivity)?.setSaveButtonClickListener {
            val controller = findNavController()
            controller.previousBackStackEntry?.savedStateHandle?.set(
                "ingredient",
                viewModel.ingredient.value
            )
            controller.popBackStack()

        }
    }
}