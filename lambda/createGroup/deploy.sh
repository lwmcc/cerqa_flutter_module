#!/bin/bash

# Deployment script for createGroup Lambda function
# Usage: ./deploy.sh [function-name] [region]

FUNCTION_NAME="${1:-createGroup}"
REGION="${2:-us-east-2}"

echo "========================================="
echo "Deploying Lambda Function: $FUNCTION_NAME"
echo "Region: $REGION"
echo "========================================="

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo "ERROR: AWS CLI is not installed. Please install it first."
    exit 1
fi

# Create deployment package
echo "Creating deployment package..."
zip -r function.zip index.js package.json node_modules/

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to create deployment package"
    exit 1
fi

echo "Deployment package created: function.zip"

# Check if function exists
FUNCTION_EXISTS=$(aws lambda get-function --function-name $FUNCTION_NAME --region $REGION 2>&1)

if echo "$FUNCTION_EXISTS" | grep -q "ResourceNotFoundException"; then
    echo ""
    echo "Function does not exist. Please create it first using AWS Console or this command:"
    echo ""
    echo "aws lambda create-function \\"
    echo "  --function-name $FUNCTION_NAME \\"
    echo "  --runtime nodejs18.x \\"
    echo "  --role <YOUR_EXECUTION_ROLE_ARN> \\"
    echo "  --handler index.handler \\"
    echo "  --zip-file fileb://function.zip \\"
    echo "  --timeout 30 \\"
    echo "  --region $REGION"
    echo ""
    echo "OR manually create it in AWS Console and then run this script again."
    exit 1
else
    # Update existing function
    echo ""
    echo "Updating existing function..."
    aws lambda update-function-code \
        --function-name $FUNCTION_NAME \
        --zip-file fileb://function.zip \
        --region $REGION

    if [ $? -eq 0 ]; then
        echo ""
        echo "========================================="
        echo "✅ SUCCESS! Function updated successfully"
        echo "========================================="
        echo ""
        echo "Function: $FUNCTION_NAME"
        echo "Region: $REGION"
        echo ""
        echo "Test your function:"
        echo "aws lambda invoke \\"
        echo "  --function-name $FUNCTION_NAME \\"
        echo "  --payload '{\"groupName\":\"Test Group\",\"memberUserIds\":[\"user1\",\"user2\"],\"creatorUserId\":\"creator1\"}' \\"
        echo "  --region $REGION \\"
        echo "  response.json"
        echo ""
    else
        echo "ERROR: Failed to update function"
        exit 1
    fi
fi

# Clean up
rm function.zip
echo "Cleaned up deployment package"
