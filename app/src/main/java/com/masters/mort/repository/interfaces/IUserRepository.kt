package com.masters.mort.repository.interfaces

import androidx.lifecycle.MutableLiveData
import com.masters.mort.model.user.User

interface IUserRepository {
    fun save(user: User, password: String)
    suspend fun update(userID: String, name: String, surname: String, newEmail: String, password: String): Boolean
    fun resetPassword(email: String)
    fun isRegistered(email: String, password:String)
    fun getAllUsers(): MutableLiveData<MutableList<User>>
    fun getAllCounties(): MutableLiveData<MutableList<String>>
}