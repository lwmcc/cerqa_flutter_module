package com.cerqa.utils

object NameValidator {
    const val MAX_LENGTH = 30
    const val MIN_LENGTH = 3

    private val regex = Regex("^[\\p{L}\\p{N} _.-]*$")

    fun isAllowedInput(input: String): Boolean =
        input.length <= MAX_LENGTH && regex.matches(input)

    fun isValidFinal(input: String): Boolean =
        input.length in MIN_LENGTH..MAX_LENGTH && regex.matches(input)
}