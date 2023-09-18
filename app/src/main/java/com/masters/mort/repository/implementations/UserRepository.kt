package com.masters.mort.repository.implementations

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.masters.mort.model.user.User
import com.masters.mort.repository.interfaces.IUserRepository
import kotlinx.coroutines.tasks.await

class UserRepository : IUserRepository {

    private var firebaseAuth: FirebaseAuth = Firebase.auth
    private var firebaseStore: FirebaseFirestore = Firebase.firestore
    private var userCollectionReference: CollectionReference = firebaseStore.collection("users")
    private var countiesCollectionReference: CollectionReference = firebaseStore.collection("counties")

    private var usersLiveData: MutableLiveData<MutableList<User>> = MutableLiveData<MutableList<User>>()
    private var countiesLiveData: MutableLiveData<MutableList<String>> = MutableLiveData<MutableList<String>>()

    override fun save(user: User, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.id = firebaseAuth.currentUser!!.uid
                    val documentReference: DocumentReference =
                        userCollectionReference.document(user.id)
                    val newUser: MutableMap<String, Any> = HashMap()
                    newUser["name"] = user.name
                    newUser["surname"] = user.surname
                    newUser["email"] = user.email
                    newUser["birthDate"] = user.birthDate
                    newUser["gender"] = user.gender
                    newUser["county"] = user.county

                    documentReference.set(user).addOnSuccessListener {
                        Log.d(
                            ContentValues.TAG,
                            "OnSuccess: user profile created for ${user.email}"
                        )
                        return@addOnSuccessListener
                    }
                        .addOnFailureListener {
                            Log.d(ContentValues.TAG, "OnFailure: ${it.message}")
                            return@addOnFailureListener
                        }
                }
            }
    }

    override suspend fun update(
        userID: String,
        name: String,
        surname: String,
        newEmail: String,
        password: String
    ): Boolean {
        if (!updateEmail(password, newEmail)) {
            return false
        } else {
            userCollectionReference.get().addOnSuccessListener { snapshots ->
                if (snapshots != null && !snapshots.isEmpty) {
                    for (snapshot in snapshots) {
                        if (snapshot.data["id"] as String == userID) {
                            userCollectionReference.document(userID).update(
                                "name", name,
                                "surname", surname,
                                "email", newEmail
                            )
                        }
                    }
                }
            }

            val users = usersLiveData.value?.iterator()
            users?.forEach {
                if (it.id == userID) {
                    it.name = name
                    it.surname = surname
                    it.email = newEmail
                }
            }
            return true
        }
    }

    override fun resetPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener {
            Log.d(ContentValues.TAG, "Reset password request sent successfully on: $email")
        }.addOnFailureListener {
            Log.d(ContentValues.TAG, "Reset password request failed for $email")
        }
    }

    private suspend fun updateEmail(password: String, newEmail: String): Boolean {
        val user = firebaseAuth.currentUser
        val authCredential: AuthCredential? =
            user?.email?.let { EmailAuthProvider.getCredential(it, password) }
        if (authCredential != null) {
            var result = true
            return try {
                user.reauthenticate(authCredential)
                    .addOnCompleteListener {
                        result = it.isSuccessful
                        if(result){
                            firebaseAuth.currentUser?.updateEmail(newEmail)
                        }
                    }.await()
                result
            } catch (exception: Exception) {
                Log.d(ContentValues.TAG, "${exception.message}")
                false
            }
        } else {
            return false
        }
    }

    override fun isRegistered(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                task.isSuccessful

            }
    }

    override fun getAllUsers(): MutableLiveData<MutableList<User>> {
        usersLiveData.value?.clear()
        userCollectionReference.get().addOnSuccessListener { snapshots ->
            if (snapshots != null && !snapshots.isEmpty) {
                val users = mutableListOf<User>()
                for (user in snapshots) {
                    users.add(
                        User(
                            user.data["id"] as String,
                            user.data["name"] as String,
                            user.data["surname"] as String,
                            user.data["email"] as String,
                            user.data["birthDate"] as String,
                            user.data["gender"] as String,
                            user.data["county"] as String
                        )
                    )
                }
                this.usersLiveData.postValue(users)
            } else {
                Log.d(ContentValues.TAG, "No data")
            }
        }
        return usersLiveData
    }

    override fun getAllCounties(): MutableLiveData<MutableList<String>> {
        val counties = mutableListOf<String>()
        countiesCollectionReference.get().addOnSuccessListener {
            if (!it.isEmpty) {
                for (cause in it) {
                    counties.add(cause.data["name"] as String)
                }
            }
        }
        this.countiesLiveData.postValue(counties)
        return this.countiesLiveData
    }
}