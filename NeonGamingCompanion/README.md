# Neon Gaming Companion (Android)

A native Kotlin + Jetpack Compose Android companion app with:
- Animated local initialization progress
- Optional launch of BGMI if the official app package is installed
- Standalone tap-target aim trainer with score, attempts, hit rate, and reset
- Sensitivity reference slider and FOV preview
- Preferences saved locally using Android SharedPreferences
- Optional neon UI effects setting

## Build
1. Install Android Studio (current stable) and its Android SDK Platform 35.
2. Open this folder (`NeonGamingCompanion`) in Android Studio.
3. Allow Gradle sync to complete. Use JDK 17.
4. Connect an Android device with USB debugging enabled, or start an emulator.
5. Press **Run**. To make an APK, select **Build > Build APK(s)**.

The project does not include a Gradle wrapper; Android Studio can sync it using an installed Gradle distribution. If Android Studio asks for a Gradle version, use a compatible Gradle 8.9+ distribution for Android Gradle Plugin 8.7.3.

## Notes
- This app is independent of BGMI. The launch button only asks Android to open the official installed app.
- The sensitivity and FOV values are reference/preference values; Android apps cannot silently apply them to BGMI.
- The aim trainer is an in-app mini-game and does not read game memory, inject into the game, or provide gameplay cheats.

## Build without installing Android Studio (cloud build)
1. Create a GitHub repository in your browser and upload the contents of this folder.
2. Open the repository's **Actions** tab and select **Build Android APK**.
3. Click **Run workflow** (or push to the `main` branch to trigger it automatically).
4. When the workflow finishes, open its run page and download the `NeonGamingCompanion-debug-apk` artifact.
5. Extract the downloaded artifact ZIP to get `app-debug.apk`.

This cloud workflow must run successfully before an APK exists; this project archive itself is source code, not an APK.
