package com.mccartycarclub.repository

import android.util.Log
import com.amplifyframework.api.ApiCategory
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.AppData
import javax.inject.Inject

class AmplifyDbRepo @Inject constructor(): DbRepo {

    override fun getUser(userId: String) {
        Amplify.API.query(
            ModelQuery.list(AppData::class.java, AppData.USER_ID.eq(userId)),
            { response ->
                if (response.data != null) {
                    val user = response.data.items.firstOrNull()
                    val name = user?.name
                    val userName = user?.userName
                    val email = user?.email
                    val contacts = user?.contacts
                    val groups = user?.groups

                    Log.d("AmplifyDbRepo  *****", "User: $name User Name: $userName Email: $email")
                    Log.d("AmplifyDbRepo  *****", "contacts: $contacts")
                    Log.d("AmplifyDbRepo  *****", "groups: $groups")
                } else {
                    Log.e("AmplifyDbRepo *****", "No data found for User with ID: 31cb55f0-1031-7026-1ea5-9e5c424b27de")
                }
            },
            { error ->
                Log.e("v *****", "Failed to query Todo by ID.", error)
            }
        )
    }
}