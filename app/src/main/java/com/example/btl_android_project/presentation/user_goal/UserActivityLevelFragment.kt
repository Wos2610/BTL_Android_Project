package com.example.btl_android_project.presentation.user_goal

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.FragmentUserActivityLevelBinding
import com.example.btl_android_project.local.ActivityLevel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserActivityLevelFragment : Fragment() {
    private var _binding: FragmentUserActivityLevelBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserActivityLevelViewModel by viewModels()

    private val args: UserActivityLevelFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.userProfileArgument = args.userProfile
        Log.d("UserActivityLevelFragment", "userProfileArgument onCreate: ${viewModel.userProfileArgument}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserActivityLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardNotVeryActive.setBackgroundResource(R.drawable.bg_card_unselected)
        binding.cardLightlyActive.setBackgroundResource(R.drawable.bg_card_unselected)
        binding.cardActive.setBackgroundResource(R.drawable.bg_card_unselected)
        binding.cardVeryActive.setBackgroundResource(R.drawable.bg_card_unselected)

        binding.cardNotVeryActive.setOnClickListener {
            binding.cardNotVeryActive.setBackgroundResource(R.drawable.bg_card_selected)
            binding.cardLightlyActive.setBackgroundResource(R.drawable.bg_card_unselected)
            binding.cardActive.setBackgroundResource(R.drawable.bg_card_unselected)
            binding.cardVeryActive.setBackgroundResource(R.drawable.bg_card_unselected)
            viewModel.userActivityLevel = ActivityLevel.NOT_ACTIVE.name
        }

        binding.cardLightlyActive.setOnClickListener {
            binding.cardNotVeryActive.setBackgroundResource(R.drawable.bg_card_unselected)
            binding.cardLightlyActive.setBackgroundResource(R.drawable.bg_card_selected)
            binding.cardActive.setBackgroundResource(R.drawable.bg_card_unselected)
            binding.cardVeryActive.setBackgroundResource(R.drawable.bg_card_unselected)
            viewModel.userActivityLevel = ActivityLevel.LIGHT_ACTIVE.name
        }

        binding.cardActive.setOnClickListener {
            binding.cardNotVeryActive.setBackgroundResource(R.drawable.bg_card_unselected)
            binding.cardLightlyActive.setBackgroundResource(R.drawable.bg_card_unselected)
            binding.cardActive.setBackgroundResource(R.drawable.bg_card_selected)
            binding.cardVeryActive.setBackgroundResource(R.drawable.bg_card_unselected)
            viewModel.userActivityLevel = ActivityLevel.ACTIVE.name
        }

        binding.cardVeryActive.setOnClickListener {
            binding.cardNotVeryActive.setBackgroundResource(R.drawable.bg_card_unselected)
            binding.cardLightlyActive.setBackgroundResource(R.drawable.bg_card_unselected)
            binding.cardActive.setBackgroundResource(R.drawable.bg_card_unselected)
            binding.cardVeryActive.setBackgroundResource(R.drawable.bg_card_selected)
            viewModel.userActivityLevel = ActivityLevel.VERY_ACTIVE.name
        }

        binding.btnNext.setOnClickListener {
            if (viewModel.userActivityLevel.isNotEmpty()) {
                viewModel.updateUserProfileArgumentBeforeSend()
                Log.d(
                    "UserActivityLevelFragment",
                    "userProfileArgument btnNext onClick: ${viewModel.userProfileArgument}"
                )
                val action =
                    UserActivityLevelFragmentDirections.actionUserActivityLevelFragmentToUserInformation1Fragment(
                        userProfile = viewModel.userProfileArgument
                    )
                findNavController().navigate(action)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please select an activity level",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}