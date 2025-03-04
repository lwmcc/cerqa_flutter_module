package com.mccartycarclub.repository

import android.util.Log
import com.amplifyframework.annotations.InternalAmplifyApi
import com.amplifyframework.api.ApiException
import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.Consumer
import com.amplifyframework.core.model.LoadedModelList
import com.amplifyframework.core.model.includes
import com.amplifyframework.datastore.generated.model.Contact
import com.amplifyframework.datastore.generated.model.ContactPath
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserGroup
import com.amplifyframework.datastore.generated.model.UserPath
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
                println("AmplifyDbRepo ***** $error")
            }
        )
    }

    // TODO: move to own class
    override fun fetchUserContacts(
        userId: String,
        userContacts: (List<Contact>) -> Unit,
    ) {

        // my id "31cb55f0-1031-7026-1ea5-9e5c424b27de"
        // "344433-1031-7026-1ea5-9e5c424b27de"
        // row id  "31cb55f0-1031-7026-1ea5-9e5c424b27de"
        Amplify.API.query(
            ModelQuery.get<User, UserPath>(
                User::class.java,
                User.UserIdentifier("31cb55f0-1031-7026-1ea5-9e5c424b27de")
            ) { userPath -> includes(userPath.contacts) },
            {
                val contacts = (it.data.contacts as? LoadedModelList<Contact>)?.items
                println("AmplifyDbRepo ***** USER CONTACTS ${contacts?.size} ")
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
                Log.d("AmplifyDbRepo", "User Name: ${user.firstName} ${user.lastName}")

                // Assuming the User model has a 'contacts' field (hasMany relationship)
                val ct = user.contacts



                println("AmplifyDbRepo ***** SIZE C ${ct}")



            } else {
                Log.e("MyAmplifyApp", "User not found")
            }
        }

        val onFailure = Consumer<ApiException> { error ->
            Log.e("AmplifyDbRepo", "Failed to fetch user: ${error.message}")
        }

        // Use the correct query to fetch the User by ID
        ModelQuery.get<User, UserPath>(
            User::class.java,
            User.UserIdentifier("31cb55f0-1031-7026-1ea5-9e5c424b27de")
        ) {
            // Including the contacts field (if exists in the User model)
            includes(it.contacts)
        }.apply {
            // Execute the query
            Amplify.API.query(this, onResponse, onFailure)
        }
    }
}