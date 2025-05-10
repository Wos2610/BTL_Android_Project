package com.example.btl_android_project.presentation.sign_in

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.btl_android_project.databinding.FragmentSignInBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val args: SignInFragmentArgs by navArgs()
    private val viewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(args.email != null) binding.etEmail.setText(args.email)
        if(args.password != null) binding.etPassword.setText(args.password)

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            viewModel.loginUser(
                email = email,
                password = password,
                onLogin = {
                    val action = SignInFragmentDirections.actionSignInFragmentToDashboardFragment()
                    findNavController().navigate(action)
                },
                onFailure = { exception ->
                    Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
                },
                onLoading = { isLoading ->
                    showLoading(isLoading)
                },
                onSetUpGoal = {
                    val action = SignInFragmentDirections.actionSignInFragmentToUserWeightGoalFragment()
                    findNavController().navigate(action)
                }
            )
        }

        binding.btnSignUp.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            findNavController().navigate(action)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.checkUserLoggedIn(
            onSuccess = {
                val action = SignInFragmentDirections.actionSignInFragmentToDashboardFragment()
                findNavController().navigate(action)
            },
            onFailure = { exception ->
                // Just ignore failure, user needs to log in
            },
            onLoading = { isLoading ->
                showLoading(isLoading)
            }
        )
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE

        // Disable interaction with UI elements when loading
        binding.etEmail.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
        binding.btnSignIn.isEnabled = !isLoading
        binding.btnSignUp.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}