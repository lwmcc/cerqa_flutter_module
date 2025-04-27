package com.mccartycarclub.repository

sealed class NetResult<out T> {
    data object Pending : NetResult<Nothing>()
    data class Success<out T>(val data: T?) : NetResult<T>()
    data class Error(val exception: Throwable) : NetResult<Nothing>()
}

sealed class NetWorkResult<out T> {
    data class Success<out T>(val data: T?) : NetWorkResult<T>()
    data class Error(val exception: Throwable) : NetWorkResult<Nothing>()
}

sealed class NetSearchResult<out T> {
    data object Pending : NetSearchResult<Nothing>()
    data object Idle : NetSearchResult<Nothing>()
    data class Success<out T>(val data: T?) : NetSearchResult<T>()
    data class Error(val exception: Throwable) : NetSearchResult<Nothing>()
}