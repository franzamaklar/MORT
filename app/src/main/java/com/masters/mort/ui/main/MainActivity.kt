package com.masters.mort.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.masters.mort.databinding.ActivityMainBinding
import com.masters.mort.ui.main.map.MapsActivity
import com.masters.mort.utilities.NetworkConnectivityChecker
import com.masters.mort.utilities.Utils
import com.masters.mort.viewmodel.MortalityViewModel
import com.masters.mort.viewmodel.DeceasedPersonViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val deceasedPersonViewModel by viewModel<DeceasedPersonViewModel>()
    private val mortalityViewModel by viewModel<MortalityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        var isNetworkAvail = false;
        connectionLiveData = NetworkConnectivityChecker(this)
        connectionLiveData.observe(this){ isNetworkAvailable ->
            isNetworkAvailable.let {
                if(it){
                    isNetworkAvail = true
                }
            }
        }
        firebaseAuth = Firebase.auth
        deceasedPersonViewModel.getDeceasedPersonsSync().isInitialized
        mortalityViewModel.getMortalitiesSync().isInitialized
        binding.dataIcon.setOnClickListener { forwardToReports() }
        binding.statsIcon.setOnClickListener { forwardToStats() }
        binding.profileIcon.setOnClickListener { forwardToProfile() }
        binding.mapIcon.setOnClickListener {
            if(isNetworkAvail)
            forwardToMap()
            else
                Toast.makeText(this, "No Internet! You can not use this function! Try again when you are online.", Toast.LENGTH_LONG).show()
        }
        setContentView(binding.root)
    }

    private fun forwardToMap(){
        Utils.switchActivity(
            this@MainActivity,
            MapsActivity::class.java,
            true
        )
    }

    private fun forwardToProfile() {
        Utils.switchActivity(
            this@MainActivity,
            ProfileActivity::class.java,
            true
        )
    }

    private fun forwardToStats() {
        Utils.switchActivity(
            this@MainActivity,
            StatisticsActivity::class.java,
            true
        )
    }

    private fun forwardToReports() {
        Utils.switchActivity(
            this@MainActivity,
            ReportActivity::class.java,
            true
        )
    }

    companion object {
        lateinit var connectionLiveData: NetworkConnectivityChecker
    }
}