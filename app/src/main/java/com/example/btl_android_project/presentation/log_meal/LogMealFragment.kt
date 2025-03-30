package com.example.btl_android_project.presentation.log_meal

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl_android_project.databinding.FragmentLogMealBinding
import com.example.btl_android_project.presentation.log_all.LogAllFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogMealFragment : Fragment() {
    private var _binding: FragmentLogMealBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = LogMealFragment()
    }

    private val viewModel: LogMealViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvMeals.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = MealAdapter(viewModel.meals)
        }

        binding.btnCreateMeal.cvLogItem.setOnClickListener {
            val action = LogAllFragmentDirections.actionLogAllFragmentToCreateMealFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}