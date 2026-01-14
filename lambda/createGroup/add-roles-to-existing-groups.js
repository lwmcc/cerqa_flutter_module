const AWS = require('aws-sdk');

const REGION = 'us-east-2';
const GROUP_TABLE = 'Group-wtl5wlqxxvb6lp2nenxcjpvpwq-NONE';
const USER_GROUP_TABLE = 'UserGroup-wtl5wlqxxvb6lp2nenxcjpvpwq-NONE';
const CHANNEL_TABLE = 'Channel-wtl5wlqxxvb6lp2nenxcjpvpwq-NONE';

const dynamodb = new AWS.DynamoDB.DocumentClient({ region: REGION });

/**
 * Add role field to existing UserGroup entries
 * - Finds the creator of each group (from the Channel table)
 * - Sets role to CREATOR for the creator
 * - Sets role to MEMBER for all other users
 */
async function addRolesToExistingGroups() {
    console.log('=========================================');
    console.log('Adding Roles to Existing UserGroup Entries');
    console.log('=========================================\n');

    try {
        // Step 1: Get all Groups
        console.log('Step 1: Fetching all Groups...');
        const groupsResult = await dynamodb.scan({
            TableName: GROUP_TABLE
        }).promise();

        const groups = groupsResult.Items || [];
        console.log(`Found ${groups.length} Groups\n`);

        // Step 2: Get all Channels (to find group creators)
        console.log('Step 2: Fetching all Channels to find group creators...');
        const channelsResult = await dynamodb.scan({
            TableName: CHANNEL_TABLE,
            FilterExpression: 'isGroup = :true',
            ExpressionAttributeValues: {
                ':true': true
            }
        }).promise();

        const channels = channelsResult.Items || [];
        console.log(`Found ${channels.length} group Channels\n`);

        // Create a map of groupId -> creatorId
        const groupCreatorMap = new Map();
        channels.forEach(channel => {
            // Match channel.receiverId (which is the group's custom groupId) to find creator
            if (channel.receiverId && channel.creatorId) {
                groupCreatorMap.set(channel.receiverId, channel.creatorId);
                console.log(`  Group ${channel.receiverId.substring(0, 20)}... created by user ${channel.creatorId.substring(0, 20)}...`);
            }
        });
        console.log('');

        // Step 3: Get all UserGroup entries
        console.log('Step 3: Fetching all UserGroup entries...');
        const userGroupsResult = await dynamodb.scan({
            TableName: USER_GROUP_TABLE
        }).promise();

        const userGroups = userGroupsResult.Items || [];
        console.log(`Found ${userGroups.length} UserGroup entries\n`);

        if (userGroups.length === 0) {
            console.log('No UserGroup entries to update. Exiting.');
            return;
        }

        // Step 4: Update UserGroup entries with roles
        console.log('Step 4: Adding roles to UserGroup entries...\n');
        let updatedCount = 0;
        let alreadyHasRoleCount = 0;
        let errorCount = 0;

        for (const userGroup of userGroups) {
            // Skip if already has a role
            if (userGroup.role) {
                console.log(`✓ SKIP: UserGroup ${userGroup.id} already has role: ${userGroup.role}`);
                alreadyHasRoleCount++;
                continue;
            }

            // Find the group for this UserGroup
            const group = groups.find(g => g.id === userGroup.groupId);
            if (!group) {
                console.log(`⚠️  WARNING: UserGroup ${userGroup.id} references non-existent group: ${userGroup.groupId}`);
                errorCount++;
                continue;
            }

            // Determine the creator from the channel
            const creatorUserId = groupCreatorMap.get(group.groupId);

            // Determine role: CREATOR if this user created the group, otherwise MEMBER
            const role = (creatorUserId && userGroup.userId === creatorUserId) ? 'CREATOR' : 'MEMBER';

            console.log(`📝 UPDATING: UserGroup ${userGroup.id}`);
            console.log(`   Group: ${group.name}`);
            console.log(`   User: ${userGroup.userId.substring(0, 20)}...`);
            console.log(`   Assigning role: ${role}`);

            try {
                // Update the UserGroup entry with role
                await dynamodb.update({
                    TableName: USER_GROUP_TABLE,
                    Key: { id: userGroup.id },
                    UpdateExpression: 'SET #role = :role',
                    ExpressionAttributeNames: {
                        '#role': 'role'
                    },
                    ExpressionAttributeValues: {
                        ':role': role
                    }
                }).promise();

                console.log(`   ✅ UPDATED!\n`);
                updatedCount++;
            } catch (error) {
                console.log(`   ❌ ERROR: ${error.message}\n`);
                errorCount++;
            }
        }

        // Summary
        console.log('\n=========================================');
        console.log('SUMMARY');
        console.log('=========================================');
        console.log(`Total UserGroups:      ${userGroups.length}`);
        console.log(`Updated with role:     ${updatedCount}`);
        console.log(`Already had role:      ${alreadyHasRoleCount}`);
        console.log(`Errors:                ${errorCount}`);
        console.log('=========================================\n');

        if (updatedCount > 0) {
            console.log('✅ Success! Roles have been added to existing group memberships.');
        } else if (alreadyHasRoleCount === userGroups.length) {
            console.log('✅ All UserGroup entries already had roles. No changes needed.');
        }

    } catch (error) {
        console.error('❌ Fatal error:', error);
        throw error;
    }
}

// Run the script
addRolesToExistingGroups()
    .then(() => {
        console.log('Script completed successfully.');
        process.exit(0);
    })
    .catch(error => {
        console.error('Script failed:', error);
        process.exit(1);
    });