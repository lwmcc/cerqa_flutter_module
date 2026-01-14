/* tslint:disable */
/* eslint-disable */
// this is an auto generated file. This will be overwritten

import * as APITypes from "./API";
type GeneratedQuery<InputType, OutputType> = string & {
  __generatedQueryInput: InputType;
  __generatedQueryOutput: OutputType;
};

export const fetchAblyJwt = /* GraphQL */ `query FetchAblyJwt($userId: String) {
  fetchAblyJwt(userId: $userId) {
    clientId
    keyName
    mac
    nonce
    timestamp
    __typename
  }
}
` as GeneratedQuery<
  APITypes.FetchAblyJwtQueryVariables,
  APITypes.FetchAblyJwtQuery
>;
export const fetchPendingSentInviteStatus = /* GraphQL */ `query FetchPendingSentInviteStatus($userName: String) {
  fetchPendingSentInviteStatus(userName: $userName)
}
` as GeneratedQuery<
  APITypes.FetchPendingSentInviteStatusQueryVariables,
  APITypes.FetchPendingSentInviteStatusQuery
>;
export const fetchUserWithContactInfo = /* GraphQL */ `query FetchUserWithContactInfo($userName: String) {
  fetchUserWithContactInfo(userName: $userName) {
    id
    userName
    __typename
  }
}
` as GeneratedQuery<
  APITypes.FetchUserWithContactInfoQueryVariables,
  APITypes.FetchUserWithContactInfoQuery
>;
export const getChannel = /* GraphQL */ `query GetChannel($id: ID!) {
  getChannel(id: $id) {
    createdAt
    creator {
      avatarUri
      createdAt
      email
      firstName
      id
      lastName
      name
      phone
      updatedAt
      userId
      userName
      __typename
    }
    creatorId
    id
    isGroup
    isPublic
    messages {
      nextToken
      __typename
    }
    name
    receiver {
      avatarUri
      createdAt
      email
      firstName
      id
      lastName
      name
      phone
      updatedAt
      userId
      userName
      __typename
    }
    receiverId
    updatedAt
    __typename
  }
}
` as GeneratedQuery<
  APITypes.GetChannelQueryVariables,
  APITypes.GetChannelQuery
>;
export const getFcmToken = /* GraphQL */ `query GetFcmToken($deviceId: ID!, $userId: ID!) {
  getFcmToken(deviceId: $deviceId, userId: $userId) {
    createdAt
    deviceId
    platform
    token
    updatedAt
    userId
    __typename
  }
}
` as GeneratedQuery<
  APITypes.GetFcmTokenQueryVariables,
  APITypes.GetFcmTokenQuery
>;
export const getGroup = /* GraphQL */ `query GetGroup($id: ID!) {
  getGroup(id: $id) {
    createdAt
    groupId
    id
    name
    updatedAt
    users {
      nextToken
      __typename
    }
    __typename
  }
}
` as GeneratedQuery<APITypes.GetGroupQueryVariables, APITypes.GetGroupQuery>;
export const getInvite = /* GraphQL */ `query GetInvite($id: ID!) {
  getInvite(id: $id) {
    createdAt
    id
    receiverId
    senderId
    updatedAt
    user {
      avatarUri
      createdAt
      email
      firstName
      id
      lastName
      name
      phone
      updatedAt
      userId
      userName
      __typename
    }
    userId
    __typename
  }
}
` as GeneratedQuery<APITypes.GetInviteQueryVariables, APITypes.GetInviteQuery>;
export const getMessage = /* GraphQL */ `query GetMessage($id: ID!) {
  getMessage(id: $id) {
    channel {
      createdAt
      creatorId
      id
      isGroup
      isPublic
      name
      receiverId
      updatedAt
      __typename
    }
    channelId
    content
    createdAt
    id
    sender {
      avatarUri
      createdAt
      email
      firstName
      id
      lastName
      name
      phone
      updatedAt
      userId
      userName
      __typename
    }
    senderId
    updatedAt
    __typename
  }
}
` as GeneratedQuery<
  APITypes.GetMessageQueryVariables,
  APITypes.GetMessageQuery
>;
export const getNotification = /* GraphQL */ `query GetNotification($id: ID!) {
  getNotification(id: $id) {
    createdAt
    id
    isRead
    message
    relatedId
    senderName
    senderUserId
    title
    type
    updatedAt
    user {
      avatarUri
      createdAt
      email
      firstName
      id
      lastName
      name
      phone
      updatedAt
      userId
      userName
      __typename
    }
    userId
    __typename
  }
}
` as GeneratedQuery<
  APITypes.GetNotificationQueryVariables,
  APITypes.GetNotificationQuery
