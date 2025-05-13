package com.example.btl_android_project.presentation.log_exercise

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentAllExercisesBinding
import com.example.btl_android_project.local.entity.Exercise
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BrowseAllExercisesFragment : Fragment(), SearchableExerciseList {

    private var _binding: FragmentAllExercisesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AllExercisesViewModel by viewModels()
    private val adapter = ExerciseAdapter(
        onExerciseClick = {},
        onLogExerciseClick = { exercise ->
            viewModel.addExerciseDiary(
                exerciseId = exercise.id,
                onSuccess = {
                    Toast.makeText(requireContext(), "Exercise logged successfully", Toast.LENGTH_SHORT).show()
                }
            )
        }
    )

    private var fullList: List<Exercise> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllExercisesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerViewMyExercises.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMyExercises.adapter = adapter

        viewModel.exercises.observe(viewLifecycleOwner) { exercises ->
            fullList = exercises
            adapter.setExercises(fullList)
        }

        viewModel.loadAllExercises()
    }

    override fun search(query: String) {
        val filtered = fullList.filter {
            it.description.contains(query, ignoreCase = true)
        }
        adapter.setExercises(filtered)
    }

    override fun loadDataAgain() {
        viewModel.loadAllExercises()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
