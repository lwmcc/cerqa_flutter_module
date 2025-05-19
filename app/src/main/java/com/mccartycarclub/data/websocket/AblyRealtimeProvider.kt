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
import io.ably.lib.types.AblyException
import io.ably.lib.types.ClientOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AblyRealtimeProvider(private val context: Context) : AblyProvider {

    private var ably: AblyRealtime? = null

    override fun getInstance(): AblyRealtime {
        val ably = AblyRealtime(
            ClientOptions().apply {
                clientId = "your-client-id" // optional if using clientId in token
                authCallback = object : Auth.TokenCallback {
                    override fun getTokenRequest(params: Auth.TokenParams?): Any {
                        return try {
                            val rawJson = runBlocking { fetchAblyToken() }

                            // Parse TokenRequest directly from raw JSON string
                            Auth.TokenRequest.fromJson(rawJson.toString())
                        } catch (e: Exception) {
                           // TODO: throw?????
                        }
                    }
                }
            }
        ).apply {
            setAndroidContext(context)
            connect()
            connection.on { stateChange ->
                println("Connection state: ${stateChange?.current?.name}")
            }
        }


        // TODO: move to try catch
/*        return if (ably == null) {

            val options = ClientOptions().apply {
                clientId = "your-client-id"
                authCallback = object : Auth.TokenCallback {
                    @Throws(AblyException::class)
                    override fun getTokenRequest(params: Auth.TokenParams?): Any {
                        return try {
                            // runBlocking blocks this thread until fetchAblyToken completes
                            runBlocking {
                                fetchAblyToken()
                            }
                        } catch (e: Exception) {
                            // Throw an error
                        }
                    }
                }
            }

            AblyRealtime(options).apply {
                setAndroidContext(context)
                connect()
                connection.on(ConnectionStateListener { state ->

                    println("AblyRealtimeProvider ***** Connection state changed to : ${state!!.current.name}")

                    when (state.current) {
                        ConnectionState.initialized -> {

                        }

                        ConnectionState.connecting -> {

                        }

                        ConnectionState.connected -> {
                            println("AblyRealtimeProvider ***** CONNECTED")

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

                        }
                    }
                })
            }
        } else {
            ably!!
        }*/
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