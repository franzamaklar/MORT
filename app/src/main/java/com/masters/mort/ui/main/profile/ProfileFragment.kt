package com.masters.mort.ui.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.masters.mort.databinding.FragmentProfileBinding
import com.masters.mort.ui.authentication.AuthenticationActivity
import com.masters.mort.ui.main.MainActivity
import com.masters.mort.utilities.Utils
import com.masters.mort.viewmodel.UserViewModel

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private lateinit var userViewModel: UserViewModel
    private lateinit var userID: String
    private var firebaseAuth:FirebaseAuth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        userViewModel = ViewModelProvider(
            requireActivity(),
            defaultViewModelProviderFactory
        )[UserViewModel::class.java]
        userID = firebaseAuth.currentUser?.uid.toString()
        setUpUserData()
        binding.backButton.setOnClickListener { backToMain() }
        binding.updateButton.setOnClickListener { forwardToUpdate() }
        binding.logoutButton.setOnClickListener { logout() }
        return binding.root
    }

    private fun setUpUserData() {
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            if (users != null && users.isNotEmpty()) {
                users.forEach {
                    if (it.id == userID) {
                        binding.fullNameHolder.text = String.format("${it.name} ${it.surname}")
                        binding.userEmail.text = it.email
                        binding.userCounty.text = it.county
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setUpUserData()
    }

    private fun logout() {
        firebaseAuth.signOut()
        Utils.switchActivity(
            requireActivity(),
            AuthenticationActivity::class.java,
            true
        )
    }

    private fun forwardToUpdate() {
        val action = ProfileFragmentDirections.actionProfileFragmentToUpdateProfileFragment(userID)
        findNavController().navigate(action)
    }

    private fun backToMain() {
        Utils.switchActivity(
            requireActivity(),
            MainActivity::class.java,
            true
        )
    }

}