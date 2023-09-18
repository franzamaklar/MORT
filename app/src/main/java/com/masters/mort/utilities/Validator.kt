package com.masters.mort.utilities

import android.text.TextUtils
import com.masters.mort.model.Error
import com.masters.mort.model.deceased_person.DeceasedPerson
import com.masters.mort.model.mortality.Mortality
import com.masters.mort.model.user.User

class Validator {

    companion object {

        fun validateUserForRegistration(
            user: User,
            password: String,
            isConsentChecked: Boolean,
            notAllowedEmails: List<String>
        ): MutableList<Error> {
            val errors: MutableList<Error> = mutableListOf()
            if (TextUtils.isEmpty(user.name) ||
                TextUtils.isEmpty(user.surname)
                || TextUtils.isEmpty(user.email)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(user.gender)
            ) {
                val error = Error(
                    "Invalid input",
                    "Personal data missing! Please check your input and try again!"
                )
                errors.add(error)
            }
            if (!isConsentChecked) {
                val error =
                    Error("Not checked consent", "Please check the box for consent to sign up!")
                errors.add(error)
            }
            if (notAllowedEmails.any { email -> email == user.email }) {
                val error = Error(
                    "Not allowed email",
                    "Email already used for sign up! Use another email please!"
                )
                errors.add(error)
            }
            return errors
        }

        fun validateUserForLogin(email: String, password: String): MutableList<Error> {
            val errors: MutableList<Error> = mutableListOf()
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                val error = Error(
                    "Invalid credentials input",
                    "Missing credentials! Please check your input again."
                )
                errors.add(error)
            }
            return errors
        }

        fun validateUserPasswordForUpdate(password: String): MutableList<Error> {
            val errors: MutableList<Error> = mutableListOf()
            if (TextUtils.isEmpty(password)) {
                val error = Error("Invalid password input", "Insert your password please!")
                errors.add(error)
            }
            return errors
        }

        fun validateDeceasedPersonData(deceasedPerson: DeceasedPerson, notAllowedPersonalIdentifiers: MutableList<String>?): MutableList<Error>{
            val errors: MutableList<Error> = mutableListOf()
            if(TextUtils.isEmpty(deceasedPerson.name) || TextUtils.isEmpty(deceasedPerson.surname) || TextUtils.isEmpty(
                deceasedPerson.pid
            ) || TextUtils.isEmpty(deceasedPerson.gender) || TextUtils.isEmpty(deceasedPerson.birthDate)){
                val error = Error("Invalid data input!", "Invalid deceased person input, please check your input again!")
                errors.add(error)
            }

            if(notAllowedPersonalIdentifiers != null && notAllowedPersonalIdentifiers.any { it == deceasedPerson.pid }){
                val error = Error("Invalid data input!", "Deceased person with given identifier already exists")
                errors.add(error)
            }

            if(deceasedPerson.pid.count() != 11){
                val error = Error("Invalid data input!", "Personal identifier must be 11 digits long!")
                errors.add(error)
            }
            return errors
        }

        fun validateMortalityDataForDeceasedPersons(mortality: Mortality): MutableList<Error>{
            val errors: MutableList<Error> = mutableListOf()
            if(TextUtils.isEmpty(mortality.deathPlace) || TextUtils.isEmpty(mortality.deathPlace) || TextUtils.isEmpty(mortality.mortalityCause) || TextUtils.isEmpty(mortality.deceasedPersonID)){
                val error = Error("Invalid data for deceased persons", "Invalid data! Please check your input again.")
                errors.add(error)
            }
            return errors
        }

    }
}