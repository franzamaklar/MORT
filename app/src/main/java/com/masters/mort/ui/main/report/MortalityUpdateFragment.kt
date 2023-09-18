package com.masters.mort.ui.main.report

import android.content.ContentValues
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.masters.mort.databinding.FragmentUpdateMortalityBinding
import com.masters.mort.model.mortality.Mortality
import com.masters.mort.utilities.DateConverter
import com.masters.mort.utilities.Validator
import com.masters.mort.viewmodel.MortalityViewModel
import java.util.*

class MortalityUpdateFragment : Fragment() {

    private lateinit var binding: FragmentUpdateMortalityBinding

    private lateinit var viewModelMortality: MortalityViewModel
    private val args: MortalityUpdateFragmentArgs by navArgs()

    private var causes: MutableList<String> = mutableListOf("Loading data!")
    private lateinit var selectedCause: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateMortalityBinding.inflate(layoutInflater)
        viewModelMortality = ViewModelProvider(
            requireActivity(),
            defaultViewModelProviderFactory
        )[MortalityViewModel::class.java]
        try {
            fillFields()
        }catch (exception:Exception){
            Toast.makeText(context, "Error while fetching data!", Toast.LENGTH_LONG).show()
        }
        binding.saveButton.setOnClickListener { updateMortalityData() }
        binding.backToPreviousFragment.setOnClickListener { backToChoice() }
        return binding.root
    }

    private fun fillFields() {
        viewModelMortality.getMortalityByDeceasedPersonID(args.deceasedPersonID).observe(viewLifecycleOwner) {
            if (it != null) {
                binding.descriptionWithPatientID.text = it.deceasedPersonID
                selectedCause = it.mortalityCause
                binding.placeInput.hint = it.deathPlace
                val maxDate = it.deathDateTime.split("/")
                val minDate = args.lowerBoundDate.split("/")
                binding.datePicker.updateDate(maxDate[2].toInt(), maxDate[1].toInt(), maxDate[0].toInt())
                binding.datePicker.maxDate = DateConverter.convertDateToLong(String.format("${maxDate[0]}/${maxDate[1]}/${maxDate[2]}"))
                binding.datePicker.minDate = DateConverter.convertDateToLong(String.format("${minDate[0]}/${minDate[1]}/${minDate[2]}"))
                setUpSpinner()
            }
        }
    }

    private fun setUpSpinner() {
        viewModelMortality.getMortalityCauses()?.observe(viewLifecycleOwner) { listOfCauses ->
            if (listOfCauses != null && listOfCauses.isNotEmpty()) {
                causes.clear()
                causes.addAll(listOfCauses)
                causes.sortByDescending { it == selectedCause }
                val adapter =
                    context?.let {
                        ArrayAdapter(
                            it,
                            android.R.layout.simple_spinner_dropdown_item,
                            causes
                        )
                    }
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
        }
    }

    private fun updateMortalityData() {
        try {
            val deathPlace =
                if (binding.placeInput.text.isNotEmpty()) binding.placeInput.text.toString() else binding.placeInput.hint.toString()
            val time =
                String.format("${Calendar.getInstance().get(Calendar.HOUR_OF_DAY)}:${Calendar.getInstance().get(Calendar.MINUTE)}:${Calendar.getInstance().get(Calendar.SECOND)}")
            val deathDateTime =
                String.format("${binding.datePicker.dayOfMonth}/${binding.datePicker.month + 1}/${binding.datePicker.year}/ $time")

            val mortality = Mortality(args.deceasedPersonID, selectedCause, deathDateTime, deathPlace)
            val errors = Validator.validateMortalityDataForDeceasedPersons(mortality)

            if (errors.isNotEmpty()) {
                Toast.makeText(
                    context,
                    errors.first().Description,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                viewModelMortality.updateMortality(
                    mortality
                )
                Toast.makeText(
                    context,
                    "Updated mortality data for patient ${args.deceasedPersonID}!",
                    Toast.LENGTH_SHORT
                ).show()
                backToChoice()
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

    private fun backToChoice() {
        val action =
            MortalityUpdateFragmentDirections.actionUpdateMortalityFragmentToUpdateChoiceFragment(
                args.deceasedPersonID,
            true, args.lowerBoundDate)
        findNavController().navigate(action)
    }
}