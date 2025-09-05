package com.mccartycarclub.repository

import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.GsonVariablesSerializer
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.api.graphql.model.ModelQuery.get
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.LazyModelReference
import com.amplifyframework.core.model.LoadedModelList
import com.amplifyframework.core.model.LoadedModelReference
import com.amplifyframework.core.model.ModelReference
import com.amplifyframework.core.model.includes
import com.amplifyframework.core.model.query.predicate.QueryField
import com.amplifyframework.datastore.generated.model.Channel
import com.amplifyframework.datastore.generated.model.ChannelPath
import com.amplifyframework.datastore.generated.model.Message as RepositoryMessage
import com.mccartycarclub.pigeon.Message as PigeonMessage
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserChannel
import com.amplifyframework.datastore.generated.model.UserContact
import com.amplifyframework.datastore.generated.model.UserPath
import com.amplifyframework.kotlin.api.KotlinApiFacade
import com.google.gson.Gson
import com.mccartycarclub.domain.helpers.createChannelId
import com.mccartycarclub.domain.helpers.toPigeonMessage
import com.mccartycarclub.pigeon.Chat
import com.mccartycarclub.pigeon.Group
import com.mccartycarclub.pigeon.Contact
import com.mccartycarclub.repository.AmplifyDbRepo.Companion.USER_ID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.String
import kotlin.collections.emptyList

class ChatRepositoryImpl @Inject constructor(
    private val localRepository: LocalRepository,
    private val amplifyApi: KotlinApiFacade,
    @param:Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : ChatRepository {

    override suspend fun fetchChats(): List<Chat>  {
        val userId = localRepository.getUserId().first()

        val document = """
                        query FetchUserChannels(${'$'}userId: ID!) {
                          fetchUserChannels(userId: ${'$'}userId) {
                            id
                            name
                            creator {
                              id
                              userName
                            }
                            receiver {
                              id
                              userName
                            }
                            members {
                              id
                              userName
                            }
                          }
                        }
                    """.trimIndent()

        val request = SimpleGraphQLRequest<String>(
            document,
            mapOf(USER_ID to userId),
            String::class.java,
            GsonVariablesSerializer()
        )

        Amplify.API.query(
            request,
            { response ->
                println("ChatRepositoryImpl ***** MESSAGE CREATED: ${response.data}")
            },
            { error ->
                println("ChatRepositoryImpl ***** ERROR MESSAGE CREATED: ${error.message}")
            }
        )


        return emptyList()
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

/*    override fun fetchDirectMessages(): Flow<List<PigeonMessage>> = flow {

        val userId = localRepository.getUserId().first()

        val document = """
                        query FetchUserChannels(${'$'}userId: ID!) {
                          fetchUserChannels(userId: ${'$'}userId) {
                            id
                            name
                            creator {
                              id
                              userName
                            }
                            receiver {
                              id
                              userName
                            }
                            members {
                              id
                              userName
                            }
                          }
                        }
                    """.trimIndent()

        val request = SimpleGraphQLRequest<String>(
            document,
            mapOf(USER_ID to userId),
            String::class.java,
            GsonVariablesSerializer()
        )

        Amplify.API.query(
            request,
            { response ->
                println("ChatRepositoryImpl ***** MESSAGE CREATED: ${response.data}")
            },
            { error ->
                println("ChatRepositoryImpl ***** ERROR MESSAGE CREATED: ${error.message}")
            }
        )

        emit(emptyList<PigeonMessage>())
    }.flowOn(ioDispatcher)*/

    override suspend fun fetchDirectMessages(): Flow<List<PigeonMessage>> = flow {

        val userId = localRepository.getUserId().first()

        val predicate = QueryField.field("userId").eq(localRepository.getUserId().first())
        val response = amplifyApi.query(ModelQuery.list(UserChannel::class.java, predicate))


        emit(emptyList<PigeonMessage>())
    }.flowOn(ioDispatcher)

    override fun createMessage(
        channelId: String?,
        message: String?, // TODO: no nulls
        receiverUserId: String,
    ): Flow<Boolean> = flow {

        val sender = localRepository.getUserId().firstOrNull()

        if (channelId == null || message == null) { // TODO: no nulls
            emit(false)
            return@flow
        }

        val creatorUser = User.justId(sender)
        val receiverUser = User.justId(receiverUserId)

        val channel = Channel.builder()
            .id(channelId)
            .name("NAME-NOT-NEEDED-FOR-PRIVATE-testing-relationship")
            .isGroup(false)
            .isPublic(false)
            .build()

        val chatMessage = RepositoryMessage.builder()
            .senderId(sender)
            .content("my message is new")
            .channel(channel)
            //.content(message)
            //.channel(channel)
            ///.sender(creatorUser)
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
                    .user(creatorUser)
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


    fun createMessageTest() {

        val currentUser = User.justId("14f8f4e8-a0b1-7015-d3b4-ded92d05abe5")

        val channel = Channel.builder()
            .name("my-fake-channel")
            //.isGroup(false)
            //.creator(currentUser)
            .build()

        Amplify.API.mutate(
            ModelMutation.create(channel),
            { channelResult ->
                val savedChannel = channelResult.data

                val message = RepositoryMessage.builder()
                    .senderId("")
                    .content("new message")
                    .channel(channel)
                    //.content("my-fake-message")
                    //.sender(currentUser)
                    //.channel(savedChannel)
                    .build()

                Amplify.API.mutate(
                    ModelMutation.create(message),
                    { msgResult ->
                        println("ChatRepositoryImpl ***** MESSAGE CREATED: ${msgResult.data.id}")
                    },
                    { err ->
                        println("ChatRepositoryImpl ***** MESSAGE FAILED: ${err.message}")
                    }
                )
            },
            { err -> println("ChatRepositoryImpl ***** CHANNEL FAILED: ${err.message}") }
        )

    }

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