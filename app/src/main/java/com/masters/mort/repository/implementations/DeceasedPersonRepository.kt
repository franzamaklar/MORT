package com.masters.mort.repository.implementations

import android.content.ContentValues.TAG
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.masters.mort.model.deceased_person.DeceasedPerson
import com.masters.mort.repository.interfaces.IDeceasedPatientsRepository

class DeceasedPersonRepository : IDeceasedPatientsRepository {

    private val firebaseStore: FirebaseFirestore = Firebase.firestore
    private val deceasedPersonsLiveData: MutableLiveData<MutableList<DeceasedPerson>?> =
        MutableLiveData<MutableList<DeceasedPerson>?>()

    private val looper = Looper.myLooper()

    override fun save(deceasedPerson: DeceasedPerson, userID: String) {
        val collectionReference: CollectionReference =
            firebaseStore.collection("/users/${userID}/deceased_persons")
        val documentReference: DocumentReference = collectionReference.document()
        val newPerson: MutableMap<String, Any> = HashMap()
        newPerson["pid"] = deceasedPerson.pid
        newPerson["name"] = deceasedPerson.name
        newPerson["surname"] = deceasedPerson.surname
        newPerson["birthDate"] = deceasedPerson.birthDate
        newPerson["gender"] = deceasedPerson.gender
        newPerson["processed"] = deceasedPerson.processed

        documentReference.set(newPerson).addOnSuccessListener {
            Log.d(
                TAG,
                "onSuccess: Deceased Person is created for ${deceasedPerson.pid}"
            )
        }.addOnFailureListener { error ->
            Log.d(TAG, "onFailure: ${error.message}")
        }

        looper?.let {
            Handler(it).postDelayed({
                deceasedPersonsLiveData.value?.add(deceasedPerson)
            }, 1000)
        }
    }

    override fun update(deceasedPerson: DeceasedPerson, userID: String) {
        val collectionReference: CollectionReference =
            firebaseStore.collection("/users/${userID}/deceased_persons")
        collectionReference.get().addOnSuccessListener { deceasedPersons ->
            if (deceasedPersons != null && !deceasedPersons.isEmpty) {
                for (deceased in deceasedPersons) {
                    if (deceased.data["pid"] as String == deceasedPerson.pid) {
                        collectionReference.document(deceased.id).update(
                            "name", deceasedPerson.name,
                            "surname", deceasedPerson.surname,
                            "birthDate", deceasedPerson.birthDate,
                            "gender", deceasedPerson.gender
                        )
                    }
                }
            }
        }

        deceasedPersonsLiveData.value?.iterator()?.forEach { deceasedPersons ->
            if (deceasedPersons.pid == deceasedPerson.pid) {
                deceasedPersons.name = deceasedPerson.name
                deceasedPersons.surname = deceasedPerson.surname
                deceasedPersons.gender = deceasedPerson.gender
                deceasedPerson.birthDate = deceasedPerson.birthDate
            }
        }
    }

    override fun delete(deceasedPerson: DeceasedPerson, userID: String) {
        var documentReference: DocumentReference
        val collectionReference: CollectionReference =
            firebaseStore.collection("/users/${userID}/deceased_persons")
        collectionReference.get().addOnSuccessListener { deceasedPersons ->
            for (deceased in deceasedPersons) {
                if (deceased.data["pid"] as String == deceasedPerson.pid) {
                    documentReference = collectionReference.document(deceased.id)
                    documentReference.delete()
                    Log.d(
                        TAG,
                        "onSuccess: Deceased person of id: ${deceasedPerson.pid} was deleted"
                    )
                }
            }
        }
        looper?.let {
            Handler(it).postDelayed({
                val updated: MutableList<DeceasedPerson>? = deceasedPersonsLiveData.value
                updated?.remove(deceasedPerson)
                if (updated != null) {
                    deceasedPersonsLiveData.postValue(updated)
                }
            }, 1000)
        }
    }

    override fun updateProcessed(deceasedPersonID: String, userID: String) {
        val collectionReference: CollectionReference =
            firebaseStore.collection("/users/${userID}/deceased_persons")
        collectionReference.get().addOnSuccessListener { deceasedPersons ->
            if (deceasedPersons != null && !deceasedPersons.isEmpty) {
                for (deceased in deceasedPersons) {
                    if (deceased.data["pid"] as String == deceasedPersonID) {
                        collectionReference.document(deceased.id).update("processed", true)
                    }
                }
            }
        }

        val patients = deceasedPersonsLiveData.value?.iterator()
        patients?.forEach {
            if (it.pid == deceasedPersonID) {
                it.processed = true
            }
        }
    }

    override fun getDeceasedPersonById(deceasedPersonID: String): DeceasedPerson? {
        val patients = deceasedPersonsLiveData.value?.iterator()
        patients?.forEach {
            if (it.pid == deceasedPersonID) {
                return it
            }
        }
        return null
    }

    override fun getAllDeceasedPersons(userID: String): MutableLiveData<MutableList<DeceasedPerson>?> {
        deceasedPersonsLiveData.value?.clear()
        val collectionReference: CollectionReference =
            firebaseStore.collection("/users/${userID}/deceased_persons")
        collectionReference.get().addOnSuccessListener { deceasedPersons ->
            if (deceasedPersons != null && !deceasedPersons.isEmpty) {
                val deceasedPeople = mutableListOf<DeceasedPerson>()
                for (deceased in deceasedPersons) {
                    deceasedPeople.add(
                        DeceasedPerson(
                            deceased.data["name"] as String,
                            deceased.data["surname"] as String,
                            deceased.data["birthDate"] as String,
                            deceased.data["gender"] as String,
                            deceased.data["pid"] as String,
                            deceased.data["processed"] as Boolean
                        )
                    )
                }
                deceasedPersonsLiveData.postValue(deceasedPeople)
                Log.d(TAG, "Successful data fetch")
            } else {
                Log.d(TAG, "No data")

            }
        }
        return deceasedPersonsLiveData
    }
}