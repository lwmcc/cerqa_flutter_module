package com.mccartycarclub.utils

import com.amplifyframework.core.model.temporal.Temporal
import junit.framework.TestCase.assertEquals
import org.junit.Test

class UtilsTest {

    val temporalDateTime: Temporal.DateTime = Temporal.DateTime("2025-05-22T22:07:10.355Z")
    val dateOutput = "May 22, 2025 10:07 PM"

    @Test
    fun `verify date and time`() {
        val parsedTime = formatDateTimeForDisplay(temporalDateTime)
        assertEquals(dateOutput, parsedTime)
    }
}
