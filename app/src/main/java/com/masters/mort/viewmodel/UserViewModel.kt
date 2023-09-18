package com.masters.mort.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.masters.mort.model.user.User
import com.masters.mort.repository.implementations.UserRepository

class UserViewModel(
    private val userRepository: UserRepository
): ViewModel() {

    var users = userRepository.getAllUsers()

    fun saveUser(user: User, password: String){
        val result = userRepository.save(user, password)
        users = userRepository.getAllUsers()
        return result
    }

    suspend fun updateUser(userID: String, name: String, surname: String, newEmail: String, password: String): Boolean{
        return userRepository.update(userID, name, surname, newEmail, password)
    }

    fun isUserRegistered(email: String, password: String){
        return userRepository.isRegistered(email, password)
    }

    fun resetPasswordForEmail(email:String){
        userRepository.resetPassword(email)
    }

    fun getCounties(): MutableLiveData<MutableList<String>>{
        return userRepository.getAllCounties()
    }
}