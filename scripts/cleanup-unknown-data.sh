#!/bin/bash

# This script invokes the cleanupUnknownData Lambda function
# It will delete all groups and channels with null or "unknown" names

echo "Starting cleanup of unknown groups and channels..."
echo "WARNING: This will delete:"
echo "  - Groups with null or 'unknown' names"
echo "  - Channels with null or 'unknown' names"
echo "  - All associated UserGroup entries"
echo "  - All messages in those channels"
echo ""
read -p "Are you sure you want to continue? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo "Cleanup cancelled."
    exit 0
fi

# Get the Lambda function name from AWS Amplify
# You'll need to update this with your actual function name after deployment
FUNCTION_NAME="cleanupUnknownData-$(npx ampx sandbox --outputs | grep 'AWS_BRANCH' | cut -d':' -f2 | tr -d ' ')"

echo ""
echo "Invoking Lambda function: $FUNCTION_NAME"
echo ""

# Invoke the Lambda function
aws lambda invoke \
  --function-name "$FUNCTION_NAME" \
  --region us-east-2 \
  --log-type Tail \
  --output json \
  response.json

# Display the response
echo ""
echo "Response from Lambda:"
cat response.json | jq '.'

# Display the logs
echo ""
echo "Cleanup completed! Check response.json for details."
