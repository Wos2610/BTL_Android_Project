package com.example.btl_android_project.presentation.log_all

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.btl_android_project.presentation.log_food.LogFoodFragment
import com.example.btl_android_project.presentation.log_meal.LogMealFragment
import com.example.btl_android_project.presentation.log_recipe.LogRecipeFragment

class LogPagerAdapter(
    fragment: Fragment,
    private val isFromCreateMealList: List<Boolean>,
) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        val isFromCreateMeal = isFromCreateMealList.getOrNull(position) == true
        return when(position) {
            0 -> LogMealFragment.newInstance(isFromCreateMeal)
            1 -> LogRecipeFragment.newInstance(isFromCreateMeal)
            2 -> LogFoodFragment.newInstance(isFromCreateMeal)
            else -> LogMealFragment.newInstance()
        }
    }

    override fun getItemCount(): Int = NUM_PAGES

    companion object {
        const val NUM_PAGES = 3
    }
}
