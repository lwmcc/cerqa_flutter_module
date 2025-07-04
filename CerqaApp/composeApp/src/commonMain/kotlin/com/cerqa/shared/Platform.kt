package com.cerqa.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform