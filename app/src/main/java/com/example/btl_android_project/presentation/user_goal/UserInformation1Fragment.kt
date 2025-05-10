package com.example.btl_android_project.presentation.user_goal

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.btl_android_project.databinding.FragmentUserInformation1Binding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class UserInformation1Fragment : Fragment() {
    private var _binding: FragmentUserInformation1Binding? = null
    private val binding get() = _binding!!
    private val args: UserInformation1FragmentArgs by navArgs()

    private val viewModel: UserInformation1ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.userProfileArgument = args.userProfile
        Log.d("UserInformation1Fragment", "userProfileArgument onCreate: ${viewModel.userProfileArgument}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserInformation1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rbFemale.isChecked = true

        binding.rbFemale.setOnClickListener {
            viewModel.isFeMale = true
        }

        binding.rbMale.setOnClickListener {
            viewModel.isFeMale = false
        }

        binding.etBirthDate.setOnClickListener {
            val today = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                    binding.etBirthDate.setText(selectedDate)
                    viewModel.dateOfBirth = selectedDate
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.spinnerProvince.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedProvince = parent?.getItemAtPosition(position).toString()
                viewModel.city = selectedProvince
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.btnNext.setOnClickListener {
            if (viewModel.dateOfBirth.isNotEmpty() && viewModel.city.isNotEmpty()) {
                viewModel.updateUserProfileArgumentBeforeSend()
                val action = UserInformation1FragmentDirections.actionUserInformation1FragmentToUserInformation2Fragment(userProfile = viewModel.userProfileArgument)
                findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}