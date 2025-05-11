package com.example.btl_android_project.presentation.log_water

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentLogWaterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogWaterFragment : Fragment() {
    private var _binding: FragmentLogWaterBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = LogWaterFragment()
    }

    private val viewModel: LogWaterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogWaterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.totalWater.observe(viewLifecycleOwner) {
            binding.tvTotalVolume.text = "${it} ml"
        }

        binding.btnAdd250.setOnClickListener {
            viewModel.addWater(250)
        }

        binding.btnAdd500.setOnClickListener {
            viewModel.addWater(500)
        }

        binding.btnAdd1000.setOnClickListener {
            viewModel.addWater(1000)
        }

        binding.btnAddCustom.setOnClickListener {
            showCustomWaterDialog()
        }

        binding.tvTotalVolume.setOnClickListener {
            showEditTotalDialog()
        }

        binding.btnSave.setOnClickListener {
            viewModel.saveLogWater(onSuccess = {
                Toast.makeText(requireContext(), "Water log saved", Toast.LENGTH_SHORT).show()
            })
        }


    }

    private fun showEditTotalDialog() {
        val input = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = "Enter new total (ml)"
            setText(viewModel.totalWater.value?.toString() ?: "")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Total Volume")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val newTotal = input.text.toString().toIntOrNull()
                if (newTotal != null && newTotal >= 0) {
                    viewModel.setWater(newTotal)
                } else {
                    Toast.makeText(requireContext(), "Invalid value", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun showCustomWaterDialog() {
        val input = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = "Enter amount in ml"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Custom Water Intake")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val amount = input.text.toString().toIntOrNull()
                if (amount != null && amount > 0) {
                    viewModel.addWater(amount)
                } else {
                    Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}