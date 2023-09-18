package com.masters.mort.ui.authentication

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.masters.mort.databinding.FragmentCreateAccountBinding
import com.masters.mort.model.Error
import com.masters.mort.model.user.User
import com.masters.mort.ui.main.MainActivity
import com.masters.mort.utilities.DateConverter
import com.masters.mort.utilities.Validator
import com.masters.mort.viewmodel.UserViewModel
import java.lang.Exception
import java.util.*

class CreateAccountFragment : Fragment() {

    private lateinit var binding: FragmentCreateAccountBinding

    private var counties: MutableList<String> = mutableListOf("Loading data!")
    private lateinit var selectedCounty: String
    private var notAllowedEmails: MutableList<String> = mutableListOf()

    private var firebaseAuth: FirebaseAuth = Firebase.auth
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateAccountBinding.inflate(layoutInflater)
        userViewModel = ViewModelProvider(
            requireActivity(),
            defaultViewModelProviderFactory
        )[UserViewModel::class.java]
        var isNetworkActive = false
        val networkConnectivity = AuthenticationActivity.connectionLiveData
        networkConnectivity.observe(this){ isNetworkAvailable ->
            isNetworkAvailable.let {
                isNetworkActive = it
            }
        }
        fillUpDataFromDatabase()
        setUpPicker()
        setUpSpinner()
        binding.buttonRegister.setOnClickListener {
            if (isNetworkActive) {
                register()
            }else{
                Toast.makeText(context, "No Internet! You can not use this function! Try again when you are online.", Toast.LENGTH_LONG).show()
            }
        }
        binding.signRequest.setOnClickListener { redirectToLogin() }
        return binding.root
    }

    private fun setUpPicker() {
        val maxYear = Calendar.getInstance().get(Calendar.YEAR) - 18
        val maxMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val maxDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val maxDateInString = String.format("$maxDay/$maxMonth/$maxYear")
        val maxDate = DateConverter.convertDateToLong(maxDateInString)
        val minDate = DateConverter.convertDateToLong("01/01/1900")
        binding.datePicker.maxDate = maxDate
        binding.datePicker.minDate = minDate
    }

    private fun fillUpDataFromDatabase() {
        userViewModel.users.observe(viewLifecycleOwner) { users ->
            if (!users.isNullOrEmpty()) {
                users.forEach { user ->
                    notAllowedEmails.add(user.email)
                }
            }
        }

        userViewModel.getCounties().observe(viewLifecycleOwner) { counties ->
            if (!counties.isNullOrEmpty()) {
                this.counties.clear()
                this.counties.addAll(counties)
            }
        }
    }


    private fun setUpSpinner() {
        val adapter = context?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_dropdown_item,
                counties
            )
        }
        binding.countySpinner.adapter = adapter
        binding.countySpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                (view as TextView).setTextColor(Color.BLACK)
                selectedCounty = counties[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun register() {
        try {
            val name = binding.nameInput.text.toString()
            val surname = binding.surnameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = binding.passwordInput.text.toString()

            val gender =
                if (binding.genderHolder.checkedRadioButtonId == binding.genderMale.id)
                    binding.genderMale.text.toString()
                else
                    binding.genderFemale.text.toString()

            val birthDate =
                String.format("${binding.datePicker.dayOfMonth}/${binding.datePicker.month}/${binding.datePicker.year}")

            val newUser = User("", name, surname, email, birthDate, gender, selectedCounty)
            val isConsentChecked = binding.checkForConsent.isChecked
            val errors: MutableList<Error> = Validator.validateUserForRegistration(
                newUser, password, isConsentChecked, notAllowedEmails)
            if (errors.isNotEmpty()) {
                var collectedErrors = String()
                errors.forEach { error -> collectedErrors += (error.Description + " ") }
                Toast.makeText(context, collectedErrors, Toast.LENGTH_LONG).show()

            } else {
                userViewModel.saveUser(newUser, password)
                Handler(Looper.getMainLooper()).postDelayed({
                        if (firebaseAuth.currentUser != null) {
                            Toast.makeText(context, "Sign in successful", Toast.LENGTH_LONG).show()
                            startActivity(Intent(context, MainActivity::class.java))
                        } else {
                            Toast.makeText(context, "Unsuccessful sign in", Toast.LENGTH_LONG).show()
                        } }, 2000)
            }
        } catch (exception: Exception) {
            Toast.makeText(context, "Unexpected error occurred, please wait and try again.", Toast.LENGTH_LONG).show()
            Log.e(ContentValues.TAG, "Error: ${exception.message}")
        }
    }

    private fun redirectToLogin() {
        val action = CreateAccountFragmentDirections.actionCreateAccountFragmentToLoginFragment()
        findNavController().navigate(action)
    }
}