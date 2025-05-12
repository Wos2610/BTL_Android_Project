package com.example.btl_android_project.presentation.log_exercise

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ExercisePagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyExercisesFragment()
            1 -> BrowseAllExercisesFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
