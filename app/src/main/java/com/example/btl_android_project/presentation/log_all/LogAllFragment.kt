package com.example.btl_android_project.presentation.log_all

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.example.btl_android_project.MainActivity
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentLogAllBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator

class LogAllFragment : Fragment() {
    private var _binding: FragmentLogAllBinding? = null
    private val binding get() = _binding!!
    private lateinit var pagerAdapter: LogPagerAdapter
    companion object {
        fun newInstance() = LogAllFragment()
    }

    private val viewModel: LogAllViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Use the ViewModel
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

        pagerAdapter = LogPagerAdapter(this)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideBottomNavigationView() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.navigation)
        bottomNavigationView.visibility = View.GONE
    }
}