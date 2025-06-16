package com.mccartycarclub.utils

import com.amplifyframework.core.model.temporal.Temporal
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDateTimeForDisplay(dateTime: Temporal.DateTime?): String? {
    return try {
        val instant: Instant = Instant.parse(dateTime?.format())

        val inputFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

        val outputFormatter =
            DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a", Locale.getDefault())

        val dateTime = LocalDateTime.parse(instant.toString(), inputFormatter)
        dateTime.format(outputFormatter).toString()
    } catch (rte: RuntimeException) {
        null
    }
}