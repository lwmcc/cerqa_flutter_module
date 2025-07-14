package com.cerqa.di

import com.cerqa.data.StoreDefaults
import com.cerqa.data.StoreUserDefaults
import platform.Foundation.NSUserDefaults

actual fun provideStoreDefaults(): StoreDefaults {
    return StoreUserDefaults(NSUserDefaults.standardUserDefaults)
}