package com.mccartycarclub.utils

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

fun phoneNumberParser(phoneNumber: String, defaultRegion: String = "US"): String? {
    val phoneUtil = PhoneNumberUtil.getInstance()

    return try {
        val number = phoneUtil.parse(phoneNumber, defaultRegion)
        if (phoneUtil.isValidNumber(number)) {
            phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164)
        } else {
            // TODO: log
            null
        }
    } catch (npe: NumberParseException) {
        // TODO: log
        null
    }
}
