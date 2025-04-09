/* tslint:disable */
/* eslint-disable */
//  This file was automatically generated and should not be edited.

export type AppData = {
  __typename: "AppData",
  avatarUri?: string | null,
  contacts?: string | null,
  createdAt: string,
  email?: string | null,
  firstName: string,
  groups?: string | null,
  id: string,
  lastName: string,
  name?: string | null,
  phone?: string | null,
  type?: string | null,
  updatedAt: string,
  userId?: string | null,
  userName?: string | null,
  vehicles?: string | null,
};

export type Contact = {
  __typename: "Contact",
  createdAt: string,
  email?: string | null,
  id: string,
  name?: string | null,
  phone?: string | null,
  updatedAt: string,
  user?: User | null,
};

export type User = {
  __typename: "User",
  avatarUri?: string | null,
  contacts?: ModelContactConnection | null,
  createdAt: string,
  email?: string | null,
  firstName: string,
  groups?: ModelUserGroupConnection | null,
  id: string,
  lastName: string,
  name?: string | null,
  phone?: string | null,
  updatedAt: string,
};

export type ModelContactConnection = {
  __typename: "ModelContactConnection",
  items:  Array<Contact | null >,
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
  groupId?: string | null,
  id: string,
  updatedAt: string,
  user?: User | null,
  userId?: string | null,
};

export type Group = {
  __typename: "Group",
  createdAt: string,
  id: string,
  isAdmin?: string | null,
  members?: string | null,
  name: string,
  updatedAt: string,
  users?: ModelUserGroupConnection | null,
};

