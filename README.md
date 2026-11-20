# SakayBudget for Android

A small Android app that wraps the SakayBudget web page (`app/src/main/assets/index.html`) in a WebView.
Your budget, trips and settings are stored on the phone and work offline (fonts are bundled, so no connection is needed).

Package: `com.sakaybudget.app`, minimum Android 8.0 (API 26), target API 34.

## Option A: Android Studio
1. Install Android Studio, choose **Open**, and select this folder.
2. Let Gradle sync finish.
3. Choose **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. The file is at `app/build/outputs/apk/debug/app-debug.apk`. Copy it to your phone and open it
   (allow "Install unknown apps" for your file manager when asked).

## Option B: GitHub, no installs
1. Create a new GitHub repository and upload everything in this folder, including the `.github` folder.
2. Open the **Actions** tab, pick **Build APK**, and press **Run workflow**.
3. When it finishes, download **SakayBudget-debug-apk** from the run page and unzip it.

## Updating the app
Replace `app/src/main/assets/index.html` with the new page, raise `versionCode` in `app/build.gradle`, and build again.

## Notes
- This is a debug-signed APK, fine for your own phone. For the Play Store you need a release build signed with your own key.
- The Android back button moves between tabs, and the status bar follows the theme you pick in Settings.
