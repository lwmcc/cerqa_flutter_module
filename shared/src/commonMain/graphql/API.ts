/* tslint:disable */
/* eslint-disable */
//  This file was automatically generated and should not be edited.

export type FetchAblyJwtReturnType = {
  __typename: "FetchAblyJwtReturnType",
  clientId: string,
  keyName: string,
  mac: string,
  nonce: string,
  timestamp: number,
};

export type FetchUserWithContactInfoReturnType = {
  __typename: "FetchUserWithContactInfoReturnType",
  id?: string | null,
  userName?: string | null,
};

export type Channel = {
  __typename: "Channel",
  createdAt: string,
  creator?: User | null,
  creatorId: string,
  id: string,
  isGroup?: boolean | null,
  isPublic?: boolean | null,
  messages?: ModelMessageConnection | null,
  name?: string | null,
  receiver?: User | null,
  receiverId?: string | null,
  updatedAt: string,
};

export type User = {
  __typename: "User",
  asContact?: ModelUserContactConnection | null,
  avatarUri?: string | null,
  contacts?: ModelUserContactConnection | null,
  createdAt: string,
  createdChannels?: ModelChannelConnection | null,
  email?: string | null,
  firstName: string,
  groups?: ModelUserGroupConnection | null,
  id: string,
  invites?: ModelInviteConnection | null,
  lastName: string,
  messages?: ModelMessageConnection | null,
  name?: string | null,
  notifications?: ModelNotificationConnection | null,
  phone?: string | null,
  receivedChannels?: ModelChannelConnection | null,
  updatedAt: string,
  userId?: string | null,
  userName?: string | null,
};

export type ModelUserContactConnection = {
  __typename: "ModelUserContactConnection",
  items:  Array<UserContact | null >,
  nextToken?: string | null,
};

export type UserContact = {
  __typename: "UserContact",
  contact?: User | null,
  contactId: string,
  createdAt: string,
  id: string,
  updatedAt: string,
  user?: User | null,
  userId: string,
};

export type ModelChannelConnection = {
  __typename: "ModelChannelConnection",
  items:  Array<Channel | null >,
  nextToken?: string | null,
};

export type ModelUserGroupConnection = {
  __typename: "ModelUserGroupConnection",
  items:  Array<UserGroup | null >,
  nextToken?: string | null,
};

export type UserGroup = {
  __typename: "UserGroup",
  createdAt: string,
  group?: Group | null,
  groupId: string,
  id: string,
  role?: UserGroupRole | null,
  updatedAt: string,
  user?: User | null,
  userId: string,
};

export type Group = {
  __typename: "Group",
  createdAt: string,
  groupId: string,
  id: string,
  name: string,
  updatedAt: string,
  users?: ModelUserGroupConnection | null,
};

export enum UserGroupRole {
  CREATOR = "CREATOR",
  MEMBER = "MEMBER",
  MODERATOR = "MODERATOR",
}


export type ModelInviteConnection = {
  __typename: "ModelInviteConnection",
  items:  Array<Invite | null >,
  nextToken?: string | null,
};

export type Invite = {
  __typename: "Invite",
  createdAt: string,
  id: string,
  receiverId?: string | null,
  senderId?: string | null,
  updatedAt: string,
  user?: User | null,
  userId: string,
};

export type ModelMessageConnection = {
  __typename: "ModelMessageConnection",
  items:  Array<Message | null >,
  nextToken?: string | null,
};

export type Message = {
  __typename: "Message",
  channel?: Channel | null,
  channelId: string,
  content: string,
  createdAt: string,
  id: string,
  sender?: User | null,
  senderId: string,
  updatedAt: string,
};

export type ModelNotificationConnection = {
  __typename: "ModelNotificationConnection",
  items:  Array<Notification | null >,
  nextToken?: string | null,
};

export type Notification = {
  __typename: "Notification",
  createdAt: string,
  id: string,
  isRead?: boolean | null,
  message: string,
  relatedId?: string | null,
  senderName?: string | null,
  senderUserId?: string | null,
  title: string,
  type: string,
  updatedAt: string,
  user?: User | null,
  userId: string,
};

export type FcmToken = {
  __typename: "FcmToken",
  createdAt: string,
  deviceId: string,
  platform: string,
  token: string,
  updatedAt: string,
  userId: string,
};

export type GetUserByUserIdReturnType = {
  __typename: "GetUserByUserIdReturnType",
  avatarUri?: string | null,
  email?: string | null,
  firstName?: string | null,
  id?: string | null,
  lastName?: string | null,
  name?: string | null,
  phone?: string | null,
  userId?: string | null,
  userName?: string | null,
};

export type HasUserCreatedProfileReturnType = {
  __typename: "HasUserCreatedProfileReturnType",
  isProfileComplete: boolean,
  missingFields: Array< string | null >,
};

export type ModelUserFilterInput = {
  and?: Array< ModelUserFilterInput | null > | null,
  avatarUri?: ModelStringInput | null,
  createdAt?: ModelStringInput | null,
  email?: ModelStringInput | null,
  firstName?: ModelStringInput | null,
  id?: ModelIDInput | null,
  lastName?: ModelStringInput | null,
  name?: ModelStringInput | null,
  not?: ModelUserFilterInput | null,
  or?: Array< ModelUserFilterInput | null > | null,
  phone?: ModelStringInput | null,
  updatedAt?: ModelStringInput | null,
  userId?: ModelIDInput | null,
  userName?: ModelStringInput | null,
};

export type ModelStringInput = {
  attributeExists?: boolean | null,
  attributeType?: ModelAttributeTypes | null,
  beginsWith?: string | null,
  between?: Array< string | null > | null,
  contains?: string | null,
  eq?: string | null,
  ge?: string | null,
  gt?: string | null,
  le?: string | null,
  lt?: string | null,
  ne?: string | null,
  notContains?: string | null,
  size?: ModelSizeInput | null,
};

export enum ModelAttributeTypes {
  _null = "_null",
  binary = "binary",
  binarySet = "binarySet",
  bool = "bool",
  list = "list",
  map = "map",
  number = "number",
  numberSet = "numberSet",
  string = "string",
  stringSet = "stringSet",
}


export type ModelSizeInput = {
  between?: Array< number | null > | null,
  eq?: number | null,
  ge?: number | null,
  gt?: number | null,
  le?: number | null,
  lt?: number | null,
  ne?: number | null,
};

export type ModelIDInput = {
  attributeExists?: boolean | null,
  attributeType?: ModelAttributeTypes | null,
  beginsWith?: string | null,
  between?: Array< string | null > | null,
  contains?: string | null,
  eq?: string | null,
  ge?: string | null,
  gt?: string | null,
  le?: string | null,
  lt?: string | null,
  ne?: string | null,
  notContains?: string | null,
  size?: ModelSizeInput | null,
};

export enum ModelSortDirection {
  ASC = "ASC",
  DESC = "DESC",
}


export type ModelStringKeyConditionInput = {
  beginsWith?: string | null,
  between?: Array< string | null > | null,
  eq?: string | null,
  ge?: string | null,
  gt?: string | null,
  le?: string | null,
  lt?: string | null,
};

export type ModelUserConnection = {
  __typename: "ModelUserConnection",
  items:  Array<User | null >,
  nextToken?: string | null,
};

export type ModelChannelFilterInput = {
  and?: Array< ModelChannelFilterInput | null > | null,
  createdAt?: ModelStringInput | null,
  creatorId?: ModelIDInput | null,
  id?: ModelIDInput | null,
  isGroup?: ModelBooleanInput | null,
  isPublic?: ModelBooleanInput | null,
  name?: ModelStringInput | null,
  not?: ModelChannelFilterInput | null,
  or?: Array< ModelChannelFilterInput | null > | null,
  receiverId?: ModelIDInput | null,
  updatedAt?: ModelStringInput | null,
};

export type ModelBooleanInput = {
  attributeExists?: boolean | null,
  attributeType?: ModelAttributeTypes | null,
  eq?: boolean | null,
  ne?: boolean | null,
};

export type ModelIDKeyConditionInput = {
  beginsWith?: string | null,
  between?: Array< string | null > | null,
  eq?: string | null,
  ge?: string | null,
  gt?: string | null,
  le?: string | null,
  lt?: string | null,
};