export type ModelAppDataFilterInput = {
  and?: Array< ModelAppDataFilterInput | null > | null,
  avatarUri?: ModelStringInput | null,
  contacts?: ModelStringInput | null,
  createdAt?: ModelStringInput | null,
  email?: ModelStringInput | null,
  firstName?: ModelStringInput | null,
  groups?: ModelStringInput | null,
  id?: ModelIDInput | null,
  lastName?: ModelStringInput | null,
  name?: ModelStringInput | null,
  not?: ModelAppDataFilterInput | null,
  or?: Array< ModelAppDataFilterInput | null > | null,
  phone?: ModelStringInput | null,
  type?: ModelStringInput | null,
  updatedAt?: ModelStringInput | null,
  userId?: ModelStringInput | null,
  userName?: ModelStringInput | null,
  vehicles?: ModelStringInput | null,
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


export type ModelAppDataConnection = {
  __typename: "ModelAppDataConnection",
  items:  Array<AppData | null >,
  nextToken?: string | null,
};

export type ModelContactFilterInput = {
  and?: Array< ModelContactFilterInput | null > | null,
  createdAt?: ModelStringInput | null,
  email?: ModelStringInput | null,
  id?: ModelIDInput | null,
  name?: ModelStringInput | null,
  not?: ModelContactFilterInput | null,
  or?: Array< ModelContactFilterInput | null > | null,
  phone?: ModelStringInput | null,
  updatedAt?: ModelStringInput | null,
};

export type ModelGroupFilterInput = {
  and?: Array< ModelGroupFilterInput | null > | null,
  createdAt?: ModelStringInput | null,
  id?: ModelIDInput | null,
  isAdmin?: ModelStringInput | null,
  members?: ModelStringInput | null,
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

export type ModelUserGroupFilterInput = {
  and?: Array< ModelUserGroupFilterInput | null > | null,
  createdAt?: ModelStringInput | null,
  groupId?: ModelIDInput | null,
  id?: ModelIDInput | null,
  not?: ModelUserGroupFilterInput | null,
  or?: Array< ModelUserGroupFilterInput | null > | null,
  updatedAt?: ModelStringInput | null,
  userId?: ModelIDInput | null,
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
};

export type ModelUserConnection = {
  __typename: "ModelUserConnection",
  items:  Array<User | null >,
  nextToken?: string | null,
};

export type ModelAppDataConditionInput = {
  and?: Array< ModelAppDataConditionInput | null > | null,
  avatarUri?: ModelStringInput | null,
  contacts?: ModelStringInput | null,
  createdAt?: ModelStringInput | null,
  email?: ModelStringInput | null,
  firstName?: ModelStringInput | null,
  groups?: ModelStringInput | null,
  lastName?: ModelStringInput | null,
  name?: ModelStringInput | null,
  not?: ModelAppDataConditionInput | null,
  or?: Array< ModelAppDataConditionInput | null > | null,
  phone?: ModelStringInput | null,
  type?: ModelStringInput | null,
  updatedAt?: ModelStringInput | null,
  userId?: ModelStringInput | null,
  userName?: ModelStringInput | null,
  vehicles?: ModelStringInput | null,
};

export type CreateAppDataInput = {
  avatarUri?: string | null,
  contacts?: string | null,
  email?: string | null,
  firstName: string,
  groups?: string | null,
  id?: string | null,
  lastName: string,
  name?: string | null,
  phone?: string | null,
  type?: string | null,
  userId?: string | null,
  userName?: string | null,
  vehicles?: string | null,
};

export type ModelContactConditionInput = {
  and?: Array< ModelContactConditionInput | null > | null,
  createdAt?: ModelStringInput | null,
  email?: ModelStringInput | null,
  name?: ModelStringInput | null,
  not?: ModelContactConditionInput | null,
  or?: Array< ModelContactConditionInput | null > | null,
  phone?: ModelStringInput | null,
  updatedAt?: ModelStringInput | null,
};

export type CreateContactInput = {
  email?: string | null,
  id?: string | null,
  name?: string | null,
  phone?: string | null,
};

export type ModelGroupConditionInput = {
  and?: Array< ModelGroupConditionInput | null > | null,
  createdAt?: ModelStringInput | null,
  isAdmin?: ModelStringInput | null,
  members?: ModelStringInput | null,
  name?: ModelStringInput | null,
  not?: ModelGroupConditionInput | null,
  or?: Array< ModelGroupConditionInput | null > | null,
  updatedAt?: ModelStringInput | null,
};

export type CreateGroupInput = {
  id?: string | null,
  isAdmin?: string | null,
  members?: string | null,
  name: string,
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
};

export type CreateUserInput = {
  avatarUri?: string | null,
  email?: string | null,
  firstName: string,
  id?: string | null,
  lastName: string,
  name?: string | null,
  phone?: string | null,
};

export type ModelUserGroupConditionInput = {
  and?: Array< ModelUserGroupConditionInput | null > | null,
  createdAt?: ModelStringInput | null,
  groupId?: ModelIDInput | null,
  not?: ModelUserGroupConditionInput | null,
  or?: Array< ModelUserGroupConditionInput | null > | null,
  updatedAt?: ModelStringInput | null,
  userId?: ModelIDInput | null,
};

export type CreateUserGroupInput = {
  groupId?: string | null,
  id?: string | null,
  userId?: string | null,
};

export type DeleteAppDataInput = {
  id: string,
};

export type DeleteContactInput = {
  id: string,
};

export type DeleteGroupInput = {
  id: string,
};

export type DeleteUserInput = {
  id: string,
};

export type DeleteUserGroupInput = {
  id: string,
};

export type UpdateAppDataInput = {
  avatarUri?: string | null,
  contacts?: string | null,
  email?: string | null,
  firstName?: string | null,
  groups?: string | null,
  id: string,
  lastName?: string | null,
  name?: string | null,
  phone?: string | null,
  type?: string | null,
  userId?: string | null,
  userName?: string | null,
  vehicles?: string | null,
};

export type UpdateContactInput = {
  email?: string | null,
  id: string,
  name?: string | null,
  phone?: string | null,
};

export type UpdateGroupInput = {
  id: string,
  isAdmin?: string | null,
  members?: string | null,
  name?: string | null,
};

export type UpdateUserInput = {
  avatarUri?: string | null,
  email?: string | null,
  firstName?: string | null,
  id: string,
  lastName?: string | null,
  name?: string | null,
  phone?: string | null,
};

export type UpdateUserGroupInput = {
  groupId?: string | null,
  id: string,
  userId?: string | null,
};

export type ModelSubscriptionAppDataFilterInput = {
  and?: Array< ModelSubscriptionAppDataFilterInput | null > | null,
  avatarUri?: ModelSubscriptionStringInput | null,
  contacts?: ModelSubscriptionStringInput | null,
  createdAt?: ModelSubscriptionStringInput | null,
  email?: ModelSubscriptionStringInput | null,
  firstName?: ModelSubscriptionStringInput | null,
  groups?: ModelSubscriptionStringInput | null,
  id?: ModelSubscriptionIDInput | null,
  lastName?: ModelSubscriptionStringInput | null,
  name?: ModelSubscriptionStringInput | null,
  or?: Array< ModelSubscriptionAppDataFilterInput | null > | null,
  phone?: ModelSubscriptionStringInput | null,
  type?: ModelSubscriptionStringInput | null,
  updatedAt?: ModelSubscriptionStringInput | null,
  userId?: ModelSubscriptionStringInput | null,
  userName?: ModelSubscriptionStringInput | null,
  vehicles?: ModelSubscriptionStringInput | null,
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

export type ModelSubscriptionContactFilterInput = {
  and?: Array< ModelSubscriptionContactFilterInput | null > | null,
  createdAt?: ModelSubscriptionStringInput | null,
  email?: ModelSubscriptionStringInput | null,
  id?: ModelSubscriptionIDInput | null,
  name?: ModelSubscriptionStringInput | null,
  or?: Array< ModelSubscriptionContactFilterInput | null > | null,
  phone?: ModelSubscriptionStringInput | null,
  updatedAt?: ModelSubscriptionStringInput | null,
};

export type ModelSubscriptionGroupFilterInput = {
  and?: Array< ModelSubscriptionGroupFilterInput | null > | null,
  createdAt?: ModelSubscriptionStringInput | null,
  id?: ModelSubscriptionIDInput | null,
  isAdmin?: ModelSubscriptionStringInput | null,
  members?: ModelSubscriptionStringInput | null,
  name?: ModelSubscriptionStringInput | null,
  or?: Array< ModelSubscriptionGroupFilterInput | null > | null,
  updatedAt?: ModelSubscriptionStringInput | null,
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
};

export type ModelSubscriptionUserGroupFilterInput = {
  and?: Array< ModelSubscriptionUserGroupFilterInput | null > | null,
  createdAt?: ModelSubscriptionStringInput | null,
  groupId?: ModelSubscriptionIDInput | null,
  id?: ModelSubscriptionIDInput | null,
  or?: Array< ModelSubscriptionUserGroupFilterInput | null > | null,
  updatedAt?: ModelSubscriptionStringInput | null,
  userId?: ModelSubscriptionIDInput | null,
};

export type GetAppDataQueryVariables = {
  id: string,
};

export type GetAppDataQuery = {
  getAppData?:  {
    __typename: "AppData",
    avatarUri?: string | null,
    contacts?: string | null,
    createdAt: string,
    email?: string | null,
    firstName: string,
    groups?: string | null,
    id: string,
    lastName: string,
    name?: string | null,
    phone?: string | null,
    type?: string | null,
    updatedAt: string,
    userId?: string | null,
    userName?: string | null,
    vehicles?: string | null,
  } | null,
};

export type GetContactQueryVariables = {
  id: string,
};

export type GetContactQuery = {
  getContact?:  {
    __typename: "Contact",
    createdAt: string,
    email?: string | null,
    id: string,
    name?: string | null,
    phone?: string | null,
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
    } | null,
  } | null,
};

export type GetGroupQueryVariables = {
  id: string,
};

export type GetGroupQuery = {
  getGroup?:  {
    __typename: "Group",
    createdAt: string,
    id: string,
    isAdmin?: string | null,
    members?: string | null,
    name: string,
    updatedAt: string,
    users?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
  } | null,
};

export type GetUserQueryVariables = {
  id: string,
};

export type GetUserQuery = {
  getUser?:  {
    __typename: "User",
    avatarUri?: string | null,
    contacts?:  {
      __typename: "ModelContactConnection",
      nextToken?: string | null,
    } | null,
    createdAt: string,
    email?: string | null,
    firstName: string,
    groups?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
    id: string,
    lastName: string,
    name?: string | null,
    phone?: string | null,
    updatedAt: string,
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
      id: string,
      isAdmin?: string | null,
      members?: string | null,
      name: string,
      updatedAt: string,
    } | null,
    groupId?: string | null,
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
    } | null,
    userId?: string | null,
  } | null,
};

