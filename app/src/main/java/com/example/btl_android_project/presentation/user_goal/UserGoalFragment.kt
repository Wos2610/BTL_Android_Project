package com.example.btl_android_project.presentation.user_goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.btl_android_project.databinding.FragmentUserGoalBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserGoalFragment : Fragment() {
    private var _binding: FragmentUserGoalBinding? = null
    private val binding get() = _binding!!
    private val args: UserGoalFragmentArgs by navArgs()
    private val viewModel: UserGoalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.userProfileArgument = args.userProfile
        viewModel.calculateGoal()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnChangeWeightGoal.setOnClickListener {
            viewModel.userProfileArgument?.let {
                val dialog = WeightGoalDialog(requireContext(), viewModel.userProfileArgument!!) { updatedProfile ->
                    viewModel.userProfileArgument = updatedProfile
                    viewModel.calculateGoal()
                }
                dialog.show()
            }
        }

        binding.btnNext.setOnClickListener {
            viewModel.saveUserProfile(
                onSuccess = {
                    val action = UserGoalFragmentDirections.actionUserGoalFragmentToDashboardFragment()
                    findNavController().navigate(action)
                },
                onLoading = {isLoading ->
                    showLoading(isLoading)
                }
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userWeightGoal.collect { userGoal ->
                        binding.tvCalorieGoal.text = userGoal.toString()
                        binding.tvWaterGoal.text = viewModel.userProfileArgument?.goalWater.toString() + " ml"
                    }
                }

                launch {
                    viewModel.userPlanInfo.collect { userPlanInfo ->
                        binding.tvPlanInfo.text = userPlanInfo
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}