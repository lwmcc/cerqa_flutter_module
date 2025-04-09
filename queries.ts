/* tslint:disable */
/* eslint-disable */
// this is an auto generated file. This will be overwritten

import * as APITypes from "./API";
type GeneratedQuery<InputType, OutputType> = string & {
  __generatedQueryInput: InputType;
  __generatedQueryOutput: OutputType;
};

export const getAppData = /* GraphQL */ `query GetAppData($id: ID!) {
  getAppData(id: $id) {
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
` as GeneratedQuery<
  APITypes.GetAppDataQueryVariables,
  APITypes.GetAppDataQuery
>;
export const getContact = /* GraphQL */ `query GetContact($id: ID!) {
  getContact(id: $id) {
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
` as GeneratedQuery<
  APITypes.GetContactQueryVariables,
  APITypes.GetContactQuery
>;
export const getGroup = /* GraphQL */ `query GetGroup($id: ID!) {
  getGroup(id: $id) {
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
` as GeneratedQuery<APITypes.GetGroupQueryVariables, APITypes.GetGroupQuery>;
export const getUser = /* GraphQL */ `query GetUser($id: ID!) {
  getUser(id: $id) {
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
` as GeneratedQuery<APITypes.GetUserQueryVariables, APITypes.GetUserQuery>;
export const getUserGroup = /* GraphQL */ `query GetUserGroup($id: ID!) {
  getUserGroup(id: $id) {
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
` as GeneratedQuery<
  APITypes.GetUserGroupQueryVariables,
  APITypes.GetUserGroupQuery
>;
export const listAppData = /* GraphQL */ `query ListAppData(
  $filter: ModelAppDataFilterInput
  $id: ID
  $limit: Int
  $nextToken: String
  $sortDirection: ModelSortDirection
) {
  listAppData(
    filter: $filter
    id: $id
    limit: $limit
    nextToken: $nextToken
    sortDirection: $sortDirection
  ) {
    items {
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
    nextToken
    __typename
  }
}
` as GeneratedQuery<
  APITypes.ListAppDataQueryVariables,
  APITypes.ListAppDataQuery
>;
export const listContacts = /* GraphQL */ `query ListContacts(
  $filter: ModelContactFilterInput
  $id: ID
  $limit: Int
  $nextToken: String
  $sortDirection: ModelSortDirection
) {
  listContacts(
    filter: $filter
    id: $id
    limit: $limit
    nextToken: $nextToken
    sortDirection: $sortDirection
  ) {
    items {
      createdAt
      email
      id
      name
      phone
      updatedAt
      __typename
    }
    nextToken
    __typename
  }
}
` as GeneratedQuery<
  APITypes.ListContactsQueryVariables,
  APITypes.ListContactsQuery
>;
export const listGroups = /* GraphQL */ `query ListGroups(
  $filter: ModelGroupFilterInput
  $limit: Int
  $nextToken: String
) {
  listGroups(filter: $filter, limit: $limit, nextToken: $nextToken) {
    items {
      createdAt
      id
      isAdmin
      members
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
  $id: ID
  $limit: Int
  $nextToken: String
  $sortDirection: ModelSortDirection
) {
  listUsers(
    filter: $filter
    id: $id
    limit: $limit
    nextToken: $nextToken
    sortDirection: $sortDirection
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
      __typename
    }
    nextToken
    __typename
  }
}
` as GeneratedQuery<APITypes.ListUsersQueryVariables, APITypes.ListUsersQuery>;
