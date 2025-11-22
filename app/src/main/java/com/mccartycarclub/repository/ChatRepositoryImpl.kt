package com.mccartycarclub.repository

import com.amplifyframework.AmplifyException
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.model.LazyModelReference
import com.amplifyframework.datastore.generated.model.Channel
import com.amplifyframework.datastore.generated.model.Message as RepositoryMessage
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserChannel
import com.amplifyframework.kotlin.api.KotlinApiFacade
import com.mccartycarclub.pigeon.ChannelsItem
import com.mccartycarclub.pigeon.Chat
import com.mccartycarclub.pigeon.Group
import com.mccartycarclub.pigeon.Contact
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Named
import kotlin.String
import kotlin.collections.emptyList

class ChatRepositoryImpl @Inject constructor(
    private val localRepository: LocalRepository,
    private val amplifyApi: KotlinApiFacade,
    @param:Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : ChatRepository {

    override suspend fun fetchChats(): Flow<List<Chat>> = flow {
        try {
            val userId = localRepository.getUserId().firstOrNull()

            // Return empty list if user is not authenticated
            if (userId.isNullOrBlank()) {
                emit(emptyList())
                return@flow
            }

            val predicate = Channel.CREATOR.eq(userId).or(Channel.RECEIVER.eq(userId))
            val response = amplifyApi.query(ModelQuery.list(Channel::class.java, predicate))

            val chats = response.data?.items?.map { channel ->
                Chat(
                    chatId = channel.id,
                    userName = (channel.receiver as? LazyModelReference<User>)?.fetchModel()?.userName,
                    userId = (channel.receiver as? LazyModelReference<User>)?.fetchModel()?.userId,
                    avatarUri = (channel.receiver as? LazyModelReference<User>)?.fetchModel()?.avatarUri,
                )
            } ?: emptyList()

            emit(chats)
        } catch (ae: AmplifyException) {
            emit(emptyList())
        } catch (ise: IllegalStateException) {
            // Amplify not configured yet, return empty list
            emit(emptyList())
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

    // TODO: refactor, this code moved to chats redo for direct messages screen
    override suspend fun fetchDirectMessages(): Flow<List<ChannelsItem>> = flow {

        try {
            val userId = localRepository.getUserId().firstOrNull()

            // Return empty list if user is not authenticated
            if (userId.isNullOrBlank()) {
                emit(emptyList())
                return@flow
            }

            val predicate = Channel.CREATOR.eq(userId).or(Channel.RECEIVER.eq(userId))
            val response = amplifyApi.query(ModelQuery.list(Channel::class.java, predicate))

            val channels = response.data?.items?.map { channel ->
                ChannelsItem(
                    id = channel.id,
                    receiverId = (channel.receiver as? LazyModelReference<User>)?.fetchModel()?.userId,
                    userName = (channel.receiver as? LazyModelReference<User>)?.fetchModel()?.userName,
                    avatarUri = (channel.receiver as? LazyModelReference<User>)?.fetchModel()?.avatarUri,
                )
            } ?: emptyList()

            emit(channels)
        } catch (ae: AmplifyException) {
            emit(emptyList())
        }
    }.flowOn(ioDispatcher)

    // TODO: refactor
    override fun createMessage(
        channelId: String?,
        message: String?, // TODO: no nulls
        receiverUserId: String,
    ): Flow<Boolean> = flow {

        val sender = localRepository.getUserId().firstOrNull().toString()

        if (channelId == null || message == null) { // TODO: no nulls
            emit(false)
            return@flow
        }

/*        val userSender = amplifyApi.query(
            ModelQuery.list(
                User::class.java,
                User.USER_ID.eq(sender)
            )
        ).data.first()
        val userReceiver = amplifyApi.query(
            ModelQuery.list(
                User::class.java,
                User.USER_ID.eq(receiverUserId)
            )
        ).data.first()*/

        val channel = Channel.builder()
            .name("NAME-NOT-NEEDED-FOR-PRIVATE-testing-relationship")
            .creator(User.justId(sender))
            .receiver(User.justId(receiverUserId))
            .isGroup(false)
            .isPublic(false)
            .id(channelId)
            .build()

        val chatMessage = RepositoryMessage.builder()
            .senderId(sender)
            .content(message)
            .channel(channel)
            .build()
        try {

            amplifyApi.mutate(ModelMutation.create(channel))

            if (channelExists(channelId)) {
                amplifyApi.mutate(ModelMutation.create(chatMessage))
            } else {
                val channelResponse = amplifyApi.mutate(ModelMutation.create(channel))

                if (!channelResponse.hasData()) {
                    emit(false)
                    return@flow
                }
                val userChannel = UserChannel.builder()
                    //.user(creatorUser)
                    .channel(channelResponse.data)
                    .build()

                amplifyApi.mutate(ModelMutation.create(userChannel))
                amplifyApi.mutate(ModelMutation.create(chatMessage))
            }
            emit(true)
        } catch (ae: AmplifyException) {
            emit(false)
        } catch (e: Exception) {
            emit(false)
        }
    }.flowOn(ioDispatcher)


    suspend fun channelExists(channelId: String?): Boolean {
        if (channelId.isNullOrBlank()) return false
        return try {
            val response = amplifyApi.query(
                ModelQuery.get(Channel::class.java, channelId)
            )
            response.data != null
        } catch (e: Exception) {
            false
        }
    }
}

data class DirectChat(
    val chatId: String,
    val userName: String,
    val avatarUri: String?,
    val lastMessage: String?
)

data class FetchDirectChatsResponse(
    val fetchDirectChats: List<DirectChat>
)