export type ModelFcmTokenFilterInput = {
  and?: Array< ModelFcmTokenFilterInput | null > | null,
  createdAt?: ModelStringInput | null,
  deviceId?: ModelIDInput | null,
  id?: ModelIDInput | null,
  not?: ModelFcmTokenFilterInput | null,
  or?: Array< ModelFcmTokenFilterInput | null > | null,
  platform?: ModelStringInput | null,
  token?: ModelStringInput | null,
  updatedAt?: ModelStringInput | null,
  userId?: ModelIDInput | null,
};

export type ModelFcmTokenConnection = {
  __typename: "ModelFcmTokenConnection",
  items:  Array<FcmToken | null >,
  nextToken?: string | null,
};

export type ModelGroupFilterInput = {
  and?: Array< ModelGroupFilterInput | null > | null,
  createdAt?: ModelStringInput | null,
  groupId?: ModelIDInput | null,
  id?: ModelIDInput | null,
  name?: ModelStringInput | null,
  not?: ModelGroupFilterInput | null,
  or?: Array< ModelGroupFilterInput | null > | null,
  updatedAt?: ModelStringInput | null,
};

export type ModelGroupConnection = {
  __typename: "ModelGroupConnection",
  items:  Array<Group | null >,
  nextToken?: string | null,
};

export type ModelInviteFilterInput = {
  and?: Array< ModelInviteFilterInput | null > | null,
  createdAt?: ModelStringInput | null,
  id?: ModelIDInput | null,
  not?: ModelInviteFilterInput | null,
  or?: Array< ModelInviteFilterInput | null > | null,
  receiverId?: ModelStringInput | null,
  senderId?: ModelStringInput | null,
  updatedAt?: ModelStringInput | null,
  userId?: ModelIDInput | null,
};

export type ModelMessageFilterInput = {
  and?: Array< ModelMessageFilterInput | null > | null,
  channelId?: ModelIDInput | null,
  content?: ModelStringInput | null,
  createdAt?: ModelStringInput | null,
  id?: ModelIDInput | null,
  not?: ModelMessageFilterInput | null,
  or?: Array< ModelMessageFilterInput | null > | null,
  senderId?: ModelIDInput | null,
  updatedAt?: ModelStringInput | null,
};

export type ModelNotificationFilterInput = {
  and?: Array< ModelNotificationFilterInput | null > | null,
  createdAt?: ModelStringInput | null,
  id?: ModelIDInput | null,
  isRead?: ModelBooleanInput | null,
  message?: ModelStringInput | null,
  not?: ModelNotificationFilterInput | null,
  or?: Array< ModelNotificationFilterInput | null > | null,
  relatedId?: ModelStringInput | null,
  senderName?: ModelStringInput | null,
  senderUserId?: ModelStringInput | null,
  title?: ModelStringInput | null,
  type?: ModelStringInput | null,
  updatedAt?: ModelStringInput | null,
  userId?: ModelIDInput | null,
};

export type ModelUserContactFilterInput = {
  and?: Array< ModelUserContactFilterInput | null > | null,
  contactId?: ModelIDInput | null,
  createdAt?: ModelStringInput | null,
  id?: ModelIDInput | null,
  not?: ModelUserContactFilterInput | null,
  or?: Array< ModelUserContactFilterInput | null > | null,
  updatedAt?: ModelStringInput | null,
  userId?: ModelIDInput | null,
};

export type ModelUserGroupFilterInput = {
  and?: Array< ModelUserGroupFilterInput | null > | null,
  createdAt?: ModelStringInput | null,
  groupId?: ModelIDInput | null,
  id?: ModelIDInput | null,
  not?: ModelUserGroupFilterInput | null,
  or?: Array< ModelUserGroupFilterInput | null > | null,
  role?: ModelUserGroupRoleInput | null,
  updatedAt?: ModelStringInput | null,
  userId?: ModelIDInput | null,
};

export type ModelUserGroupRoleInput = {
  eq?: UserGroupRole | null,
  ne?: UserGroupRole | null,
};

export type ModelChannelConditionInput = {
  and?: Array< ModelChannelConditionInput | null > | null,
  createdAt?: ModelStringInput | null,
  creatorId?: ModelIDInput | null,
  isGroup?: ModelBooleanInput | null,
  isPublic?: ModelBooleanInput | null,
  name?: ModelStringInput | null,
  not?: ModelChannelConditionInput | null,
  or?: Array< ModelChannelConditionInput | null > | null,
  receiverId?: ModelIDInput | null,
  updatedAt?: ModelStringInput | null,
};

export type CreateChannelInput = {
  creatorId: string,
  id?: string | null,
  isGroup?: boolean | null,
  isPublic?: boolean | null,
  name?: string | null,
  receiverId?: string | null,
};

export type ModelFcmTokenConditionInput = {
  and?: Array< ModelFcmTokenConditionInput | null > | null,
  createdAt?: ModelStringInput | null,
  not?: ModelFcmTokenConditionInput | null,
  or?: Array< ModelFcmTokenConditionInput | null > | null,
  platform?: ModelStringInput | null,
  token?: ModelStringInput | null,
  updatedAt?: ModelStringInput | null,
};

export type CreateFcmTokenInput = {
  deviceId: string,
  platform: string,
  token: string,
  userId: string,
};

export type ModelGroupConditionInput = {
  and?: Array< ModelGroupConditionInput | null > | null,
  createdAt?: ModelStringInput | null,
  groupId?: ModelIDInput | null,
  name?: ModelStringInput | null,
  not?: ModelGroupConditionInput | null,
  or?: Array< ModelGroupConditionInput | null > | null,
  updatedAt?: ModelStringInput | null,
};

export type CreateGroupInput = {
  groupId: string,
  id?: string | null,
  name: string,
};

export type ModelInviteConditionInput = {
  and?: Array< ModelInviteConditionInput | null > | null,
  createdAt?: ModelStringInput | null,
  not?: ModelInviteConditionInput | null,
  or?: Array< ModelInviteConditionInput | null > | null,
  receiverId?: ModelStringInput | null,
  senderId?: ModelStringInput | null,
  updatedAt?: ModelStringInput | null,
  userId?: ModelIDInput | null,
};

export type CreateInviteInput = {
  id?: string | null,
  receiverId?: string | null,
  senderId?: string | null,
  userId: string,
};

export type ModelMessageConditionInput = {
  and?: Array< ModelMessageConditionInput | null > | null,
  channelId?: ModelIDInput | null,
  content?: ModelStringInput | null,
  createdAt?: ModelStringInput | null,
  not?: ModelMessageConditionInput | null,
  or?: Array< ModelMessageConditionInput | null > | null,
  senderId?: ModelIDInput | null,
  updatedAt?: ModelStringInput | null,
};

export type CreateMessageInput = {
  channelId: string,
  content: string,
  id?: string | null,
  senderId: string,
};

export type ModelNotificationConditionInput = {
  and?: Array< ModelNotificationConditionInput | null > | null,
  createdAt?: ModelStringInput | null,
  isRead?: ModelBooleanInput | null,
  message?: ModelStringInput | null,
  not?: ModelNotificationConditionInput | null,
  or?: Array< ModelNotificationConditionInput | null > | null,
  relatedId?: ModelStringInput | null,
  senderName?: ModelStringInput | null,
  senderUserId?: ModelStringInput | null,
  title?: ModelStringInput | null,
  type?: ModelStringInput | null,
  updatedAt?: ModelStringInput | null,
  userId?: ModelIDInput | null,
};

export type CreateNotificationInput = {
  id?: string | null,
  isRead?: boolean | null,
  message: string,
  relatedId?: string | null,
  senderName?: string | null,
  senderUserId?: string | null,
  title: string,
  type: string,
  userId: string,
};

export type ModelUserConditionInput = {
  and?: Array< ModelUserConditionInput | null > | null,
  avatarUri?: ModelStringInput | null,
  createdAt?: ModelStringInput | null,
  email?: ModelStringInput | null,
  firstName?: ModelStringInput | null,
  lastName?: ModelStringInput | null,
  name?: ModelStringInput | null,
  not?: ModelUserConditionInput | null,
  or?: Array< ModelUserConditionInput | null > | null,
  phone?: ModelStringInput | null,
  updatedAt?: ModelStringInput | null,
  userId?: ModelIDInput | null,
  userName?: ModelStringInput | null,
};

export type CreateUserInput = {
  avatarUri?: string | null,
  email?: string | null,
  firstName: string,
  id?: string | null,
  lastName: string,
  name?: string | null,
  phone?: string | null,
  userId?: string | null,
  userName?: string | null,
};

