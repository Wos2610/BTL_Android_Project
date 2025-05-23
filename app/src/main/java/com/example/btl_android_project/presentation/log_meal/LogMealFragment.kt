package com.example.btl_android_project.presentation.log_meal

import android.os.Bundle
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
import com.example.btl_android_project.MainActivity
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentLogMealBinding
import com.example.btl_android_project.presentation.log_all.LogAllFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogMealFragment : Fragment() {
    private var _binding: FragmentLogMealBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LogMealViewModel by viewModels()

    private var isFromCreateMeal: Boolean = false

    private var mealAdapter: MealItemAdapter? = null

    val onSearchQueryChanged : (String) -> Unit = { query ->
        viewModel.searchMeals(query = query)
    }

    companion object {
        private const val ARG_IS_FROM_CREATE_MEAL = "isFromCreateMeal"

        fun newInstance(isFromCreateMeal: Boolean = false): LogMealFragment {
            val fragment = LogMealFragment()
            val args = Bundle()
            args.putBoolean(ARG_IS_FROM_CREATE_MEAL, isFromCreateMeal)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFromCreateMeal = arguments?.getBoolean(ARG_IS_FROM_CREATE_MEAL, false) ?: false
        viewModel.loadMeals()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupMealListener()

        binding.btnCreateMeal.tvLogItem.text = "Create New Meal"
        binding.btnCreateMeal.cvLogItem.setOnClickListener {
            val action = LogAllFragmentDirections.actionLogAllFragmentToCreateMealFragment()
            findNavController().navigate(action)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.meals.collect { meals ->
                        mealAdapter?.updateData(meals)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvMeals.layoutManager = LinearLayoutManager(requireContext())
        mealAdapter = MealItemAdapter(
            meals = emptyList(),
            onItemClick = { meal ->
                if(isFromCreateMeal){
                    val action = LogAllFragmentDirections.actionLogAllFragmentToLogMealDiaryFragment(
                        mealId = meal.id
                    )
                    findNavController().navigate(action)
                }
                else{
                    val action = LogAllFragmentDirections.actionLogAllFragmentToEditMealFragment(
                        mealId = meal.id,
                    )
                    findNavController().navigate(action)
                }
            },
            onAddToDiaryClick = { meal ->
                if(isFromCreateMeal){

                }
                else{
                    if(viewModel.selectedMealType == null){
                        val selectedMeal = getSelectedMealFromActivity()
                        if(selectedMeal != null || selectedMeal.equals(getString(R.string.select_a_meal))) viewModel.selectedMealType = selectedMeal
                        else Toast.makeText(requireContext(), "Please select a meal type", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        viewModel.addMealToDiary(
                            mealId = meal.id,
                            onSuccess = {
                                Toast.makeText(requireContext(), "Meal added to diary", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

        )
        binding.rvMeals.adapter = mealAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupMealListener() {
        (activity as MainActivity).setOnMealSelectedListener(object : MainActivity.OnMealSelectedListener {
            override fun onMealSelected(meal: String?) {
                if (meal != null && meal != getString(R.string.select_a_meal)) {
                    viewModel.selectedMealType = meal
                } else {
                    viewModel.selectedMealType = null
                }
            }
        })
    }

    private fun getSelectedMealFromActivity(): String? {
        return (activity as MainActivity).getSelectedMeal()
    }
}