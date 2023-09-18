package com.masters.mort.repository.interfaces

import androidx.lifecycle.MutableLiveData
import com.masters.mort.model.deceased_person.DeceasedPerson

interface IDeceasedPatientsRepository {
    fun save(deceasedPerson: DeceasedPerson, userID: String)
    fun update(deceasedPerson: DeceasedPerson, userID: String)
    fun delete(deceasedPerson: DeceasedPerson, userID: String)
    fun updateProcessed(deceasedPersonID: String, userID: String)
    fun getDeceasedPersonById(deceasedPersonID: String): DeceasedPerson?
    fun getAllDeceasedPersons(userID: String): MutableLiveData<MutableList<DeceasedPerson>?>
}