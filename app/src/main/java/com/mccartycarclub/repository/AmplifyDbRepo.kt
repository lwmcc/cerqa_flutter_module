package com.mccartycarclub.repository

import android.util.Log
import com.amplifyframework.annotations.InternalAmplifyApi
import com.amplifyframework.api.ApiException
import com.amplifyframework.api.aws.GsonVariablesSerializer
import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.api.graphql.model.ModelQuery.get
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.Consumer
import com.amplifyframework.core.model.LoadedModelList
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.includes
import com.amplifyframework.datastore.generated.model.Contact
import com.amplifyframework.datastore.generated.model.ContactInvite
import com.amplifyframework.datastore.generated.model.InviteToConnect
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserContact
import com.amplifyframework.datastore.generated.model.UserGroup
import com.amplifyframework.datastore.generated.model.UserInviteToConnect
import com.amplifyframework.datastore.generated.model.UserPath
import com.google.gson.Gson
import com.mccartycarclub.ui.viewmodels.MainViewModel.Companion.TEST_USER_1
import com.mccartycarclub.ui.viewmodels.MainViewModel.Companion.TEST_USER_2
import javax.inject.Inject


class AmplifyDbRepo @Inject constructor() : DbRepo {

    @OptIn(InternalAmplifyApi::class)
    override fun fetchUserGroups(userId: String) {

        Amplify.API.query(
            ModelQuery.list(
                UserGroup::class.java,
                UserGroup.USER.eq(userId)
            ),
            { response ->
                response.data.items.forEach { item ->
                    //println("AmplifyDbRepo ***** RESPONSE ${item.id}")
                    // println("AmplifyDbRepo ***** RESPONSE ${item.user.getIdentifier()}")
                    // println("AmplifyDbRepo ***** RESPONSE ${item.group.getIdentifier()}")
                }

            },
            { error ->
                //  println("AmplifyDbRepo ***** $error")
            }
        )
    }

    // TODO: move to own class
    override fun fetchUserContacts(
        userId: String,
        userContacts: (List<Contact>) -> Unit,
    ) {

        Amplify.API.query(
            ModelQuery.get<User, UserPath>(
                User::class.java,
                User.UserIdentifier(TEST_USER_1)
            ) { userPath -> includes(userPath.contacts) },
            {
                val contacts = (it.data.contacts as? LoadedModelList<Contact>)?.items
                println("AmplifyDbRepo ***** fetchUserContacts FETCH USER CONTACTS ${contacts?.size} ")
            },
            {
                println("AmplifyDbRepo ***** ERROR ")
            }
        )
    }

    override fun fetchUsers() {

        // 344433-1031-7026-1ea5-9e5c424b27de
        // 31cb55f0-1031-7026-1ea5-9e5c424b27de

        val onResponse = Consumer<GraphQLResponse<User>> { response ->
            val user = response.data
            if (user != null) {
                // Log user info
                // Log.d("AmplifyDbRepo", "User Name: ${user.firstName} ${user.lastName}")

                // Assuming the User model has a 'contacts' field (hasMany relationship)
                val ct = user.contacts

            } else {
                Log.e("MyAmplifyApp", "User not found")
            }
        }

        val onFailure = Consumer<ApiException> { error ->
            //Log.e("AmplifyDbRepo", "Failed to fetch user: ${error.message}")
        }

        // Use the correct query to fetch the User by ID
        ModelQuery.get<User, UserPath>(
            User::class.java,
            User.UserIdentifier(TEST_USER_1)
        ) {
            // Including the contacts field (if exists in the User model)
            includes(it.contacts)
        }.apply {
            // Execute the query
            Amplify.API.query(this, onResponse, onFailure)
        }
    }

    override fun fetchUser(userId: String, user: (User) -> Unit) {
        Amplify.API.query(get(User::class.java, userId),
            { response: GraphQLResponse<User> ->
                println("AmplifyDbRepo ***** USER ${response.data}")
                //user(response.data as User)
            },
            { println("AmplifyDbRepo ***** ERROR") }
        )
    }

    override fun createContact(user: User) {
        val contact = Contact.builder()
            .contactId(TEST_USER_2)
            .build()

        Amplify.API.mutate(ModelMutation.create(contact),
            { contactResponse ->
                val userContact = UserContact.builder()
                    .id(contact.id)
                    .user(user)
                    .contact(contact)
                    .build()

                //  TODO: move ot own functionn
                Amplify.API.mutate(ModelMutation.create(userContact),
                    {
                        println("AmplifyDbRepo ***** createContact USER CONTACT: ${userContact}")
                    },
                    { error ->
                        println("AmplifyDbRepo *****Failed to create USER Contact: $error")
                    }
                )
                println("AmplifyDbRepo ***** contactResponse: ${contactResponse}")
            },
            { error ->
                println("AmplifyDbRepo *****Failed to create Contact: $error")
            }
        )
    }
/*
    override fun fetchUserByUserName(
        userName: String,
        data: (NetResult<User>) -> Unit,
    ) {
        Amplify.API.query(
            ModelQuery.list(User::class.java, User.USER_NAME.eq(userName)),
            { response ->
                // TODO: log success
                validate(response, data = {
                    data(it)
                })
            },
            { error ->
                // TODO: log error
                data(NetResult.Error(error))
            }
        )
    }*/

