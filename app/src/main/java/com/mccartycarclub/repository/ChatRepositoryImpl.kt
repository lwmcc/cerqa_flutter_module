package com.mccartycarclub.repository

import com.amplifyframework.AmplifyException
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.model.LazyModelReference
import com.amplifyframework.core.model.LoadedModelReference
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserChannel
import com.amplifyframework.kotlin.api.KotlinApiFacade
import com.mccartycarclub.pigeon.Chat
import com.mccartycarclub.pigeon.Group
import com.mccartycarclub.pigeon.Contact
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class ChatRepositoryImpl @Inject constructor(
    private val localRepository: LocalRepository,
    private val amplifyApi: KotlinApiFacade,
    @param:Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : ChatRepository {

    override suspend fun fetchChats(): List<Chat> = withContext(ioDispatcher) {

        try {
            val predicate = UserChannel.USER.eq(localRepository.getUserId().first())
            val response = amplifyApi.query(ModelQuery.list(UserChannel::class.java, predicate))

            if (response.hasData()) {
                val chats = response.data.items.mapNotNull { userChannel ->
                    val userRef = userChannel.user ?: return@mapNotNull null
                    val userObj = when (userRef) {
                        is LazyModelReference<*> -> userRef.fetchModel()
                        is LoadedModelReference<*> -> userRef
                    } as? User ?: return@mapNotNull null

                    Chat(
                        chatId = userObj.id,
                        userName = userObj.userName ?: "Unknown",
                        avatarUri = userObj.avatarUri ?: ""
                    )
                }

                chats
            } else {
                emptyList()
            }

        } catch (ae: AmplifyException) {
            emptyList()
        }
    }

    override fun fetchContacts(): List<Contact> {

        // Fake data to test flutter side
        return mutableListOf(
            Contact(
                userName = "MLarryM",
                phoneNumber = "",
                userId = "11111",
                avatarUri = "",
            ),
            Contact(
                userName = "Bron",
                phoneNumber = "",
                userId = "22222",
                avatarUri = "",
            ),
            Contact(
                userName = "Luka",
                phoneNumber = "",
                userId = "33333",
                avatarUri = "",
            ),
        )
    }

    override fun fetchDirectConversation() {
        TODO("Not yet implemented")
    }

    override fun fetchGroups(): List<Group> {
        // Fake data to tst flutter side
        return mutableListOf(
            Group(
                groudId = "12345",
                groupName = "CarClub",
                groupAvatarUri = "",
            ),
            Group(
                groudId = "12121",
                groupName = "Nav Club",
                groupAvatarUri = "",
            ),
            Group(
                groudId = "12312",
                groupName = "Day Trippers",
                groupAvatarUri = "",
            ),
        )
    }
}