package com.masters.mort.ui.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import com.masters.mort.databinding.ActivityAuthorisationBinding
import com.masters.mort.utilities.NetworkConnectivityChecker
import com.masters.mort.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthorisationBinding
    private val userViewModel by viewModel<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorisationBinding.inflate(layoutInflater)
        connectionLiveData = NetworkConnectivityChecker(this)
        userViewModel.users.isInitialized
        userViewModel.getCounties()
        setContentView(binding.root)
    }

    companion object {
        lateinit var connectionLiveData: NetworkConnectivityChecker
    }
}