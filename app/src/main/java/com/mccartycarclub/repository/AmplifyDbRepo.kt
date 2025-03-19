package com.mccartycarclub.repository

import android.R.id
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
import com.amplifyframework.core.model.ModelReference
import com.amplifyframework.core.model.includes
import com.amplifyframework.datastore.generated.model.Contact
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserContact
import com.amplifyframework.datastore.generated.model.UserGroup
import com.amplifyframework.datastore.generated.model.UserInviteToConnect
import com.amplifyframework.datastore.generated.model.UserPath
import com.google.gson.Gson
import com.mccartycarclub.ui.viewmodels.MainViewModel.Companion.TEST_USER_1
import com.mccartycarclub.ui.viewmodels.MainViewModel.Companion.TEST_USER_2
import java.util.UUID
import javax.inject.Inject


class AmplifyDbRepo @Inject constructor() : DbRepo {

    @OptIn(InternalAmplifyApi::class)
    override fun fetchUserGroups(userId: String) {

        Amplify.API.query(
            ModelQuery.list(
                UserGroup::class.java,
                UserGroup.USER.eq("344433-1031-7026-1ea5-9e5c424b27de")
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

        // "344433-1031-7026-1ea5-9e5c424b27de"

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
                //println("AmplifyDbRepo ***** USER ${response.data}")
                user(response.data as User)
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
                        // println("AmplifyDbRepo *****Failed to create USER Contact: $error")
                    }
                )
                //   println("AmplifyDbRepo ***** contactResponse: ${contactResponse}")
            },
            { error ->
                //  println("AmplifyDbRepo *****Failed to create Contact: $error")
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
                    //println("AmplifyDbRepo ***** ID ${userContact.contact}")
                    // println("AmplifyDbRepo ***** USER ${userContact.user}")

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

    val document = """
            query SenderReceiverContactsQuery(${'$'}senderId: String!) {
                contactInviteAccept(senderId: ${'$'}senderId)
            }
          }""".trimIndent()

    // TODO: update sender and receiver contacts
    data class SenderReceiverContactsUpdateIds(
        val senderId: String,
        val receiverUserId: String
    )

    data class SenderReceiverContactsResponse(
        val contactInviteAccept: SenderReceiverContactsUpdateIds
    )

    override fun updateSenderReceiverContacts() {

        val document = """
            query InviteSenderUserIdQuery(${'$'}senderUserId: String!) {
                inviteSenderUserId(senderUserId: ${'$'}senderUserId) {
                    senderUserId
                    executionDuration
                }
            }
        """.trimIndent()

        val inviteSenderUserIdQuery = SimpleGraphQLRequest<String>(
            document,
            mapOf("senderUserId" to "hello world!!!"),
            String::class.java,
            GsonVariablesSerializer())

        Amplify.API.query(
            inviteSenderUserIdQuery,
            {
                println("AmplifyDbRepo ***** DATA ${it.errors}")
                println("AmplifyDbRepo ***** DATA ${it.data}")
                println("AmplifyDbRepo ***** DATA ${it.hasErrors()}")
                var gson = Gson()
                val response = gson.fromJson(it.data, InviteSenderUserIdResponse::class.java)
                println("AmplifyDbRepo ***** RES $$response")
            },
            { println("AmplifyDbRepo ***** ERR $it") }
        )

/*        val senderReceiverContactsUpdateIdsQuery = SimpleGraphQLRequest<String>(
            document,
            mapOf("senderId" to "Amplify"),
            String::class.java,
            GsonVariablesSerializer()
        )

        Amplify.API.query(
            senderReceiverContactsUpdateIdsQuery,
            {
                if (it.hasErrors()) {
                    println("AmplifyDbRepo ***** RAW RESPONSE ERROR : ${it.errors}")
                } else {
                    println("AmplifyDbRepo ***** RAW RESPONSE: ${it.data}")
                }

                val gson = Gson()
                val response = gson.fromJson(it.data, SenderReceiverContactsResponse::class.java)
                println("AmplifyDbRepo ***** RESPONSE ${response}")

            },
            { println("AmplifyDbRepo ***** MESSAGE $it") },
        )*/
    }

    data class InviteSenderUserIdDetails(
        val senderUserId: String,
        val executionDuration: Float
    )

    data class InviteSenderUserIdResponse(
        val inviteSenderUserId: InviteSenderUserIdDetails
    )
}