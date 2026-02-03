# Jarvis Assistant - Android System-Level AI Assistant

This is a personal "Jarvis" application for Android that functions as a system-level assistant, capable of listening and responding even when the phone is locked, the screen is off, or you are using other apps.

## Features

### 1. Background Wake Word Detection
- Implements a Foreground Service with persistent notification
- Uses streaming audio recorder to feed data into a wake word engine
- Designed for integration with Picovoice Porcupine for offline wake word detection
- Automatically starts on device boot

### 2. Accessibility Service (The "Iron Man" Control)
- Reads text from the currently active screen
- Performs global actions (Back, Home, Recents)
- Clicks specific buttons in other apps based on text content
- Enables control of other applications through voice commands

### 3. Overlay Interface (HUD)
- Draws over other apps with SYSTEM_ALERT_WINDOW permission
- Displays a circular, futuristic HUD when wake word is triggered
- Shows audio waveform and text transcription
- Automatically dismisses after AI response

### 4. AI Integration
- Connects to Pollinations AI API for Mistral Large model
- Implements local Room database for user preferences and context
- Injects user context into prompts (location, activity, preferences)

## Architecture

The application follows MVVM pattern with heavy emphasis on Foreground Services:

- `AlwaysListeningService.kt` - Handles wake word detection in background
- `JarvisAccessibilityService.kt` - Controls other apps and reads screen content
- `OverlayManager.kt` - Manages the HUD interface
- `BrainManager.kt` - Handles AI API calls and command processing
- `MainActivity.kt` - Requests permissions and starts services
- Room database - Stores user context and preferences

## Permissions Required

- RECORD_AUDIO - For wake word detection
- FOREGROUND_SERVICE - To keep service alive
- FOREGROUND_SERVICE_MICROPHONE - Android 14+ requirement
- SYSTEM_ALERT_WINDOW - For overlay HUD
- BIND_ACCESSIBILITY_SERVICE - For accessibility features
- RECEIVE_BOOT_COMPLETED - To auto-start on boot

## Setup Instructions

1. Clone the repository
2. Add Picovoice Porcupine SDK dependency for wake word detection
3. Configure your Pollinations API endpoint
4. Request all required permissions on first launch
5. Enable the accessibility service in device settings

## Important Notes

- This is designed for personal use
- Battery optimization should be disabled for the app
- The accessibility service must be enabled manually in Settings > Accessibility
- Wake word detection requires the Picovoice Porcupine SDK (not included due to licensing)

## Testing

The application includes simulation for wake word detection and command processing. In a real implementation, you would integrate with Picovoice Porcupine for actual wake word detection.