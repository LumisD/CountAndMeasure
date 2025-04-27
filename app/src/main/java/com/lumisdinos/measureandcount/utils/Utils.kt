package com.lumisdinos.measureandcount.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun getCurrentDateTime(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy, hh:mm a")
    return currentDateTime.format(formatter)
}