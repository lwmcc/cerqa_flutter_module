package com.mccartycarclub.repository

import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.GsonVariablesSerializer
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.LazyModelReference
import com.amplifyframework.core.model.LoadedModelReference
import com.amplifyframework.datastore.generated.model.Channel
import com.amplifyframework.datastore.generated.model.Message as RepositoryMessage
import com.mccartycarclub.pigeon.Message as PigeonMessage
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserChannel
import com.amplifyframework.kotlin.api.KotlinApiFacade
import com.google.gson.Gson
import com.mccartycarclub.domain.helpers.createChannelId
import com.mccartycarclub.domain.helpers.toPigeonMessage
import com.mccartycarclub.pigeon.Chat
import com.mccartycarclub.pigeon.Group
import com.mccartycarclub.pigeon.Contact
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.emptyList

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

    override fun fetchDirectMessages(receiverUserId: String): Flow<List<PigeonMessage>> = flow {

        val loggedInUserId = localRepository.getUserId().firstOrNull()
        val document = """
                        query FetchDirectChats(${'$'}userId: String!) {
                            fetchDirectChats(userId: ${'$'}userId) {
                                chatId
                                userName
                                avatarUri
                                lastMessage
                            }
                        }
                    """.trimIndent()

        val request = SimpleGraphQLRequest<String>(
            document,
            mapOf("userId" to loggedInUserId),
            String::class.java,
            GsonVariablesSerializer()
        )

        val response2 = amplifyApi.query(request)
/*
        val response = Amplify.API.query(
            request,
            { result ->
                val gson = Gson()
                val json = gson.fromJson(result.data, FetchDirectChatsResponse::class.java)

                println("ChatRepositoryImpl ***** RESPONSE ${json.fetchDirectChats}")
*//*                response.fetchDirectChats.forEach {
                    println("Chat with ${it.userName}: ${it.lastMessage}")
                }*//*
            },
            { error -> println("Error: $error") }
        )*/

        if (response2.hasData()) {
            if (response2.hasErrors()) {
                println("ChatRepositoryImpl ***** RESPONSE ERRORS ${response2.errors}")
            } else {
                println("ChatRepositoryImpl ***** RESPONSE ${response2.data}")
            }
        } else {
            println("ChatRepositoryImpl ***** RESPONSE NO DATA}")
        }



        emit(emptyList<PigeonMessage>())
/*        val loggedInUserId = localRepository.getUserId().firstOrNull()

        val messageId = loggedInUserId?.createChannelId(receiverUserId)

        val response = amplifyApi.query(
            ModelQuery.list(
                RepositoryMessage::class.java,
                RepositoryMessage.CHANNEL.eq(messageId)
            )
        )

        if (response.hasData()) {
            val messages = response.data.items.toList().sortedBy { it.createdAt }
            emit(toPigeonMessage(messages))
        } else {
            emit(emptyList())
        }*/
    }.flowOn(ioDispatcher)

    override fun createMessage(
        channelId: String?,
        message: String?,
        senderUserId: String,
    ): Flow<Boolean> = flow {

        if (channelId == null || message == null) {
            emit(false)
            return@flow
        }

        val user = User.justId(senderUserId)

        val channel = Channel.builder()
            .id(channelId)
            .name("NAME-NOT-NEEDED-FOR-PRIVATE-v2")
            .isGroup(false)
            .isPublic(false)
            .creator(user)
            .build()

        val chatMessage = RepositoryMessage.builder()
            .content(message)
            .channel(channel)
            .sender(user)
            .build()

        try {

            if (channelExists(channelId)) {
                amplifyApi.mutate(ModelMutation.create(chatMessage))
            } else {
                val channelResponse = amplifyApi.mutate(ModelMutation.create(channel))

                if (!channelResponse.hasData()) {
                    emit(false)
                    return@flow
                }
                val userChannel = UserChannel.builder()
                    .user(user)
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


    // TODO: testing
    suspend fun fetchDirectChatsTest(userId: String): List<Chat> = withContext(ioDispatcher) {

        val document = """
                        query FetchDirectChats(${'$'}userId: String!) {
                            fetchDirectChats(userId: ${'$'}userId) {
                                chatId
                                userName
                                avatarUri
                                lastMessage
                            }
                        }
                    """.trimIndent()

        val request = SimpleGraphQLRequest<String>(
            document,
            mapOf("userId" to userId),
            String::class.java,
            GsonVariablesSerializer()
        )

        Amplify.API.query(
            request,
            { result ->
                val gson = Gson()
                val response = gson.fromJson(result.data, FetchDirectChatsResponse::class.java)
                response.fetchDirectChats.forEach {
                    println("ChatRepositoryImpl ***** ${it.userName}: ${it.lastMessage}")
                }
            },
            { error -> println("Error: $error") }
        )

        emptyList()
/*        try {
            val document = """
                        query FetchDirectChats(${'$'}userId: ID!) {
                          fetchDirectChats(userId: ${'$'}userId) {
                            chatId
                            userName
                            avatarUri
                            lastMessage
                          }
                        }
                        """.trimIndent()

            val request: GraphQLRequest<String> = SimpleGraphQLRequest(
                document,
                mapOf("userId" to userId),
                String::class.java,
                null,
            )

            println("ChatRepositoryImpl ***** fetchDirectChatsTest REQUEST $request")

            val response = amplifyApi.query(request)

            println("ChatRepositoryImpl ***** fetchDirectChatsTest RESPONSE $response")

            val json = response.data

            println("ChatRepositoryImpl ***** fetchDirectChatsTest JSON $json")

            emptyList()
        }  catch (ae: AmplifyException) {
            println("ChatRepositoryImpl ***** fetchDirectChatsTest AMPLIFY ERROR ${ae.message}")
            emptyList()
        }

        catch (e: Exception) {
            println("ChatRepositoryImpl ***** fetchDirectChatsTest ERROR ${e.message}")
            emptyList()
        }*/
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