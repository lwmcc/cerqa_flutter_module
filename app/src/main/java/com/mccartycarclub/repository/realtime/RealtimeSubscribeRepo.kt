package com.mccartycarclub.repository.realtime

import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.datastore.generated.model.Channel
import kotlinx.coroutines.flow.Flow

interface RealtimeSubscribeRepo {
    suspend fun createUserChannel(userId: String)
}