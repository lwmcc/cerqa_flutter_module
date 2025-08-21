package com.mccartycarclub.pigeon

import io.flutter.plugin.common.BinaryMessenger

class PigeonFlutterApi(messenger: BinaryMessenger) {
    var flutterApi: CerqaFlutterApi? = null

    init {
        flutterApi = CerqaFlutterApi(messenger)
    }

/*    fun sendChats(chats: List<Chat>, callback: (Result<List<Chat>>) -> Unit) {
        flutterApi!!.sendChats(chats) { result -> callback(result) }
    }*/
}