#!/bin/bash

echo "========================================="
echo "Testing FCM Token Storage"
echo "========================================="
echo ""

# Check which device is available
DEVICE=$(~/Library/Android/sdk/platform-tools/adb devices | grep -v "List" | grep "device" | awk '{print $1}' | head -1)

if [ -z "$DEVICE" ]; then
    echo "❌ No Android device/emulator found"
    echo "Please start an emulator first"
    exit 1
fi

echo "📱 Using device: $DEVICE"
echo ""

# Clear logcat
echo "🧹 Clearing logs..."
~/Library/Android/sdk/platform-tools/adb -s $DEVICE logcat -c

# Build and install app
echo "🔨 Building and installing app..."
./gradlew :app:installDebug

if [ $? -ne 0 ]; then
    echo "❌ Build failed"
    exit 1
fi

echo ""
echo "🚀 Launching app..."
~/Library/Android/sdk/platform-tools/adb -s $DEVICE shell "am start -n com.mccartycarclub/.MainActivity"

echo ""
echo "⏳ Waiting 5 seconds for app to initialize..."
sleep 5

echo ""
echo "========================================="
echo "📋 FCM Token Logs:"
echo "========================================="
~/Library/Android/sdk/platform-tools/adb -s $DEVICE logcat -d | grep -E "MainViewModel.*[Ff]cm|NotificationRepository|FcmToken|storeFcmToken"

echo ""
echo "========================================="
echo "💡 What to look for:"
echo "========================================="
echo "✓ 'MainViewModel: Getting FCM token...'"
echo "✓ 'MainViewModel: Got FCM token: <token>'"
echo "✓ 'MainViewModel: Platform: android'"
echo "✓ 'NotificationRepository: Storing FCM token for platform: android'"
echo "✓ 'NotificationRepository: FCM token stored successfully: true'"
echo ""
echo "If you see errors, check:"
echo "1. Apollo client configuration"
echo "2. AppSync endpoint in amplify_outputs.json"
echo "3. User authentication token"
echo ""
