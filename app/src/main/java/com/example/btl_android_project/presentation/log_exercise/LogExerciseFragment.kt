package com.example.btl_android_project.presentation.log_exercise

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentLogExerciseBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogExerciseFragment : Fragment() {

    private var _binding: FragmentLogExerciseBinding? = null
    private val binding get() = _binding!!

    private val fragmentList = listOf(
        MyExercisesFragment(),
        BrowseAllExercisesFragment()
    )

    companion object {
        fun newInstance() = LogExerciseFragment()
    }

    private val viewModel: LogExerciseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCreateExercise.setOnClickListener {
            val action = LogExerciseFragmentDirections.actionLogExerciseFragmentToNewExerciseFragment()
            findNavController().navigate(action)
        }

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragmentList.size
            override fun createFragment(position: Int): Fragment {
                return fragmentList[position] as Fragment
            }
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) "MY EXERCISES" else "BROWSE ALL"
        }.attach()

        binding.etSearch.addTextChangedListener {
            val query = it.toString()
            val currentFragment = fragmentList[binding.viewPager.currentItem]
            if (currentFragment is SearchableExerciseList) {
                currentFragment.search(query)
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val currentFragment = childFragmentManager.findFragmentByTag("f${tab.position}")
                when (currentFragment) {
                    is MyExercisesFragment -> currentFragment.loadDataAgain()
                    is BrowseAllExercisesFragment -> currentFragment.loadDataAgain()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}