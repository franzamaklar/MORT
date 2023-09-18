package com.masters.mort.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.masters.mort.model.mortality.Mortality
import com.masters.mort.repository.implementations.MortalityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MortalityViewModel(
    private val mortalityRepository: MortalityRepository
): ViewModel() {

    var userID: String = Firebase.auth.currentUser?.uid as String
    var mortality:MutableLiveData<Mortality> = MutableLiveData<Mortality>()

    private val mortalities: MutableLiveData<MutableList<Mortality>?> = MutableLiveData<MutableList<Mortality>?>()

    init {
        loadMortalities()
    }

    fun saveMortality(mortality: Mortality){
        mortalityRepository.save(mortality, userID)
        loadMortalities()
    }

    fun deleteMortality(patientID: String){
        mortalityRepository.delete(patientID, userID)
    }

    fun updateMortality(mortality: Mortality){
        mortalityRepository.update(mortality, userID)
    }

    fun getMortalityByDeceasedPersonID(patientID: String): MutableLiveData<Mortality> {
        loadMortalities()
        val mortality = mortalityRepository.getMoralityByDeceasedPersonId(patientID)
        this.mortality.postValue(mortality)
        return this.mortality
    }

    fun getMortalitiesSync():MutableLiveData<MutableList<Mortality>?>{
        return mortalityRepository.getAllMortalities(userID)
    }

    fun getMortalityCauses(): MutableLiveData<MutableList<String>?>? {
        return mortalityRepository.getMortalityCauses()
    }

    fun loadMortalities(){
        viewModelScope.launch {
            val allMortalities: MutableLiveData<MutableList<Mortality>?> = withContext(Dispatchers.Default){
                return@withContext mortalityRepository.getAllMortalities(userID)
            }
            mortalities.value = allMortalities.value
        }
    }
}