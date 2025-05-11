package com.example.btl_android_project.presentation.user_goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentUserWeightGoalBinding
import com.example.btl_android_project.local.enums.WeightGoal
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserWeightGoalFragment : Fragment() {
    private var _binding: FragmentUserWeightGoalBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserWeightGoalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserWeightGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardGainWeight.setBackgroundResource(R.drawable.bg_card_unselected)
        binding.cardLoseWeight.setBackgroundResource(R.drawable.bg_card_unselected)
        binding.cardMaintainWeight.setBackgroundResource(R.drawable.bg_card_unselected)

        binding.cardGainWeight.setOnClickListener {
            binding.cardGainWeight.setBackgroundResource(R.drawable.bg_card_selected)
            binding.cardLoseWeight.setBackgroundResource(R.drawable.bg_card_unselected)
            binding.cardMaintainWeight.setBackgroundResource(R.drawable.bg_card_unselected)
            viewModel.userGoal = WeightGoal.GAIN_WEIGHT.name
        }

        binding.cardLoseWeight.setOnClickListener {
            binding.cardGainWeight.setBackgroundResource(R.drawable.bg_card_unselected)
            binding.cardLoseWeight.setBackgroundResource(R.drawable.bg_card_selected)
            binding.cardMaintainWeight.setBackgroundResource(R.drawable.bg_card_unselected)
            viewModel.userGoal = WeightGoal.LOSE_WEIGHT.name
        }

        binding.cardMaintainWeight.setOnClickListener {
            binding.cardGainWeight.setBackgroundResource(R.drawable.bg_card_unselected)
            binding.cardLoseWeight.setBackgroundResource(R.drawable.bg_card_unselected)
            binding.cardMaintainWeight.setBackgroundResource(R.drawable.bg_card_selected)
            viewModel.userGoal = WeightGoal.MAINTAIN_WEIGHT.name
        }
        binding.btnNext.setOnClickListener {
            if(viewModel.userGoal.isNotEmpty()) {
                viewModel.updateUserProfileArgumentBeforeSend()
                val action = UserWeightGoalFragmentDirections.actionUserWeightGoalFragmentToUserActivityLevelFragment(userProfile = viewModel.userProfileArgument)
                findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "Please select a goal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}