package com.jarvis.assistant.utils

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.ProgressBar
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.core.content.res.ResourcesCompat
import com.jarvis.assistant.R

class OverlayManager(private val context: Context) {

    companion object {
        private const val TAG = "OverlayManager"
    }

    private var overlayView: View? = null
    private var windowManager: WindowManager? = null
    private var isShowing = false

    private lateinit var hudContainer: FrameLayout
    private lateinit var audioWaveform: ProgressBar
    private lateinit var transcriptionText: TextView
    private lateinit var listeningIndicator: ImageView

    init {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun showOverlay() {
        if (isShowing) return

        // Create the overlay view
        overlayView = createOverlayView()
        
        // Define layout parameters
        val layoutParams = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            )
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            )
        }

        try {
            windowManager?.addView(overlayView, layoutParams)
            isShowing = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideOverlay() {
        if (!isShowing) return

        try {
            overlayView?.let { view ->
                windowManager?.removeView(view)
                isShowing = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createOverlayView(): View {
        // Create the main container
        val container = FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Create HUD container
        hudContainer = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                (context.resources.getDimension(R.dimen.hud_size)).toInt(),
                (context.resources.getDimension(R.dimen.hud_size)).toInt()
            ).apply {
                gravity = Gravity.CENTER
            }
            
            // Create background for HUD
            background = createHudBackground()
        }

        // Create listening indicator (circular animation)
        listeningIndicator = ImageView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                (context.resources.getDimension(R.dimen.indicator_size)).toInt(),
                (context.resources.getDimension(R.dimen.indicator_size)).toInt()
            ).apply {
                gravity = Gravity.CENTER
            }
            
            // Set a simple indicator drawable (in real app, this could be animated)
            setImageResource(R.drawable.ic_mic_on) // Placeholder
            setColorFilter(Color.WHITE)
        }

        // Create audio waveform visualization
        audioWaveform = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
            layoutParams = FrameLayout.LayoutParams(
                (context.resources.getDimension(R.dimen.waveform_width)).toInt(),
                (context.resources.getDimension(R.dimen.waveform_height)).toInt()
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                setMargins(0, 0, 0, (context.resources.getDimension(R.dimen.waveform_bottom_margin)).toInt())
            }
            
            max = 100
            progress = 50 // Placeholder value
            progressDrawable = createWaveformDrawable()
        }

        // Create transcription text
        transcriptionText = TextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                setMargins(
                    0,
                    (context.resources.getDimension(R.dimen.transcription_top_margin)).toInt(),
                    0,
                    0
                )
            }
            
            text = "Listening..."
            textSize = 14f
            setTextColor(Color.WHITE)
            setShadowLayer(2f, 1f, 1f, Color.BLACK)
        }

        // Add all views to the HUD container
        hudContainer.addView(listeningIndicator)
        hudContainer.addView(audioWaveform)
        hudContainer.addView(transcriptionText)

        // Add HUD container to main container
        container.addView(hudContainer)

        return container
    }

    private fun createHudBackground(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor("#80000000")) // Semi-transparent black
           .setStroke(4, Color.WHITE) // White border
        }
    }

    private fun createWaveformDrawable(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.parseColor("#FF4CAF50")) // Green color for waveform
            cornerRadius = 8f
        }
    }

    fun updateTranscription(text: String) {
        transcriptionText?.post {
            transcriptionText?.text = text
        }
    }

    fun updateAudioLevel(level: Float) {
        audioWaveform?.post {
            // Normalize level to 0-100 range for progress bar
            val normalizedLevel = (level * 100).toInt().coerceIn(0, 100)
            audioWaveform?.progress = normalizedLevel
        }
    }

    fun setIsListening(isListening: Boolean) {
        listeningIndicator?.post {
            if (isListening) {
                // In a real implementation, this would show a listening animation
                listeningIndicator?.setColorFilter(Color.GREEN)
            } else {
                listeningIndicator?.setColorFilter(Color.WHITE)
            }
        }
    }

    fun isShowing(): Boolean {
        return isShowing
    }
}