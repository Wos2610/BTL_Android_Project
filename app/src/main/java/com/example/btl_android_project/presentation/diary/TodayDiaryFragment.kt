package com.example.btl_android_project.presentation.diary

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl_android_project.MainActivity
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.DialogEditMealBinding
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
                    viewModel.totalCalories.collect { totalCalories ->
                        binding.goalTextView.text = totalCalories.toString()
                    }
                }

                launch {
                    viewModel.totalFoodCalories.collect { totalFoodCalories ->
                        binding.foodTextView.text = totalFoodCalories.toString()
                    }
                }
                launch {
                    viewModel.totalExerciseCalories.collect { totalExerciseCalories ->
                        binding.exerciseTextView.text = totalExerciseCalories.toString()
                    }
                }
                launch {
                    viewModel.totalRemainingCalories.collect { totalRemainingCalories ->
                        binding.remainingTextView.text = totalRemainingCalories.toString()
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
            MealSection("Snacks", 0, emptyList()),
            MealSection("Water", 0, emptyList()),
        )

        adapter = MealSectionAdapter(
            sections = sections,
            onItemLongClick = { mealItem, view, position ->
                val popup = PopupMenu(view.context, view)
                popup.menuInflater.inflate(R.menu.menu_meal_item, popup.menu)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_delete -> {
                            viewModel.deleteItemFromDiary(
                                mealItem = mealItem,
                                onSuccess = {
                                    viewModel.getDiaryByDate(
                                        date = viewModel.selectedDate.value,
                                        onSuccess = { listMealSection ->
                                            adapter.updateData(listMealSection)
                                        }
                                    )
                                },
                                onFailure = {
                                    Toast.makeText(requireContext(), "Can't delete history item", Toast.LENGTH_SHORT).show()
                                }
                            )
                            true
                        }
                        R.id.action_edit -> {
                            val dialogBinding = DialogEditMealBinding.inflate(LayoutInflater.from(view.context))

                            dialogBinding.editTextName.setText(mealItem.name)
                            dialogBinding.editTextCalories.setText(mealItem.calories.toString())
                            dialogBinding.editTextServings.setText(mealItem.servings.toString())

                            AlertDialog.Builder(view.context)
                                .setTitle("Edit Meal")
                                .setView(dialogBinding.root)
                                .setPositiveButton("Save") { _, _ ->
                                    val newServings = dialogBinding.editTextServings.text.toString().toIntOrNull()
                                    if (newServings != null) {
                                        viewModel.updateDiary(
                                            mealItem = mealItem,
                                            servings = newServings,
                                            onSuccess = {
                                                viewModel.getDiaryByDate(
                                                    date = viewModel.selectedDate.value,
                                                    onSuccess = { listMealSection ->
                                                        adapter.updateData(listMealSection)
                                                    }
                                                )
                                            },
                                            onFailure = {
                                                Toast.makeText(view.context, "Can't update history item", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    } else {
                                        Toast.makeText(view.context, "Servings không hợp lệ", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .setNegativeButton("Discard", null)
                                .show()
                            true
                        }

                        else -> false
                    }
                }
                popup.show()
            }
        )
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