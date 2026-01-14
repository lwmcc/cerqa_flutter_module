# Cleanup Unknown Groups and Channels

This document explains how to delete all groups and channels with unknown or null names from your database.

## What Gets Deleted

The cleanup function will permanently delete:
- **Groups** with `null`, empty, or names containing "unknown"
- **Channels** with `null`, empty, or names containing "unknown"
- All **UserGroup** entries associated with deleted groups
- All **messages** in deleted channels

## Method 1: From Your App (Recommended)

You can call the cleanup function directly from your Kotlin code:

```kotlin
// In any viewmodel or repository
val groupRepository: GroupRepository = koinInject()

viewModelScope.launch {
    groupRepository.cleanupUnknownData()
        .onSuccess { result ->
            println("Cleanup result: $result")
            // Show success message to user
        }
        .onFailure { error ->
            println("Cleanup failed: ${error.message}")
            // Show error message to user
        }
}
```

## Method 2: Deploy and Use the Lambda Function

1. **Deploy the new Lambda function:**
   ```bash
   cd /Users/larrymccarty/AndroidStudioProjects/carclub
   npx ampx sandbox
   ```

2. **The function will be automatically deployed** as part of your Amplify backend.

3. **Run the cleanup script:**
   ```bash
   cd /Users/larrymccarty/AndroidStudioProjects/carclub
   ./scripts/cleanup-unknown-data.sh
   ```

## Method 3: Use GraphQL Directly

You can also call the cleanup mutation directly through your GraphQL client:

```graphql
mutation {
  cleanupUnknownData
}
```

## Verification

After running the cleanup, you can verify the results by:

1. Checking the CloudWatch logs for the Lambda function
2. Querying your groups and channels to confirm unknown entries are removed:
   ```graphql
   query {
     listGroups {
       items {
         id
         name
       }
     }
   }
   ```

## Safety Notes

⚠️ **Warning**: This operation is destructive and cannot be undone!

- Always backup your data before running cleanup
- Test in a development environment first
- The function checks for:
  - Names that are `null`
  - Names that are empty strings
  - Names containing the word "unknown" (case-insensitive)

## Files Added/Modified

- `amplify/functions/cleanupUnknownData/handler.ts` - Lambda function implementation
- `amplify/functions/cleanupUnknownData/resource.ts` - Lambda function configuration
- `amplify/backend.ts` - Added function to backend
- `amplify/data/resource.ts` - Added GraphQL mutation
- `shared/src/commonMain/graphql/mutations.graphql` - Added CleanupUnknownData mutation
- `shared/src/commonMain/kotlin/com/cerqa/repository/GroupRepository.kt` - Added interface method
- `shared/src/commonMain/kotlin/com/cerqa/repository/GroupRepositoryImpl.kt` - Added implementation
- `scripts/cleanup-unknown-data.sh` - Bash script to invoke the function

## Troubleshooting

If the cleanup fails:

1. **Check permissions**: Ensure the Lambda function has DynamoDB permissions
2. **Check logs**: View CloudWatch logs for detailed error messages
3. **Check GraphQL errors**: The mutation will return detailed error information
4. **Manual cleanup**: If automated cleanup fails, you can use the AWS Console to manually delete problem records

## Support

If you encounter issues, check the Lambda CloudWatch logs or the console output for detailed error messages.
