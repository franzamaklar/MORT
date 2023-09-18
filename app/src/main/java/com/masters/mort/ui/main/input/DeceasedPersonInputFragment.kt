package com.masters.mort.ui.main.input

import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.masters.mort.databinding.FragmentDeceasedPersonInputBinding
import com.masters.mort.model.deceased_person.DeceasedPerson
import com.masters.mort.utilities.DateConverter
import com.masters.mort.utilities.Validator
import com.masters.mort.viewmodel.MortalityViewModel
import com.masters.mort.viewmodel.DeceasedPersonViewModel
import java.util.*

class DeceasedPersonInputFragment : Fragment() {

    private lateinit var binding: FragmentDeceasedPersonInputBinding
    private lateinit var viewModelDeceasedPerson: DeceasedPersonViewModel
    private lateinit var viewModelMortality: MortalityViewModel
    private var notAllowedPersonalIDs = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeceasedPersonInputBinding.inflate(layoutInflater)
        requireActivity().onBackPressedDispatcher.addCallback(this){
            this.isEnabled = false
            Toast.makeText(context, "You can not go back to previous state! Please use navigation!", Toast.LENGTH_LONG).show()
        }
        viewModelDeceasedPerson = ViewModelProvider(
            requireActivity(),
            defaultViewModelProviderFactory
        )[DeceasedPersonViewModel::class.java]
        viewModelMortality = ViewModelProvider(
            requireActivity(),
            defaultViewModelProviderFactory
        )[MortalityViewModel::class.java]
        setUpPicker()
        binding.nextButton.setOnClickListener { saveDeceasedPersonData() }
        try {
            forwardToMortalityInputIfNeededOrFillData()
        }catch (exception: Exception){
            Toast.makeText(context, "Error while forwarding to mortality data input. Please reload application!", Toast.LENGTH_LONG).show()
        }
        return binding.root
    }

    private fun setUpPicker() {
        val maxYear = Calendar.getInstance().get(Calendar.YEAR)
        val maxMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val maxDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val maxDateInString = String.format("$maxDay/$maxMonth/$maxYear")
        val maxDate = DateConverter.convertDateToLong(maxDateInString)
        val minDate = DateConverter.convertDateToLong("01/01/1900")
        binding.datePicker.maxDate = maxDate
        binding.datePicker.minDate = minDate
    }


    private fun forwardToMortalityInputIfNeededOrFillData() {
        viewModelMortality.getMortalityCauses()
        viewModelDeceasedPerson.getDeceasedPersonsSync().observe(viewLifecycleOwner) { deceasedPersons ->
            if (!deceasedPersons.isNullOrEmpty()) {
                if (deceasedPersons.any { !it.processed }) {
                    deceasedPersons.first { !it.processed }
                        .let {
                            val action =
                                DeceasedPersonInputFragmentDirections.actionPatientInputFragmentToMortalityInputFragment(
                                    it.pid,
                                    it.birthDate
                                )
                            findNavController().navigate(action)
                        }
                }else {
                    deceasedPersons.forEach {
                        notAllowedPersonalIDs.add(it.pid)
                    }
                }
            }
        }
    }


    private fun saveDeceasedPersonData() {
        try {
            val name = binding.nameInput.text.toString()
            val surname = binding.surnameInput.text.toString()
            val gender =
                if (binding.genderHolder.checkedRadioButtonId == binding.genderMale.id) binding.genderMale.text.toString() else binding.genderFemale.text.toString()
            val birthDate =
                String.format("${binding.datePicker.dayOfMonth}/${binding.datePicker.month + 1}/${binding.datePicker.year}")
            val personalIdentifier = binding.personalIdentifierInput.text.toString()

            val deceasedPerson = DeceasedPerson(name, surname, birthDate, gender, personalIdentifier, false)
            val errors = Validator.validateDeceasedPersonData(deceasedPerson, notAllowedPersonalIDs)

            if (errors.isNotEmpty()) {
                var collectedErrors = String()
                errors.forEach { error -> collectedErrors += (error.Description + " ") }
                    Toast.makeText(context, collectedErrors, Toast.LENGTH_LONG).show()
            }
            else {
                viewModelDeceasedPerson.saveDeceasedPerson(
                    DeceasedPerson(
                        name,
                        surname,
                        birthDate,
                        gender,
                        personalIdentifier,
                        false
                    )
                )
                val action =
                    DeceasedPersonInputFragmentDirections.actionPatientInputFragmentToMortalityInputFragment(
                        personalIdentifier,
                        birthDate
                    )
                findNavController().navigate(action)
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