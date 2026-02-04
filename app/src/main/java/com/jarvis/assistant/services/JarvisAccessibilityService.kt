package com.jarvis.assistant.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import android.util.Log
import java.util.Locale

class JarvisAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "JarvisAccessibilityService"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Accessibility service connected")
        // Initialization code here
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let { ev ->
            Log.d(TAG, "Accessibility event: ${ev.eventType}, Package: ${ev.packageName}")
            
            // Process the accessibility event based on type
            when (ev.eventType) {
                AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                    Log.d(TAG, "View clicked: ${ev.text}")
                }
                AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                    Log.d(TAG, "View focused: ${ev.text}")
                }
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    Log.d(TAG, "Window state changed: ${ev.className}")
                }
                AccessibilityEvent.TYPE_WINDOWS_CHANGED -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Log.d(TAG, "Windows changed")
                    } else {
                        Log.d(TAG, "Cannot detect windows on this version")
                    }
                }
                else -> {
                    // Handle other event types if needed
                    Log.d(TAG, "Other event type: ${ev.eventType}")
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted")
    }

    /**
     * Reads the text content of the current screen
     */
    fun readScreenContent(): String {
        val rootNode = rootInActiveWindow
        return if (rootNode != null) {
            extractTextFromNode(rootNode)
        } else {
            ""
        }
    }

    /**
     * Extracts text from an accessibility node and its children
     */
    private fun extractTextFromNode(node: AccessibilityNodeInfo): String {
        val textList = mutableListOf<String>()
        
        // Get text from the current node
        val text = node.text?.toString()
        if (!text.isNullOrEmpty()) {
            textList.add(text)
        }
        
        // Recursively get text from child nodes
        for (i in 0 until node.childCount) {
            val childNode = node.getChild(i)
            if (childNode != null) {
                textList.add(extractTextFromNode(childNode))
                childNode.recycle()
            }
        }
    
        return textList.filter { it.isNotEmpty() }.joinToString("\n")
    }

    /**
     * Performs a global action (back, home, recents)
     */
    fun doGlobalAction(action: Int): Boolean {
        return when (action) {
            GLOBAL_ACTION_BACK,
            GLOBAL_ACTION_HOME,
            GLOBAL_ACTION_RECENTS,
            GLOBAL_ACTION_NOTIFICATIONS,
            GLOBAL_ACTION_QUICK_SETTINGS,
            GLOBAL_ACTION_LOCK_SCREEN,
            GLOBAL_ACTION_TAKE_SCREENSHOT -> {
                super.performGlobalAction(action)
            }
            else -> false
        }
    }

    /**
     * Finds and clicks a button based on its text content
     */
    fun clickOnButtonByText(text: String): Boolean {
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            val node = findNodeByText(rootNode, text)
            if (node != null && node.isClickable) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                node.recycle()
                return true
            }
            node?.recycle()
        }
        return false
    }

    /**
     * Finds a node by its text content
     */
    private fun findNodeByText(node: AccessibilityNodeInfo, text: String): AccessibilityNodeInfo? {
        // Check current node
        if (node.text?.toString()?.lowercase(Locale.getDefault())
                ?.contains(text.lowercase(Locale.getDefault())) == true) {
            return node
        }

        // Check child nodes
        for (i in 0 until node.childCount) {
            val childNode = node.getChild(i)
            if (childNode != null) {
                val result = findNodeByText(childNode, text)
                if (result != null) {
                    childNode.recycle()
                    return result
                }
                childNode.recycle()
            }
        }
        
        return null
    }

    /**
     * Finds and clicks a button based on its resource ID
     */
    fun clickOnButtonById(resourceId: String): Boolean {
        val rootNode = rootInActiveWindow
        if (rootNode != null) {
            val nodes = rootNode.findAccessibilityNodeInfosByViewId(resourceId)
            if (nodes.isNotEmpty()) {
                val node = nodes[0]
                if (node.isClickable) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    return true
                }
            }
        }
        return false
    }

    /**
     * Types text into a focused input field
     */
    fun typeText(text: String): Boolean {
        val bundle = Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
        }
        
        val focusedNode = rootInActiveWindow?.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
        return if (focusedNode != null) {
            focusedNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle)
        } else {
            false
        }
    }

    /**
     * Gets information about the current window
     */
    fun getCurrentWindowInfo(): String {
        return "Window info from accessibility service"
    }

    /**
     * Finds elements by their class name
     */
    fun findElementsByClassName(className: String): List<AccessibilityNodeInfo> {
        val rootNode = rootInActiveWindow
        val results = mutableListOf<AccessibilityNodeInfo>()
        
        if (rootNode != null) {
            findNodesByClass(rootNode, className, results)
        }
        
        return results
    }

    private fun findNodesByClass(
        node: AccessibilityNodeInfo,
        className: String,
        results: MutableList<AccessibilityNodeInfo>
    ) {
        if (node.className?.toString() == className) {
            results.add(node)
        }
        
        for (i in 0 until node.childCount) {
            val childNode = node.getChild(i)
            if (childNode != null) {
                findNodesByClass(childNode, className, results)
                childNode.recycle()
            }
        }
    }
}