const https = require('https');
const crypto = require('crypto');
const AWS = require('aws-sdk');

const APPSYNC_API_ENDPOINT_URL = 'qtjqzunv4rgbtkphmdz2f2ybrq.appsync-api.us-east-2.amazonaws.com';
const APPSYNC_API_KEY = 'da2-mjgfdw4g6zfv5jgzxsytr4mupa';
const GROUP_TABLE_NAME = 'Group-wtl5wlqxxvb6lp2nenxcjpvpwq-NONE';

// Initialize DynamoDB DocumentClient (simpler than low-level DynamoDB client)
const docClient = new AWS.DynamoDB.DocumentClient({ region: 'us-east-2' });

/**
 * Lambda function to create a group with members
 *
 * Input:
 * {
 *   groupName: string,
 *   memberUserIds: string[],
 *   creatorUserId: string
 * }
 *
 * Output:
 * {
 *   groupId: string,
 *   channelId: string,
 *   memberCount: number
 * }
 */
exports.handler = async (event) => {
    console.log('CreateGroup Lambda - Event:', JSON.stringify(event, null, 2));

    try {
        // Extract input from event
        const { groupName, memberUserIds, creatorUserId } = event.arguments || event;

        // Validate input
        if (!groupName || groupName.trim().length < 3) {
            throw new Error('Group name must be at least 3 characters');
        }

        if (!memberUserIds || memberUserIds.length === 0) {
            throw new Error('At least one member is required');
        }

        if (!creatorUserId) {
            throw new Error('Creator user ID is required');
        }

        // Check if group name already exists
        console.log(`Checking if group name "${groupName}" already exists...`);
        const nameExists = await checkGroupNameExists(groupName);
        console.log(`Group name check result: ${nameExists ? 'EXISTS' : 'AVAILABLE'}`);

        if (nameExists) {
            console.log(`Rejecting duplicate group name: "${groupName}"`);
            throw new Error(`Group name "${groupName}" already exists. Please choose a different name.`);
        }

        console.log(`Group name "${groupName}" is available, proceeding with creation`);

        // Generate unique IDs
        const groupId = `group_${crypto.randomUUID()}`;
        const channelId = `channel_${crypto.randomUUID()}`;

        console.log('Generated IDs:', { groupId, channelId });

        // Step 1: Create the Group
        const group = await createGroup(groupId, groupName);
        console.log('Group created:', group);

        // IMPORTANT: Use the DynamoDB id (not the custom groupId) for relationships
        const dynamoDbGroupId = group.id;
        console.log('Using DynamoDB Group ID for relationships:', dynamoDbGroupId);

        // Step 2: Create UserGroup entries for each member (including creator)
        const allMemberIds = [...new Set([creatorUserId, ...memberUserIds])]; // Ensure creator is included and no duplicates

        // Create UserGroup with appropriate roles
        const userGroups = await Promise.all(
            allMemberIds.map(userId => {
                // Creator gets CREATOR role, all other members get MEMBER role
                const role = userId === creatorUserId ? 'CREATOR' : 'MEMBER';
                return createUserGroup(dynamoDbGroupId, userId, role);
            })
        );
        console.log(`Created ${userGroups.length} UserGroup entries (1 CREATOR, ${userGroups.length - 1} MEMBER(s))`);

        // Step 3: Create a Channel for the group chat
        // For group channels, we use the creatorId as the main user and a placeholder for receiverId
        const channel = await createChannel(
            channelId,
            groupName,
            creatorUserId,
            groupId, // Use groupId as receiverId for group channels
            true // isGroup = true
        );
        console.log('Channel created:', channel);

        // Step 4: Create UserChannel entries for all members
        const userChannels = await Promise.all(
            allMemberIds.map(userId => createUserChannel(userId, channelId))
        );
        console.log(`Created ${userChannels.length} UserChannel entries`);

        // Return success response (AppSync expects the object directly, not wrapped in HTTP format)
        return {
            success: true,
            groupId: groupId,
            channelId: channelId,
            memberCount: allMemberIds.length,
            message: `Group "${groupName}" created successfully with ${allMemberIds.length} members`,
            error: null
        };

    } catch (error) {
        console.error('Error creating group:', error);
        return {
            success: false,
            groupId: null,
            channelId: null,
            memberCount: null,
            message: null,
            error: error.message
        };
    }
};

/**
 * Create a Group in AppSync
 */
