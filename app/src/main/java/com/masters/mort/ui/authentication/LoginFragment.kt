package com.masters.mort.ui.authentication

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.masters.mort.R
import com.masters.mort.databinding.FragmentLoginBinding
import com.masters.mort.model.slider_item.AnimationSliderItem
import com.masters.mort.ui.authentication.utilities.SliderAdapter
import com.masters.mort.ui.main.MainActivity
import com.masters.mort.utilities.Validator
import com.masters.mort.viewmodel.UserViewModel
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var adapter: SliderAdapter
    private var firebaseAuth: FirebaseAuth = Firebase.auth

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        requireActivity().onBackPressedDispatcher.addCallback(this){
            this.isEnabled = false
            Toast.makeText(context, "You can not go back to previous state! Please use navigation!", Toast.LENGTH_LONG).show()
        }
        var isNetworkActive = false
        val networkConnectivity = AuthenticationActivity.connectionLiveData
        networkConnectivity.observe(this){ isNetworkAvailable ->
            isNetworkAvailable.let {
                if(it == true){
                    isNetworkActive = true
                }
            }
        }
        userViewModel = ViewModelProvider(
            requireActivity(),
            defaultViewModelProviderFactory
        )[UserViewModel::class.java]
        setupSliderAdapter()
        binding.buttonSignIn.setOnClickListener {
            if(isNetworkActive){
                login()
            }else{
                Toast.makeText(context, "No Internet! You can not use this function! Try again when you are online.", Toast.LENGTH_LONG).show()
            }
        }
        binding.newAccountRequest.setOnClickListener { redirectToNewAccountRequest() }
        binding.forgotPassword.setOnClickListener { redirectToPasswordReset() }
        return binding.root
    }

    private fun setupSliderAdapter() {
        val sliderView: SliderView = binding.slider
        adapter = context?.let { SliderAdapter(it) }!!
        adapter.addItem(AnimationSliderItem(R.drawable.mortician))
        adapter.addItem(AnimationSliderItem(R.drawable.hospital))
        adapter.addItem(AnimationSliderItem(R.drawable.docstats))
        sliderView.setSliderAdapter(adapter)

        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        sliderView.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_RIGHT
        sliderView.scrollTimeInSec = 3
        sliderView.isAutoCycle = true
        sliderView.startAutoCycle()
    }

    private fun login() {
        try {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            val errors = Validator.validateUserForLogin(email, password)
            if (errors.isNotEmpty()) {
                Toast.makeText(context, errors.first().Description, Toast.LENGTH_LONG).show()
            } else {
                userViewModel.isUserRegistered(email, password)
                Handler(Looper.getMainLooper()).postDelayed({
                    if (firebaseAuth.currentUser != null) {
                        Toast.makeText(context, "Logged in successfully!", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(
                            Intent(
                                context,
                                MainActivity::class.java
                            )
                        )
                    } else
                        Toast.makeText(context, "Error! Login unsuccessful!", Toast.LENGTH_LONG)
                            .show()
                }, 2000)
            }
        }catch (exception: Exception){
            Toast.makeText(context, "Unexpected error occurred, please right again in a minute!", Toast.LENGTH_LONG).show()
            exception.message?.let { Log.e(ContentValues.TAG, it) }
        }
    }

    private fun redirectToPasswordReset() {
        val action = LoginFragmentDirections.actionLoginFragmentToPasswordResetFragment()
        findNavController().navigate(action)
    }

    private fun redirectToNewAccountRequest() {
        val action = LoginFragmentDirections.actionLoginFragmentToCreateAccountFragment()
        findNavController().navigate(action)
    }

}