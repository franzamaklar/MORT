package com.masters.mort.ui.main.profile

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.masters.mort.databinding.FragmentUpdateProfileBinding
import com.masters.mort.utilities.Validator
import com.masters.mort.viewmodel.UserViewModel
import kotlinx.coroutines.launch


class UpdateProfileFragment : Fragment() {

    private lateinit var binding: FragmentUpdateProfileBinding
    private lateinit var userViewModel: UserViewModel
    private val args: UpdateProfileFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateProfileBinding.inflate(layoutInflater)
        binding.backToPreviousFragment.setOnClickListener { backwardToProfile() }
        userViewModel = ViewModelProvider(
            requireActivity(),
            defaultViewModelProviderFactory
        )[UserViewModel::class.java]
        setUpFields()
        binding.updateButton.setOnClickListener {
            updateUserData()
        }
        return binding.root
    }

    private fun setUpFields() {
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            if (users != null && users.isNotEmpty()) {
                users.forEach {
                    if (it.id == args.userID) {
                        binding.userName.hint = it.name
                        binding.userSurname.hint = it.surname
                        binding.userEmail.hint = it.email
                    }
                }
            }
        }
    }

    private fun updateUserData() {
        val name =
            if (binding.userName.text.isNotEmpty()) binding.userName.text.toString() else binding.userName.hint.toString()
        val surname =
            if (binding.userSurname.text.isNotEmpty()) binding.userSurname.text.toString() else binding.userSurname.hint.toString()
        val newEmail =
            if (binding.userEmail.text.isNotEmpty()) binding.userEmail.text.toString() else binding.userEmail.hint.toString()
        val password = binding.userPassword.text.toString()

        val errors = Validator.validateUserPasswordForUpdate(password)
        try {
            if (errors.isNotEmpty()) {
                Toast.makeText(context, errors.first().Description, Toast.LENGTH_LONG).show()
            } else {
                lifecycle.coroutineScope.launch {
                    if (userViewModel.updateUser(
                            args.userID, name, surname, newEmail, password
                        )
                    ) {
                        Toast.makeText(
                            context,
                            "Data saved successfully! Preparing your new data profile for you!",
                            Toast.LENGTH_LONG
                        ).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            val action =
                                UpdateProfileFragmentDirections.actionUpdateProfileFragmentToProfileFragment()
                            findNavController().navigate(action)
                        }, 2000)
                    } else {
                        Toast.makeText(
                            context,
                            "Unsuccessful email changing! Please check all inputs again!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } catch (exception: Exception) {
            Toast.makeText(context, "Error occurred while updating user data!", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun backwardToProfile() {
        val action = UpdateProfileFragmentDirections.actionUpdateProfileFragmentToProfileFragment()
        findNavController().navigate(action)
    }

}