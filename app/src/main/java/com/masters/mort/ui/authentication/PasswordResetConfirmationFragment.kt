package com.masters.mort.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.masters.mort.databinding.FragmentPasswordResetConfirmationBinding


class PasswordResetConfirmationFragment : Fragment() {

    private lateinit var binding: FragmentPasswordResetConfirmationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordResetConfirmationBinding.inflate(layoutInflater)
        requireActivity().onBackPressedDispatcher.addCallback(this){
            this.isEnabled = false
            Toast.makeText(context, "You can not go back to previous state! Please use navigation!", Toast.LENGTH_LONG).show()
        }
        binding.backToLogin.setOnClickListener { redirectToLogin() }
        return binding.root
    }

    private fun redirectToLogin() {
        val action = PasswordResetConfirmationFragmentDirections.actionPasswordResetConfirmationFragmentToLoginFragment()
        findNavController().navigate(action)
    }
}