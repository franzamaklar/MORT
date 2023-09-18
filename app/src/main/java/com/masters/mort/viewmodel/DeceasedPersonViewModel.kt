package com.masters.mort.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.masters.mort.model.deceased_person.DeceasedPerson
import com.masters.mort.repository.implementations.DeceasedPersonRepository

class DeceasedPersonViewModel(
    private val deceasedPersonRepository: DeceasedPersonRepository
): ViewModel() {

    var userID: String = Firebase.auth.currentUser?.uid as String

    private val deceasedPerson:MutableLiveData<DeceasedPerson> = MutableLiveData<DeceasedPerson>()


    fun saveDeceasedPerson(deceasedPerson: DeceasedPerson){
        deceasedPersonRepository.save(deceasedPerson, userID)
    }

    fun updateDeceasedPerson(deceasedPerson: DeceasedPerson){
        deceasedPersonRepository.update(deceasedPerson, userID)
    }

    fun getDeceasedPersonById(pid: String): MutableLiveData<DeceasedPerson> {
        val deceasedPerson = deceasedPersonRepository.getDeceasedPersonById(pid)
        this.deceasedPerson.postValue(deceasedPerson)
        return this.deceasedPerson
    }

    fun getDeceasedPersonsSync(): MutableLiveData<MutableList<DeceasedPerson>?>{
        return deceasedPersonRepository.getAllDeceasedPersons(userID)
    }

    fun updateProcessed(pid: String){
        deceasedPersonRepository.updateProcessed(pid, userID)
    }

    fun deleteDeceasedPerson(deceasedPerson: DeceasedPerson){
        deceasedPersonRepository.delete(deceasedPerson, userID)
    }

}