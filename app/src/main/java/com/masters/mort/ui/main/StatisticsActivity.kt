package com.masters.mort.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.addCallback
import com.masters.mort.R
import com.masters.mort.databinding.ActivityStatisticsBinding
import com.masters.mort.ui.main.stats.StatisticsFragment
import com.masters.mort.viewmodel.MortalityViewModel
import com.masters.mort.viewmodel.DeceasedPersonViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class StatisticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsBinding
    private val deceasedPersonViewModel by viewModel<DeceasedPersonViewModel>()
    private val mortalityViewModel by viewModel<MortalityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val statisticsFragment = StatisticsFragment()
        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.root_container, statisticsFragment)
                .commitAllowingStateLoss()
        }
        deceasedPersonViewModel.getDeceasedPersonsSync().isInitialized
        mortalityViewModel.getMortalitiesSync().isInitialized
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}