export type ListAppDataQueryVariables = {
  filter?: ModelAppDataFilterInput | null,
  id?: string | null,
  limit?: number | null,
  nextToken?: string | null,
  sortDirection?: ModelSortDirection | null,
};

export type ListAppDataQuery = {
  listAppData?:  {
    __typename: "ModelAppDataConnection",
    items:  Array< {
      __typename: "AppData",
      avatarUri?: string | null,
      contacts?: string | null,
      createdAt: string,
      email?: string | null,
      firstName: string,
      groups?: string | null,
      id: string,
      lastName: string,
      name?: string | null,
      phone?: string | null,
      type?: string | null,
      updatedAt: string,
      userId?: string | null,
      userName?: string | null,
      vehicles?: string | null,
    } | null >,
    nextToken?: string | null,
  } | null,
};

export type ListContactsQueryVariables = {
  filter?: ModelContactFilterInput | null,
  id?: string | null,
  limit?: number | null,
  nextToken?: string | null,
  sortDirection?: ModelSortDirection | null,
};

export type ListContactsQuery = {
  listContacts?:  {
    __typename: "ModelContactConnection",
    items:  Array< {
      __typename: "Contact",
      createdAt: string,
      email?: string | null,
      id: string,
      name?: string | null,
      phone?: string | null,
      updatedAt: string,
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
      id: string,
      isAdmin?: string | null,
      members?: string | null,
      name: string,
      updatedAt: string,
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
      groupId?: string | null,
      id: string,
      updatedAt: string,
      userId?: string | null,
    } | null >,
    nextToken?: string | null,
  } | null,
};

