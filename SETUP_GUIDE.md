# Setup Guide for XO Game

## Quick Start

### Step 1: Firebase Project Setup

1. **Go to Firebase Console**
   - Visit https://console.firebase.google.com/
   - Click "Add project" or select existing project

2. **Add Android App**
   - Click "Add app" > Select Android
   - Package name: `com.mustafa.xogame`
   - App nickname: XO Game (optional)
   - Click "Register app"

3. **Download Configuration File**
   - Download `google-services.json`
   - Replace `/app/google-services.json` with your downloaded file

### Step 2: Enable Firebase Services

1. **Enable Authentication**
   - Go to Authentication > Sign-in method
   - Enable "Google" provider
   - Save changes
   - Copy the "Web client ID" (you'll need this in Step 3)

2. **Create Realtime Database**
   - Go to Realtime Database
   - Click "Create Database"
   - Start in **test mode** for now (we'll add security rules later)
   - Choose database location (preferably closest to your users)

3. **Create Storage Bucket**
   - Go to Storage
   - Click "Get started"
   - Start in **test mode** for now (we'll add security rules later)

### Step 3: Configure the App

1. **Update Web Client ID**
   - Open `app/src/main/res/values/strings.xml`
   - Find: `<string name="default_web_client_id">YOUR_WEB_CLIENT_ID_HERE</string>`
   - Replace `YOUR_WEB_CLIENT_ID_HERE` with the Web client ID you copied in Step 2

### Step 4: Apply Security Rules (IMPORTANT!)

1. **Database Security Rules**
   - Go to Firebase Console > Realtime Database > Rules
   - Copy the rules from `FIREBASE_SECURITY.md` (Database Rules section)
   - Click "Publish"

2. **Storage Security Rules**
   - Go to Firebase Console > Storage > Rules
   - Copy the rules from `FIREBASE_SECURITY.md` (Storage Rules section)
   - Click "Publish"

### Step 5: Configure Google Sign-In (Android)

1. **Get SHA-1 Fingerprint**
   
   For debug builds:
   ```bash
   cd android
   ./gradlew signingReport
   ```
   
   Or using keytool:
   ```bash
   keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   ```

2. **Add SHA-1 to Firebase**
   - Go to Firebase Console > Project Settings
   - Scroll to "Your apps" section
   - Click on your Android app
   - Click "Add fingerprint"
   - Paste the SHA-1 fingerprint
   - Click "Save"

3. **For Release Builds**
   - Generate your release keystore (if not already done)
   - Get SHA-1 from release keystore
   - Add to Firebase (same as above)

### Step 6: Build and Run

1. **Open in Android Studio**
   ```bash
   # Open the project folder in Android Studio
   ```

2. **Sync Gradle**
   - File > Sync Project with Gradle Files
   - Wait for dependencies to download

3. **Run the App**
   - Connect Android device or start emulator
   - Click Run (green play button)
   - Select device and click OK

## Common Issues and Solutions

### Issue 1: Google Sign-In Fails
**Solution:**
- Verify SHA-1 fingerprint is added to Firebase
- Check Web client ID in strings.xml is correct
- Ensure Google Sign-In is enabled in Firebase Console

### Issue 2: Build Fails with "google-services.json not found"
**Solution:**
- Ensure `google-services.json` is in `/app/` directory
- Verify package name in the file matches `com.mustafa.xogame`

### Issue 3: Database/Storage Permission Denied
**Solution:**
- Apply security rules from `FIREBASE_SECURITY.md`
- Check if user is authenticated before accessing Firebase
- Verify rules are published in Firebase Console

### Issue 4: Profile Image Upload Fails
**Solution:**
- Check Storage security rules are applied
- Ensure image is less than 5MB
- Verify user is signed in

### Issue 5: Room Creation/Join Fails
**Solution:**
- Verify Database security rules are applied
- Check room name is alphanumeric (1-20 characters)
- Ensure user is signed in

## Testing

### Test Offline Mode
1. Launch app
2. Click "Offline Mode"
3. Select "Player vs Player" or "Player vs AI"
4. Play a game

### Test Online Mode
1. Launch app on two devices
2. Sign in with Google on both devices
3. Set up profiles on both devices
4. Device 1: Click "Online Mode" > "Create Room" > Enter room name
5. Device 2: Click "Online Mode" > "Join Room" > Enter same room name
6. Play game in real-time

## Build for Release

1. **Create Release Keystore**
   ```bash
   keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
   ```

2. **Configure Signing in build.gradle**
   Add to `app/build.gradle`:
   ```gradle
   android {
       signingConfigs {
           release {
               storeFile file('my-release-key.jks')
               storePassword 'your-store-password'
               keyAlias 'my-key-alias'
               keyPassword 'your-key-password'
           }
       }
       buildTypes {
           release {
               signingConfig signingConfigs.release
               minifyEnabled true
               proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
           }
       }
   }
   ```

3. **Build Release APK**
   ```bash
   ./gradlew assembleRelease
   ```
   
   APK will be in: `app/build/outputs/apk/release/`

## Production Checklist

- [ ] Replace google-services.json with production config
- [ ] Apply Firebase security rules (Database and Storage)
- [ ] Add release SHA-1 to Firebase
- [ ] Test all features (offline, online, sign-in, profile)
- [ ] Enable Firebase App Check (recommended)
- [ ] Set up Firebase Analytics (optional)
- [ ] Configure ProGuard rules if needed
- [ ] Test on multiple devices and Android versions
- [ ] Prepare app signing key and keep it secure
- [ ] Review and accept Google Play policies

## Support

For Firebase issues:
- Firebase Documentation: https://firebase.google.com/docs
- Firebase Support: https://firebase.google.com/support

For Android development:
- Android Documentation: https://developer.android.com/docs
- Android Studio: https://developer.android.com/studio

## Developer

**مصطفى عايد**  
Version 1.0