>;
export const getUser = /* GraphQL */ `query GetUser($id: ID!) {
  getUser(id: $id) {
    asContact {
      nextToken
      __typename
    }
    avatarUri
    contacts {
      nextToken
      __typename
    }
    createdAt
    createdChannels {
      nextToken
      __typename
    }
    email
    firstName
    groups {
      nextToken
      __typename
    }
    id
    invites {
      nextToken
      __typename
    }
    lastName
    messages {
      nextToken
      __typename
    }
    name
    notifications {
      nextToken
      __typename
    }
    phone
    receivedChannels {
      nextToken
      __typename
    }
    updatedAt
    userId
    userName
    __typename
  }
}
` as GeneratedQuery<APITypes.GetUserQueryVariables, APITypes.GetUserQuery>;
export const getUserByUserId = /* GraphQL */ `query GetUserByUserId($userId: String!) {
  getUserByUserId(userId: $userId) {
    avatarUri
    email
    firstName
    id
    lastName
    name
    phone
    userId
    userName
    __typename
  }
}
` as GeneratedQuery<
  APITypes.GetUserByUserIdQueryVariables,
  APITypes.GetUserByUserIdQuery
>;
export const getUserContact = /* GraphQL */ `query GetUserContact($id: ID!) {
  getUserContact(id: $id) {
    contact {
      avatarUri
      createdAt
      email
      firstName
      id
      lastName
      name
      phone
      updatedAt
      userId
      userName
      __typename
    }
    contactId
    createdAt
    id
    updatedAt
    user {
      avatarUri
      createdAt
      email
      firstName
      id
      lastName
      name
      phone
      updatedAt
      userId
      userName
      __typename
    }
    userId
    __typename
  }
}
` as GeneratedQuery<
  APITypes.GetUserContactQueryVariables,
  APITypes.GetUserContactQuery
>;
export const getUserGroup = /* GraphQL */ `query GetUserGroup($id: ID!) {
  getUserGroup(id: $id) {
    createdAt
    group {
      createdAt
      groupId
      id
      name
      updatedAt
      __typename
    }
    groupId
    id
    role
    updatedAt
    user {
      avatarUri
      createdAt
      email
      firstName
      id
      lastName
      name
      phone
      updatedAt
      userId
      userName
      __typename
    }
    userId
    __typename
  }
}
` as GeneratedQuery<
  APITypes.GetUserGroupQueryVariables,
  APITypes.GetUserGroupQuery
>;
export const hasUserCreatedProfile = /* GraphQL */ `query HasUserCreatedProfile($userId: String!) {
  hasUserCreatedProfile(userId: $userId) {
    isProfileComplete
    missingFields
    __typename
  }
}
` as GeneratedQuery<
  APITypes.HasUserCreatedProfileQueryVariables,
  APITypes.HasUserCreatedProfileQuery
>;
export const listByPhone = /* GraphQL */ `query ListByPhone(
  $filter: ModelUserFilterInput
  $limit: Int
  $nextToken: String
  $phone: String!
  $sortDirection: ModelSortDirection
  $userName: ModelStringKeyConditionInput
) {
  listByPhone(
    filter: $filter
    limit: $limit
    nextToken: $nextToken
    phone: $phone
    sortDirection: $sortDirection
    userName: $userName
  ) {
    items {
      avatarUri
      createdAt
      email
      firstName
      id
      lastName
      name
      phone
      updatedAt
      userId
      userName
      __typename
    }
    nextToken
    __typename
  }
}
` as GeneratedQuery<
  APITypes.ListByPhoneQueryVariables,
  APITypes.ListByPhoneQuery
>;
export const listChannels = /* GraphQL */ `query ListChannels(
  $filter: ModelChannelFilterInput
  $limit: Int
  $nextToken: String
) {
  listChannels(filter: $filter, limit: $limit, nextToken: $nextToken) {
    items {
      createdAt
      creatorId
      id
      isGroup
      isPublic
      name
      receiverId
      updatedAt
      __typename
    }
    nextToken
    __typename
  }
}
` as GeneratedQuery<
  APITypes.ListChannelsQueryVariables,
  APITypes.ListChannelsQuery
