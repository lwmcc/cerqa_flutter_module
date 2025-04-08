package com.mccartycarclub.repository

sealed class NetResult<out T> {
    data object Pending : NetResult<Nothing>()
    data class Success<out T>(val data: T?) : NetResult<T>()
    data class Error(val exception: Throwable) : NetResult<Nothing>()
}