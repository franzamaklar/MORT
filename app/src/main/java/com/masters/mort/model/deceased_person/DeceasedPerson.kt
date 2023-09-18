package com.masters.mort.model.deceased_person

data class DeceasedPerson(
    var name: String,
    var surname: String,
    var birthDate: String,
    var gender: String,
    var pid: String,
    var processed: Boolean
)
