package com.example.btl_android_project.presentation.log_meal

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
        viewModel.loadMealFoodCrossRef()
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
                val action = LogAllFragmentDirections.actionLogAllFragmentToLogMealDiaryFragment(
                    mealId = meal.id
                )
                findNavController().navigate(action)
            }

        )
        binding.rvMeals.adapter = mealAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}