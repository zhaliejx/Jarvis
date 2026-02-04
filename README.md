# Jarvis Assistant - Android System-Level AI Assistant

This is a personal "Jarvis" application for Android that functions as a system-level assistant, capable of listening and responding even when the phone is locked, the screen is off, or you are using other apps.

## Project Structure

```
jarvis-assistant/
├── app/
│   ├── src/main/
│   │   ├── java/com/jarvis/assistant/
│   │   │   ├── MainActivity.kt
│   │   │   ├── JarvisApplication.kt
│   │   │   ├── data/
│   │   │   │   ├── JarvisDatabase.kt
│   │   │   │   └── UserContext.kt
│   │   │   ├── managers/
│   │   │   │   └── BrainManager.kt
│   │   │   ├── services/
│   │   │   │   ├── AlwaysListeningService.kt
│   │   │   │   └── JarvisAccessibilityService.kt
│   │   │   ├── utils/
│   │   │   │   └── OverlayManager.kt
│   │   │   └── receivers/
│   │   │       └── BootCompletedReceiver.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   ├── styles.xml
│   │   │   │   └── dimens.xml
│   │   │   ├── drawable/
│   │   │   │   └── ic_mic_on.xml
│   │   │   ├── mipmap-anydpi-v26/
│   │   │   │   └── ic_launcher.xml
│   │   │   └── xml/
│   │   │       └── accessibility_service_config.xml
│   │   └── AndroidManifest.xml
├── .github/workflows/
│   ├── android.yml
│   ├── release.yml
│   ├── instrumented-tests.yml
│   └── code-quality.yml
├── gradle/
│   └── wrapper/
├── build.gradle
├── settings.gradle
└── gradlew
```

## GitHub Actions Workflows

The project includes several GitHub Actions workflows:

1. **Build and Test** (`android.yml`) - Builds and tests the application
2. **Release** (`release.yml`) - Creates releases when tags are pushed
3. **Instrumented Tests** (`instrumented-tests.yml`) - Runs tests on multiple API levels
4. **Code Quality** (`code-quality.yml`) - Performs code quality checks

## Setting Up the Project

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd jarvis-assistant
   ```

2. The project is ready for GitHub Actions. The workflows will automatically:
   - Set up the Android SDK
   - Build the application
   - Run tests
   - Create releases when tags are pushed

## Building the APK

The APKs are automatically built by GitHub Actions and available as artifacts.

For local development:
```bash
./gradlew assembleDebug
```

## Features

- **System-Level Assistant**: Works when screen is off, locked, or other apps are in use
- **Wake Word Detection**: Background listening with foreground service
- **App Control**: Accessibility service to control other apps
- **HUD Interface**: Overlay display with audio visualization
- **AI Integration**: API connectivity for intelligent responses
- **Local Storage**: Room database for user context and preferences

## Permissions Required

- RECORD_AUDIO - For wake word detection
- FOREGROUND_SERVICE - To keep service alive
- FOREGROUND_SERVICE_MICROPHONE - Android 14+ requirement
- SYSTEM_ALERT_WINDOW - For overlay HUD
- BIND_ACCESSIBILITY_SERVICE - For accessibility features
- RECEIVE_BOOT_COMPLETED - To auto-start on boot

## Post-Installation Setup

After installing the app, you need to enable special permissions:

1. **Accessibility Service**: Go to Settings → Accessibility → Find "Jarvis Accessibility Service" and enable it
2. **Overlay Permission**: Go to Settings → Apps → Special Access → Draw over other apps → Find "Jarvis Assistant" and enable it
3. **Microphone Permission**: Go to Settings → Apps → Jarvis Assistant → Permissions → Enable Microphone permission

## GitHub Actions Configuration

The project is configured with the following GitHub Actions:

### Build Workflow (`.github/workflows/android.yml`)
- Builds the Android application on every push to main/develop branches
- Runs unit tests
- Creates debug and release APKs
- Uploads artifacts for download

### Release Workflow (`.github/workflows/release.yml`)
- Creates a GitHub release when a tag is pushed (e.g., `v1.0.0`)
- Builds and signs the release APK
- Uploads the APK to the GitHub release

### Instrumented Tests (`.github/workflows/instrumented-tests.yml`)
- Runs instrumented tests on multiple Android API levels
- Executes weekly to ensure compatibility

### Code Quality (`.github/workflows/code-quality.yml`)
- Runs code quality checks on every push and PR
- Includes ktlint for Kotlin code formatting
- Includes detekt for static code analysis
- Includes dependency vulnerability scanning

## Secrets Required for Releases

For the release workflow to work, configure these secrets in your GitHub repository:

- `SIGNING_KEY`: Base64-encoded keystore file
- `SIGNING_PASSWORD`: Keystore password
- `KEY_ALIAS`: Key alias
- `KEY_PASSWORD`: Key password