package com.example.btl_android_project.presentation.user_goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.btl_android_project.databinding.FragmentUserInformation2Binding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserInformation2Fragment : Fragment() {
    private var _binding: FragmentUserInformation2Binding? = null
    private val binding get() = _binding!!
    private val args: UserInformation2FragmentArgs by navArgs()

    private val viewModel: UserInformation2ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.userProfileArgument = args.userProfile
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserInformation2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etHeightCm.doOnTextChanged { text, _, _, _ ->
            if (text.toString().isNotEmpty()) {
                try {
                    val height = text.toString().toFloat()
                    if (height < 0) {
                        binding.etHeightCm.error = "Height cannot be negative"
                    } else {
                        binding.etHeightCm.error = null
                    }
                    viewModel.userHeightCm = height
                } catch (e: NumberFormatException) {
                    binding.etHeightCm.error = "Invalid height"
                }
            }
        }

        binding.etCurrentWeight.doOnTextChanged { text, _, _, _ ->
            if (text.toString().isNotEmpty()) {
                try {
                    val weight = text.toString().toFloat()
                    if (weight < 0) {
                        binding.etCurrentWeight.error = "Weight cannot be negative"
                    } else {
                        binding.etCurrentWeight.error = null
                    }
                    viewModel.userCurrentWeight = weight
                } catch (e: NumberFormatException) {
                    binding.etCurrentWeight.error = "Invalid weight"
                }
            }
        }

        binding.etGoalWeight.doOnTextChanged { text, _, _, _ ->
            if (text.toString().isNotEmpty()) {
                try {
                    val weight = text.toString().toFloat()
                    if (weight < 0) {
                        binding.etGoalWeight.error = "Weight cannot be negative"
                    } else {
                        binding.etGoalWeight.error = null
                    }
                    viewModel.userGoalWeight = weight
                } catch (e: NumberFormatException) {
                    binding.etGoalWeight.error = "Invalid weight"
                }
            }
        }

        binding.btnNext.setOnClickListener {
            if (viewModel.userHeightCm > 0 && viewModel.userCurrentWeight > 0 && viewModel.userGoalWeight > 0) {
                viewModel.updateUserProfileArgumentBeforeSend()
                val action =
                    UserInformation2FragmentDirections.actionUserInformation2FragmentToUserGoalFragment(
                        viewModel.userProfileArgument
                    )
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