async function createGroup(groupId, name) {
    const mutation = `
        mutation CreateGroup($input: CreateGroupInput!) {
            createGroup(input: $input) {
                id
                groupId
                name
                createdAt
            }
        }
    `;

    const variables = {
        input: {
            groupId: groupId,
            name: name
        }
    };

    const result = await executeGraphQL(mutation, variables);
    return result.data.createGroup;
}

/**
 * Create a UserGroup entry (links user to group with role)
 */
async function createUserGroup(groupId, userId, role) {
    const mutation = `
        mutation CreateUserGroup($input: CreateUserGroupInput!) {
            createUserGroup(input: $input) {
                id
                userId
                groupId
                role
                createdAt
            }
        }
    `;

    const variables = {
        input: {
            groupId: groupId,
            userId: userId,
            role: role  // CREATOR, MODERATOR, or MEMBER
        }
    };

    const result = await executeGraphQL(mutation, variables);
    return result.data.createUserGroup;
}

/**
 * Create a Channel for the group
 */
async function createChannel(channelId, name, creatorId, receiverId, isGroup = true) {
    const mutation = `
        mutation CreateChannel($input: CreateChannelInput!) {
            createChannel(input: $input) {
                id
                name
                creatorId
                receiverId
                isGroup
                isPublic
                createdAt
            }
        }
    `;

    const variables = {
        input: {
            id: channelId,
            name: name,
            creatorId: creatorId,
            receiverId: receiverId,
            isGroup: isGroup,
            isPublic: false
        }
    };

    const result = await executeGraphQL(mutation, variables);
    return result.data.createChannel;
}

/**
 * Create a UserChannel entry (links user to channel)
 */
async function createUserChannel(userId, channelId) {
    const mutation = `
        mutation CreateUserChannel($input: CreateUserChannelInput!) {
            createUserChannel(input: $input) {
                id
                userId
                channelId
                createdAt
            }
        }
    `;

    const variables = {
        input: {
            userId: userId,
            channelId: channelId
        }
    };

    const result = await executeGraphQL(mutation, variables);
    return result.data.createUserChannel;
}

/**
 * Check if a group name already exists using DynamoDB scan
 */
async function checkGroupNameExists(groupName) {
    console.log(`checkGroupNameExists() called for: "${groupName}"`);

    try {
        console.log('Scanning DynamoDB Group table for existing groups...');
        console.log('Table name:', GROUP_TABLE_NAME);
        console.log('Looking for group name:', groupName);

        const params = {
            TableName: GROUP_TABLE_NAME,
            FilterExpression: '#n = :name',
            ExpressionAttributeNames: {
                '#n': 'name'
            },
            ExpressionAttributeValues: {
                ':name': groupName  // DocumentClient handles type conversion automatically
            }
            // Note: No Limit - we need to scan entire table to find all matches
        };

        console.log('Scan params:', JSON.stringify(params, null, 2));

        const result = await docClient.scan(params).promise();

        console.log('Scan complete. Count:', result.Count, 'ScannedCount:', result.ScannedCount);
        console.log('Raw scan result:', JSON.stringify(result, null, 2));
        const items = result.Items || [];

        console.log(`Found ${items.length} existing group(s) with name "${groupName}"`);
        if (items.length > 0) {
            console.log('Existing groups:', JSON.stringify(items, null, 2));
        }

        return items.length > 0;
    } catch (error) {
        console.error('Error checking group name:', error);
        console.error('Error details:', error.message, error.stack);
        // If we can't check, fail safely by assuming it exists
        // This prevents creating duplicates if the check fails
        throw new Error('Unable to verify group name availability. Please try again.');
    }
}

/**
 * Execute a GraphQL request to AppSync
 */
function executeGraphQL(query, variables) {
    return new Promise((resolve, reject) => {
        const payload = JSON.stringify({
            query: query,
            variables: variables
        });

        const options = {
            hostname: APPSYNC_API_ENDPOINT_URL,
            path: '/graphql',
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'x-api-key': APPSYNC_API_KEY,
                'Content-Length': Buffer.byteLength(payload)
            }
        };

        const req = https.request(options, (res) => {
            let data = '';

            res.on('data', (chunk) => {
                data += chunk;
            });

            res.on('end', () => {
                try {
                    const response = JSON.parse(data);

                    if (response.errors) {
                        console.error('GraphQL Errors:', JSON.stringify(response.errors, null, 2));
                        reject(new Error(`GraphQL Error: ${response.errors[0].message}`));
                    } else {
                        resolve(response);
                    }
                } catch (error) {
                    reject(new Error(`Failed to parse response: ${error.message}`));
                }
            });
        });

        req.on('error', (error) => {
            reject(error);
        });

        req.write(payload);
        req.end();
    });
}
