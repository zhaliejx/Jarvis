package com.jarvis.assistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.jarvis.assistant.services.AlwaysListeningService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            startJarvisService()
        } else {
            // Handle denied permissions
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
    }

    private fun setupViews() {
        findViewById<android.widget.Button>(R.id.btnStartService).setOnClickListener {
            startJarvisService()
        }

        findViewById<android.widget.Button>(R.id.btnRequestPermissions).setOnClickListener {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        val permissions = mutableListOf<String>().apply {
            add(Manifest.permission.RECORD_AUDIO)
            
            // Check for overlay permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                }
            }
        }.toTypedArray()

        if (permissions.isNotEmpty()) {
            requestPermissionLauncher.launch(permissions)
        } else {
            startJarvisService()
        }
    }

    private fun startJarvisService() {
        if (hasRequiredPermissions()) {
            val intent = Intent(this, AlwaysListeningService::class.java)
            ContextCompat.startForegroundService(this, intent)
            
            // Update UI
            findViewById<android.widget.TextView>(R.id.tvStatus).text = "Service Status: Started"
        } else {
            requestPermissions()
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        val audioPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        val overlayPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }

        return audioPermission && overlayPermission
    }
}