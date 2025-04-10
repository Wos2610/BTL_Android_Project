package com.example.btl_android_project.presentation.log_recipe

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentLogRecipeBinding
import com.example.btl_android_project.presentation.log_all.LogAllFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogRecipeFragment : Fragment() {
    private var _binding: FragmentLogRecipeBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = LogRecipeFragment()
    }

    private val viewModel: LogRecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvRecipes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = RecipeAdapter(viewModel.recipes)
        }

        binding.btnCreateRecipe.cvLogItem.setOnClickListener {
            val action = LogAllFragmentDirections.actionLogAllFragmentToNewRecipeFragment()
            findNavController().navigate(action)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}