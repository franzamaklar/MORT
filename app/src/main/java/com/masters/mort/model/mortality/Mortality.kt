package com.masters.mort.model.mortality

data class Mortality(
    var deceasedPersonID: String,
    var mortalityCause: String,
    var deathDateTime: String,
    var deathPlace: String
)
