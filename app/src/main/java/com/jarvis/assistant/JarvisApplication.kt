package com.jarvis.assistant

import android.app.Application
import android.util.Log

class JarvisApplication : Application() {

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
    }
}