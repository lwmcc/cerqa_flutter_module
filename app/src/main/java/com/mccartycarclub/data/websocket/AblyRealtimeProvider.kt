package com.mccartycarclub.data.websocket

import android.content.Context
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.api.rest.RestResponse
import com.amplifyframework.kotlin.core.Amplify
import com.mccartycarclub.domain.websocket.AblyProvider
import io.ably.lib.realtime.AblyRealtime
import io.ably.lib.realtime.ConnectionState
import io.ably.lib.realtime.ConnectionStateListener
import io.ably.lib.rest.Auth
import io.ably.lib.rest.Auth.TokenDetails
import io.ably.lib.types.AblyException
import io.ably.lib.types.ClientOptions
import io.ably.lib.types.Callback
import io.ably.lib.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AblyRealtimeProvider(private val context: Context) : AblyProvider {

    private var ably: AblyRealtime? = null
    private var token: String? = null

    override fun getInstance(token: Auth.TokenRequest?): AblyRealtime {

        val ably = AblyRealtime(
            ClientOptions().apply {
                authCallback = Auth.TokenCallback { params ->
                    token
                }
            }
        ).apply {
            setAndroidContext(context)
            connect()
            connection.on(ConnectionStateListener { state ->
                when (state.current) {
                    ConnectionState.initialized -> {

                    }

                    ConnectionState.connecting -> {

                    }

                    ConnectionState.connected -> {
                        push.activate()
                    }

                    ConnectionState.disconnected -> {

                    }

                    ConnectionState.suspended -> {

                    }

                    ConnectionState.closing -> {

                    }

                    ConnectionState.closed -> {

                    }

                    ConnectionState.failed -> {
                        println("AblyRealtimeProvider ***** failed")
                    }
                }
            })
        }
        return ably
    }

    suspend fun fetchAblyToken(): Token = withContext(Dispatchers.IO) {
        try {
            val restOptions = RestOptions.builder()
                .addPath("/getAblyTokenRequest")
                .build()

            val response: RestResponse = Amplify.API.get(restOptions, "fetchAblyJwt")

            val body = response.data.asString()
            val json = JSONObject(body)

            Token(
                keyName = json.getString("keyName"),
                nonce = json.getString("nonce"),
                ttl = json.getLong("ttl"),
                timestamp = json.getLong("timestamp"),
                mac = json.getString("mac"),
                clientId = json.optString("clientId", null.toString())
            )
        } catch (e: Exception) {

            throw e  // Let caller handle the exception
        }
    }
}

data class Token(
    val keyName: String,
    val nonce: String,
    val ttl: Long,
    val timestamp: Long,
    val mac: String,
    val clientId: String? = null
)
