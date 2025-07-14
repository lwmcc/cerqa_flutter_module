package com.cerqa.di

import android.content.Context
import android.content.SharedPreferences
import com.cerqa.data.StoreDefaults
import com.cerqa.data.StoreUserDefaults

private lateinit var appContext: Context

fun initPlatformContext(context: Context) {
    appContext = context
}

fun getPlatformContext(): Context {
    if (!::appContext.isInitialized) {
        throw IllegalStateException("Context Error")
    }
    return appContext
}
actual fun provideStoreDefaults(): StoreDefaults {
    val prefs: SharedPreferences = getPlatformContext()
        .getSharedPreferences("preferences", Context.MODE_PRIVATE)
    return StoreUserDefaults(prefs)
}