package com.example.btl_android_project.presentation.log_food

import android.os.Bundle
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
import com.example.btl_android_project.MainActivity
import com.example.btl_android_project.databinding.FragmentLogFoodDiaryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogFoodDiaryFragment : Fragment() {
    private var _binding: FragmentLogFoodDiaryBinding? = null
    private val binding get() = _binding!!
    private val args: LogFoodDiaryFragmentArgs by navArgs()
    private val viewModel: LogFoodDiaryViewModel by viewModels()
    private var isFromCreateMeal: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFromCreateMeal = args.isFromCreateMeal

        if (args.foodId != null && args.foodId != "" ) {
            viewModel.getFoodById(args.foodId!!)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogFoodDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSpinner()
        setSaveButtonToolBarOnClickListener()
        binding.etServings.setText(viewModel.servings.toString())

        binding.etServings.doOnTextChanged { text, _, _, _ ->
            viewModel.servings = text.toString().toIntOrNull() ?: 1
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.food.collect { food ->
                        food?.let {
                            binding.tvTitle.text = it.name
                            binding.etServings.setText(it.servings.toString())
                            binding.tvCalories.text = it.calories.toString()
                            binding.tvCarbs.text = it.carbs.toString()
                            binding.tvProtein.text = it.protein.toString()
                            binding.tvFat.text = it.fat.toString()
                            binding.tvCarbsAmount.text = viewModel.carbsAmount.toString() + "%"
                            binding.tvProteinAmount.text = viewModel.proteinAmount.toString() + "%"
                            binding.tvFatAmount.text = viewModel.fatAmount.toString() + "%"
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

    private fun setUpSpinner() {
//        val mealOptions = listOf(
//            R.string.select_a_meal,
//            R.string.breakfast,
//            R.string.lunch,
//            R.string.dinner,
//            R.string.snack
//        ).map { getString(it) }
//
//        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, mealOptions)
//        binding.spinnerMeal.adapter = adapter
//
//        binding.spinnerMeal.setSelection(0)
//
//        binding.spinnerMeal.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View,
//                position: Int,
//                id: Long
//            ) {
//                val selectedMeal = mealOptions[position]
//                when (selectedMeal) {
//                    getString(R.string.breakfast) -> viewModel.mealType = "Breakfast"
//                    getString(R.string.lunch) -> viewModel.mealType = "Lunch"
//                    getString(R.string.dinner) -> viewModel.mealType = "Dinner"
//                    getString(R.string.snack) -> viewModel.mealType = "Snack"
//                    else -> viewModel.mealType = ""
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {}
//        })
    }

    private fun setSaveButtonToolBarOnClickListener() {
        if(isFromCreateMeal){
            (requireActivity() as? MainActivity)?.setSaveButtonClickListener {
                val sendFood = viewModel.sendFood()
                findNavController().previousBackStackEntry?.savedStateHandle?.set("food", sendFood)
                findNavController().popBackStack()
            }

        }else{
            (requireActivity() as? MainActivity)?.setSaveButtonClickListener {

            }
        }
    }

}