export type ListUsersQueryVariables = {
  filter?: ModelUserFilterInput | null,
  id?: string | null,
  limit?: number | null,
  nextToken?: string | null,
  sortDirection?: ModelSortDirection | null,
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
    } | null >,
    nextToken?: string | null,
  } | null,
};

export type CreateAppDataMutationVariables = {
  condition?: ModelAppDataConditionInput | null,
  input: CreateAppDataInput,
};

export type CreateAppDataMutation = {
  createAppData?:  {
    __typename: "AppData",
    avatarUri?: string | null,
    contacts?: string | null,
    createdAt: string,
    email?: string | null,
    firstName: string,
    groups?: string | null,
    id: string,
    lastName: string,
    name?: string | null,
    phone?: string | null,
    type?: string | null,
    updatedAt: string,
    userId?: string | null,
    userName?: string | null,
    vehicles?: string | null,
  } | null,
};

export type CreateContactMutationVariables = {
  condition?: ModelContactConditionInput | null,
  input: CreateContactInput,
};

export type CreateContactMutation = {
  createContact?:  {
    __typename: "Contact",
    createdAt: string,
    email?: string | null,
    id: string,
    name?: string | null,
    phone?: string | null,
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
    } | null,
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
    id: string,
    isAdmin?: string | null,
    members?: string | null,
    name: string,
    updatedAt: string,
    users?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
  } | null,
};

export type CreateUserMutationVariables = {
  condition?: ModelUserConditionInput | null,
  input: CreateUserInput,
};

export type CreateUserMutation = {
  createUser?:  {
    __typename: "User",
    avatarUri?: string | null,
    contacts?:  {
      __typename: "ModelContactConnection",
      nextToken?: string | null,
    } | null,
    createdAt: string,
    email?: string | null,
    firstName: string,
    groups?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
    id: string,
    lastName: string,
    name?: string | null,
    phone?: string | null,
    updatedAt: string,
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
      id: string,
      isAdmin?: string | null,
      members?: string | null,
      name: string,
      updatedAt: string,
    } | null,
    groupId?: string | null,
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
    } | null,
    userId?: string | null,
  } | null,
};

export type DeleteAppDataMutationVariables = {
  condition?: ModelAppDataConditionInput | null,
  input: DeleteAppDataInput,
};

export type DeleteAppDataMutation = {
  deleteAppData?:  {
    __typename: "AppData",
    avatarUri?: string | null,
    contacts?: string | null,
    createdAt: string,
    email?: string | null,
    firstName: string,
    groups?: string | null,
    id: string,
    lastName: string,
    name?: string | null,
    phone?: string | null,
    type?: string | null,
    updatedAt: string,
    userId?: string | null,
    userName?: string | null,
    vehicles?: string | null,
  } | null,
};

export type DeleteContactMutationVariables = {
  condition?: ModelContactConditionInput | null,
  input: DeleteContactInput,
};

export type DeleteContactMutation = {
  deleteContact?:  {
    __typename: "Contact",
    createdAt: string,
    email?: string | null,
    id: string,
    name?: string | null,
    phone?: string | null,
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
    } | null,
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
    id: string,
    isAdmin?: string | null,
    members?: string | null,
    name: string,
    updatedAt: string,
    users?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
  } | null,
};

export type DeleteUserMutationVariables = {
  condition?: ModelUserConditionInput | null,
  input: DeleteUserInput,
};

