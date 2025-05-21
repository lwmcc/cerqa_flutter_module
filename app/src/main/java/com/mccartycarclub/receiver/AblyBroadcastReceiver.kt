package com.mccartycarclub.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mccartycarclub.data.websocket.AblyPushMessagingService
import com.mccartycarclub.data.websocket.AblyService
import io.ably.lib.realtime.CompletionListener
import io.ably.lib.types.AblyException
import io.ably.lib.types.ErrorInfo
import io.ably.lib.util.IntentUtils
import javax.inject.Inject


class AblyBroadcastReceiver @Inject constructor(private val ablyService: AblyService) :
    BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("AblyBroadcastReceiver ***** NOTIFICATION START ACTION ${intent.action}")
        if (intent.action.equals("io.ably.broadcast.PUSH_ACTIVATE")) {

            // com.mccartycarclub.data.websocket.AblyPushMessagingService.PUSH_NOTIFICATION_MESSAGE
            val error: ErrorInfo? = IntentUtils.getErrorInfo(intent)
            if (error != null) {

                println("AblyBroadcastReceiver ***** RECEIVER ERROR")
                return;
            }

            try {
                subscribeChannels()
                println("AblyBroadcastReceiver ***** NOTIFICATION ${intent.extras}")
                //println("AblyBroadcastReceiver ***** Device is now registered for push with ${deviceId()}")
            } catch (e: AblyException) {
                println("AblyBroadcastReceiver ***** AblyException getting deviceId: $e")
            }


            if (AblyPushMessagingService.PUSH_NOTIFICATION_ACTION == intent.action) {
                println("AblyBroadcastReceiver ***** RECEIVED PUSH")
            }
        }
    }

    //@Throws(AblyException::class)
    //private fun deviceId(): String {
        //return ablyService.provider.getInstance().device().id
    //}

    private fun subscribeChannels() {
/*        ablyService.provider.getInstance().channels.get("push:test_push_channel").push.subscribeClientAsync(
            object :
                CompletionListener {
                override fun onSuccess() {
                    println("AblyBroadcastReceiver ***** Subscribed to push for the channel")
                }

                override fun onError(reason: ErrorInfo) {
                    println("AblyBroadcastReceiver ***** Error subscribing to push channel " + reason.message)
                    println("AblyBroadcastReceiver ***** Visit link for more details: " + reason.href)
                }
            })*/
    }
}