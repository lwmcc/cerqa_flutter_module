# Apollo Kotlin + AppSync Setup Guide

This guide explains how to use Apollo Kotlin with AWS AppSync in your Kotlin Multiplatform project.

## Architecture Overview

```
┌─────────────────────────────────────────┐
│         iOS/Android Native Layer        │
│      (Amplify Auth for tokens)          │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│       Shared KMP Module                 │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │  Apollo Kotlin Client           │   │
│  │  (uses Ktor HTTP engine)        │   │
│  └─────────────────────────────────┘   │
│                                         │
│  - Type-safe GraphQL operations         │
│  - Normalized cache                     │
│  - Real-time subscriptions              │
│  - Repositories & ViewModels            │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         AWS AppSync GraphQL API         │
│        (Cognito authentication)         │
└─────────────────────────────────────────┘
```

## What's Already Configured

✅ Apollo Kotlin plugin and dependencies
✅ Apollo client factory with Ktor engine
✅ Auth token injection from Amplify
✅ Koin DI modules for Android/iOS
✅ Example GraphQL schema and operations

## Next Steps

### 1. Download Your AppSync Schema

You need to replace the example schema with your actual AppSync schema:

```bash
# Using AWS CLI
aws appsync get-introspection-schema \
  --api-id YOUR_API_ID \
  --format SDL \
  shared/src/commonMain/graphql/schema.graphqls

# OR download from AppSync Console:
# 1. Go to AWS AppSync Console
# 2. Select your API
# 3. Click "Schema" in sidebar
# 4. Click "Export schema" → Download SDL
# 5. Replace shared/src/commonMain/graphql/schema.graphqls
```

### 2. Write Your GraphQL Operations

Create `.graphql` files in `shared/src/commonMain/graphql/`:

**queries.graphql**
```graphql
query ListUserContacts($userId: ID!) {
  listUserContacts(filter: { userId: { eq: $userId } }) {
    items {
      id
      contact {
        id
        firstName
        lastName
        email
        phone
      }
    }
  }
}
```

**mutations.graphql**
```graphql
mutation CreateContact($input: CreateContactInput!) {
  createContact(input: $input) {
    id
    firstName
    lastName
    email
  }
}
```

**subscriptions.graphql**
```graphql
subscription OnCreateContact {
  onCreateContact {
    id
    firstName
    lastName
  }
}
```

### 3. Generate Type-Safe Code

Run Apollo's code generator:

```bash
./gradlew :shared:generateApolloSources
```

This generates Kotlin classes in:
```
shared/build/generated/source/apollo/commonMain/com/cerqa/graphql/
```

You'll get:
- `ListUserContactsQuery` class
- `CreateContactMutation` class
- `OnCreateContactSubscription` class
- Type-safe data classes for responses

### 4. Use Generated Code in Repositories

**Before (Manual GraphQL with Ktor):**
```kotlin
suspend fun fetchContacts(): Result<List<Contact>> {
    val query = """
        query { ... }  // String, no type safety
    """
    val request = GraphQLRequest(query, variables)
    val response = httpClient.post { ... }
    val data: GraphQLResponse<SomeType> = response.body()  // Manual parsing
}
```

**After (Apollo Kotlin):**
```kotlin
suspend fun fetchContacts(): Result<List<Contact>> {
    val response = apolloClient
        .query(ListUserContactsQuery(userId = currentUserId))  // Type-safe!
        .execute()

    if (response.hasErrors()) {
        return Result.failure(...)
    }

    val contacts = response.data?.listUserContacts?.items?.map { it.toContact() }
    return Result.success(contacts)
}
```

### 5. Real-Time Subscriptions

Apollo makes subscriptions incredibly easy:

```kotlin
fun watchContactUpdates(): Flow<Contact> {
    return apolloClient
        .subscription(OnCreateContactSubscription())
        .toFlow()
        .map { response ->
            response.data?.onCreateContact?.toContact()
                ?: throw Exception("Invalid data")
        }
}

// In your ViewModel:
viewModelScope.launch {
    contactsRepository.watchContactUpdates()
        .collect { newContact ->
            _contacts.update { it + newContact }
        }
}
```

### 6. Update DI to Use Apollo Repository

In `shared/src/commonMain/kotlin/com/cerqa/di/CommonModule.kt`:

