#!/bin/bash
# Script to build and run iOS app from Android Studio

echo "Building and running iOS app..."

# Navigate to iOS project directory
cd "$(dirname "$0")/cerqaiOS"

# Build and run in simulator
xcodebuild -project cerqaiOS.xcodeproj \
    -scheme cerqaiOS \
    -configuration Debug \
    -destination 'platform=iOS Simulator,id=2C600CC4-3651-4DCC-A3FC-D39C5F0CB9DB' \
    clean build | xcpretty || cat

# Get the app path (using default derived data location)
DERIVED_DATA=$(xcodebuild -project cerqaiOS.xcodeproj -scheme cerqaiOS -showBuildSettings | grep -m 1 "BUILD_DIR" | grep -oE "/.*")
APP_PATH="${DERIVED_DATA}/Debug-iphonesimulator/cerqaiOS.app"

if [ -d "$APP_PATH" ]; then
    echo "Launching app in simulator..."
    # Boot simulator if not already running (using device ID)
    xcrun simctl boot "2C600CC4-3651-4DCC-A3FC-D39C5F0CB9DB" 2>/dev/null || true
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
