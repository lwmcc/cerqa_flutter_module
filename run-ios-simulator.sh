#!/bin/bash

# Build the shared framework first
echo "Building Shared framework..."
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# Build and run the iOS app in simulator
echo "Building and running iOS app..."
cd cerqaiOS
xcodebuild -project cerqaiOS.xcodeproj \
  -scheme cerqaiOS \
  -configuration Debug \
  -destination 'platform=iOS Simulator,name=iPhone 16 Pro' \
  -derivedDataPath build

# Install and launch in simulator
echo "Installing in simulator..."
xcrun simctl boot "iPhone 16 Pro" 2>/dev/null || true
xcrun simctl install booted build/Build/Products/Debug-iphonesimulator/cerqaiOS.app
xcrun simctl launch booted com.cerqa.carclub.ios

echo "✅ iOS app launched in simulator!"
