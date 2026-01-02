#!/bin/bash
# Sync Gradle for Android only (skip iOS tasks)
./gradlew :app:assemble -x podInstallSyntheticIos
