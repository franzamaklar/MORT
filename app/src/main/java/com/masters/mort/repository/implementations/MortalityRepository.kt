package com.masters.mort.repository.implementations

import android.content.ContentValues
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.masters.mort.model.mortality.Mortality
import com.masters.mort.repository.interfaces.IMortalityRepository

class MortalityRepository : IMortalityRepository {

    private val firebaseStore: FirebaseFirestore = Firebase.firestore
    private val collectionCausesReference: CollectionReference = firebaseStore.collection("/causes")

    private val mortalitiesLiveData: MutableLiveData<MutableList<Mortality>?> = MutableLiveData<MutableList<Mortality>?>()
    private val causesLiveData: MutableLiveData<MutableList<String>?> = MutableLiveData<MutableList<String>?>()

    private val looper = Looper.myLooper()

    override fun save(mortality: Mortality, userID: String) {
        val collectionReference: CollectionReference =
            firebaseStore.collection("/users/${userID}/mortalities")
        val documentReference: DocumentReference = collectionReference.document()
        val newMortality: MutableMap<String, Any> = HashMap()
        newMortality["deceasedPersonID"] = mortality.deceasedPersonID
        newMortality["deathCause"] = mortality.mortalityCause
        newMortality["deathDateTime"] = mortality.deathDateTime
        newMortality["deathPlace"] = mortality.deathPlace
        documentReference.set(newMortality).addOnSuccessListener {
            Log.d(
                ContentValues.TAG,
                "onSuccess: Mortality for patient ${mortality.deceasedPersonID} is created"
            )
        }.addOnFailureListener { error ->
            Log.d(ContentValues.TAG, "onFailure: ${error.message}")
        }

        looper?.let {
            Handler(it).postDelayed({
                mortalitiesLiveData.value?.add(mortality)
            }, 1000)
        }
    }

    override fun delete(deceasedPersonID: String, userID: String) {
        var documentReference: DocumentReference
        val collectionReference: CollectionReference =
            firebaseStore.collection("/users/${userID}/mortalities")
        collectionReference.get().addOnSuccessListener { mortalities ->
            if (mortalities != null && !mortalities.isEmpty) {
                for (mort in mortalities) {
                    if (mort.data["deceasedPersonID"] as String == deceasedPersonID) {
                        documentReference = collectionReference.document(mort.id)
                        documentReference.delete()
                        Log.d(
                            ContentValues.TAG,
                            "onSuccess: Mortality of patient with id: $deceasedPersonID was deleted"
                        )
                    }
                }
            }
        }

        looper?.let {
            Handler(it).postDelayed({
                val updated: MutableList<Mortality>? = mortalitiesLiveData.value
                updated?.forEach { mortality ->
                    if (mortality.deceasedPersonID == deceasedPersonID) {
                        updated.remove(mortality)
                    }
                }
                if (updated != null) {
                    mortalitiesLiveData.postValue(updated)
                }
            }, 2000)
        }
    }

    override fun update(mortality: Mortality, userID: String) {
        val collectionReference: CollectionReference =
            firebaseStore.collection("/users/${userID}/mortalities")
        collectionReference.get().addOnSuccessListener { snapshots ->
            if (snapshots != null && !snapshots.isEmpty) {
                for (snapshot in snapshots) {
                    if (snapshot.data["deceasedPersonID"] as String == mortality.deceasedPersonID) {
                        collectionReference.document(snapshot.id).update(
                            "pid", mortality.deceasedPersonID,
                            "deathCause", mortality.mortalityCause,
                            "deathDateTime", mortality.deathDateTime,
                            "deathPlace", mortality.deathPlace
                        )
                    }
                }
            }
        }

        mortalitiesLiveData.value?.iterator()?.forEach { it ->
            if (it.deceasedPersonID == mortality.deceasedPersonID) {
                it.mortalityCause = mortality.mortalityCause
                it.deathDateTime = mortality.deathDateTime
                it.deathPlace = mortality.deathPlace
            }
        }
    }

    override fun getAllMortalities(userID: String): MutableLiveData<MutableList<Mortality>?> {
        mortalitiesLiveData.value?.clear()
        val collectionReference: CollectionReference =
            firebaseStore.collection("/users/${userID}/mortalities")
        collectionReference.get().addOnSuccessListener { snapshots ->

            if (snapshots != null && !snapshots.isEmpty) {
                val mortalities = mutableListOf<Mortality>()

                for (mortality in snapshots) {
                    mortalities.add(
                        Mortality(
                            mortality.data["deceasedPersonID"] as String,
                            mortality.data["deathCause"] as String,
                            mortality.data["deathDateTime"] as String,
                            mortality.data["deathPlace"] as String,
                        )
                    )
                }
                mortalitiesLiveData.postValue(mortalities)
                Log.d(ContentValues.TAG, "Successful data fetch")
            } else {
                Log.d(ContentValues.TAG, "No data")

            }
        }
        return mortalitiesLiveData
    }

    override fun getMoralityByDeceasedPersonId(deceasedPersonID: String): Mortality? {
        val mortalities = mortalitiesLiveData.value?.iterator()
        mortalities?.forEach {
            if (it.deceasedPersonID == deceasedPersonID) {
                return it
            }
        }
        return null
    }

    override fun getMortalityCauses(): MutableLiveData<MutableList<String>?> {
        val causes = mutableListOf<String>()
        collectionCausesReference.get().addOnSuccessListener {
            if (!it.isEmpty) {
                for (cause in it) {
                    causes.add(cause.data["name"] as String)
                }
            }
        }
        causesLiveData.postValue(causes)
        return causesLiveData
    }
}