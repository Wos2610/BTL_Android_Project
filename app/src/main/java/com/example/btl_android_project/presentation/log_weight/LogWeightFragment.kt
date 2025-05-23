package com.example.btl_android_project.presentation.log_weight

import android.app.DatePickerDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentLogWeightBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class LogWeightFragment : Fragment() {
    private var _binding: FragmentLogWeightBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = LogWeightFragment()
    }

    private val viewModel: LogWeightViewModel by viewModels()
    private var selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
        Date()
    )
    private var isUpdateMode = false
    private var currentLogWeightId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogWeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        binding.tvDate.text = formatDateDisplay(selectedDate)

        binding.tvDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            val weightStr = binding.etWeight.text.toString()
            val weight = weightStr.toFloatOrNull()

            if (weight != null && weight > 0) {
                if (isUpdateMode && currentLogWeightId != null) {
                    viewModel.saveLogWeight(weight, selectedDate)
                } else {
                    viewModel.saveLogWeight(weight, selectedDate)
                }
            } else {
                Toast.makeText(requireContext(), "Invalid weight", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDelete.setOnClickListener {
            if (isUpdateMode && currentLogWeightId != null) {
                viewModel.deleteLogWeight(currentLogWeightId!!)
            } else {
                Toast.makeText(requireContext(), "No item selected to delete", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnClear.setOnClickListener {
            clearForm()
        }

        viewModel.saveStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Saved successfully", Toast.LENGTH_SHORT).show()
                clearForm()
                viewModel.loadLogWeights()
            } else {
                Toast.makeText(requireContext(), "Failed to save", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.updateStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Updated successfully", Toast.LENGTH_SHORT).show()
                clearForm()
                viewModel.loadLogWeights()
            } else {
                Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.deleteStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Deleted successfully", Toast.LENGTH_SHORT).show()
                clearForm()
                viewModel.loadLogWeights()
            } else {
                Toast.makeText(requireContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.logWeights.observe(viewLifecycleOwner) { logWeights ->
            (binding.recyclerView.adapter as LogWeightAdapter).submitList(logWeights)
        }

        viewModel.loadLogWeights()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = LogWeightAdapter { logWeight ->
            isUpdateMode = true
            currentLogWeightId = logWeight.id
            binding.etWeight.setText(logWeight.weight.toString())
            selectedDate = logWeight.date
            binding.tvDate.text = formatDateDisplay(selectedDate)

            binding.btnSave.text = "Update"
        }
    }

    private fun clearForm() {
        binding.etWeight.setText("")
        selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        binding.tvDate.text = formatDateDisplay(selectedDate)
        isUpdateMode = false
        currentLogWeightId = null
        binding.btnSave.text = "Save"
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val datePicker = DatePickerDialog(requireContext(),
            { _, year, month, dayOfMonth ->
                cal.set(year, month, dayOfMonth)
                selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                binding.tvDate.text = formatDateDisplay(selectedDate)
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun formatDateDisplay(dateStr: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateStr)
        return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}