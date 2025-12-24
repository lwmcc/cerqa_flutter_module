package com.cerqa.realtime

/**
 * Platform-agnostic representation of Ably token data.
 * This can be converted to platform-specific Ably token types.
 */
data class AblyTokenData(
    val keyName: String,
    val clientId: String,
    val timestamp: Long,
    val nonce: String,
    val mac: String
)
