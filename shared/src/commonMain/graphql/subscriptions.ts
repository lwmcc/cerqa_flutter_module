/* tslint:disable */
/* eslint-disable */
// this is an auto generated file. This will be overwritten

import * as APITypes from "./API";
type GeneratedSubscription<InputType, OutputType> = string & {
  __generatedSubscriptionInput: InputType;
  __generatedSubscriptionOutput: OutputType;
};

export const onCreateChannel = /* GraphQL */ `subscription OnCreateChannel($filter: ModelSubscriptionChannelFilterInput) {
  onCreateChannel(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnCreateChannelSubscriptionVariables,
  APITypes.OnCreateChannelSubscription
>;
export const onCreateFcmToken = /* GraphQL */ `subscription OnCreateFcmToken($filter: ModelSubscriptionFcmTokenFilterInput) {
  onCreateFcmToken(filter: $filter) {
    createdAt
    deviceId
    platform
    token
    updatedAt
    userId
    __typename
  }
}
` as GeneratedSubscription<
  APITypes.OnCreateFcmTokenSubscriptionVariables,
  APITypes.OnCreateFcmTokenSubscription
>;
export const onCreateGroup = /* GraphQL */ `subscription OnCreateGroup($filter: ModelSubscriptionGroupFilterInput) {
  onCreateGroup(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnCreateGroupSubscriptionVariables,
  APITypes.OnCreateGroupSubscription
>;
export const onCreateInvite = /* GraphQL */ `subscription OnCreateInvite($filter: ModelSubscriptionInviteFilterInput) {
  onCreateInvite(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnCreateInviteSubscriptionVariables,
  APITypes.OnCreateInviteSubscription
>;
export const onCreateMessage = /* GraphQL */ `subscription OnCreateMessage($filter: ModelSubscriptionMessageFilterInput) {
  onCreateMessage(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnCreateMessageSubscriptionVariables,
  APITypes.OnCreateMessageSubscription
>;
export const onCreateNotification = /* GraphQL */ `subscription OnCreateNotification(
  $filter: ModelSubscriptionNotificationFilterInput
) {
  onCreateNotification(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnCreateNotificationSubscriptionVariables,
  APITypes.OnCreateNotificationSubscription
>;
export const onCreateUser = /* GraphQL */ `subscription OnCreateUser($filter: ModelSubscriptionUserFilterInput) {
  onCreateUser(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnCreateUserSubscriptionVariables,
  APITypes.OnCreateUserSubscription
>;
export const onCreateUserContact = /* GraphQL */ `subscription OnCreateUserContact(
  $filter: ModelSubscriptionUserContactFilterInput
) {
  onCreateUserContact(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnCreateUserContactSubscriptionVariables,
  APITypes.OnCreateUserContactSubscription
>;
export const onCreateUserGroup = /* GraphQL */ `subscription OnCreateUserGroup($filter: ModelSubscriptionUserGroupFilterInput) {
  onCreateUserGroup(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnCreateUserGroupSubscriptionVariables,
  APITypes.OnCreateUserGroupSubscription
>;
export const onDeleteChannel = /* GraphQL */ `subscription OnDeleteChannel($filter: ModelSubscriptionChannelFilterInput) {
  onDeleteChannel(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnDeleteChannelSubscriptionVariables,
  APITypes.OnDeleteChannelSubscription
>;
export const onDeleteFcmToken = /* GraphQL */ `subscription OnDeleteFcmToken($filter: ModelSubscriptionFcmTokenFilterInput) {
  onDeleteFcmToken(filter: $filter) {
    createdAt
    deviceId
    platform
    token
    updatedAt
    userId
    __typename
  }
}
` as GeneratedSubscription<
  APITypes.OnDeleteFcmTokenSubscriptionVariables,
  APITypes.OnDeleteFcmTokenSubscription
>;
export const onDeleteGroup = /* GraphQL */ `subscription OnDeleteGroup($filter: ModelSubscriptionGroupFilterInput) {
  onDeleteGroup(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnDeleteGroupSubscriptionVariables,
  APITypes.OnDeleteGroupSubscription
>;
export const onDeleteInvite = /* GraphQL */ `subscription OnDeleteInvite($filter: ModelSubscriptionInviteFilterInput) {
  onDeleteInvite(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnDeleteInviteSubscriptionVariables,
  APITypes.OnDeleteInviteSubscription
>;
export const onDeleteMessage = /* GraphQL */ `subscription OnDeleteMessage($filter: ModelSubscriptionMessageFilterInput) {
  onDeleteMessage(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnDeleteMessageSubscriptionVariables,
  APITypes.OnDeleteMessageSubscription
>;
export const onDeleteNotification = /* GraphQL */ `subscription OnDeleteNotification(
  $filter: ModelSubscriptionNotificationFilterInput
) {
  onDeleteNotification(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnDeleteNotificationSubscriptionVariables,
  APITypes.OnDeleteNotificationSubscription
>;
export const onDeleteUser = /* GraphQL */ `subscription OnDeleteUser($filter: ModelSubscriptionUserFilterInput) {
  onDeleteUser(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnDeleteUserSubscriptionVariables,
  APITypes.OnDeleteUserSubscription
>;
export const onDeleteUserContact = /* GraphQL */ `subscription OnDeleteUserContact(
  $filter: ModelSubscriptionUserContactFilterInput
) {
  onDeleteUserContact(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnDeleteUserContactSubscriptionVariables,
  APITypes.OnDeleteUserContactSubscription
>;
export const onDeleteUserGroup = /* GraphQL */ `subscription OnDeleteUserGroup($filter: ModelSubscriptionUserGroupFilterInput) {
  onDeleteUserGroup(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnDeleteUserGroupSubscriptionVariables,
  APITypes.OnDeleteUserGroupSubscription
>;
export const onUpdateChannel = /* GraphQL */ `subscription OnUpdateChannel($filter: ModelSubscriptionChannelFilterInput) {
  onUpdateChannel(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnUpdateChannelSubscriptionVariables,
  APITypes.OnUpdateChannelSubscription
>;
export const onUpdateFcmToken = /* GraphQL */ `subscription OnUpdateFcmToken($filter: ModelSubscriptionFcmTokenFilterInput) {
  onUpdateFcmToken(filter: $filter) {
    createdAt
    deviceId
    platform
    token
    updatedAt
    userId
    __typename
  }
}
` as GeneratedSubscription<
  APITypes.OnUpdateFcmTokenSubscriptionVariables,
  APITypes.OnUpdateFcmTokenSubscription
>;
export const onUpdateGroup = /* GraphQL */ `subscription OnUpdateGroup($filter: ModelSubscriptionGroupFilterInput) {
  onUpdateGroup(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnUpdateGroupSubscriptionVariables,
  APITypes.OnUpdateGroupSubscription
>;
export const onUpdateInvite = /* GraphQL */ `subscription OnUpdateInvite($filter: ModelSubscriptionInviteFilterInput) {
  onUpdateInvite(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnUpdateInviteSubscriptionVariables,
  APITypes.OnUpdateInviteSubscription
>;
export const onUpdateMessage = /* GraphQL */ `subscription OnUpdateMessage($filter: ModelSubscriptionMessageFilterInput) {
  onUpdateMessage(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnUpdateMessageSubscriptionVariables,
  APITypes.OnUpdateMessageSubscription
>;
export const onUpdateNotification = /* GraphQL */ `subscription OnUpdateNotification(
  $filter: ModelSubscriptionNotificationFilterInput
) {
  onUpdateNotification(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnUpdateNotificationSubscriptionVariables,
  APITypes.OnUpdateNotificationSubscription
>;
export const onUpdateUser = /* GraphQL */ `subscription OnUpdateUser($filter: ModelSubscriptionUserFilterInput) {
  onUpdateUser(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnUpdateUserSubscriptionVariables,
  APITypes.OnUpdateUserSubscription
>;
export const onUpdateUserContact = /* GraphQL */ `subscription OnUpdateUserContact(
  $filter: ModelSubscriptionUserContactFilterInput
) {
  onUpdateUserContact(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnUpdateUserContactSubscriptionVariables,
  APITypes.OnUpdateUserContactSubscription
>;
export const onUpdateUserGroup = /* GraphQL */ `subscription OnUpdateUserGroup($filter: ModelSubscriptionUserGroupFilterInput) {
  onUpdateUserGroup(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnUpdateUserGroupSubscriptionVariables,
  APITypes.OnUpdateUserGroupSubscription
>;
