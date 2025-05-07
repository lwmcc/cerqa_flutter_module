package com.mccartycarclub.repository

import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.datastore.generated.model.Invite
import com.amplifyframework.datastore.generated.model.User
import javax.inject.Inject

class ContactsQueryBuilder @Inject constructor() : QueryBuilder {
    override fun buildSenderQueryPredicate(invites: List<Invite>) = invites
        .map { User.USER_ID.eq(it.receiverId) as QueryPredicate }
        .reduceOrNull { acc, value -> acc.or(value) }

    override fun buildReceiverQueryPredicate(invites: List<Invite>) =
        invites.map { User.USER_ID.eq(it.senderId) as QueryPredicate }
            .reduceOrNull { acc, value -> acc.or(value) }
}