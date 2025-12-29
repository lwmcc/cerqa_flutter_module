package com.cerqa.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val appModule = module {
    single { Dispatchers.IO }
    single { Dispatchers.Main }
}