export type DeleteUserMutation = {
  deleteUser?:  {
    __typename: "User",
    avatarUri?: string | null,
    contacts?:  {
      __typename: "ModelContactConnection",
      nextToken?: string | null,
    } | null,
    createdAt: string,
    email?: string | null,
    firstName: string,
    groups?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
    id: string,
    lastName: string,
    name?: string | null,
    phone?: string | null,
    updatedAt: string,
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
      id: string,
      isAdmin?: string | null,
      members?: string | null,
      name: string,
      updatedAt: string,
    } | null,
    groupId?: string | null,
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
    } | null,
    userId?: string | null,
  } | null,
};

export type UpdateAppDataMutationVariables = {
  condition?: ModelAppDataConditionInput | null,
  input: UpdateAppDataInput,
};

export type UpdateAppDataMutation = {
  updateAppData?:  {
    __typename: "AppData",
    avatarUri?: string | null,
    contacts?: string | null,
    createdAt: string,
    email?: string | null,
    firstName: string,
    groups?: string | null,
    id: string,
    lastName: string,
    name?: string | null,
    phone?: string | null,
    type?: string | null,
    updatedAt: string,
    userId?: string | null,
    userName?: string | null,
    vehicles?: string | null,
  } | null,
};

export type UpdateContactMutationVariables = {
  condition?: ModelContactConditionInput | null,
  input: UpdateContactInput,
};

export type UpdateContactMutation = {
  updateContact?:  {
    __typename: "Contact",
    createdAt: string,
    email?: string | null,
    id: string,
    name?: string | null,
    phone?: string | null,
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
    } | null,
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
    id: string,
    isAdmin?: string | null,
    members?: string | null,
    name: string,
    updatedAt: string,
    users?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
  } | null,
};

export type UpdateUserMutationVariables = {
  condition?: ModelUserConditionInput | null,
  input: UpdateUserInput,
};

export type UpdateUserMutation = {
  updateUser?:  {
    __typename: "User",
    avatarUri?: string | null,
    contacts?:  {
      __typename: "ModelContactConnection",
      nextToken?: string | null,
    } | null,
    createdAt: string,
    email?: string | null,
    firstName: string,
    groups?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
    id: string,
    lastName: string,
    name?: string | null,
    phone?: string | null,
    updatedAt: string,
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
      id: string,
      isAdmin?: string | null,
      members?: string | null,
      name: string,
      updatedAt: string,
    } | null,
    groupId?: string | null,
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
    } | null,
    userId?: string | null,
  } | null,
};

export type OnCreateAppDataSubscriptionVariables = {
  filter?: ModelSubscriptionAppDataFilterInput | null,
};

export type OnCreateAppDataSubscription = {
  onCreateAppData?:  {
    __typename: "AppData",
    avatarUri?: string | null,
    contacts?: string | null,
    createdAt: string,
    email?: string | null,
    firstName: string,
    groups?: string | null,
    id: string,
    lastName: string,
    name?: string | null,
    phone?: string | null,
    type?: string | null,
    updatedAt: string,
    userId?: string | null,
    userName?: string | null,
    vehicles?: string | null,
  } | null,
};

export type OnCreateContactSubscriptionVariables = {
  filter?: ModelSubscriptionContactFilterInput | null,
};

export type OnCreateContactSubscription = {
  onCreateContact?:  {
    __typename: "Contact",
    createdAt: string,
    email?: string | null,
    id: string,
    name?: string | null,
    phone?: string | null,
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
    } | null,
  } | null,
};

export type OnCreateGroupSubscriptionVariables = {
  filter?: ModelSubscriptionGroupFilterInput | null,
};

export type OnCreateGroupSubscription = {
  onCreateGroup?:  {
    __typename: "Group",
    createdAt: string,
    id: string,
    isAdmin?: string | null,
    members?: string | null,
    name: string,
    updatedAt: string,
    users?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
  } | null,
};

export type OnCreateUserSubscriptionVariables = {
  filter?: ModelSubscriptionUserFilterInput | null,
};

export type OnCreateUserSubscription = {
  onCreateUser?:  {
    __typename: "User",
    avatarUri?: string | null,
    contacts?:  {
      __typename: "ModelContactConnection",
      nextToken?: string | null,
    } | null,
    createdAt: string,
    email?: string | null,
    firstName: string,
    groups?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
    id: string,
    lastName: string,
    name?: string | null,
    phone?: string | null,
    updatedAt: string,
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
      id: string,
      isAdmin?: string | null,
      members?: string | null,
      name: string,
      updatedAt: string,
    } | null,
    groupId?: string | null,
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
    } | null,
    userId?: string | null,
  } | null,
};

