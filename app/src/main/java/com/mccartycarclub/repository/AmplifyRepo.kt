package com.mccartycarclub.repository

import com.amplifyframework.api.aws.GsonVariablesSerializer
import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.model.query.predicate.QueryField
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.datastore.generated.model.InviteToConnect
import com.amplifyframework.datastore.generated.model.UserContact
import com.amplifyframework.kotlin.core.Amplify
import com.amplifyframework.core.Amplify as AmplifyCore
import com.mccartycarclub.repository.AmplifyDbRepo.Companion.USER_ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class AmplifyRepo @Inject constructor() : RemoteRepo {
    override suspend fun contactExists(
        senderUserId: String,
        receiverUserId: String,
    ): Flow<Boolean> = flow {
        val filter = UserContact.USER.eq(senderUserId)
            .and(UserContact.CONTACT.eq(receiverUserId))

        val response = Amplify.API.query(ModelQuery.list(UserContact::class.java, filter))
        val count = response.data.items.count()
        emit(count > 0)
    }

    override suspend fun hasExistingInvite(
        senderUserId: String,
        receiverUserId: String,
    ): Flow<Boolean> = flow {
        val filter = InviteToConnect.INVITES.eq(senderUserId)
            .and(InviteToConnect.RECEIVER_USER_ID.eq(receiverUserId))
        val response = Amplify.API.query(ModelQuery.list(InviteToConnect::class.java, filter))
        val count = response.data.items.count()
        emit(count > 0)
    }
}