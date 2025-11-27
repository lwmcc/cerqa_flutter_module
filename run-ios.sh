#!/bin/bash
# Script to build and run iOS app from Android Studio

echo "Building and running iOS app..."

# Navigate to iOS project directory
cd "$(dirname "$0")/cerqaiOS"

# Build and run in simulator
xcodebuild -project cerqaiOS.xcodeproj \
    -scheme cerqaiOS \
    -configuration Debug \
    -destination 'platform=iOS Simulator,name=iPhone 15' \
    -derivedDataPath build \
    clean build | xcpretty || cat

# Get the app path
APP_PATH="build/Build/Products/Debug-iphonesimulator/cerqaiOS.app"

if [ -d "$APP_PATH" ]; then
    echo "Launching app in simulator..."
    # Boot simulator if not already running
    xcrun simctl boot "iPhone 15" 2>/dev/null || true
    # Open simulator
    open -a Simulator
    # Install and launch app
    xcrun simctl install booted "$APP_PATH"
    xcrun simctl launch booted com.mccartycarclub.cerqaiOS
    echo "✅ iOS app launched successfully!"
else
    echo "❌ Build failed - app not found"
    exit 1
fi