    override fun fetchUserByUserName(
        userName: String,
        data: (NetResult<User?>) -> Unit,
    ) {
        Amplify.API.query(
            ModelQuery.list(User::class.java, User.USER_NAME.eq(userName)),
            { response ->
                if (response.hasData()) {
                    data(NetResult.Success(response.data.firstOrNull()))
                } else {
                    data(NetResult.Success(null))
                }
            },
            { error ->
                data(NetResult.Error(error))
            }
        )
    }

    override fun fetchUserContacts(userId: String) {
        Amplify.API.query(
            get<User, UserPath>(
                User::class.java,
                User.UserIdentifier(userId)
            ) { userPath -> includes(userPath.contacts) },
            {

                val contacts = (it.data.contacts as? LoadedModelList<UserContact>)?.items

                contacts?.forEach { userContact ->
                    println("AmplifyDbRepo ***** fetchUserContacts ${userContact.id}")
                    println("AmplifyDbRepo ***** ID ${userContact.contact}")
                    println("AmplifyDbRepo ***** USER ${userContact.user}")

                }
            },
            { println("AmplifyDbRepo ***** ERROR FETCHING USER CONTACTS") }
        )



    }

    // TODO: rename
    override fun acceptContactInvite(userId: String, rowId: (String?) -> Unit) {

        Amplify.API.mutate(ModelMutation.create(
            UserInviteToConnect.builder().userId(userId).build()
        ),
            { rowId(it.data.id) },
            { error ->
                rowId(null)
                // TODO: to log
                println("AmplifyDbRepo *****Failed to create acceptContactInvite: $error")
            }
        )
    }

    override fun fetchUserIdFromSentInvite(rowId: String, userId: (String?) -> Unit) {
        Amplify.API.query(get(UserInviteToConnect::class.java, rowId),
            {
                it.data?.let { data ->
                    userId(data.userId)
                } ?: run { println("AmplifyDbRepo ***** ERROR $it") }
            },
            { println("AmplifyDbRepo ***** ERROR $it") }
        )
    }

    override fun updateSenderReceiverContacts() {

        // TODO: testing
        val rowId = "3f725f30-4e34-4111-aa67-4870921536e8"

        val document = """
            query getUserInvite(${'$'}id: ID!) {
                getUserInviteToConnect(id: ${'$'}id) {
                    userId
                }
            }
            """.trimIndent()

        println("AmplifyDbRepo ***** DOC $document")

        val inviteSenderUserIdQuery = SimpleGraphQLRequest<String>(
            document,
            mapOf("id" to rowId),
            String::class.java,
            GsonVariablesSerializer())

        Amplify.API.query(
            inviteSenderUserIdQuery,
            {
                val gson = Gson()
                val response = gson.fromJson(it.data, InviteSenderUserIdResponse::class.java)

                response?.getUserInviteToConnect?.userId?.let { userId ->

                    val contactId = "216ba540-0011-70d0-bb72-5b51c19ae56a"
                    val mutationDocument = """
                        mutation addUserContact(${'$'}userId: ID!, ${'$'}contactId: ID!) {
                            createUserContact(input: {userId: ${'$'}userId, contactId: ${'$'}contactId}) {
                                userId
                                contactId
                            }
                        }
                    """.trimIndent()

                    val addUserContactMutation = SimpleGraphQLRequest<String>(
                        mutationDocument,
                        mapOf("userId" to userId, "contactId" to contactId),
                        String::class.java,
                        GsonVariablesSerializer()
                    )

                    Amplify.API.mutate(
                        addUserContactMutation,
                        { mutationResult ->
                            println("AmplifyDbRepo ***** Mutation SUCCESS: ${mutationResult.data}")
                        },
                        { mutationError ->
                            println("AmplifyDbRepo ***** Mutation FAILED: $mutationError")
                        }
                    )

                    val addUserContactMutation2 = SimpleGraphQLRequest<String>(
                        mutationDocument,
                        mapOf("contactId" to userId, "userId" to contactId),
                        String::class.java,
                        GsonVariablesSerializer()
                    )

                    Amplify.API.mutate(
                        addUserContactMutation2,
                        { mutationResult ->
                            println("AmplifyDbRepo ***** Mutation SUCCESS: ${mutationResult.data}")
                        },
                        { mutationError ->
                            println("AmplifyDbRepo ***** Mutation FAILED: $mutationError")
                        }
                    )

                }
            },
            { println("AmplifyDbRepo ***** ERR $it") }
        )
    }

    override fun createConnectInvite(userIds: Pair<String?, String?>) {

        val senderUser = User.builder()
            .firstName("test first")
            .lastName("test second")
            .userId(userIds.first)
            .build()

        val inviteToConnect = InviteToConnect.builder()
            .receiverUserId(userIds.second)
            .invites(senderUser)
            .build()

        Amplify.API.mutate(
            ModelMutation.create(inviteToConnect),
            { response -> println("Success! Invite created: ${response.data}") },
            { error -> println("Failed to create invite: $error") }
        )
    }

    data class InviteSenderUserIdResponse(
        val getUserInviteToConnect: UserInviteDetails
    )

    data class UserInviteDetails(
        val userId: String
    )
}

