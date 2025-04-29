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
    private val fragments = mutableMapOf<Int, Fragment>()
    override fun createFragment(position: Int): Fragment {
        val isFromCreateMeal = isFromCreateMealList.getOrNull(position) == true
        val fragment = when(position) {
            0 -> LogMealFragment.newInstance(isFromCreateMeal)
            1 -> LogRecipeFragment.newInstance(isFromCreateMeal)
            2 -> LogFoodFragment.newInstance(isFromCreateMeal)
            else -> LogMealFragment.newInstance()
        }

        fragments[position] = fragment
        return fragment
    }

    override fun getItemCount(): Int = NUM_PAGES

    fun getFragmentAtPosition(position: Int): Fragment? {
        return fragments[position]
    }

    companion object {
        const val NUM_PAGES = 3
    }
}
