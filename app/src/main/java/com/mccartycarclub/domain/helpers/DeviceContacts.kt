package com.mccartycarclub.domain.helpers

import com.mccartycarclub.domain.model.LocalContact

interface DeviceContacts {
    fun getAllContacts(localContacts: (List<LocalContact>) -> Unit)
}
