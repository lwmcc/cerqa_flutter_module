/* tslint:disable */
/* eslint-disable */
// this is an auto generated file. This will be overwritten

import * as APITypes from "./API";
type GeneratedMutation<InputType, OutputType> = string & {
  __generatedMutationInput: InputType;
  __generatedMutationOutput: OutputType;
};

export const cleanupUnknownData = /* GraphQL */ `mutation CleanupUnknownData {
  cleanupUnknownData
}
` as GeneratedMutation<
  APITypes.CleanupUnknownDataMutationVariables,
  APITypes.CleanupUnknownDataMutation
>;
export const createChannel = /* GraphQL */ `mutation CreateChannel(
  $condition: ModelChannelConditionInput
  $input: CreateChannelInput!
) {
  createChannel(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.CreateChannelMutationVariables,
  APITypes.CreateChannelMutation
>;
export const createFcmToken = /* GraphQL */ `mutation CreateFcmToken(
  $condition: ModelFcmTokenConditionInput
  $input: CreateFcmTokenInput!
) {
  createFcmToken(condition: $condition, input: $input) {
    createdAt
    deviceId
    platform
    token
    updatedAt
    userId
    __typename
  }
}
` as GeneratedMutation<
  APITypes.CreateFcmTokenMutationVariables,
  APITypes.CreateFcmTokenMutation
>;
export const createGroup = /* GraphQL */ `mutation CreateGroup(
  $condition: ModelGroupConditionInput
  $input: CreateGroupInput!
) {
  createGroup(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.CreateGroupMutationVariables,
  APITypes.CreateGroupMutation
>;
export const createInvite = /* GraphQL */ `mutation CreateInvite(
  $condition: ModelInviteConditionInput
  $input: CreateInviteInput!
) {
  createInvite(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.CreateInviteMutationVariables,
  APITypes.CreateInviteMutation
>;
export const createMessage = /* GraphQL */ `mutation CreateMessage(
  $condition: ModelMessageConditionInput
  $input: CreateMessageInput!
) {
  createMessage(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.CreateMessageMutationVariables,
  APITypes.CreateMessageMutation
>;
export const createNotification = /* GraphQL */ `mutation CreateNotification(
  $condition: ModelNotificationConditionInput
  $input: CreateNotificationInput!
) {
  createNotification(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.CreateNotificationMutationVariables,
  APITypes.CreateNotificationMutation
>;
export const createUser = /* GraphQL */ `mutation CreateUser(
  $condition: ModelUserConditionInput
  $input: CreateUserInput!
) {
  createUser(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.CreateUserMutationVariables,
  APITypes.CreateUserMutation
>;
export const createUserContact = /* GraphQL */ `mutation CreateUserContact(
  $condition: ModelUserContactConditionInput
  $input: CreateUserContactInput!
) {
  createUserContact(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.CreateUserContactMutationVariables,
  APITypes.CreateUserContactMutation
>;
export const createUserGroup = /* GraphQL */ `mutation CreateUserGroup(
  $condition: ModelUserGroupConditionInput
  $input: CreateUserGroupInput!
) {
  createUserGroup(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.CreateUserGroupMutationVariables,
  APITypes.CreateUserGroupMutation
>;
export const deleteChannel = /* GraphQL */ `mutation DeleteChannel(
  $condition: ModelChannelConditionInput
  $input: DeleteChannelInput!
) {
  deleteChannel(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.DeleteChannelMutationVariables,
  APITypes.DeleteChannelMutation
>;
export const deleteFcmToken = /* GraphQL */ `mutation DeleteFcmToken(
  $condition: ModelFcmTokenConditionInput
  $input: DeleteFcmTokenInput!
) {
  deleteFcmToken(condition: $condition, input: $input) {
    createdAt
    deviceId
    platform
    token
    updatedAt
    userId
    __typename
  }
}
` as GeneratedMutation<
  APITypes.DeleteFcmTokenMutationVariables,
  APITypes.DeleteFcmTokenMutation
>;
export const deleteGroup = /* GraphQL */ `mutation DeleteGroup(
  $condition: ModelGroupConditionInput
  $input: DeleteGroupInput!
) {
  deleteGroup(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.DeleteGroupMutationVariables,
  APITypes.DeleteGroupMutation
>;
export const deleteInvite = /* GraphQL */ `mutation DeleteInvite(
  $condition: ModelInviteConditionInput
  $input: DeleteInviteInput!
) {
  deleteInvite(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.DeleteInviteMutationVariables,
  APITypes.DeleteInviteMutation
>;
export const deleteMessage = /* GraphQL */ `mutation DeleteMessage(
  $condition: ModelMessageConditionInput
  $input: DeleteMessageInput!
) {
  deleteMessage(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.DeleteMessageMutationVariables,
  APITypes.DeleteMessageMutation
>;
export const deleteNotification = /* GraphQL */ `mutation DeleteNotification(
  $condition: ModelNotificationConditionInput
  $input: DeleteNotificationInput!
) {
  deleteNotification(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.DeleteNotificationMutationVariables,
  APITypes.DeleteNotificationMutation
>;
export const deleteUser = /* GraphQL */ `mutation DeleteUser(
  $condition: ModelUserConditionInput
  $input: DeleteUserInput!
) {
  deleteUser(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.DeleteUserMutationVariables,
  APITypes.DeleteUserMutation
>;
export const deleteUserContact = /* GraphQL */ `mutation DeleteUserContact(
  $condition: ModelUserContactConditionInput
  $input: DeleteUserContactInput!
) {
  deleteUserContact(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.DeleteUserContactMutationVariables,
  APITypes.DeleteUserContactMutation
>;
export const deleteUserGroup = /* GraphQL */ `mutation DeleteUserGroup(
  $condition: ModelUserGroupConditionInput
  $input: DeleteUserGroupInput!
) {
  deleteUserGroup(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.DeleteUserGroupMutationVariables,
  APITypes.DeleteUserGroupMutation
>;
export const sendInviteNotification = /* GraphQL */ `mutation SendInviteNotification(
  $inviteId: String!
  $recipientUserId: String!
  $senderName: String!
) {
  sendInviteNotification(
    inviteId: $inviteId
    recipientUserId: $recipientUserId
    senderName: $senderName
  ) {
    deviceCount
    message
    success
    __typename
  }
}
` as GeneratedMutation<
  APITypes.SendInviteNotificationMutationVariables,
  APITypes.SendInviteNotificationMutation
>;
export const updateChannel = /* GraphQL */ `mutation UpdateChannel(
  $condition: ModelChannelConditionInput
  $input: UpdateChannelInput!
) {
  updateChannel(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.UpdateChannelMutationVariables,
  APITypes.UpdateChannelMutation
>;
export const updateFcmToken = /* GraphQL */ `mutation UpdateFcmToken(
  $condition: ModelFcmTokenConditionInput
  $input: UpdateFcmTokenInput!
) {
  updateFcmToken(condition: $condition, input: $input) {
    createdAt
    deviceId
    platform
    token
    updatedAt
    userId
    __typename
  }
}
` as GeneratedMutation<
  APITypes.UpdateFcmTokenMutationVariables,
  APITypes.UpdateFcmTokenMutation
>;
export const updateGroup = /* GraphQL */ `mutation UpdateGroup(
  $condition: ModelGroupConditionInput
  $input: UpdateGroupInput!
) {
  updateGroup(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.UpdateGroupMutationVariables,
  APITypes.UpdateGroupMutation
>;
export const updateInvite = /* GraphQL */ `mutation UpdateInvite(
  $condition: ModelInviteConditionInput
  $input: UpdateInviteInput!
) {
  updateInvite(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.UpdateInviteMutationVariables,
  APITypes.UpdateInviteMutation
>;
export const updateMessage = /* GraphQL */ `mutation UpdateMessage(
  $condition: ModelMessageConditionInput
  $input: UpdateMessageInput!
) {
  updateMessage(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.UpdateMessageMutationVariables,
  APITypes.UpdateMessageMutation
>;
export const updateNotification = /* GraphQL */ `mutation UpdateNotification(
  $condition: ModelNotificationConditionInput
  $input: UpdateNotificationInput!
) {
  updateNotification(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.UpdateNotificationMutationVariables,
  APITypes.UpdateNotificationMutation
>;
export const updatePresence = /* GraphQL */ `mutation UpdatePresence(
  $isOnline: Boolean!
  $platform: String
  $userId: String!
) {
  updatePresence(isOnline: $isOnline, platform: $platform, userId: $userId)
}
` as GeneratedMutation<
  APITypes.UpdatePresenceMutationVariables,
  APITypes.UpdatePresenceMutation
>;
export const updateUser = /* GraphQL */ `mutation UpdateUser(
  $condition: ModelUserConditionInput
  $input: UpdateUserInput!
) {
  updateUser(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.UpdateUserMutationVariables,
  APITypes.UpdateUserMutation
>;
export const updateUserContact = /* GraphQL */ `mutation UpdateUserContact(
  $condition: ModelUserContactConditionInput
  $input: UpdateUserContactInput!
) {
  updateUserContact(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.UpdateUserContactMutationVariables,
  APITypes.UpdateUserContactMutation
>;
export const updateUserGroup = /* GraphQL */ `mutation UpdateUserGroup(
  $condition: ModelUserGroupConditionInput
  $input: UpdateUserGroupInput!
) {
  updateUserGroup(condition: $condition, input: $input) {
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
` as GeneratedMutation<
  APITypes.UpdateUserGroupMutationVariables,
  APITypes.UpdateUserGroupMutation
>;
