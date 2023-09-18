package com.masters.mort.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.addCallback
import com.masters.mort.databinding.ActivityReportBinding
import com.masters.mort.utilities.NetworkConnectivityChecker
import com.masters.mort.utilities.Utils
import com.masters.mort.viewmodel.MortalityViewModel
import com.masters.mort.viewmodel.DeceasedPersonViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportBinding
    private val deceasedPersonViewModel by viewModel<DeceasedPersonViewModel>()
    private val mortalityViewModel by viewModel<MortalityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        connectionLiveData = NetworkConnectivityChecker(this)
        deceasedPersonViewModel.getDeceasedPersonsSync().isInitialized
        mortalityViewModel.getMortalitiesSync().isInitialized
        mortalityViewModel.getMortalityCauses()?.isInitialized
        binding.backButton.setOnClickListener { backToMain() }
        setContentView(binding.root)
    }

    private fun backToMain() {
        Utils.switchActivity(
            this@ReportActivity,
            MainActivity::class.java,
            true
        )
    }

    companion object {
        lateinit var connectionLiveData: NetworkConnectivityChecker
    }
}