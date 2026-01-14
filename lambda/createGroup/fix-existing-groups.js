const AWS = require('aws-sdk');

const REGION = 'us-east-2';
const GROUP_TABLE = 'Group-wtl5wlqxxvb6lp2nenxcjpvpwq-NONE';
const USER_GROUP_TABLE = 'UserGroup-wtl5wlqxxvb6lp2nenxcjpvpwq-NONE';

const dynamodb = new AWS.DynamoDB.DocumentClient({ region: REGION });

/**
 * Fix existing UserGroup entries to use correct Group IDs
 */
async function fixExistingUserGroups() {
    console.log('=========================================');
    console.log('Fixing Existing UserGroup Entries');
    console.log('=========================================\n');

    try {
        // Step 1: Get all UserGroup entries
        console.log('Step 1: Fetching all UserGroup entries...');
        const userGroupsResult = await dynamodb.scan({
            TableName: USER_GROUP_TABLE
        }).promise();

        const userGroups = userGroupsResult.Items || [];
        console.log(`Found ${userGroups.length} UserGroup entries\n`);

        if (userGroups.length === 0) {
            console.log('No UserGroup entries to fix. Exiting.');
            return;
        }

        // Step 2: Get all Groups
        console.log('Step 2: Fetching all Groups...');
        const groupsResult = await dynamodb.scan({
            TableName: GROUP_TABLE
        }).promise();

        const groups = groupsResult.Items || [];
        console.log(`Found ${groups.length} Groups\n`);

        // Create a map of groupId (custom) -> DynamoDB id
        const groupIdMap = new Map();
        groups.forEach(group => {
            if (group.groupId && group.id) {
                groupIdMap.set(group.groupId, group.id);
                console.log(`  Mapped: ${group.groupId} -> ${group.id} (${group.name})`);
            }
        });
        console.log('');

        // Step 3: Check and fix UserGroup entries
        console.log('Step 3: Checking UserGroup entries...\n');
        let fixedCount = 0;
        let alreadyCorrectCount = 0;
        let errorCount = 0;

        for (const userGroup of userGroups) {
            const currentGroupId = userGroup.groupId;

            // Check if this groupId looks like a custom ID (starts with "group_")
            if (currentGroupId.startsWith('group_')) {
                const correctDynamoDbId = groupIdMap.get(currentGroupId);

                if (correctDynamoDbId) {
                    console.log(`⚠️  NEEDS FIX: UserGroup ${userGroup.id}`);
                    console.log(`   Current groupId: ${currentGroupId}`);
                    console.log(`   Correct groupId: ${correctDynamoDbId}`);

                    try {
                        // Update the UserGroup entry
                        await dynamodb.update({
                            TableName: USER_GROUP_TABLE,
                            Key: { id: userGroup.id },
                            UpdateExpression: 'SET groupId = :newGroupId',
                            ExpressionAttributeValues: {
                                ':newGroupId': correctDynamoDbId
                            }
                        }).promise();

                        console.log(`   ✅ FIXED!\n`);
                        fixedCount++;
                    } catch (error) {
                        console.log(`   ❌ ERROR: ${error.message}\n`);
                        errorCount++;
                    }
                } else {
                    console.log(`⚠️  WARNING: UserGroup ${userGroup.id} references non-existent group: ${currentGroupId}\n`);
                    errorCount++;
                }
            } else {
                // This groupId is already a DynamoDB ID (UUID format)
                console.log(`✓ OK: UserGroup ${userGroup.id} already has correct groupId: ${currentGroupId}`);
                alreadyCorrectCount++;
            }
        }

        // Summary
        console.log('\n=========================================');
        console.log('SUMMARY');
        console.log('=========================================');
        console.log(`Total UserGroups:      ${userGroups.length}`);
        console.log(`Fixed:                 ${fixedCount}`);
        console.log(`Already Correct:       ${alreadyCorrectCount}`);
        console.log(`Errors:                ${errorCount}`);
        console.log('=========================================\n');

        if (fixedCount > 0) {
            console.log('✅ Success! Your existing groups should now display correctly in the app.');
        } else if (alreadyCorrectCount === userGroups.length) {
            console.log('✅ All UserGroup entries were already correct. No changes needed.');
        }

    } catch (error) {
        console.error('❌ Fatal error:', error);
        throw error;
    }
}

// Run the script
fixExistingUserGroups()
    .then(() => {
        console.log('Script completed successfully.');
        process.exit(0);
    })
    .catch(error => {
        console.error('Script failed:', error);
        process.exit(1);
    });