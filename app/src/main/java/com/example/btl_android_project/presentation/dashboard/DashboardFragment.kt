package com.example.btl_android_project.presentation.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentDashboardBinding
import com.example.btl_android_project.local.entity.LogWeight
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = DashboardFragment()
    }

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getTodayDiary()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.todayDiary.collect { diary ->
                    binding.tvGoalResult.text = diary?.caloriesGoal.toString()
                    binding.tvFoodResult.text = (diary?.totalFoodCalories ?: 0).toString()
                }
            }
        }

        viewModel.getLatestWeights()

        viewModel.latestWeights.observe(viewLifecycleOwner) { weights ->
            updateWeightChart(weights)
        }

        binding.cardWeightChart.setOnClickListener {
            val action = DashboardFragmentDirections.actionDashboardFragmentToLogWeightFragment()
            findNavController().navigate(action)
        }

    }

    private fun updateWeightChart(logs: List<LogWeight>) {
        if (logs.isEmpty()) {
            binding.weightChart.clear()
            return
        }

        val entries = logs.mapIndexed { index, log ->
            Entry(index.toFloat(), log.weight)
        }

        val dateLabels = logs.map { it.date }

        val dataSet = LineDataSet(entries, "Weight (kg)").apply {
            color = ContextCompat.getColor(requireContext(), R.color.blue)
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.white)
            lineWidth = 2f
            circleRadius = 5f
            setDrawValues(false)
            setDrawCircles(true)
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(requireContext(), R.color.blue)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        val lineData = LineData(dataSet)
        binding.weightChart.apply {
            data = lineData
            setTouchEnabled(true)
            setScaleEnabled(false)
            setPinchZoom(false)
            legend.isEnabled = false
            description.isEnabled = false
            setDrawBorders(false)

            setExtraOffsets(10f, 10f, 10f, 20f)

            description.isEnabled = false
            legend.textColor = ContextCompat.getColor(requireContext(), R.color.white)

            xAxis.apply {
                textColor = ContextCompat.getColor(requireContext(), R.color.white)
                granularity = 1f
                isGranularityEnabled = true
                setDrawGridLines(false)
                setAvoidFirstLastClipping(true)
                position = XAxis.XAxisPosition.BOTTOM
                labelRotationAngle = -45f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt()
                        return if (index in dateLabels.indices) dateLabels[index] else ""
                    }
                }
            }

            axisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.white)
            axisRight.isEnabled = false

            invalidate()
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}