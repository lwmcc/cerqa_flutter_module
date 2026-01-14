import type { Handler } from 'aws-lambda';
import { generateClient } from 'aws-amplify/data';
import type { Schema } from '../../data/resource';

const client = generateClient<Schema>();

export const handler: Handler = async (event) => {
  console.log('Starting cleanup of unknown groups and channels...');

  const results = {
    groupsDeleted: [] as string[],
    channelsDeleted: [] as string[],
    userGroupsDeleted: [] as string[],
    errors: [] as string[],
  };

  try {
    // 1. Find and delete groups with null or "unknown" names
    console.log('Fetching all groups...');
    const { data: groups } = await client.models.Group.list({
      limit: 1000
    });

    if (groups) {
      for (const group of groups) {
        const groupName = group.name?.toLowerCase() || '';

        // Check if group name is null, empty, or contains "unknown"
        if (!group.name || groupName.includes('unknown') || groupName.trim() === '') {
          console.log(`Deleting group: ${group.id} (name: ${group.name})`);

          try {
            // First, delete all UserGroup entries for this group
            const { data: userGroups } = await client.models.UserGroup.list({
              filter: { groupId: { eq: group.id } },
              limit: 1000
            });

            if (userGroups) {
              for (const userGroup of userGroups) {
                await client.models.UserGroup.delete({ id: userGroup.id });
                results.userGroupsDeleted.push(userGroup.id);
                console.log(`  Deleted UserGroup: ${userGroup.id}`);
              }
            }

            // Then delete the group itself
            await client.models.Group.delete({ id: group.id });
            results.groupsDeleted.push(group.id);
            console.log(`  Successfully deleted group: ${group.id}`);
          } catch (error) {
            const errorMsg = `Failed to delete group ${group.id}: ${error}`;
            console.error(errorMsg);
            results.errors.push(errorMsg);
          }
        }
      }
    }

    // 2. Find and delete channels with null names or "unknown"
    console.log('Fetching all channels...');
    const { data: channels } = await client.models.Channel.list({
      limit: 1000
    });

    if (channels) {
      for (const channel of channels) {
        const channelName = channel.name?.toLowerCase() || '';

        // Check if channel name is null, empty, or contains "unknown"
        // Also check for orphaned group channels
        if (!channel.name || channelName.includes('unknown') || channelName.trim() === '') {
          console.log(`Deleting channel: ${channel.id} (name: ${channel.name})`);

          try {
            // Delete all messages in the channel first
            const { data: messages } = await client.models.Message.list({
              filter: { channelId: { eq: channel.id } },
              limit: 1000
            });

            if (messages) {
              for (const message of messages) {
                await client.models.Message.delete({ id: message.id });
                console.log(`  Deleted message: ${message.id}`);
              }
            }

            // Then delete the channel
            await client.models.Channel.delete({ id: channel.id });
            results.channelsDeleted.push(channel.id);
            console.log(`  Successfully deleted channel: ${channel.id}`);
          } catch (error) {
            const errorMsg = `Failed to delete channel ${channel.id}: ${error}`;
            console.error(errorMsg);
            results.errors.push(errorMsg);
          }
        }
      }
    }

    console.log('Cleanup completed!');
    console.log(`Groups deleted: ${results.groupsDeleted.length}`);
    console.log(`Channels deleted: ${results.channelsDeleted.length}`);
    console.log(`UserGroups deleted: ${results.userGroupsDeleted.length}`);
    console.log(`Errors: ${results.errors.length}`);

    return {
      statusCode: 200,
      body: JSON.stringify({
        message: 'Cleanup completed successfully',
        results
      })
    };
  } catch (error) {
    console.error('Fatal error during cleanup:', error);
    return {
      statusCode: 500,
      body: JSON.stringify({
        message: 'Cleanup failed',
        error: String(error),
        results
      })
    };
  }
};
