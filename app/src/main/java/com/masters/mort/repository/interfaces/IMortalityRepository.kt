package com.masters.mort.repository.interfaces

import androidx.lifecycle.MutableLiveData
import com.masters.mort.model.mortality.Mortality

interface IMortalityRepository {
    fun save(mortality:Mortality, userID: String)
    fun delete(deceasedPersonID: String, userID:String)
    fun update(mortality: Mortality, userID: String)
    fun getAllMortalities(userID:String): MutableLiveData<MutableList<Mortality>?>
    fun getMoralityByDeceasedPersonId(deceasedPersonID: String) : Mortality?
    fun getMortalityCauses(): MutableLiveData<MutableList<String>?>
}