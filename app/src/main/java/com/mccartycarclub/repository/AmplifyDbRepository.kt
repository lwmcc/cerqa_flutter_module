package com.mccartycarclub.repository

import com.amplifyframework.annotations.InternalAmplifyApi
import com.amplifyframework.api.aws.GsonVariablesSerializer
import com.amplifyframework.api.graphql.SimpleGraphQLRequest

import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserContact
import com.squareup.moshi.Json
import javax.inject.Inject


class AmplifyDbRepo @Inject constructor() : DbRepository {

    @OptIn(InternalAmplifyApi::class)
    override fun fetchUserGroups(userId: String) {

/*        Amplify.API.query(
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
        )*/
    }

    // TODO: move to own class
    override fun fetchUserContacts(
        userId: String,
        userContacts: (List<Contact>) -> Unit,
    ) {

/*        Amplify.API.query(
            get<User, UserPath>(
                User::class.java,
                User.UserIdentifier(userId)
            ) { userPath -> includes(userPath.contacts) },
            {
                val contacts = (it.data.contacts as? LoadedModelList<Contact>)?.items
                println("AmplifyDbRepo ***** fetchUserContacts FETCH USER CONTACTS ${contacts?.size} ")
            },
            {
                println("AmplifyDbRepo ***** ERROR ")
            }
        )*/
    }

    override fun fetchUsers() {

/*        val onResponse = Consumer<GraphQLResponse<User>> { response ->
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

        get<User, UserPath>(
            User::class.java,
            User.UserIdentifier("")
        ) {
            // Including the contacts field (if exists in the User model)
            includes(it.contacts)
        }.apply {
            // Execute the query
            Amplify.API.query(this, onResponse, onFailure)
        }*/
    }

    override fun fetchUser(userId: String, user: (User) -> Unit) {
/*        Amplify.API.query(get(User::class.java, userId),
            { response: GraphQLResponse<User> ->
                println("AmplifyDbRepo ***** USER ${response.data}")
                //user(response.data as User)
            },
            { println("AmplifyDbRepo ***** ERROR") }
        )*/
    }

    override fun createContact(user: User) {
/*        val contact = Contact.builder()
            .contactId(user.userId)
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
        )*/
    }

    override fun fetchUserByUserName(
        userName: String,
        data: (NetResult<User?>) -> Unit,
    ) {
/*        Amplify.API.query(
            ModelQuery.list(User::class.java, User.USER_NAME.eq(userName)),
            { response ->
                if (response.hasData()) {
                    val data = response.data.firstOrNull()
                    data(NetResult.Success(data))
                    //data?.userId
                } else {
                    data(NetResult.Success(null))
                }
            },
            { error ->
                data(NetResult.Error(error))
            }
        )*/
    }

    override fun fetchUserContacts(userId: String) {
/*        Amplify.API.query(
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
        )*/



    }

    // TODO: rename
    override fun acceptContactInvite(userId: String, rowId: (String?) -> Unit) {

/*        Amplify.API.mutate(ModelMutation.create(
            UserInviteToConnect.builder().userId(userId).build()
        ),
            { rowId(it.data.id) },
            { error ->
                rowId(null)
                // TODO: to log
                println("AmplifyDbRepo *****Failed to create acceptContactInvite: $error")
            }
        )*/
    }

    override fun fetchUserIdFromSentInvite(rowId: String, userId: (String?) -> Unit) {
/*        Amplify.API.query(get(UserInviteToConnect::class.java, rowId),
            {
                it.data?.let { data ->
                    userId(data.userId)
                } ?: run { println("AmplifyDbRepo ***** ERROR $it") }
            },
            { println("AmplifyDbRepo ***** ERROR $it") }
        )*/
    }

    override fun hasExistingInvite(
        senderUserId: String,
        receiverUserId: String,
        hasInvite: (Boolean) -> Unit,
    ) {
        val document = """
            query ListInviteToConnects {
              listInviteToConnects(
                filter: {
                  senderUserId: { eq: "$senderUserId" }
                  receiverUserId: { eq: "$receiverUserId" }
                }
              ) {
                items {
                  id
                  senderUserId
                  receiverUserId
                  invites {
                    id
                    firstName
                    lastName
                  }
                }
              }
            }
        """.trimIndent()

        val request = SimpleGraphQLRequest<String>(
            document,
            mapOf(USER_ID to senderUserId),
            String::class.java,
            GsonVariablesSerializer()
        )
/*
        Amplify.API.query(
            request,
            { response ->
                val numInvites = parseJson(response.data)?.listInviteToConnects?.items?.size ?: 0
                if (numInvites > 0) {
                    hasInvite(true)
                } else {
                    hasInvite(false)
                }
            },
            { error ->
                hasInvite(false)
                println("AmplifyDbRepo ***** ERROR ${error.message}")
            }
        )*/
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
            mapOf(USER_ID to rowId),
            String::class.java,
            GsonVariablesSerializer())

/*        Amplify.API.query(
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
        )*/
    }

    override fun createConnectInvite(
        userIds: Pair<String?, String?>,
        hasExistingInvite: (Boolean) -> Unit,
    ) {

/*        hasExistingInvite(
            senderUserId = userIds.first.toString(),
            receiverUserId = userIds.second.toString(),
            hasInvite = { hasInvite ->
                hasExistingInvite(hasInvite)
            },
        )*/
/*
        val senderUser = User.builder()
            .firstName("test first")
            .lastName("test second")
            .id(userIds.first)
            .userId(userIds.first)
            .build()

        val inviteToConnect = InviteToConnect.builder()
            .receiverUserId(userIds.second)
            .invites(senderUser)
            .build()

        Amplify.API.mutate(
            ModelMutation.create(inviteToConnect),
            { response -> println("AmplifyDbRepo ***** Success! Invite created: ${response.data}") },
            { error -> println("AmplifyDbRepo *****Failed to create invite: $error") }
        )*/
    }

    override fun contactExists(
        senderUserId: String,
        receiverUserId: String,
        hasConnection: (Boolean) -> Unit,
    ) {

        val filter = UserContact.USER.eq(senderUserId)
            .and(UserContact.CONTACT.eq(receiverUserId))



/*        Amplify.API.query(
            ModelQuery.list(UserContact::class.java, filter),
            { response ->
                val count = response.data.items.count()
                hasConnection(count > 0)
            },
            { error ->
                println("AmplifyDbRepo ***** ${error.message}")
                hasConnection(false)
            }
        )*/
    }

    companion object {
        const val USER_ID = "id"
    }
}

data class InviteSenderUserIdResponse(
    val getUserInviteToConnect: UserInviteDetails
)

data class UserInviteDetails(
    val userId: String
)

// TODO: move
data class CheckUserContactResponse(
    val listUserContacts: ListUserContacts
)

data class ListUserContacts(
    val items: List<UserContact>
)

data class UserContact(
    val userId: String,
    val contactId: String
)

// TODO: move
data class RootResponse(
    @Json(name = "listUserContacts")
    val listUserContacts: ListUserContacts
)

data class ContactItem(
    val userId: String,
    val contactId: String
)

