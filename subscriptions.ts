/* tslint:disable */
/* eslint-disable */
// this is an auto generated file. This will be overwritten

import * as APITypes from "./API";
type GeneratedSubscription<InputType, OutputType> = string & {
  __generatedSubscriptionInput: InputType;
  __generatedSubscriptionOutput: OutputType;
};

export const onCreateAppData = /* GraphQL */ `subscription OnCreateAppData($filter: ModelSubscriptionAppDataFilterInput) {
  onCreateAppData(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnCreateAppDataSubscriptionVariables,
  APITypes.OnCreateAppDataSubscription
>;
export const onCreateContact = /* GraphQL */ `subscription OnCreateContact($filter: ModelSubscriptionContactFilterInput) {
  onCreateContact(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnCreateContactSubscriptionVariables,
  APITypes.OnCreateContactSubscription
>;
export const onCreateGroup = /* GraphQL */ `subscription OnCreateGroup($filter: ModelSubscriptionGroupFilterInput) {
  onCreateGroup(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnCreateGroupSubscriptionVariables,
  APITypes.OnCreateGroupSubscription
>;
export const onCreateUser = /* GraphQL */ `subscription OnCreateUser($filter: ModelSubscriptionUserFilterInput) {
  onCreateUser(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnCreateUserSubscriptionVariables,
  APITypes.OnCreateUserSubscription
>;
export const onCreateUserGroup = /* GraphQL */ `subscription OnCreateUserGroup($filter: ModelSubscriptionUserGroupFilterInput) {
  onCreateUserGroup(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnCreateUserGroupSubscriptionVariables,
  APITypes.OnCreateUserGroupSubscription
>;
export const onDeleteAppData = /* GraphQL */ `subscription OnDeleteAppData($filter: ModelSubscriptionAppDataFilterInput) {
  onDeleteAppData(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnDeleteAppDataSubscriptionVariables,
  APITypes.OnDeleteAppDataSubscription
>;
export const onDeleteContact = /* GraphQL */ `subscription OnDeleteContact($filter: ModelSubscriptionContactFilterInput) {
  onDeleteContact(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnDeleteContactSubscriptionVariables,
  APITypes.OnDeleteContactSubscription
>;
export const onDeleteGroup = /* GraphQL */ `subscription OnDeleteGroup($filter: ModelSubscriptionGroupFilterInput) {
  onDeleteGroup(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnDeleteGroupSubscriptionVariables,
  APITypes.OnDeleteGroupSubscription
>;
export const onDeleteUser = /* GraphQL */ `subscription OnDeleteUser($filter: ModelSubscriptionUserFilterInput) {
  onDeleteUser(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnDeleteUserSubscriptionVariables,
  APITypes.OnDeleteUserSubscription
>;
export const onDeleteUserGroup = /* GraphQL */ `subscription OnDeleteUserGroup($filter: ModelSubscriptionUserGroupFilterInput) {
  onDeleteUserGroup(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnDeleteUserGroupSubscriptionVariables,
  APITypes.OnDeleteUserGroupSubscription
>;
export const onUpdateAppData = /* GraphQL */ `subscription OnUpdateAppData($filter: ModelSubscriptionAppDataFilterInput) {
  onUpdateAppData(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnUpdateAppDataSubscriptionVariables,
  APITypes.OnUpdateAppDataSubscription
>;
export const onUpdateContact = /* GraphQL */ `subscription OnUpdateContact($filter: ModelSubscriptionContactFilterInput) {
  onUpdateContact(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnUpdateContactSubscriptionVariables,
  APITypes.OnUpdateContactSubscription
>;
export const onUpdateGroup = /* GraphQL */ `subscription OnUpdateGroup($filter: ModelSubscriptionGroupFilterInput) {
  onUpdateGroup(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnUpdateGroupSubscriptionVariables,
  APITypes.OnUpdateGroupSubscription
>;
export const onUpdateUser = /* GraphQL */ `subscription OnUpdateUser($filter: ModelSubscriptionUserFilterInput) {
  onUpdateUser(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnUpdateUserSubscriptionVariables,
  APITypes.OnUpdateUserSubscription
>;
export const onUpdateUserGroup = /* GraphQL */ `subscription OnUpdateUserGroup($filter: ModelSubscriptionUserGroupFilterInput) {
  onUpdateUserGroup(filter: $filter) {
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
` as GeneratedSubscription<
  APITypes.OnUpdateUserGroupSubscriptionVariables,
  APITypes.OnUpdateUserGroupSubscription
>;