```kotlin
val commonModule = module {
    // Apollo client is already configured ✅
    single { createApolloClient(get(), get()) }

    // Switch to Apollo-based repository
    single { ContactsRepositoryApollo(get(), get()) }

    factory { ContactsViewModel(get()) }
}
```

## Key Benefits

### Type Safety
- Queries, mutations, and responses are type-checked at compile time
- No more manual JSON parsing or string-based queries
- Catch GraphQL schema changes at build time

### Normalized Cache
- Apollo automatically caches and normalizes data
- Updates to one object update it everywhere
- Configurable cache policies (cache-first, network-only, etc.)

### Developer Experience
- Auto-complete for all GraphQL fields
- Jump to definition for types
- Refactoring support across queries

### Performance
- Efficient caching reduces network requests
- Request deduplication
- Optimistic updates for instant UI

## Example: Complete Flow

1. **Write GraphQL Query:**
```graphql
# shared/src/commonMain/graphql/GetContact.graphql
query GetContact($id: ID!) {
  getContact(id: $id) {
    id
    firstName
    lastName
    email
  }
}
```

2. **Generate Code:**
```bash
./gradlew :shared:generateApolloSources
```

3. **Use in Repository:**
```kotlin
class ContactsRepository(private val apolloClient: ApolloClient) {
    suspend fun getContact(id: String): Contact? {
        val response = apolloClient
            .query(GetContactQuery(id = id))
            .execute()

        return response.data?.getContact?.let {
            Contact(
                id = it.id,
                firstName = it.firstName,
                lastName = it.lastName,
                email = it.email
            )
        }
    }
}
```

4. **Call from ViewModel:**
```kotlin
class ContactsViewModel(private val repo: ContactsRepository) : ViewModel() {
    fun loadContact(id: String) {
        viewModelScope.launch {
            val contact = repo.getContact(id)
            _uiState.update { it.copy(contact = contact) }
        }
    }
}
```

## Troubleshooting

### Code Generation Fails
- Make sure your schema file is valid GraphQL SDL
- Check that all directives used in queries exist in schema
- Run `./gradlew clean` and try again

### Auth Errors
- Verify Amplify Auth is configured correctly on native side
- Check that tokens are being passed in headers
- Test with AWS Console's GraphQL explorer first

### Build Errors After Adding Apollo
- Sync Gradle: `./gradlew --refresh-dependencies`
- Clean build: `./gradlew clean`
- Rebuild: `./gradlew :shared:build`

## Migration Strategy

You can migrate gradually from manual GraphQL to Apollo:

1. Keep existing `ContactsRepository` working
2. Create `ContactsRepositoryApollo` alongside it
3. Test Apollo version thoroughly
4. Switch DI to use Apollo version
5. Remove old repository when confident

Both can coexist since they use different clients (HttpClient vs ApolloClient).

## Resources

- [Apollo Kotlin Docs](https://www.apollographql.com/docs/kotlin/)
- [AppSync GraphQL Docs](https://docs.aws.amazon.com/appsync/)
- [Ktor Client](https://ktor.io/docs/client.html)
- [Koin DI](https://insert-koin.io/)

## File Structure

```
shared/src/commonMain/
├── graphql/                           # GraphQL definitions
│   ├── schema.graphqls               # Your AppSync schema
│   ├── queries.graphql               # Query definitions
│   ├── mutations.graphql             # Mutation definitions
│   └── subscriptions.graphql         # Subscription definitions
│
├── kotlin/com/cerqa/
│   ├── network/
│   │   ├── ApolloClientFactory.kt    # Apollo client setup
│   │   └── HttpClientFactory.kt      # Ktor client (legacy)
│   │
│   ├── repository/
│   │   ├── ContactsRepository.kt     # Manual GraphQL (legacy)
│   │   └── ContactsRepositoryApollo.kt  # Apollo-based
│   │
│   └── di/
│       └── CommonModule.kt           # DI configuration
│
└── build/generated/source/apollo/    # Auto-generated code
    └── commonMain/com/cerqa/graphql/
        ├── ListContactsQuery.kt
        ├── CreateContactMutation.kt
        └── ... (all your operations)
```

## Summary

You now have:
- ✅ Apollo Kotlin integrated with Ktor
- ✅ Auth tokens automatically injected
- ✅ Type-safe GraphQL operations
- ✅ Real-time subscription support
- ✅ Normalized caching
- ✅ Full KMP code sharing

Just download your schema, write your operations, and start using type-safe GraphQL!
