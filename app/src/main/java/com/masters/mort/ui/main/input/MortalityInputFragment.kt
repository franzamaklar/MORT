package com.masters.mort.ui.main.input

import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.masters.mort.databinding.FragmentMortalityInputBinding
import com.masters.mort.model.mortality.Mortality
import com.masters.mort.utilities.DateConverter
import com.masters.mort.utilities.Validator
import com.masters.mort.viewmodel.MortalityViewModel
import com.masters.mort.viewmodel.DeceasedPersonViewModel
import java.util.*


class MortalityInputFragment : Fragment() {

    private lateinit var binding: FragmentMortalityInputBinding
    private var causes: MutableList<String> = mutableListOf("Loading data")
    private lateinit var selectedCause: String

    private lateinit var deceasedPersonViewModel: DeceasedPersonViewModel
    private lateinit var viewModelMortality: MortalityViewModel
    private val args: MortalityInputFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMortalityInputBinding.inflate(layoutInflater)
        binding.descriptionWithDeceasedPersonID.append(":" + args.deceasedPersonId)
        deceasedPersonViewModel = ViewModelProvider(
            requireActivity(),
            defaultViewModelProviderFactory
        )[DeceasedPersonViewModel::class.java]
        viewModelMortality = ViewModelProvider(
            requireActivity(),
            defaultViewModelProviderFactory
        )[MortalityViewModel::class.java]
        setUpPicker()
        setUpSpinner()
        binding.saveButton.setOnClickListener { save() }
        return binding.root
    }

    private fun setUpPicker() {
        val maxYear = Calendar.getInstance().get(Calendar.YEAR)
        val maxMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val maxDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val maxDateInString = String.format("$maxDay/$maxMonth/$maxYear")
        val minDate = args.lowerBoundDate.split("/")
        binding.datePicker.minDate = DateConverter.convertDateToLong(String.format("${minDate[0]}/${minDate[1]}/${minDate[2]}"))
        binding.datePicker.maxDate = DateConverter.convertDateToLong(maxDateInString)
    }

    private fun setUpSpinner() {
        viewModelMortality.getMortalityCauses()?.observe(viewLifecycleOwner) { list ->
            if (list != null && list.isNotEmpty()) {
                causes.clear()
                causes.addAll(list)
                val populatedAdapter = ArrayAdapter(
                    context!!,
                    android.R.layout.simple_spinner_dropdown_item,
                    causes
                )
                bindToSpinner(populatedAdapter)
            } else {
                val unPopulatedAdapter = ArrayAdapter(
                    context!!,
                    android.R.layout.simple_spinner_dropdown_item,
                    causes
                )
                bindToSpinner(unPopulatedAdapter)
            }
        }
    }

    private fun bindToSpinner(adapter: ArrayAdapter<String>) {
        binding.causesSpinner.adapter = adapter
        binding.causesSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                (view as TextView).setTextColor(Color.WHITE)
                selectedCause = causes[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun save() {
        try {
            val time =
                String.format("${Calendar.getInstance().get(Calendar.HOUR_OF_DAY)}:${Calendar.getInstance().get(Calendar.MINUTE)}:${Calendar.getInstance().get(Calendar.SECOND)}")
            val deathDateTime =
                String.format("${binding.datePicker.dayOfMonth}/${binding.datePicker.month + 1}/${binding.datePicker.year}/ $time")
            val deathPlace = binding.placeInput.text.toString()

            val mortality = Mortality(args.deceasedPersonId, selectedCause, deathDateTime, deathPlace)
            val errors = Validator.validateMortalityDataForDeceasedPersons(mortality)

            if (errors.isNotEmpty()) {
                Toast.makeText(
                    context,
                    errors.first().Description,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                viewModelMortality.saveMortality(
                    Mortality(
                        args.deceasedPersonId,
                        selectedCause,
                        deathDateTime,
                        deathPlace
                    )
                )
                deceasedPersonViewModel.updateProcessed(args.deceasedPersonId)
                Toast.makeText(context, "Saving data. Please wait.", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        val action =
                            MortalityInputFragmentDirections.actionMortalityInputFragmentToDeceasedPersonInputFragment()
                        findNavController().navigate(action)
                        Toast.makeText(
                            context,
                            "Saving successful. You may continue with work.",
                            Toast.LENGTH_LONG
                        ).show()
                    }, 2000
                )
            }
        } catch (exception: Exception) {
            Toast.makeText(
                context,
                "Unexpected error occurred, please wait and try again.",
                Toast.LENGTH_LONG
            ).show()
            Log.e(ContentValues.TAG, "Error: ${exception.message}")
        }
    }
}