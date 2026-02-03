package com.jarvis.assistant.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jarvis.assistant.services.AlwaysListeningService

class BootCompletedReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "BootCompletedReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            
            Log.d(TAG, "Boot completed received, starting service")
            
            // Start the always-listening service
            val serviceIntent = Intent(context, AlwaysListeningService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}