export type ModelUserContactConditionInput = {
  and?: Array< ModelUserContactConditionInput | null > | null,
  contactId?: ModelIDInput | null,
  createdAt?: ModelStringInput | null,
  not?: ModelUserContactConditionInput | null,
  or?: Array< ModelUserContactConditionInput | null > | null,
  updatedAt?: ModelStringInput | null,
  userId?: ModelIDInput | null,
};

export type CreateUserContactInput = {
  contactId: string,
  id?: string | null,
  userId: string,
};

export type ModelUserGroupConditionInput = {
  and?: Array< ModelUserGroupConditionInput | null > | null,
  createdAt?: ModelStringInput | null,
  groupId?: ModelIDInput | null,
  not?: ModelUserGroupConditionInput | null,
  or?: Array< ModelUserGroupConditionInput | null > | null,
  role?: ModelUserGroupRoleInput | null,
  updatedAt?: ModelStringInput | null,
  userId?: ModelIDInput | null,
};

export type CreateUserGroupInput = {
  groupId: string,
  id?: string | null,
  role?: UserGroupRole | null,
  userId: string,
};

export type DeleteChannelInput = {
  id: string,
};

export type DeleteFcmTokenInput = {
  deviceId: string,
  userId: string,
};

export type DeleteGroupInput = {
  id: string,
};

export type DeleteInviteInput = {
  id: string,
};

export type DeleteMessageInput = {
  id: string,
};

export type DeleteNotificationInput = {
  id: string,
};

export type DeleteUserInput = {
  id: string,
};

export type DeleteUserContactInput = {
  id: string,
};

export type DeleteUserGroupInput = {
  id: string,
};

export type SendInviteNotificationReturnType = {
  __typename: "SendInviteNotificationReturnType",
  deviceCount?: number | null,
  message: string,
  success: boolean,
};

export type UpdateChannelInput = {
  creatorId?: string | null,
  id: string,
  isGroup?: boolean | null,
  isPublic?: boolean | null,
  name?: string | null,
  receiverId?: string | null,
};

export type UpdateFcmTokenInput = {
  deviceId: string,
  platform?: string | null,
  token?: string | null,
  userId: string,
};

export type UpdateGroupInput = {
  groupId?: string | null,
  id: string,
  name?: string | null,
};

export type UpdateInviteInput = {
  id: string,
  receiverId?: string | null,
  senderId?: string | null,
  userId?: string | null,
};

export type UpdateMessageInput = {
  channelId?: string | null,
  content?: string | null,
  id: string,
  senderId?: string | null,
};

export type UpdateNotificationInput = {
  id: string,
  isRead?: boolean | null,
  message?: string | null,
  relatedId?: string | null,
  senderName?: string | null,
  senderUserId?: string | null,
  title?: string | null,
  type?: string | null,
  userId?: string | null,
};

export type UpdateUserInput = {
  avatarUri?: string | null,
  email?: string | null,
  firstName?: string | null,
  id: string,
  lastName?: string | null,
  name?: string | null,
  phone?: string | null,
  userId?: string | null,
  userName?: string | null,
};

export type UpdateUserContactInput = {
  contactId?: string | null,
  id: string,
  userId?: string | null,
};

export type UpdateUserGroupInput = {
  groupId?: string | null,
  id: string,
  role?: UserGroupRole | null,
  userId?: string | null,
};

export type ModelSubscriptionChannelFilterInput = {
  and?: Array< ModelSubscriptionChannelFilterInput | null > | null,
  createdAt?: ModelSubscriptionStringInput | null,
  creatorId?: ModelSubscriptionIDInput | null,
  id?: ModelSubscriptionIDInput | null,
  isGroup?: ModelSubscriptionBooleanInput | null,
  isPublic?: ModelSubscriptionBooleanInput | null,
  name?: ModelSubscriptionStringInput | null,
  or?: Array< ModelSubscriptionChannelFilterInput | null > | null,
  receiverId?: ModelSubscriptionIDInput | null,
  updatedAt?: ModelSubscriptionStringInput | null,
};

export type ModelSubscriptionStringInput = {
  beginsWith?: string | null,
  between?: Array< string | null > | null,
  contains?: string | null,
  eq?: string | null,
  ge?: string | null,
  gt?: string | null,
  in?: Array< string | null > | null,
  le?: string | null,
  lt?: string | null,
  ne?: string | null,
  notContains?: string | null,
  notIn?: Array< string | null > | null,
};

export type ModelSubscriptionIDInput = {
  beginsWith?: string | null,
  between?: Array< string | null > | null,
  contains?: string | null,
  eq?: string | null,
  ge?: string | null,
  gt?: string | null,
  in?: Array< string | null > | null,
  le?: string | null,
  lt?: string | null,
  ne?: string | null,
  notContains?: string | null,
  notIn?: Array< string | null > | null,
};

export type ModelSubscriptionBooleanInput = {
  eq?: boolean | null,
  ne?: boolean | null,
};

export type ModelSubscriptionFcmTokenFilterInput = {
  and?: Array< ModelSubscriptionFcmTokenFilterInput | null > | null,
  createdAt?: ModelSubscriptionStringInput | null,
  deviceId?: ModelSubscriptionIDInput | null,
  id?: ModelSubscriptionIDInput | null,
  or?: Array< ModelSubscriptionFcmTokenFilterInput | null > | null,
  platform?: ModelSubscriptionStringInput | null,
  token?: ModelSubscriptionStringInput | null,
  updatedAt?: ModelSubscriptionStringInput | null,
  userId?: ModelSubscriptionIDInput | null,
};

export type ModelSubscriptionGroupFilterInput = {
  and?: Array< ModelSubscriptionGroupFilterInput | null > | null,
  createdAt?: ModelSubscriptionStringInput | null,
  groupId?: ModelSubscriptionIDInput | null,
  id?: ModelSubscriptionIDInput | null,
  name?: ModelSubscriptionStringInput | null,
  or?: Array< ModelSubscriptionGroupFilterInput | null > | null,
  updatedAt?: ModelSubscriptionStringInput | null,
};

export type ModelSubscriptionInviteFilterInput = {
  and?: Array< ModelSubscriptionInviteFilterInput | null > | null,
  createdAt?: ModelSubscriptionStringInput | null,
  id?: ModelSubscriptionIDInput | null,
  or?: Array< ModelSubscriptionInviteFilterInput | null > | null,
  receiverId?: ModelSubscriptionStringInput | null,
  senderId?: ModelSubscriptionStringInput | null,
  updatedAt?: ModelSubscriptionStringInput | null,
  userId?: ModelSubscriptionIDInput | null,
};

export type ModelSubscriptionMessageFilterInput = {
  and?: Array< ModelSubscriptionMessageFilterInput | null > | null,
  channelId?: ModelSubscriptionIDInput | null,
  content?: ModelSubscriptionStringInput | null,
  createdAt?: ModelSubscriptionStringInput | null,
  id?: ModelSubscriptionIDInput | null,
  or?: Array< ModelSubscriptionMessageFilterInput | null > | null,
  senderId?: ModelSubscriptionIDInput | null,
  updatedAt?: ModelSubscriptionStringInput | null,
};

export type ModelSubscriptionNotificationFilterInput = {
  and?: Array< ModelSubscriptionNotificationFilterInput | null > | null,
  createdAt?: ModelSubscriptionStringInput | null,
  id?: ModelSubscriptionIDInput | null,
  isRead?: ModelSubscriptionBooleanInput | null,
  message?: ModelSubscriptionStringInput | null,
  or?: Array< ModelSubscriptionNotificationFilterInput | null > | null,
  relatedId?: ModelSubscriptionStringInput | null,
  senderName?: ModelSubscriptionStringInput | null,
  senderUserId?: ModelSubscriptionStringInput | null,
  title?: ModelSubscriptionStringInput | null,
  type?: ModelSubscriptionStringInput | null,
  updatedAt?: ModelSubscriptionStringInput | null,
  userId?: ModelSubscriptionIDInput | null,
};

export type ModelSubscriptionUserFilterInput = {
  and?: Array< ModelSubscriptionUserFilterInput | null > | null,
  avatarUri?: ModelSubscriptionStringInput | null,
  createdAt?: ModelSubscriptionStringInput | null,
  email?: ModelSubscriptionStringInput | null,
  firstName?: ModelSubscriptionStringInput | null,
  id?: ModelSubscriptionIDInput | null,
  lastName?: ModelSubscriptionStringInput | null,
  name?: ModelSubscriptionStringInput | null,
  or?: Array< ModelSubscriptionUserFilterInput | null > | null,
  phone?: ModelSubscriptionStringInput | null,
  updatedAt?: ModelSubscriptionStringInput | null,
  userId?: ModelSubscriptionIDInput | null,
  userName?: ModelSubscriptionStringInput | null,
};

