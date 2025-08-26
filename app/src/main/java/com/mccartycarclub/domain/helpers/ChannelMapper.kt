package com.mccartycarclub.domain.helpers

import com.amplifyframework.core.model.LazyModelReference
import com.amplifyframework.core.model.LoadedModelReference
import com.amplifyframework.core.model.ModelReference
import com.amplifyframework.datastore.generated.model.User
import com.mccartycarclub.pigeon.Message as PigeonMessage
import com.amplifyframework.datastore.generated.model.Message as RepositoryMessage

fun toPigeonMessage(messages: List<RepositoryMessage>): List<PigeonMessage> {
    return messages.map { message ->

/*        when (val user: ModelReference<User> = message.sender) {
            is LazyModelReference<*> -> {
                val message = user.fetchModel()


            }

            is LoadedModelReference<*> -> {

            }
        }*/
        PigeonMessage(
            id = message.id,
            messageId = message.id, // TODO: use one
            content = message.content,
            senderId = "",
            createdAt = "",
        )
    }
}