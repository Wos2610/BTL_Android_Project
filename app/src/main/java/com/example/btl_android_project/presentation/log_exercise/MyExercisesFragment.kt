package com.example.btl_android_project.presentation.log_exercise

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentMyExercisesBinding
import com.example.btl_android_project.local.entity.Exercise
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyExercisesFragment : Fragment(), SearchableExerciseList {

    private var _binding: FragmentMyExercisesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyExercisesViewModel by viewModels()
    private val adapter = ExerciseAdapter { showEditDialog(it) }

    private var fullList: List<Exercise> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyExercisesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerViewMyExercises.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMyExercises.adapter = adapter

        viewModel.exercises.observe(viewLifecycleOwner) { exercises ->
            fullList = exercises
            adapter.setExercises(fullList)
        }

        viewModel.loadExercisesForCurrentUser()
    }

    private fun showEditDialog(exercise: Exercise) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_exercise, null)
        val etDescription = dialogView.findViewById<EditText>(R.id.etDialogDescription)
        val etMinutes = dialogView.findViewById<EditText>(R.id.etDialogMinutes)
        val etCalories = dialogView.findViewById<EditText>(R.id.etDialogCalories)

        etDescription.setText(exercise.description)
        etMinutes.setText(exercise.minutesPerformed.toString())
        etCalories.setText(exercise.caloriesBurned.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Exercise")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val updated = exercise.copy(
                    description = etDescription.text.toString(),
                    minutesPerformed = etMinutes.text.toString().toIntOrNull() ?: 0,
                    caloriesBurned = etCalories.text.toString().toFloatOrNull() ?: 0f
                )
                viewModel.updateExercise(updated)
            }
            .setNegativeButton("Delete") { _, _ ->
                viewModel.deleteExercise(exercise)
            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    override fun search(query: String) {
        val filtered = fullList.filter {
            it.description.contains(query, ignoreCase = true)
        }
        adapter.setExercises(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
