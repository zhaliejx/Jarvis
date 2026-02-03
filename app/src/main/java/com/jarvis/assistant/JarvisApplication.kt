package com.jarvis.assistant

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import com.jarvis.assistant.data.JarvisDatabase

class JarvisApplication : Application(), Configuration.Provider {

    companion object {
        private const val TAG = "JarvisApplication"
        @JvmStatic
        lateinit var instance: JarvisApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        Log.d(TAG, "Jarvis Application created")
        
        // Initialize database
        // The database will be initialized when first accessed
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }
}