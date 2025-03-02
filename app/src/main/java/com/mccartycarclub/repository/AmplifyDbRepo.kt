package com.mccartycarclub.repository

import android.util.Log
import com.amplifyframework.api.ApiCategory
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.AppData
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.datastore.generated.model.UserGroup
import javax.inject.Inject

class AmplifyDbRepo @Inject constructor(): DbRepo {

    override fun fetchUserGroups(userId: String) {
        Amplify.API.query(
            ModelQuery.list(UserGroup::class.java, UserGroup.USER.eq(userId)),
            { response ->
                if (response.data != null) {
                    val userGroups = response.data.items
                    println("AmplifyDbRepo ***** RESPONSE ${response.data.items}")
                } else {
                    println("AmplifyDbRepo ***** RESPONSE NULL")
                }
            },
            { error ->
                println("AmplifyDbRepo ***** $error")
            }
        )
    }
}