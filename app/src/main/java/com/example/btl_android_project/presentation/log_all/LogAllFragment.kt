package com.example.btl_android_project.presentation.log_all

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentLogAllBinding
import com.example.btl_android_project.local.entity.Food
import com.example.btl_android_project.local.entity.MealItem
import com.example.btl_android_project.local.entity.Recipe
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import androidx.viewpager2.widget.ViewPager2
import com.example.btl_android_project.presentation.log_food.LogFoodFragment
import com.example.btl_android_project.presentation.log_meal.LogMealFragment
import com.example.btl_android_project.presentation.log_recipe.LogRecipeFragment

@AndroidEntryPoint
class LogAllFragment : Fragment() {
    private var _binding: FragmentLogAllBinding? = null
    private val binding get() = _binding!!
    private lateinit var pagerAdapter: LogPagerAdapter

    private val viewModel: LogAllViewModel by viewModels()
    private val args: LogAllFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isFromCreateMeal = args.isFromCreateMeal
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideBottomNavigationView()
        setUpViewPager()
        setUpSearchView()

        val navController = findNavController()
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Recipe>("recipe")
            ?.observe(viewLifecycleOwner) { recipe ->
                Log.d("LogAllFragment", "Received recipe: $recipe")
                findNavController().previousBackStackEntry?.savedStateHandle?.set("recipe", recipe)
                navController.currentBackStackEntry?.savedStateHandle?.remove<Recipe>("recipe")
                findNavController().popBackStack()
            }

        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Food>("food")
            ?.observe(viewLifecycleOwner) { food ->
                Log.d("LogAllFragment", "Received food: $food")
                findNavController().previousBackStackEntry?.savedStateHandle?.set("food", food)
                navController.currentBackStackEntry?.savedStateHandle?.remove<Food>("food")
                findNavController().popBackStack()
            }

        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<List<MealItem>>("mealItems")
            ?.observe(viewLifecycleOwner) { mealItems ->
                Log.d("LogAllFragment", "Received meal items: $mealItems")
                findNavController().previousBackStackEntry?.savedStateHandle?.set("mealItems", mealItems)
                navController.currentBackStackEntry?.savedStateHandle?.remove<List<MealItem>>("mealItems")
                findNavController().popBackStack()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideBottomNavigationView() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.navigation)
        bottomNavigationView.visibility = View.GONE
    }

    private fun setUpViewPager() {
        pagerAdapter = LogPagerAdapter(
            this,
            isFromCreateMealList = listOf(viewModel.isFromCreateMeal, viewModel.isFromCreateMeal, viewModel.isFromCreateMeal),
        )

        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position){
                0 -> getString(R.string.my_meals)
                1 -> getString(R.string.my_recipes)
                2 -> getString(R.string.my_foods)
                else -> getString(R.string.my_meals)
            }
        }.attach()
    }

    private fun setUpSearchView() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString() ?: ""
                val currentPosition = binding.viewPager.currentItem
                val currentFragment = pagerAdapter.getFragmentAtPosition(currentPosition)
                if (currentFragment is LogMealFragment) {
                    currentFragment.onSearchQueryChanged(query)
                } else if (currentFragment is LogRecipeFragment) {
                    currentFragment.onSearchQueryChanged(query)
                } else if (currentFragment is LogFoodFragment) {
                    currentFragment.onSearchQueryChanged(query)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}