>;
export const listFcmTokens = /* GraphQL */ `query ListFcmTokens(
  $deviceId: ModelIDKeyConditionInput
  $filter: ModelFcmTokenFilterInput
  $limit: Int
  $nextToken: String
  $sortDirection: ModelSortDirection
  $userId: ID
) {
  listFcmTokens(
    deviceId: $deviceId
    filter: $filter
    limit: $limit
    nextToken: $nextToken
    sortDirection: $sortDirection
    userId: $userId
  ) {
    items {
      createdAt
      deviceId
      platform
      token
      updatedAt
      userId
      __typename
    }
    nextToken
    __typename
  }
}
` as GeneratedQuery<
  APITypes.ListFcmTokensQueryVariables,
  APITypes.ListFcmTokensQuery
>;
export const listGroups = /* GraphQL */ `query ListGroups(
  $filter: ModelGroupFilterInput
  $limit: Int
  $nextToken: String
) {
  listGroups(filter: $filter, limit: $limit, nextToken: $nextToken) {
    items {
      createdAt
      groupId
      id
      name
      updatedAt
      __typename
    }
    nextToken
    __typename
  }
}
` as GeneratedQuery<
  APITypes.ListGroupsQueryVariables,
  APITypes.ListGroupsQuery
>;
export const listInvites = /* GraphQL */ `query ListInvites(
  $filter: ModelInviteFilterInput
  $limit: Int
  $nextToken: String
) {
  listInvites(filter: $filter, limit: $limit, nextToken: $nextToken) {
    items {
      createdAt
      id
      receiverId
      senderId
      updatedAt
      userId
      __typename
    }
    nextToken
    __typename
  }
}
` as GeneratedQuery<
  APITypes.ListInvitesQueryVariables,
  APITypes.ListInvitesQuery
>;
export const listMessages = /* GraphQL */ `query ListMessages(
  $filter: ModelMessageFilterInput
  $limit: Int
  $nextToken: String
) {
  listMessages(filter: $filter, limit: $limit, nextToken: $nextToken) {
    items {
      channelId
      content
      createdAt
      id
      senderId
      updatedAt
      __typename
    }
    nextToken
    __typename
  }
}
` as GeneratedQuery<
  APITypes.ListMessagesQueryVariables,
  APITypes.ListMessagesQuery
>;
export const listNotifications = /* GraphQL */ `query ListNotifications(
  $filter: ModelNotificationFilterInput
  $limit: Int
  $nextToken: String
) {
  listNotifications(filter: $filter, limit: $limit, nextToken: $nextToken) {
    items {
      createdAt
      id
      isRead
      message
      relatedId
      senderName
      senderUserId
      title
      type
      updatedAt
      userId
      __typename
    }
    nextToken
    __typename
  }
}
` as GeneratedQuery<
  APITypes.ListNotificationsQueryVariables,
  APITypes.ListNotificationsQuery
>;
export const listUserContacts = /* GraphQL */ `query ListUserContacts(
  $filter: ModelUserContactFilterInput
  $limit: Int
  $nextToken: String
) {
  listUserContacts(filter: $filter, limit: $limit, nextToken: $nextToken) {
    items {
      contactId
      createdAt
      id
      updatedAt
      userId
      __typename
    }
    nextToken
    __typename
  }
}
` as GeneratedQuery<
  APITypes.ListUserContactsQueryVariables,
  APITypes.ListUserContactsQuery
>;
export const listUserGroups = /* GraphQL */ `query ListUserGroups(
  $filter: ModelUserGroupFilterInput
  $limit: Int
  $nextToken: String
) {
  listUserGroups(filter: $filter, limit: $limit, nextToken: $nextToken) {
    items {
      createdAt
      groupId
      id
      role
      updatedAt
      userId
      __typename
    }
    nextToken
    __typename
  }
}
` as GeneratedQuery<
  APITypes.ListUserGroupsQueryVariables,
  APITypes.ListUserGroupsQuery
>;
export const listUsers = /* GraphQL */ `query ListUsers(
  $filter: ModelUserFilterInput
  $limit: Int
  $nextToken: String
) {
  listUsers(filter: $filter, limit: $limit, nextToken: $nextToken) {
    items {
      avatarUri
      createdAt
      email
      firstName
      id
      lastName
      name
      phone
      updatedAt
      userId
      userName
      __typename
    }
    nextToken
    __typename
  }
}
` as GeneratedQuery<APITypes.ListUsersQueryVariables, APITypes.ListUsersQuery>;
