package com.example.btl_android_project.presentation.log_all

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.btl_android_project.presentation.log_food.LogFoodFragment
import com.example.btl_android_project.presentation.log_meal.LogMealFragment
import com.example.btl_android_project.presentation.log_recipe.LogRecipeFragment

class LogPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> LogMealFragment.Companion.newInstance()
            1 -> LogRecipeFragment.Companion.newInstance()
            2 -> LogFoodFragment.Companion.newInstance()
            else -> LogMealFragment.Companion.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return NUM_PAGES
    }

    companion object{
        const val NUM_PAGES = 3
    }
}