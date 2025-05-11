package com.example.btl_android_project.presentation.diary

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl_android_project.MainActivity
import com.example.btl_android_project.databinding.FragmentTodayDiaryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate

@AndroidEntryPoint
class TodayDiaryFragment : Fragment() {
    private var _binding: FragmentTodayDiaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TodayDiaryViewModel by viewModels()

    private lateinit var adapter: MealSectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodayDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setUpDatePicker()
        setSaveButtonToolBarOnClickListener()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.selectedDate.collect { selectedDate ->
                        binding.dateTextView.text = "${selectedDate.dayOfMonth}/${selectedDate.monthValue}/${selectedDate.year}"
                        viewModel.getDiaryByDate(
                            date = selectedDate,
                            onSuccess = { listMealSection ->
                                adapter.updateData(listMealSection)
                            }
                        )
                    }
                }

                launch {
                    viewModel.dailyDiary.collect { diaryWithNutrition ->
                        if (diaryWithNutrition != null) {
                            binding.foodTextView.text = diaryWithNutrition.totalFoodCalories.toString()
                            binding.goalTextView.text = diaryWithNutrition.caloriesGoal.toString()
                        }
                        else{
                            binding.foodTextView.text = "0"
                            binding.goalTextView.text = "0"
                        }
                    }
                }

                launch {
                    viewModel.todayDiary.collect { diaryWithNutrition ->
                        if (diaryWithNutrition != null) {
                            binding.foodTextView.text = diaryWithNutrition.diary.totalFoodCalories.toString()
                            binding.goalTextView.text = diaryWithNutrition.diary.caloriesGoal.toString()
                        }
                        else{
                            binding.foodTextView.text = "0"
                            binding.goalTextView.text = "0"
                        }
                    }
                }
            }
        }

    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        val sections = listOf(
            MealSection("Breakfast", 0, emptyList()),
            MealSection("Lunch", 0, emptyList()),
            MealSection("Dinner", 0, emptyList()),
            MealSection("Snacks", 0, emptyList())
        )

        adapter = MealSectionAdapter(sections)
        binding.recyclerView.adapter = adapter
    }

    private fun setUpDatePicker() {
        binding.dateTextView.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    viewModel.setSelectedDate(selectedDate)
                },
                viewModel.year,
                viewModel.month - 1,
                viewModel.day
            )
            datePickerDialog.show()
        }

        binding.nextDayButton.setOnClickListener {
            val nextDate = viewModel.selectedDate.value.plusDays(1)
            viewModel.setSelectedDate(nextDate)
        }

        binding.previousDayButton.setOnClickListener {
            val previousDate = viewModel.selectedDate.value.minusDays(1)
            viewModel.setSelectedDate(previousDate)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setSaveButtonToolBarOnClickListener() {
        (requireActivity() as? MainActivity)?.setSaveButtonClickListener {
            viewModel.saveSnapshot()
            setSaveButtonVisible()
        }
    }

    private fun setSaveButtonVisible() {
        (requireActivity() as? MainActivity)?.setSaveButtonVisibility(false)
    }
}