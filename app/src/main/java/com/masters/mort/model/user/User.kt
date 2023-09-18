package com.masters.mort.model.user

data class User(
    var id: String = "",
    var name: String,
    var surname: String,
    var email: String,
    val birthDate:String,
    val gender:String,
    val county: String
)