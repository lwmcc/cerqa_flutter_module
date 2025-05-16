package com.mccartycarclub.data.websocket

import android.content.Context
import com.mccartycarclub.R
import com.mccartycarclub.domain.websocket.AblyProvider
import io.ably.lib.realtime.AblyRealtime
import io.ably.lib.realtime.ConnectionState
import io.ably.lib.realtime.ConnectionStateListener
import io.ably.lib.types.ClientOptions

class AblyRealtimeProvider(private val context: Context) : AblyProvider {

    private var ably: AblyRealtime? = null

    override fun getInstance(): AblyRealtime {
        // TODO: move to try catch
        return if (ably == null) {
            val options =
                ClientOptions(context.applicationContext.getString(R.string.ABLY_TESTING_KEY))
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
        }
    }
}