export type ModelSubscriptionUserContactFilterInput = {
  and?: Array< ModelSubscriptionUserContactFilterInput | null > | null,
  contactId?: ModelSubscriptionIDInput | null,
  createdAt?: ModelSubscriptionStringInput | null,
  id?: ModelSubscriptionIDInput | null,
  or?: Array< ModelSubscriptionUserContactFilterInput | null > | null,
  updatedAt?: ModelSubscriptionStringInput | null,
  userId?: ModelSubscriptionIDInput | null,
};

export type ModelSubscriptionUserGroupFilterInput = {
  and?: Array< ModelSubscriptionUserGroupFilterInput | null > | null,
  createdAt?: ModelSubscriptionStringInput | null,
  groupId?: ModelSubscriptionIDInput | null,
  id?: ModelSubscriptionIDInput | null,
  or?: Array< ModelSubscriptionUserGroupFilterInput | null > | null,
  role?: ModelSubscriptionStringInput | null,
  updatedAt?: ModelSubscriptionStringInput | null,
  userId?: ModelSubscriptionIDInput | null,
};

export type FetchAblyJwtQueryVariables = {
  userId?: string | null,
};

export type FetchAblyJwtQuery = {
  fetchAblyJwt?:  {
    __typename: "FetchAblyJwtReturnType",
    clientId: string,
    keyName: string,
    mac: string,
    nonce: string,
    timestamp: number,
  } | null,
};

export type FetchPendingSentInviteStatusQueryVariables = {
  userName?: string | null,
};

export type FetchPendingSentInviteStatusQuery = {
  fetchPendingSentInviteStatus?: string | null,
};

export type FetchUserWithContactInfoQueryVariables = {
  userName?: string | null,
};

export type FetchUserWithContactInfoQuery = {
  fetchUserWithContactInfo?:  {
    __typename: "FetchUserWithContactInfoReturnType",
    id?: string | null,
    userName?: string | null,
  } | null,
};

export type GetChannelQueryVariables = {
  id: string,
};

export type GetChannelQuery = {
  getChannel?:  {
    __typename: "Channel",
    createdAt: string,
    creator?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    creatorId: string,
    id: string,
    isGroup?: boolean | null,
    isPublic?: boolean | null,
    messages?:  {
      __typename: "ModelMessageConnection",
      nextToken?: string | null,
    } | null,
    name?: string | null,
    receiver?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    receiverId?: string | null,
    updatedAt: string,
  } | null,
};

export type GetFcmTokenQueryVariables = {
  deviceId: string,
  userId: string,
};

export type GetFcmTokenQuery = {
  getFcmToken?:  {
    __typename: "FcmToken",
    createdAt: string,
    deviceId: string,
    platform: string,
    token: string,
    updatedAt: string,
    userId: string,
  } | null,
};

export type GetGroupQueryVariables = {
  id: string,
};

export type GetGroupQuery = {
  getGroup?:  {
    __typename: "Group",
    createdAt: string,
    groupId: string,
    id: string,
    name: string,
    updatedAt: string,
    users?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
  } | null,
};

export type GetInviteQueryVariables = {
  id: string,
};

