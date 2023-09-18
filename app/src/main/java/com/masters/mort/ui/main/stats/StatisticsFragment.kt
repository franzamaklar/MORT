package com.masters.mort.ui.main.stats

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.PieChart
import com.masters.mort.R
import com.masters.mort.databinding.FragmentStatisticsBinding
import com.masters.mort.ui.main.MainActivity
import com.masters.mort.ui.main.stats.utilities.PieDrawer
import com.masters.mort.utilities.Utils
import com.masters.mort.viewmodel.MortalityViewModel
import com.masters.mort.viewmodel.DeceasedPersonViewModel
import java.util.*

class StatisticsFragment : Fragment() {

    private lateinit var binding: FragmentStatisticsBinding

    private lateinit var viewModelPatient: DeceasedPersonViewModel
    private lateinit var viewModelMort: MortalityViewModel
    private lateinit var pieChart: PieChart

    private var demographicData: MutableMap<String, Int> = mutableMapOf()
    private var causesData: MutableMap<String, Int> = mutableMapOf()

    private lateinit var years: Array<String>
    private lateinit var initialsOfCauses: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(layoutInflater)
        viewModelPatient = ViewModelProvider(
            requireActivity(),
            defaultViewModelProviderFactory
        )[DeceasedPersonViewModel::class.java]
        viewModelMort = ViewModelProvider(
            requireActivity(),
            defaultViewModelProviderFactory
        )[MortalityViewModel::class.java]
        prepareDataForVisualization()
        pieChart = binding.pieChart
        pieChart.setNoDataText("Choose one of the options to visualize.")
        binding.showDemographic.setOnClickListener {
            showStats(
                demographicData,
                years,
                "Demographic"
            )
        }
        binding.showCauses.setOnClickListener { showStats(causesData, initialsOfCauses, "Cause") }
        binding.backButton.setOnClickListener { backToMain() }
        return binding.root
    }

    private fun prepareDataForVisualization() {
        years = resources.getStringArray(R.array.years)
        initialsOfCauses = resources.getStringArray(R.array.initialsOfCauses)
        val causes = resources.getStringArray(R.array.causes)
        var incrementedValue: Int

        for (i in 0..4) {
            demographicData[years.elementAt(i)] = 0
            causesData[initialsOfCauses.elementAt(i)] = 0
        }

        viewModelPatient.getDeceasedPersonsSync().observe(viewLifecycleOwner) { patients ->
            if (patients != null) {
                if (patients.isNotEmpty()) {
                    patients.iterator().forEach { patient ->
                        val birthDate = patient.birthDate.split("/")
                        when (Calendar.getInstance().get(Calendar.YEAR)
                            .minus(birthDate[2].toInt())) {
                            in 0..12 -> {
                                incrementedValue =
                                    demographicData.getValue(years[0]).inc()
                                demographicData[years[0]] = incrementedValue
                            }
                            in 13..26 -> {
                                incrementedValue =
                                    demographicData.getValue(years[1]).inc()
                                demographicData[years[1]] = incrementedValue
                            }
                            in 27..40 -> {
                                incrementedValue =
                                    demographicData.getValue(years[2]).inc()
                                demographicData[years[2]] = incrementedValue
                            }
                            in 41..56 -> {
                                incrementedValue =
                                    demographicData.getValue(years[3]).inc()
                                demographicData[years[3]] = incrementedValue
                            }
                            else -> {
                                incrementedValue =
                                    demographicData.getValue(years[4]).inc()
                                demographicData[years[4]] = incrementedValue
                            }
                        }
                    }
                }
            }
        }

        viewModelMort.getMortalitiesSync().observe(viewLifecycleOwner) { mortalities ->
            if (mortalities != null) {
                if (mortalities.isNotEmpty()) {
                    mortalities.iterator().forEach { mortality ->
                        if (mortality.mortalityCause.equals(causes.elementAt(0))) {
                            incrementedValue = causesData.getValue(initialsOfCauses[0]).inc()
                            causesData[initialsOfCauses[0]] = incrementedValue
                        } else if (mortality.mortalityCause.equals(causes.elementAt(1))) {
                            incrementedValue = causesData.getValue(initialsOfCauses[1]).inc()
                            causesData[initialsOfCauses[1]] = incrementedValue

                        } else if (mortality.mortalityCause.equals(causes.elementAt(2))) {
                            incrementedValue = causesData.getValue(initialsOfCauses[2]).inc()
                            causesData[initialsOfCauses[2]] = incrementedValue
                        } else if (mortality.mortalityCause.equals(causes.elementAt(3))) {
                            incrementedValue = causesData.getValue(initialsOfCauses[3]).inc()
                            causesData[initialsOfCauses[3]] = incrementedValue
                        } else {
                            incrementedValue = causesData.getValue(initialsOfCauses[4]).inc()
                            causesData[initialsOfCauses[4]] = incrementedValue
                        }
                    }
                }
            }
        }
    }

    private fun showStats(
        dataToShow: MutableMap<String, Int>,
        propertiesData: Array<String>,
        legendName: String
    ) {
        try {
            val isCreated = PieDrawer.createPieWithStatistics(
                pieChart,
                dataToShow,
                propertiesData,
                requireActivity()
            )
            if(isCreated) {
                setUpLegend(legendName)
            }else{
                Toast.makeText(context, "No data available in database! Enter some data to visualize!", Toast.LENGTH_LONG).show()
            }
        } catch (exception: Exception) {
            Toast.makeText(
                context,
                "Showing statistics has gone wrong, please try again!",
                Toast.LENGTH_LONG
            ).show()
            exception.message?.let { Log.e(ContentValues.TAG, it) }
        }
    }


    private fun setUpLegend(legendName: String) {
        if (legendName.contains("Demographic")) {
            binding.firstDesc.text = years.elementAt(0)
            binding.secondDesc.text = years.elementAt(1)
            binding.thirdDesc.text = years.elementAt(2)
            binding.fourthDesc.text = years.elementAt(3)
            binding.fifthDesc.text = years.elementAt(4)
        } else {
            binding.firstDesc.text = initialsOfCauses.elementAt(0)
            binding.secondDesc.text = initialsOfCauses.elementAt(1)
            binding.thirdDesc.text = initialsOfCauses.elementAt(2)
            binding.fourthDesc.text = initialsOfCauses.elementAt(3)
            binding.fifthDesc.text = initialsOfCauses.elementAt(4)
        }
    }

    private fun backToMain() {
        Utils.switchActivity(
            requireActivity(),
            MainActivity::class.java,
            true
        )
    }
}