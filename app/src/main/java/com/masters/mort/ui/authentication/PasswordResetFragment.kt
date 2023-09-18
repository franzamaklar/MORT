package com.masters.mort.ui.authentication

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.masters.mort.databinding.FragmentPasswordResetBinding
import com.masters.mort.viewmodel.UserViewModel

class PasswordResetFragment : Fragment() {

    private lateinit var binding: FragmentPasswordResetBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordResetBinding.inflate(layoutInflater)
        userViewModel = ViewModelProvider(
            requireActivity(),
            defaultViewModelProviderFactory
        )[UserViewModel::class.java]
        var isNetworkActive = false
        val networkConnectivity = AuthenticationActivity.connectionLiveData
        networkConnectivity.observe(this){ isNetworkAvailable ->
            isNetworkAvailable.let {
                isNetworkActive = it
            }
        }
        binding.backButton.setOnClickListener { redirectToLogin() }
        binding.submitReset.setOnClickListener {
            if(isNetworkActive){
                submit()
            }else{
                Toast.makeText(context, "No Internet! You can not use this function! Try again when you are online.", Toast.LENGTH_LONG).show()
            }
        }
        return binding.root
    }

    private fun submit(){
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            if (users.isNotEmpty()) {
                val email = binding.emailInput.text.toString()
                if (!TextUtils.isEmpty(email) && users.firstOrNull { user -> user.email == email } != null) {
                    redirectToConfirmationPage(email)
                } else {
                    Toast.makeText(context, "Please provide correct email input!", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun redirectToLogin() {
        val action = PasswordResetFragmentDirections.actionPasswordResetFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun redirectToConfirmationPage(email:String) {
        userViewModel.resetPasswordForEmail(email)

        val action = PasswordResetFragmentDirections.actionPasswordResetFragmentToPasswordResetConfirmationFragment()
        findNavController().navigate(action)
    }
}