export type OnDeleteAppDataSubscriptionVariables = {
  filter?: ModelSubscriptionAppDataFilterInput | null,
};

export type OnDeleteAppDataSubscription = {
  onDeleteAppData?:  {
    __typename: "AppData",
    avatarUri?: string | null,
    contacts?: string | null,
    createdAt: string,
    email?: string | null,
    firstName: string,
    groups?: string | null,
    id: string,
    lastName: string,
    name?: string | null,
    phone?: string | null,
    type?: string | null,
    updatedAt: string,
    userId?: string | null,
    userName?: string | null,
    vehicles?: string | null,
  } | null,
};

export type OnDeleteContactSubscriptionVariables = {
  filter?: ModelSubscriptionContactFilterInput | null,
};

export type OnDeleteContactSubscription = {
  onDeleteContact?:  {
    __typename: "Contact",
    createdAt: string,
    email?: string | null,
    id: string,
    name?: string | null,
    phone?: string | null,
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
    } | null,
  } | null,
};

export type OnDeleteGroupSubscriptionVariables = {
  filter?: ModelSubscriptionGroupFilterInput | null,
};

export type OnDeleteGroupSubscription = {
  onDeleteGroup?:  {
    __typename: "Group",
    createdAt: string,
    id: string,
    isAdmin?: string | null,
    members?: string | null,
    name: string,
    updatedAt: string,
    users?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
  } | null,
};

export type OnDeleteUserSubscriptionVariables = {
  filter?: ModelSubscriptionUserFilterInput | null,
};

export type OnDeleteUserSubscription = {
  onDeleteUser?:  {
    __typename: "User",
    avatarUri?: string | null,
    contacts?:  {
      __typename: "ModelContactConnection",
      nextToken?: string | null,
    } | null,
    createdAt: string,
    email?: string | null,
    firstName: string,
    groups?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
    id: string,
    lastName: string,
    name?: string | null,
    phone?: string | null,
    updatedAt: string,
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
      id: string,
      isAdmin?: string | null,
      members?: string | null,
      name: string,
      updatedAt: string,
    } | null,
    groupId?: string | null,
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
    } | null,
    userId?: string | null,
  } | null,
};

export type OnUpdateAppDataSubscriptionVariables = {
  filter?: ModelSubscriptionAppDataFilterInput | null,
};

export type OnUpdateAppDataSubscription = {
  onUpdateAppData?:  {
    __typename: "AppData",
    avatarUri?: string | null,
    contacts?: string | null,
    createdAt: string,
    email?: string | null,
    firstName: string,
    groups?: string | null,
    id: string,
    lastName: string,
    name?: string | null,
    phone?: string | null,
    type?: string | null,
    updatedAt: string,
    userId?: string | null,
    userName?: string | null,
    vehicles?: string | null,
  } | null,
};

export type OnUpdateContactSubscriptionVariables = {
  filter?: ModelSubscriptionContactFilterInput | null,
};

export type OnUpdateContactSubscription = {
  onUpdateContact?:  {
    __typename: "Contact",
    createdAt: string,
    email?: string | null,
    id: string,
    name?: string | null,
    phone?: string | null,
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
    } | null,
  } | null,
};

export type OnUpdateGroupSubscriptionVariables = {
  filter?: ModelSubscriptionGroupFilterInput | null,
};

export type OnUpdateGroupSubscription = {
  onUpdateGroup?:  {
    __typename: "Group",
    createdAt: string,
    id: string,
    isAdmin?: string | null,
    members?: string | null,
    name: string,
    updatedAt: string,
    users?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
  } | null,
};

export type OnUpdateUserSubscriptionVariables = {
  filter?: ModelSubscriptionUserFilterInput | null,
};

export type OnUpdateUserSubscription = {
  onUpdateUser?:  {
    __typename: "User",
    avatarUri?: string | null,
    contacts?:  {
      __typename: "ModelContactConnection",
      nextToken?: string | null,
    } | null,
    createdAt: string,
    email?: string | null,
    firstName: string,
    groups?:  {
      __typename: "ModelUserGroupConnection",
      nextToken?: string | null,
    } | null,
    id: string,
    lastName: string,
    name?: string | null,
    phone?: string | null,
    updatedAt: string,
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
      id: string,
      isAdmin?: string | null,
      members?: string | null,
      name: string,
      updatedAt: string,
    } | null,
    groupId?: string | null,
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
    } | null,
    userId?: string | null,
  } | null,
};
