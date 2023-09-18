package com.masters.mort.utilities

import java.text.SimpleDateFormat

class DateConverter {
    companion object{
        fun convertDateToLong(date: String): Long {
            val df = SimpleDateFormat("dd/MM/yyyy")
            return df.parse(date)?.time ?: System.currentTimeMillis()
        }
    }
}