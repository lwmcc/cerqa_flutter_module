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
    data object NoInternet : NetSearchResult<Nothing>()
    data object Idle : NetSearchResult<Nothing>()
    data class Success<out T>(val data: T?) : NetSearchResult<T>()
    data class Error(val exception: Throwable) : NetSearchResult<Nothing>()
}

sealed class UiStateResult<out T> {
    data object Pending : UiStateResult<Nothing>()
    data object NoInternet : UiStateResult<Nothing>()
    data object Idle : UiStateResult<Nothing>()
    data class Success<out T>(val data: T?) : UiStateResult<T>()
    data class Error(val exception: Throwable) : UiStateResult<Nothing>()
}

sealed class NetDeleteResult {
    data object Pending : NetResult<Nothing>()
    data object Success : NetDeleteResult()
    data object NoInternet : NetDeleteResult()
    data class Error(val exception: Throwable) : NetDeleteResult()
}