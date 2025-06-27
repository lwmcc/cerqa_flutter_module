package com.mccartycarclub.utils

import org.junit.Test

class PhoneNumberParserTest {

    @Test
    fun `valid phone number`() {
        phoneNumberParser("[+1 480-521-5556]")
    }
}