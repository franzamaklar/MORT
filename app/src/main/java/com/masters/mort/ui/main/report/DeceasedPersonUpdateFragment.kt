package com.masters.mort.ui.main.report

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.masters.mort.databinding.FragmentUpdateDeceasedPersonBinding
import com.masters.mort.model.deceased_person.DeceasedPerson
import com.masters.mort.utilities.DateConverter
import com.masters.mort.utilities.Validator
import com.masters.mort.viewmodel.DeceasedPersonViewModel


class DeceasedPersonUpdateFragment : Fragment() {

    private lateinit var binding: FragmentUpdateDeceasedPersonBinding
    private val args: DeceasedPersonUpdateFragmentArgs by navArgs()
    private lateinit var deceasedPersonViewModel: DeceasedPersonViewModel

    private lateinit var deceasedPersonBirthDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateDeceasedPersonBinding.inflate(layoutInflater)
        deceasedPersonViewModel = ViewModelProvider(
            requireActivity(),
            defaultViewModelProviderFactory
        )[DeceasedPersonViewModel::class.java]
        fillFields()
        binding.backToPreviousFragment.setOnClickListener { backToChoice() }
        binding.saveButton.setOnClickListener { updatePatientData() }
        return binding.root
    }

    private fun fillFields() {
        deceasedPersonViewModel.getDeceasedPersonById(args.deceasedPersonID).observe(viewLifecycleOwner) {
            if (it != null) {
                binding.nameInput.hint = it.name
                binding.surnameInput.hint = it.surname
                binding.personalIdentifierInput.hint = it.pid

                if (it.gender == "F") {
                    binding.genderFemale.isChecked = true
                } else
                    binding.genderMale.isChecked = true

                deceasedPersonBirthDate = it.birthDate
                val date = it.birthDate.split("/")
                binding.datePicker.updateDate(date[2].toInt(), date[1].toInt(), date[0].toInt())
                binding.datePicker.maxDate = DateConverter.convertDateToLong(String.format("${date[0]}/${date[1]}/${date[2]}"))
                binding.datePicker.minDate = DateConverter.convertDateToLong(String.format("01/01/1900"))
            }
        }
    }

    private fun updatePatientData() {
        try {
            val name =
                if (binding.nameInput.text.isNotEmpty()) binding.nameInput.text.toString() else binding.nameInput.hint.toString()
            val surname =
                if (binding.surnameInput.text.isNotEmpty()) binding.surnameInput.text.toString() else binding.surnameInput.hint.toString()
            val gender =
                if (binding.genderHolder.checkedRadioButtonId == binding.genderMale.id) binding.genderMale.text.toString() else binding.genderFemale.text.toString()
            val birthDate =
                String.format("${binding.datePicker.dayOfMonth}/${binding.datePicker.month + 1}/${binding.datePicker.year}")
            deceasedPersonBirthDate = birthDate
            val personalIdentifier =
                args.deceasedPersonID

            val deceasedPerson = DeceasedPerson(name, surname, birthDate, gender, personalIdentifier, args.isProcessed)
            val errors = Validator.validateDeceasedPersonData(deceasedPerson, null)

            if(errors.isNotEmpty()){
                Toast.makeText(context, errors.first().Description, Toast.LENGTH_LONG).show()
            }else {
                deceasedPersonViewModel.updateDeceasedPerson(
                    deceasedPerson
                )
                Toast.makeText(context, "Patient data updated successfully!", Toast.LENGTH_LONG)
                    .show()
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
            DeceasedPersonUpdateFragmentDirections.actionUpdateDeceasedPersonFragmentToUpdateChoiceFragment(args.deceasedPersonID, args.isProcessed,deceasedPersonBirthDate)
        findNavController().navigate(action)
    }
}