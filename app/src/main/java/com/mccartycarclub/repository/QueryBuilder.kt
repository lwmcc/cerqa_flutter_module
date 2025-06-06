package com.mccartycarclub.repository

import com.amplifyframework.core.model.query.predicate.QueryPredicate
import com.amplifyframework.datastore.generated.model.Invite

interface QueryBuilder {
    fun buildSenderQueryPredicate(invites: List<Invite>): QueryPredicate?
    fun buildReceiverQueryPredicate(invites: List<Invite>): QueryPredicate?

    // TODO: change name of this function
    fun buildInviteQueryPredicate(senderUserId: String, receiverUserId: String) =
        Invite.SENDER_ID.eq(senderUserId)
            .and(Invite.RECEIVER_ID.eq(receiverUserId))
}