export type GetInviteQuery = {
  getInvite?:  {
    __typename: "Invite",
    createdAt: string,
    id: string,
    receiverId?: string | null,
    senderId?: string | null,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type GetMessageQueryVariables = {
  id: string,
};

export type GetMessageQuery = {
  getMessage?:  {
    __typename: "Message",
    channel?:  {
      __typename: "Channel",
      createdAt: string,
      creatorId: string,
      id: string,
      isGroup?: boolean | null,
      isPublic?: boolean | null,
      name?: string | null,
      receiverId?: string | null,
      updatedAt: string,
    } | null,
    channelId: string,
    content: string,
    createdAt: string,
    id: string,
    sender?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    senderId: string,
    updatedAt: string,
  } | null,
};

export type GetNotificationQueryVariables = {
  id: string,
};

export type GetNotificationQuery = {
  getNotification?:  {
    __typename: "Notification",
    createdAt: string,
    id: string,
    isRead?: boolean | null,
    message: string,
    relatedId?: string | null,
    senderName?: string | null,
    senderUserId?: string | null,
    title: string,
    type: string,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type GetUserQueryVariables = {
  id: string,
};

export type GetUserQuery = {
  getUser?:  {
    __typename: "User",
    asContact?:  {
      __typename: "ModelUserContactConnection",
      nextToken?: string | null,
    } | null,
    avatarUri?: string | null,
    contacts?:  {
      __typename: "ModelUserContactConnection",
      nextToken?: string | null,
    } | null,
    createdAt: string,
    createdChannels?:  {
      __typename: "ModelChannelConnection",
      nextToken?: string | null,
    } | null,
    email?: string | null,
    firstName: string,
    groups?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
    id: string,
    invites?:  {
      __typename: "ModelInviteConnection",
      nextToken?: string | null,
    } | null,
    lastName: string,
    messages?:  {
      __typename: "ModelMessageConnection",
      nextToken?: string | null,
    } | null,
    name?: string | null,
    notifications?:  {
      __typename: "ModelNotificationConnection",
      nextToken?: string | null,
    } | null,
    phone?: string | null,
    receivedChannels?:  {
      __typename: "ModelChannelConnection",
      nextToken?: string | null,
    } | null,
    updatedAt: string,
    userId?: string | null,
    userName?: string | null,
  } | null,
};

export type GetUserByUserIdQueryVariables = {
  userId: string,
};

export type GetUserByUserIdQuery = {
  getUserByUserId?:  {
    __typename: "GetUserByUserIdReturnType",
    avatarUri?: string | null,
    email?: string | null,
    firstName?: string | null,
    id?: string | null,
    lastName?: string | null,
    name?: string | null,
    phone?: string | null,
    userId?: string | null,
    userName?: string | null,
  } | null,
};

export type GetUserContactQueryVariables = {
  id: string,
};

export type GetUserContactQuery = {
  getUserContact?:  {
    __typename: "UserContact",
    contact?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    contactId: string,
    createdAt: string,
    id: string,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type GetUserGroupQueryVariables = {
  id: string,
};

export type GetUserGroupQuery = {
  getUserGroup?:  {
    __typename: "UserGroup",
    createdAt: string,
    group?:  {
      __typename: "Group",
      createdAt: string,
      groupId: string,
      id: string,
      name: string,
      updatedAt: string,
    } | null,
    groupId: string,
    id: string,
    role?: UserGroupRole | null,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type HasUserCreatedProfileQueryVariables = {
  userId: string,
};

export type HasUserCreatedProfileQuery = {
  hasUserCreatedProfile?:  {
    __typename: "HasUserCreatedProfileReturnType",
    isProfileComplete: boolean,
    missingFields: Array< string | null >,
  } | null,
};

export type ListByPhoneQueryVariables = {
  filter?: ModelUserFilterInput | null,
  limit?: number | null,
  nextToken?: string | null,
  phone: string,
  sortDirection?: ModelSortDirection | null,
  userName?: ModelStringKeyConditionInput | null,
};

export type ListByPhoneQuery = {
  listByPhone?:  {
    __typename: "ModelUserConnection",
    items:  Array< {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null >,
    nextToken?: string | null,
  } | null,
};

export type ListChannelsQueryVariables = {
  filter?: ModelChannelFilterInput | null,
  limit?: number | null,
  nextToken?: string | null,
};

export type ListChannelsQuery = {
  listChannels?:  {
    __typename: "ModelChannelConnection",
    items:  Array< {
      __typename: "Channel",
      createdAt: string,
      creatorId: string,
      id: string,
      isGroup?: boolean | null,
      isPublic?: boolean | null,
      name?: string | null,
      receiverId?: string | null,
      updatedAt: string,
    } | null >,
    nextToken?: string | null,
  } | null,
};

export type ListFcmTokensQueryVariables = {
  deviceId?: ModelIDKeyConditionInput | null,
  filter?: ModelFcmTokenFilterInput | null,
  limit?: number | null,
  nextToken?: string | null,
  sortDirection?: ModelSortDirection | null,
  userId?: string | null,
};

export type ListFcmTokensQuery = {
  listFcmTokens?:  {
    __typename: "ModelFcmTokenConnection",
    items:  Array< {
      __typename: "FcmToken",
      createdAt: string,
      deviceId: string,
      platform: string,
      token: string,
      updatedAt: string,
      userId: string,
    } | null >,
    nextToken?: string | null,
  } | null,
};

export type ListGroupsQueryVariables = {
  filter?: ModelGroupFilterInput | null,
  limit?: number | null,
  nextToken?: string | null,
};

export type ListGroupsQuery = {
  listGroups?:  {
    __typename: "ModelGroupConnection",
    items:  Array< {
      __typename: "Group",
      createdAt: string,
      groupId: string,
      id: string,
      name: string,
      updatedAt: string,
    } | null >,
    nextToken?: string | null,
  } | null,
};

export type ListInvitesQueryVariables = {
  filter?: ModelInviteFilterInput | null,
  limit?: number | null,
  nextToken?: string | null,
};

export type ListInvitesQuery = {
  listInvites?:  {
    __typename: "ModelInviteConnection",
    items:  Array< {
      __typename: "Invite",
      createdAt: string,
      id: string,
      receiverId?: string | null,
      senderId?: string | null,
      updatedAt: string,
      userId: string,
    } | null >,
    nextToken?: string | null,
  } | null,
};

export type ListMessagesQueryVariables = {
  filter?: ModelMessageFilterInput | null,
  limit?: number | null,
  nextToken?: string | null,
};

export type ListMessagesQuery = {
  listMessages?:  {
    __typename: "ModelMessageConnection",
    items:  Array< {
      __typename: "Message",
      channelId: string,
      content: string,
      createdAt: string,
      id: string,
      senderId: string,
      updatedAt: string,
    } | null >,
    nextToken?: string | null,
  } | null,
};

export type ListNotificationsQueryVariables = {
  filter?: ModelNotificationFilterInput | null,
  limit?: number | null,
  nextToken?: string | null,
};

export type ListNotificationsQuery = {
  listNotifications?:  {
    __typename: "ModelNotificationConnection",
    items:  Array< {
      __typename: "Notification",
      createdAt: string,
      id: string,
      isRead?: boolean | null,
      message: string,
      relatedId?: string | null,
      senderName?: string | null,
      senderUserId?: string | null,
      title: string,
      type: string,
      updatedAt: string,
      userId: string,
    } | null >,
    nextToken?: string | null,
  } | null,
};

export type ListUserContactsQueryVariables = {
  filter?: ModelUserContactFilterInput | null,
  limit?: number | null,
  nextToken?: string | null,
};

export type ListUserContactsQuery = {
  listUserContacts?:  {
    __typename: "ModelUserContactConnection",
    items:  Array< {
      __typename: "UserContact",
      contactId: string,
      createdAt: string,
      id: string,
      updatedAt: string,
      userId: string,
    } | null >,
    nextToken?: string | null,
  } | null,
};

export type ListUserGroupsQueryVariables = {
  filter?: ModelUserGroupFilterInput | null,
  limit?: number | null,
  nextToken?: string | null,
};

export type ListUserGroupsQuery = {
  listUserGroups?:  {
    __typename: "ModelUserGroupConnection",
    items:  Array< {
      __typename: "UserGroup",
      createdAt: string,
      groupId: string,
      id: string,
      role?: UserGroupRole | null,
      updatedAt: string,
      userId: string,
    } | null >,
    nextToken?: string | null,
  } | null,
};

export type ListUsersQueryVariables = {
  filter?: ModelUserFilterInput | null,
  limit?: number | null,
  nextToken?: string | null,
};

export type ListUsersQuery = {
  listUsers?:  {
    __typename: "ModelUserConnection",
    items:  Array< {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null >,
    nextToken?: string | null,
  } | null,
};

export type CleanupUnknownDataMutationVariables = {
};

export type CleanupUnknownDataMutation = {
  cleanupUnknownData?: string | null,
};

export type CreateChannelMutationVariables = {
  condition?: ModelChannelConditionInput | null,
  input: CreateChannelInput,
};

export type CreateChannelMutation = {
  createChannel?:  {
    __typename: "Channel",
    createdAt: string,
    creator?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    creatorId: string,
    id: string,
    isGroup?: boolean | null,
    isPublic?: boolean | null,
    messages?:  {
      __typename: "ModelMessageConnection",
      nextToken?: string | null,
    } | null,
    name?: string | null,
    receiver?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    receiverId?: string | null,
    updatedAt: string,
  } | null,
};

export type CreateFcmTokenMutationVariables = {
  condition?: ModelFcmTokenConditionInput | null,
  input: CreateFcmTokenInput,
};

export type CreateFcmTokenMutation = {
  createFcmToken?:  {
    __typename: "FcmToken",
    createdAt: string,
    deviceId: string,
    platform: string,
    token: string,
    updatedAt: string,
    userId: string,
  } | null,
};

export type CreateGroupMutationVariables = {
  condition?: ModelGroupConditionInput | null,
  input: CreateGroupInput,
};

export type CreateGroupMutation = {
  createGroup?:  {
    __typename: "Group",
    createdAt: string,
    groupId: string,
    id: string,
    name: string,
    updatedAt: string,
    users?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
  } | null,
};

export type CreateInviteMutationVariables = {
  condition?: ModelInviteConditionInput | null,
  input: CreateInviteInput,
};

export type CreateInviteMutation = {
  createInvite?:  {
    __typename: "Invite",
    createdAt: string,
    id: string,
    receiverId?: string | null,
    senderId?: string | null,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type CreateMessageMutationVariables = {
  condition?: ModelMessageConditionInput | null,
  input: CreateMessageInput,
};

export type CreateMessageMutation = {
  createMessage?:  {
    __typename: "Message",
    channel?:  {
      __typename: "Channel",
      createdAt: string,
      creatorId: string,
      id: string,
      isGroup?: boolean | null,
      isPublic?: boolean | null,
      name?: string | null,
      receiverId?: string | null,
      updatedAt: string,
    } | null,
    channelId: string,
    content: string,
    createdAt: string,
    id: string,
    sender?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    senderId: string,
    updatedAt: string,
  } | null,
};

export type CreateNotificationMutationVariables = {
  condition?: ModelNotificationConditionInput | null,
  input: CreateNotificationInput,
};

export type CreateNotificationMutation = {
  createNotification?:  {
    __typename: "Notification",
    createdAt: string,
    id: string,
    isRead?: boolean | null,
    message: string,
    relatedId?: string | null,
    senderName?: string | null,
    senderUserId?: string | null,
    title: string,
    type: string,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type CreateUserMutationVariables = {
  condition?: ModelUserConditionInput | null,
  input: CreateUserInput,
};

export type CreateUserMutation = {
  createUser?:  {
    __typename: "User",
    asContact?:  {
      __typename: "ModelUserContactConnection",
      nextToken?: string | null,
    } | null,
    avatarUri?: string | null,
    contacts?:  {
      __typename: "ModelUserContactConnection",
      nextToken?: string | null,
    } | null,
    createdAt: string,
    createdChannels?:  {
      __typename: "ModelChannelConnection",
      nextToken?: string | null,
    } | null,
    email?: string | null,
    firstName: string,
    groups?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
    id: string,
    invites?:  {
      __typename: "ModelInviteConnection",
      nextToken?: string | null,
    } | null,
    lastName: string,
    messages?:  {
      __typename: "ModelMessageConnection",
      nextToken?: string | null,
    } | null,
    name?: string | null,
    notifications?:  {
      __typename: "ModelNotificationConnection",
      nextToken?: string | null,
    } | null,
    phone?: string | null,
    receivedChannels?:  {
      __typename: "ModelChannelConnection",
      nextToken?: string | null,
    } | null,
    updatedAt: string,
    userId?: string | null,
    userName?: string | null,
  } | null,
};

export type CreateUserContactMutationVariables = {
  condition?: ModelUserContactConditionInput | null,
  input: CreateUserContactInput,
};

export type CreateUserContactMutation = {
  createUserContact?:  {
    __typename: "UserContact",
    contact?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    contactId: string,
    createdAt: string,
    id: string,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type CreateUserGroupMutationVariables = {
  condition?: ModelUserGroupConditionInput | null,
  input: CreateUserGroupInput,
};

export type CreateUserGroupMutation = {
  createUserGroup?:  {
    __typename: "UserGroup",
    createdAt: string,
    group?:  {
      __typename: "Group",
      createdAt: string,
      groupId: string,
      id: string,
      name: string,
      updatedAt: string,
    } | null,
    groupId: string,
    id: string,
    role?: UserGroupRole | null,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type DeleteChannelMutationVariables = {
  condition?: ModelChannelConditionInput | null,
  input: DeleteChannelInput,
};

export type DeleteChannelMutation = {
  deleteChannel?:  {
    __typename: "Channel",
    createdAt: string,
    creator?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    creatorId: string,
    id: string,
    isGroup?: boolean | null,
    isPublic?: boolean | null,
    messages?:  {
      __typename: "ModelMessageConnection",
      nextToken?: string | null,
    } | null,
    name?: string | null,
    receiver?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    receiverId?: string | null,
    updatedAt: string,
  } | null,
};

export type DeleteFcmTokenMutationVariables = {
  condition?: ModelFcmTokenConditionInput | null,
  input: DeleteFcmTokenInput,
};

export type DeleteFcmTokenMutation = {
  deleteFcmToken?:  {
    __typename: "FcmToken",
    createdAt: string,
    deviceId: string,
    platform: string,
    token: string,
    updatedAt: string,
    userId: string,
  } | null,
};

export type DeleteGroupMutationVariables = {
  condition?: ModelGroupConditionInput | null,
  input: DeleteGroupInput,
};

export type DeleteGroupMutation = {
  deleteGroup?:  {
    __typename: "Group",
    createdAt: string,
    groupId: string,
    id: string,
    name: string,
    updatedAt: string,
    users?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
  } | null,
};

export type DeleteInviteMutationVariables = {
  condition?: ModelInviteConditionInput | null,
  input: DeleteInviteInput,
};

export type DeleteInviteMutation = {
  deleteInvite?:  {
    __typename: "Invite",
    createdAt: string,
    id: string,
    receiverId?: string | null,
    senderId?: string | null,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type DeleteMessageMutationVariables = {
  condition?: ModelMessageConditionInput | null,
  input: DeleteMessageInput,
};

export type DeleteMessageMutation = {
  deleteMessage?:  {
    __typename: "Message",
    channel?:  {
      __typename: "Channel",
      createdAt: string,
      creatorId: string,
      id: string,
      isGroup?: boolean | null,
      isPublic?: boolean | null,
      name?: string | null,
      receiverId?: string | null,
      updatedAt: string,
    } | null,
    channelId: string,
    content: string,
    createdAt: string,
    id: string,
    sender?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    senderId: string,
    updatedAt: string,
  } | null,
};

export type DeleteNotificationMutationVariables = {
  condition?: ModelNotificationConditionInput | null,
  input: DeleteNotificationInput,
};

export type DeleteNotificationMutation = {
  deleteNotification?:  {
    __typename: "Notification",
    createdAt: string,
    id: string,
    isRead?: boolean | null,
    message: string,
    relatedId?: string | null,
    senderName?: string | null,
    senderUserId?: string | null,
    title: string,
    type: string,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type DeleteUserMutationVariables = {
  condition?: ModelUserConditionInput | null,
  input: DeleteUserInput,
};

export type DeleteUserMutation = {
  deleteUser?:  {
    __typename: "User",
    asContact?:  {
      __typename: "ModelUserContactConnection",
      nextToken?: string | null,
    } | null,
    avatarUri?: string | null,
    contacts?:  {
      __typename: "ModelUserContactConnection",
      nextToken?: string | null,
    } | null,
    createdAt: string,
    createdChannels?:  {
      __typename: "ModelChannelConnection",
      nextToken?: string | null,
    } | null,
    email?: string | null,
    firstName: string,
    groups?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
    id: string,
    invites?:  {
      __typename: "ModelInviteConnection",
      nextToken?: string | null,
    } | null,
    lastName: string,
    messages?:  {
      __typename: "ModelMessageConnection",
      nextToken?: string | null,
    } | null,
    name?: string | null,
    notifications?:  {
      __typename: "ModelNotificationConnection",
      nextToken?: string | null,
    } | null,
    phone?: string | null,
    receivedChannels?:  {
      __typename: "ModelChannelConnection",
      nextToken?: string | null,
    } | null,
    updatedAt: string,
    userId?: string | null,
    userName?: string | null,
  } | null,
};

export type DeleteUserContactMutationVariables = {
  condition?: ModelUserContactConditionInput | null,
  input: DeleteUserContactInput,
};

export type DeleteUserContactMutation = {
  deleteUserContact?:  {
    __typename: "UserContact",
    contact?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    contactId: string,
    createdAt: string,
    id: string,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type DeleteUserGroupMutationVariables = {
  condition?: ModelUserGroupConditionInput | null,
  input: DeleteUserGroupInput,
};

export type DeleteUserGroupMutation = {
  deleteUserGroup?:  {
    __typename: "UserGroup",
    createdAt: string,
    group?:  {
      __typename: "Group",
      createdAt: string,
      groupId: string,
      id: string,
      name: string,
      updatedAt: string,
    } | null,
    groupId: string,
    id: string,
    role?: UserGroupRole | null,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type SendInviteNotificationMutationVariables = {
  inviteId: string,
  recipientUserId: string,
  senderName: string,
};

export type SendInviteNotificationMutation = {
  sendInviteNotification?:  {
    __typename: "SendInviteNotificationReturnType",
    deviceCount?: number | null,
    message: string,
    success: boolean,
  } | null,
};

export type UpdateChannelMutationVariables = {
  condition?: ModelChannelConditionInput | null,
  input: UpdateChannelInput,
};

export type UpdateChannelMutation = {
  updateChannel?:  {
    __typename: "Channel",
    createdAt: string,
    creator?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    creatorId: string,
    id: string,
    isGroup?: boolean | null,
    isPublic?: boolean | null,
    messages?:  {
      __typename: "ModelMessageConnection",
      nextToken?: string | null,
    } | null,
    name?: string | null,
    receiver?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    receiverId?: string | null,
    updatedAt: string,
  } | null,
};

export type UpdateFcmTokenMutationVariables = {
  condition?: ModelFcmTokenConditionInput | null,
  input: UpdateFcmTokenInput,
};

export type UpdateFcmTokenMutation = {
  updateFcmToken?:  {
    __typename: "FcmToken",
    createdAt: string,
    deviceId: string,
    platform: string,
    token: string,
    updatedAt: string,
    userId: string,
  } | null,
};

export type UpdateGroupMutationVariables = {
  condition?: ModelGroupConditionInput | null,
  input: UpdateGroupInput,
};

export type UpdateGroupMutation = {
  updateGroup?:  {
    __typename: "Group",
    createdAt: string,
    groupId: string,
    id: string,
    name: string,
    updatedAt: string,
    users?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
  } | null,
};

export type UpdateInviteMutationVariables = {
  condition?: ModelInviteConditionInput | null,
  input: UpdateInviteInput,
};

export type UpdateInviteMutation = {
  updateInvite?:  {
    __typename: "Invite",
    createdAt: string,
    id: string,
    receiverId?: string | null,
    senderId?: string | null,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type UpdateMessageMutationVariables = {
  condition?: ModelMessageConditionInput | null,
  input: UpdateMessageInput,
};

export type UpdateMessageMutation = {
  updateMessage?:  {
    __typename: "Message",
    channel?:  {
      __typename: "Channel",
      createdAt: string,
      creatorId: string,
      id: string,
      isGroup?: boolean | null,
      isPublic?: boolean | null,
      name?: string | null,
      receiverId?: string | null,
      updatedAt: string,
    } | null,
    channelId: string,
    content: string,
    createdAt: string,
    id: string,
    sender?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    senderId: string,
    updatedAt: string,
  } | null,
};

export type UpdateNotificationMutationVariables = {
  condition?: ModelNotificationConditionInput | null,
  input: UpdateNotificationInput,
};

export type UpdateNotificationMutation = {
  updateNotification?:  {
    __typename: "Notification",
    createdAt: string,
    id: string,
    isRead?: boolean | null,
    message: string,
    relatedId?: string | null,
    senderName?: string | null,
    senderUserId?: string | null,
    title: string,
    type: string,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type UpdatePresenceMutationVariables = {
  isOnline: boolean,
  platform?: string | null,
  userId: string,
};

export type UpdatePresenceMutation = {
  updatePresence?: boolean | null,
};

export type UpdateUserMutationVariables = {
  condition?: ModelUserConditionInput | null,
  input: UpdateUserInput,
};

export type UpdateUserMutation = {
  updateUser?:  {
    __typename: "User",
    asContact?:  {
      __typename: "ModelUserContactConnection",
      nextToken?: string | null,
    } | null,
    avatarUri?: string | null,
    contacts?:  {
      __typename: "ModelUserContactConnection",
      nextToken?: string | null,
    } | null,
    createdAt: string,
    createdChannels?:  {
      __typename: "ModelChannelConnection",
      nextToken?: string | null,
    } | null,
    email?: string | null,
    firstName: string,
    groups?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
    id: string,
    invites?:  {
      __typename: "ModelInviteConnection",
      nextToken?: string | null,
    } | null,
    lastName: string,
    messages?:  {
      __typename: "ModelMessageConnection",
      nextToken?: string | null,
    } | null,
    name?: string | null,
    notifications?:  {
      __typename: "ModelNotificationConnection",
      nextToken?: string | null,
    } | null,
    phone?: string | null,
    receivedChannels?:  {
      __typename: "ModelChannelConnection",
      nextToken?: string | null,
    } | null,
    updatedAt: string,
    userId?: string | null,
    userName?: string | null,
  } | null,
};

export type UpdateUserContactMutationVariables = {
  condition?: ModelUserContactConditionInput | null,
  input: UpdateUserContactInput,
};

export type UpdateUserContactMutation = {
  updateUserContact?:  {
    __typename: "UserContact",
    contact?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    contactId: string,
    createdAt: string,
    id: string,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type UpdateUserGroupMutationVariables = {
  condition?: ModelUserGroupConditionInput | null,
  input: UpdateUserGroupInput,
};

export type UpdateUserGroupMutation = {
  updateUserGroup?:  {
    __typename: "UserGroup",
    createdAt: string,
    group?:  {
      __typename: "Group",
      createdAt: string,
      groupId: string,
      id: string,
      name: string,
      updatedAt: string,
    } | null,
    groupId: string,
    id: string,
    role?: UserGroupRole | null,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type OnCreateChannelSubscriptionVariables = {
  filter?: ModelSubscriptionChannelFilterInput | null,
};

export type OnCreateChannelSubscription = {
  onCreateChannel?:  {
    __typename: "Channel",
    createdAt: string,
    creator?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    creatorId: string,
    id: string,
    isGroup?: boolean | null,
    isPublic?: boolean | null,
    messages?:  {
      __typename: "ModelMessageConnection",
      nextToken?: string | null,
    } | null,
    name?: string | null,
    receiver?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    receiverId?: string | null,
    updatedAt: string,
  } | null,
};

export type OnCreateFcmTokenSubscriptionVariables = {
  filter?: ModelSubscriptionFcmTokenFilterInput | null,
};

export type OnCreateFcmTokenSubscription = {
  onCreateFcmToken?:  {
    __typename: "FcmToken",
    createdAt: string,
    deviceId: string,
    platform: string,
    token: string,
    updatedAt: string,
    userId: string,
  } | null,
};

export type OnCreateGroupSubscriptionVariables = {
  filter?: ModelSubscriptionGroupFilterInput | null,
};

export type OnCreateGroupSubscription = {
  onCreateGroup?:  {
    __typename: "Group",
    createdAt: string,
    groupId: string,
    id: string,
    name: string,
    updatedAt: string,
    users?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
  } | null,
};

export type OnCreateInviteSubscriptionVariables = {
  filter?: ModelSubscriptionInviteFilterInput | null,
};

export type OnCreateInviteSubscription = {
  onCreateInvite?:  {
    __typename: "Invite",
    createdAt: string,
    id: string,
    receiverId?: string | null,
    senderId?: string | null,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type OnCreateMessageSubscriptionVariables = {
  filter?: ModelSubscriptionMessageFilterInput | null,
};

export type OnCreateMessageSubscription = {
  onCreateMessage?:  {
    __typename: "Message",
    channel?:  {
      __typename: "Channel",
      createdAt: string,
      creatorId: string,
      id: string,
      isGroup?: boolean | null,
      isPublic?: boolean | null,
      name?: string | null,
      receiverId?: string | null,
      updatedAt: string,
    } | null,
    channelId: string,
    content: string,
    createdAt: string,
    id: string,
    sender?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    senderId: string,
    updatedAt: string,
  } | null,
};

export type OnCreateNotificationSubscriptionVariables = {
  filter?: ModelSubscriptionNotificationFilterInput | null,
};

export type OnCreateNotificationSubscription = {
  onCreateNotification?:  {
    __typename: "Notification",
    createdAt: string,
    id: string,
    isRead?: boolean | null,
    message: string,
    relatedId?: string | null,
    senderName?: string | null,
    senderUserId?: string | null,
    title: string,
    type: string,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type OnCreateUserSubscriptionVariables = {
  filter?: ModelSubscriptionUserFilterInput | null,
};

export type OnCreateUserSubscription = {
  onCreateUser?:  {
    __typename: "User",
    asContact?:  {
      __typename: "ModelUserContactConnection",
      nextToken?: string | null,
    } | null,
    avatarUri?: string | null,
    contacts?:  {
      __typename: "ModelUserContactConnection",
      nextToken?: string | null,
    } | null,
    createdAt: string,
    createdChannels?:  {
      __typename: "ModelChannelConnection",
      nextToken?: string | null,
    } | null,
    email?: string | null,
    firstName: string,
    groups?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
    id: string,
    invites?:  {
      __typename: "ModelInviteConnection",
      nextToken?: string | null,
    } | null,
    lastName: string,
    messages?:  {
      __typename: "ModelMessageConnection",
      nextToken?: string | null,
    } | null,
    name?: string | null,
    notifications?:  {
      __typename: "ModelNotificationConnection",
      nextToken?: string | null,
    } | null,
    phone?: string | null,
    receivedChannels?:  {
      __typename: "ModelChannelConnection",
      nextToken?: string | null,
    } | null,
    updatedAt: string,
    userId?: string | null,
    userName?: string | null,
  } | null,
};

export type OnCreateUserContactSubscriptionVariables = {
  filter?: ModelSubscriptionUserContactFilterInput | null,
};

export type OnCreateUserContactSubscription = {
  onCreateUserContact?:  {
    __typename: "UserContact",
    contact?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    contactId: string,
    createdAt: string,
    id: string,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type OnCreateUserGroupSubscriptionVariables = {
  filter?: ModelSubscriptionUserGroupFilterInput | null,
};

export type OnCreateUserGroupSubscription = {
  onCreateUserGroup?:  {
    __typename: "UserGroup",
    createdAt: string,
    group?:  {
      __typename: "Group",
      createdAt: string,
      groupId: string,
      id: string,
      name: string,
      updatedAt: string,
    } | null,
    groupId: string,
    id: string,
    role?: UserGroupRole | null,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type OnDeleteChannelSubscriptionVariables = {
  filter?: ModelSubscriptionChannelFilterInput | null,
};

export type OnDeleteChannelSubscription = {
  onDeleteChannel?:  {
    __typename: "Channel",
    createdAt: string,
    creator?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    creatorId: string,
    id: string,
    isGroup?: boolean | null,
    isPublic?: boolean | null,
    messages?:  {
      __typename: "ModelMessageConnection",
      nextToken?: string | null,
    } | null,
    name?: string | null,
    receiver?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    receiverId?: string | null,
    updatedAt: string,
  } | null,
};

export type OnDeleteFcmTokenSubscriptionVariables = {
  filter?: ModelSubscriptionFcmTokenFilterInput | null,
};

export type OnDeleteFcmTokenSubscription = {
  onDeleteFcmToken?:  {
    __typename: "FcmToken",
    createdAt: string,
    deviceId: string,
    platform: string,
    token: string,
    updatedAt: string,
    userId: string,
  } | null,
};

export type OnDeleteGroupSubscriptionVariables = {
  filter?: ModelSubscriptionGroupFilterInput | null,
};

export type OnDeleteGroupSubscription = {
  onDeleteGroup?:  {
    __typename: "Group",
    createdAt: string,
    groupId: string,
    id: string,
    name: string,
    updatedAt: string,
    users?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
  } | null,
};

export type OnDeleteInviteSubscriptionVariables = {
  filter?: ModelSubscriptionInviteFilterInput | null,
};

export type OnDeleteInviteSubscription = {
  onDeleteInvite?:  {
    __typename: "Invite",
    createdAt: string,
    id: string,
    receiverId?: string | null,
    senderId?: string | null,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type OnDeleteMessageSubscriptionVariables = {
  filter?: ModelSubscriptionMessageFilterInput | null,
};

export type OnDeleteMessageSubscription = {
  onDeleteMessage?:  {
    __typename: "Message",
    channel?:  {
      __typename: "Channel",
      createdAt: string,
      creatorId: string,
      id: string,
      isGroup?: boolean | null,
      isPublic?: boolean | null,
      name?: string | null,
      receiverId?: string | null,
      updatedAt: string,
    } | null,
    channelId: string,
    content: string,
    createdAt: string,
    id: string,
    sender?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    senderId: string,
    updatedAt: string,
  } | null,
};

export type OnDeleteNotificationSubscriptionVariables = {
  filter?: ModelSubscriptionNotificationFilterInput | null,
};

export type OnDeleteNotificationSubscription = {
  onDeleteNotification?:  {
    __typename: "Notification",
    createdAt: string,
    id: string,
    isRead?: boolean | null,
    message: string,
    relatedId?: string | null,
    senderName?: string | null,
    senderUserId?: string | null,
    title: string,
    type: string,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type OnDeleteUserSubscriptionVariables = {
  filter?: ModelSubscriptionUserFilterInput | null,
};

export type OnDeleteUserSubscription = {
  onDeleteUser?:  {
    __typename: "User",
    asContact?:  {
      __typename: "ModelUserContactConnection",
      nextToken?: string | null,
    } | null,
    avatarUri?: string | null,
    contacts?:  {
      __typename: "ModelUserContactConnection",
      nextToken?: string | null,
    } | null,
    createdAt: string,
    createdChannels?:  {
      __typename: "ModelChannelConnection",
      nextToken?: string | null,
    } | null,
    email?: string | null,
    firstName: string,
    groups?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
    id: string,
    invites?:  {
      __typename: "ModelInviteConnection",
      nextToken?: string | null,
    } | null,
    lastName: string,
    messages?:  {
      __typename: "ModelMessageConnection",
      nextToken?: string | null,
    } | null,
    name?: string | null,
    notifications?:  {
      __typename: "ModelNotificationConnection",
      nextToken?: string | null,
    } | null,
    phone?: string | null,
    receivedChannels?:  {
      __typename: "ModelChannelConnection",
      nextToken?: string | null,
    } | null,
    updatedAt: string,
    userId?: string | null,
    userName?: string | null,
  } | null,
};

export type OnDeleteUserContactSubscriptionVariables = {
  filter?: ModelSubscriptionUserContactFilterInput | null,
};

export type OnDeleteUserContactSubscription = {
  onDeleteUserContact?:  {
    __typename: "UserContact",
    contact?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    contactId: string,
    createdAt: string,
    id: string,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type OnDeleteUserGroupSubscriptionVariables = {
  filter?: ModelSubscriptionUserGroupFilterInput | null,
};

export type OnDeleteUserGroupSubscription = {
  onDeleteUserGroup?:  {
    __typename: "UserGroup",
    createdAt: string,
    group?:  {
      __typename: "Group",
      createdAt: string,
      groupId: string,
      id: string,
      name: string,
      updatedAt: string,
    } | null,
    groupId: string,
    id: string,
    role?: UserGroupRole | null,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type OnUpdateChannelSubscriptionVariables = {
  filter?: ModelSubscriptionChannelFilterInput | null,
};

export type OnUpdateChannelSubscription = {
  onUpdateChannel?:  {
    __typename: "Channel",
    createdAt: string,
    creator?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    creatorId: string,
    id: string,
    isGroup?: boolean | null,
    isPublic?: boolean | null,
    messages?:  {
      __typename: "ModelMessageConnection",
      nextToken?: string | null,
    } | null,
    name?: string | null,
    receiver?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    receiverId?: string | null,
    updatedAt: string,
  } | null,
};

export type OnUpdateFcmTokenSubscriptionVariables = {
  filter?: ModelSubscriptionFcmTokenFilterInput | null,
};

export type OnUpdateFcmTokenSubscription = {
  onUpdateFcmToken?:  {
    __typename: "FcmToken",
    createdAt: string,
    deviceId: string,
    platform: string,
    token: string,
    updatedAt: string,
    userId: string,
  } | null,
};

export type OnUpdateGroupSubscriptionVariables = {
  filter?: ModelSubscriptionGroupFilterInput | null,
};

export type OnUpdateGroupSubscription = {
  onUpdateGroup?:  {
    __typename: "Group",
    createdAt: string,
    groupId: string,
    id: string,
    name: string,
    updatedAt: string,
    users?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
  } | null,
};

export type OnUpdateInviteSubscriptionVariables = {
  filter?: ModelSubscriptionInviteFilterInput | null,
};

export type OnUpdateInviteSubscription = {
  onUpdateInvite?:  {
    __typename: "Invite",
    createdAt: string,
    id: string,
    receiverId?: string | null,
    senderId?: string | null,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type OnUpdateMessageSubscriptionVariables = {
  filter?: ModelSubscriptionMessageFilterInput | null,
};

export type OnUpdateMessageSubscription = {
  onUpdateMessage?:  {
    __typename: "Message",
    channel?:  {
      __typename: "Channel",
      createdAt: string,
      creatorId: string,
      id: string,
      isGroup?: boolean | null,
      isPublic?: boolean | null,
      name?: string | null,
      receiverId?: string | null,
      updatedAt: string,
    } | null,
    channelId: string,
    content: string,
    createdAt: string,
    id: string,
    sender?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    senderId: string,
    updatedAt: string,
  } | null,
};

export type OnUpdateNotificationSubscriptionVariables = {
  filter?: ModelSubscriptionNotificationFilterInput | null,
};

export type OnUpdateNotificationSubscription = {
  onUpdateNotification?:  {
    __typename: "Notification",
    createdAt: string,
    id: string,
    isRead?: boolean | null,
    message: string,
    relatedId?: string | null,
    senderName?: string | null,
    senderUserId?: string | null,
    title: string,
    type: string,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type OnUpdateUserSubscriptionVariables = {
  filter?: ModelSubscriptionUserFilterInput | null,
};

export type OnUpdateUserSubscription = {
  onUpdateUser?:  {
    __typename: "User",
    asContact?:  {
      __typename: "ModelUserContactConnection",
      nextToken?: string | null,
    } | null,
    avatarUri?: string | null,
    contacts?:  {
      __typename: "ModelUserContactConnection",
      nextToken?: string | null,
    } | null,
    createdAt: string,
    createdChannels?:  {
      __typename: "ModelChannelConnection",
      nextToken?: string | null,
    } | null,
    email?: string | null,
    firstName: string,
    groups?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
    id: string,
    invites?:  {
      __typename: "ModelInviteConnection",
      nextToken?: string | null,
    } | null,
    lastName: string,
    messages?:  {
      __typename: "ModelMessageConnection",
      nextToken?: string | null,
    } | null,
    name?: string | null,
    notifications?:  {
      __typename: "ModelNotificationConnection",
      nextToken?: string | null,
    } | null,
    phone?: string | null,
    receivedChannels?:  {
      __typename: "ModelChannelConnection",
      nextToken?: string | null,
    } | null,
    updatedAt: string,
    userId?: string | null,
    userName?: string | null,
  } | null,
};

export type OnUpdateUserContactSubscriptionVariables = {
  filter?: ModelSubscriptionUserContactFilterInput | null,
};

export type OnUpdateUserContactSubscription = {
  onUpdateUserContact?:  {
    __typename: "UserContact",
    contact?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    contactId: string,
    createdAt: string,
    id: string,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};

export type OnUpdateUserGroupSubscriptionVariables = {
  filter?: ModelSubscriptionUserGroupFilterInput | null,
};

export type OnUpdateUserGroupSubscription = {
  onUpdateUserGroup?:  {
    __typename: "UserGroup",
    createdAt: string,
    group?:  {
      __typename: "Group",
      createdAt: string,
      groupId: string,
      id: string,
      name: string,
      updatedAt: string,
    } | null,
    groupId: string,
    id: string,
    role?: UserGroupRole | null,
    updatedAt: string,
    user?:  {
      __typename: "User",
      avatarUri?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
    } | null,
    userId: string,
  } | null,
};
