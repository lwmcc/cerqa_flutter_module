package com.mccartycarclub.repository

import com.amplifyframework.api.ApiException
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.model.Model
import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.kotlin.api.KotlinApiFacade

class QueryHelper (private val amplifyApi: KotlinApiFacade) {

    suspend fun validate(predicate: QueryPredicate) {
        try {
            val response = amplifyApi.query(ModelQuery.list(User::class.java, predicate))

            if (response.hasData()) {
                // success
            } else {
                // error
            }
        } catch(e: ApiException) {
            // error
        }
    }
}
