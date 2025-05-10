package com.example.btl_android_project.presentation.user_goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.btl_android_project.databinding.FragmentUserGoalBinding
import dagger.hilt.android.AndroidEntryPoint

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}