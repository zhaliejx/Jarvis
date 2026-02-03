# Jarvis Assistant - Android System-Level AI Assistant

This is a personal "Jarvis" application for Android that functions as a system-level assistant, capable of listening and responding even when the phone is locked, the screen is off, or you are using other apps.

## Installation Instructions

1. Download the APK file to your device
2. Open the downloaded APK file to begin installation
3. If prompted about installing from unknown sources, allow it for your file manager app
4. Complete the installation

## Post-Installation Setup

After installing the app, you need to enable special permissions:

### 1. Accessibility Service
- Go to Settings → Accessibility
- Find "Jarvis Accessibility Service" and enable it
- This allows the app to control other apps and read screen content

### 2. Overlay Permission (Draw over other apps)
- Go to Settings → Apps → Special Access → Draw over other apps
- Find "Jarvis Assistant" and enable it
- This allows the HUD interface to appear over other apps

### 3. Microphone Permission
- Go to Settings → Apps → Jarvis Assistant → Permissions
- Enable Microphone permission
- This allows the app to listen for wake words

## Features

### 1. Background Wake Word Detection
- The app runs as a foreground service with persistent notification
- Constantly listens for the wake word "Jarvis"
- Works even when screen is off or locked

### 2. Accessibility Control
- Reads text from the currently active screen
- Performs global actions (Back, Home, Recents)
- Clicks specific buttons in other apps based on text content

### 3. Overlay Interface (HUD)
- Circular, futuristic HUD appears when wake word is detected
- Shows audio waveform and text transcription
- Automatically dismisses after AI response

### 4. AI Integration
- Connects to AI API for intelligent responses
- Maintains user context and preferences locally
- Can perform actions based on voice commands

## Usage

1. Say "Jarvis" to activate the assistant
2. Give a command (e.g., "What's the weather?", "Open YouTube")
3. The HUD will appear showing transcription
4. The AI will process your request and respond
5. The HUD will automatically dismiss when finished

## Important Notes

- This app is designed for personal use
- For best performance, disable battery optimization for the app
- The accessibility service must remain enabled for full functionality
- Wake word detection uses offline processing for privacy

## Troubleshooting

If the app stops working:
1. Check that accessibility service is still enabled
2. Verify overlay permission is still granted
3. Restart the app if needed

For any issues, check that all required permissions are still granted in Settings.