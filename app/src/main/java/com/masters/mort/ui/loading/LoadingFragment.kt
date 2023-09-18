package com.masters.mort.ui.loading

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.masters.mort.databinding.FragmentLoadingBinding
import com.masters.mort.ui.authentication.AuthenticationActivity
import com.masters.mort.ui.main.MainActivity

class LoadingFragment : Fragment() {

    private lateinit var binding: FragmentLoadingBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firebaseAuth = Firebase.auth
        requireActivity().onBackPressedDispatcher.addCallback(this){
            this.isEnabled = false
            Toast.makeText(context, "Loading application, please wait!", Toast.LENGTH_LONG).show()
        }
        setUpApplication()
        binding = FragmentLoadingBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun setUpApplication() {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if (firebaseAuth.currentUser != null) {
                    startActivity(
                        Intent(
                            context,
                            MainActivity::class.java
                        )
                    )
                } else {
                    startActivity(
                        Intent(
                            context,
                            AuthenticationActivity::class.java
                        )
                    )
                }
            }, 2000
        )
    }

}