package com.jarvis.assistant.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.jarvis.assistant.MainActivity
import com.jarvis.assistant.data.JarvisDataStore
import com.jarvis.assistant.managers.BrainManager
import com.jarvis.assistant.utils.OverlayManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class AlwaysListeningService : Service() {

    companion object {
        private const val TAG = "AlwaysListeningService"
        private const val NOTIFICATION_CHANNEL_ID = "jarvis_foreground_channel"
        private const val NOTIFICATION_ID = 1001

        // Constants for audio recording
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    private val binder = LocalBinder()
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val executor = Executors.newSingleThreadExecutor()

    // Components
    private lateinit var overlayManager: OverlayManager
    private lateinit var brainManager: BrainManager
    private var isWakeWordActive = false

    override fun onCreate() {
        super.onCreate()

        // Initialize components
        overlayManager = OverlayManager(this)

        // Initialize datastore and brain manager
        val dataStore = JarvisDataStore.getInstance(this)
        brainManager = BrainManager(dataStore)

        createNotificationChannel()
        startAudioRecording()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification().build())
        return START_STICKY // Restart service if killed
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): AlwaysListeningService = this@AlwaysListeningService
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Jarvis Assistant Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Always listening for wake word"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): NotificationCompat.Builder {
        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(
                this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Jarvis Assistant Active")
            .setContentText("Listening for wake word...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setTicker("Jarvis Active")
    }

    private fun startAudioRecording() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Initialize audio recorder
                val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)

                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    CHANNEL_CONFIG,
                    AUDIO_FORMAT,
                    bufferSize * 2
                )

                audioRecord?.startRecording()
                isRecording = true

                val audioData = ShortArray(bufferSize)

                // Continuously read audio data and process for wake word
                while (isRecording) {
                    val bytesRead = audioRecord?.read(audioData, 0, bufferSize) ?: 0

                    if (bytesRead > 0) {
                        // Process audio data for wake word detection
                        // In a real implementation, this would be sent to Porcupine
                        processAudioData(audioData, bytesRead)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in audio recording: ${e.message}", e)
            }
        }
    }

    private fun processAudioData(audioData: ShortArray, bytesRead: Int) {
        // In a real implementation, this would send the audio data to Porcupine
        // for wake word detection
        // For now, we'll simulate wake word detection periodically
        if (!isWakeWordActive && Math.random() > 0.999) { // Simulate rare wake word detection
            onWakeWordDetected()
        }
    }

    private fun onWakeWordDetected() {
        Log.i(TAG, "Wake word detected!")

        isWakeWordActive = true

        // Show the overlay HUD
        overlayManager.showOverlay()
        overlayManager.updateTranscription("Listening...")
        overlayManager.setIsListening(true)

        // In a real implementation, this would start speech recognition
        // and eventually call the brain manager to process the command
        startSpeechRecognition()
    }

    private fun startSpeechRecognition() {
        // In a real implementation, this would use Android's SpeechRecognizer
        // For now, we'll simulate a command after a delay
        CoroutineScope(Dispatchers.Main).launch {
            // Simulate listening for a few seconds
            kotlinx.coroutines.delay(3000)

            // Simulate a user command
            val simulatedCommand = "What's the weather today?"
            overlayManager.updateTranscription(simulatedCommand)

            // Process the command with the brain manager
            processCommand(simulatedCommand)
        }
    }

    private fun processCommand(command: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val (response, action) = brainManager.processStructuredResponse(command)

                // Update the overlay with the response
                overlayManager.updateTranscription(response)

                // Execute any detected action
                action?.let { executeAction(it) }

                // Simulate AI speaking time
                kotlinx.coroutines.delay(2000)

                // Hide the overlay after response
                overlayManager.hideOverlay()
                isWakeWordActive = false

            } catch (e: Exception) {
                Log.e(TAG, "Error processing command: ${e.message}", e)
                overlayManager.updateTranscription("Sorry, I encountered an error.")

                // Hide the overlay after error
                CoroutineScope(Dispatchers.Main).launch {
                    kotlinx.coroutines.delay(2000)
                    overlayManager.hideOverlay()
                    isWakeWordActive = false
                }
            }
        }
    }

    private suspend fun executeAction(action: com.jarvis.assistant.managers.Action) {
        when (action) {
            is com.jarvis.assistant.managers.OpenAppAction -> {
                // In a real implementation, this would open the specified app
                Log.d(TAG, "Opening app: ${action.appName}")
            }
            is com.jarvis.assistant.managers.SearchAction -> {
                // In a real implementation, this would initiate a search
                Log.d(TAG, "Searching for: ${action.query}")
            }
            is com.jarvis.assistant.managers.NavigationAction -> {
                // In a real implementation, this would perform navigation
                Log.d(TAG, "Navigating: ${action.action}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        // Hide overlay if it's showing
        if (overlayManager.isShowing()) {
            overlayManager.hideOverlay()
        }
    }
}