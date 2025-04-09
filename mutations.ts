/* tslint:disable */
/* eslint-disable */
// this is an auto generated file. This will be overwritten

import * as APITypes from "./API";
type GeneratedMutation<InputType, OutputType> = string & {
  __generatedMutationInput: InputType;
  __generatedMutationOutput: OutputType;
};

export const createAppData = /* GraphQL */ `mutation CreateAppData(
  $condition: ModelAppDataConditionInput
  $input: CreateAppDataInput!
) {
  createAppData(condition: $condition, input: $input) {
    avatarUri
    contacts
    createdAt
    email
    firstName
    groups
    id
    lastName
    name
    phone
    type
    updatedAt
    userId
    userName
    vehicles
    __typename
  }
}
` as GeneratedMutation<
  APITypes.CreateAppDataMutationVariables,
  APITypes.CreateAppDataMutation
>;
export const createContact = /* GraphQL */ `mutation CreateContact(
  $condition: ModelContactConditionInput
  $input: CreateContactInput!
) {
  createContact(condition: $condition, input: $input) {
    createdAt
    email
    id
    name
    phone
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
      __typename
    }
    __typename
  }
}
` as GeneratedMutation<
  APITypes.CreateContactMutationVariables,
  APITypes.CreateContactMutation
>;
export const createGroup = /* GraphQL */ `mutation CreateGroup(
  $condition: ModelGroupConditionInput
  $input: CreateGroupInput!
) {
  createGroup(condition: $condition, input: $input) {
    createdAt
    id
    isAdmin
    members
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
export const createUser = /* GraphQL */ `mutation CreateUser(
  $condition: ModelUserConditionInput
  $input: CreateUserInput!
) {
  createUser(condition: $condition, input: $input) {
    avatarUri
    contacts {
      nextToken
      __typename
    }
    createdAt
    email
    firstName
    groups {
      nextToken
      __typename
    }
    id
    lastName
    name
    phone
    updatedAt
    __typename
  }
}
` as GeneratedMutation<
  APITypes.CreateUserMutationVariables,
  APITypes.CreateUserMutation
>;
export const createUserGroup = /* GraphQL */ `mutation CreateUserGroup(
  $condition: ModelUserGroupConditionInput
  $input: CreateUserGroupInput!
) {
  createUserGroup(condition: $condition, input: $input) {
    createdAt
    group {
      createdAt
      id
      isAdmin
      members
      name
      updatedAt
      __typename
    }
    groupId
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
export const deleteAppData = /* GraphQL */ `mutation DeleteAppData(
  $condition: ModelAppDataConditionInput
  $input: DeleteAppDataInput!
) {
  deleteAppData(condition: $condition, input: $input) {
    avatarUri
    contacts
    createdAt
    email
    firstName
    groups
    id
    lastName
    name
    phone
    type
    updatedAt
    userId
    userName
    vehicles
    __typename
  }
}
` as GeneratedMutation<
  APITypes.DeleteAppDataMutationVariables,
  APITypes.DeleteAppDataMutation
>;
export const deleteContact = /* GraphQL */ `mutation DeleteContact(
  $condition: ModelContactConditionInput
  $input: DeleteContactInput!
) {
  deleteContact(condition: $condition, input: $input) {
    createdAt
    email
    id
    name
    phone
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
      __typename
    }
    __typename
  }
}
` as GeneratedMutation<
  APITypes.DeleteContactMutationVariables,
  APITypes.DeleteContactMutation
>;
export const deleteGroup = /* GraphQL */ `mutation DeleteGroup(
  $condition: ModelGroupConditionInput
  $input: DeleteGroupInput!
) {
  deleteGroup(condition: $condition, input: $input) {
    createdAt
    id
    isAdmin
    members
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
export const deleteUser = /* GraphQL */ `mutation DeleteUser(
  $condition: ModelUserConditionInput
  $input: DeleteUserInput!
) {
  deleteUser(condition: $condition, input: $input) {
    avatarUri
    contacts {
      nextToken
      __typename
    }
    createdAt
    email
    firstName
    groups {
      nextToken
      __typename
    }
    id
    lastName
    name
    phone
    updatedAt
    __typename
  }
}
` as GeneratedMutation<
  APITypes.DeleteUserMutationVariables,
  APITypes.DeleteUserMutation
>;
export const deleteUserGroup = /* GraphQL */ `mutation DeleteUserGroup(
  $condition: ModelUserGroupConditionInput
  $input: DeleteUserGroupInput!
) {
  deleteUserGroup(condition: $condition, input: $input) {
    createdAt
    group {
      createdAt
      id
      isAdmin
      members
      name
      updatedAt
      __typename
    }
    groupId
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
export const updateAppData = /* GraphQL */ `mutation UpdateAppData(
  $condition: ModelAppDataConditionInput
  $input: UpdateAppDataInput!
) {
  updateAppData(condition: $condition, input: $input) {
    avatarUri
    contacts
    createdAt
    email
    firstName
    groups
    id
    lastName
    name
    phone
    type
    updatedAt
    userId
    userName
    vehicles
    __typename
  }
}
` as GeneratedMutation<
  APITypes.UpdateAppDataMutationVariables,
  APITypes.UpdateAppDataMutation
>;
export const updateContact = /* GraphQL */ `mutation UpdateContact(
  $condition: ModelContactConditionInput
  $input: UpdateContactInput!
) {
  updateContact(condition: $condition, input: $input) {
    createdAt
    email
    id
    name
    phone
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
      __typename
    }
    __typename
  }
}
` as GeneratedMutation<
  APITypes.UpdateContactMutationVariables,
  APITypes.UpdateContactMutation
>;
export const updateGroup = /* GraphQL */ `mutation UpdateGroup(
  $condition: ModelGroupConditionInput
  $input: UpdateGroupInput!
) {
  updateGroup(condition: $condition, input: $input) {
    createdAt
    id
    isAdmin
    members
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
export const updateUser = /* GraphQL */ `mutation UpdateUser(
  $condition: ModelUserConditionInput
  $input: UpdateUserInput!
) {
  updateUser(condition: $condition, input: $input) {
    avatarUri
    contacts {
      nextToken
      __typename
    }
    createdAt
    email
    firstName
    groups {
      nextToken
      __typename
    }
    id
    lastName
    name
    phone
    updatedAt
    __typename
  }
}
` as GeneratedMutation<
  APITypes.UpdateUserMutationVariables,
  APITypes.UpdateUserMutation
>;
export const updateUserGroup = /* GraphQL */ `mutation UpdateUserGroup(
  $condition: ModelUserGroupConditionInput
  $input: UpdateUserGroupInput!
) {
  updateUserGroup(condition: $condition, input: $input) {
    createdAt
    group {
      createdAt
      id
      isAdmin
      members
      name
      updatedAt
      __typename
    }
    groupId
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
