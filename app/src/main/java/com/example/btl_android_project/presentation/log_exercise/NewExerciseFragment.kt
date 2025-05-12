package com.example.btl_android_project.presentation.log_exercise

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.btl_android_project.databinding.FragmentNewExerciseBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewExerciseFragment : Fragment() {
    private var _binding: FragmentNewExerciseBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = NewExerciseFragment()
    }

    private val viewModel: NewExerciseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreateExercise.setOnClickListener {
            val description = binding.etDescription.text.toString().trim()
            val minutesStr = binding.etMinutesPerformed.text.toString().trim()
            val caloriesStr = binding.etCaloriesBurned.text.toString().trim()

            if (description.isEmpty() || minutesStr.isEmpty() || caloriesStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val minutes = minutesStr.toIntOrNull()
            val calories = caloriesStr.toFloatOrNull()

            if (minutes == null || calories == null) {
                Toast.makeText(requireContext(), "Invalid number input", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.createExercise(description, minutes, calories)
        }

        viewModel.createStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Exercise added!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Failed to add exercise", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}