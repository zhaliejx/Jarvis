package com.jarvis.assistant.managers

import android.util.Log
import com.jarvis.assistant.data.UserContext
import com.jarvis.assistant.data.UserPreferencesDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class BrainManager(
    private val userPreferencesDao: UserPreferencesDao
) {

    companion object {
        private const val TAG = "BrainManager"
        private const val POLLINATIONS_API_URL = "https://pollinations.ai/p/"
        private const val MISTRAL_MODEL = "mistral-large"
    }

    private val httpClient = OkHttpClient()

    suspend fun processQuery(query: String): String {
        // Get user context from database
        val userContext = getUserContext()
        
        // Construct the prompt with user context
        val fullPrompt = buildPrompt(query, userContext)
        
        // Call the Pollinations API
        return callPollinationsAPI(fullPrompt)
    }

    private fun buildPrompt(userQuery: String, userContext: UserContext): String {
        return """
            You are Jarvis, a helpful AI assistant. 
            
            Current user context:
            - Location: ${userContext.location}
            - Time: ${userContext.currentTime}
            - Activity: ${userContext.currentActivity}
            - Preferences: ${userContext.preferences}
            
            User query: $userQuery
            
            Respond concisely and helpfully. If the user wants to perform an action (like opening an app or searching), 
            provide a structured response that indicates the action to be taken.
        """.trimIndent()
    }

    private suspend fun callPollinationsAPI(prompt: String): String {
        return try {
            val requestBody = JSONObject().apply {
                put("messages", listOf(
                    mapOf("role" to "user", "content" to prompt)
                ))
                put("model", MISTRAL_MODEL)
                put("stream", false)
            }.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(POLLINATIONS_API_URL)
                .post(requestBody)
                .build()

            val response = httpClient.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                parseResponse(responseBody ?: "")
            } else {
                Log.e(TAG, "API request failed: ${response.code} - ${response.message}")
                "Sorry, I encountered an error processing your request."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error calling Pollinations API", e)
            "Sorry, I encountered an error processing your request."
        }
    }

    private fun parseResponse(responseBody: String): String {
        return try {
            val jsonObject = JSONObject(responseBody)
            val choices = jsonObject.getJSONArray("choices")
            if (choices.length() > 0) {
                val message = choices.getJSONObject(0).getJSONObject("message")
                message.getString("content")
            } else {
                "Sorry, I couldn't understand the response."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing response", e)
            "Sorry, I couldn't understand the response."
        }
    }

    private suspend fun getUserContext(): UserContext {
        return try {
            // Get user preferences from database
            val preferences = userPreferencesDao.getAllPreferences()
            
            // Build context object
            UserContext(
                location = preferences.firstOrNull { it.key == "location" }?.value ?: "unknown",
                currentTime = System.currentTimeMillis().toString(),
                currentActivity = preferences.firstOrNull { it.key == "current_activity" }?.value ?: "unknown",
                preferences = preferences.joinToString(", ") { "${it.key}:${it.value}" }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user context", e)
            UserContext(
                location = "unknown",
                currentTime = System.currentTimeMillis().toString(),
                currentActivity = "unknown",
                preferences = ""
            )
        }
    }

    // Method to handle structured responses for app actions
    suspend fun processStructuredResponse(query: String): Pair<String, Action?> {
        val aiResponse = processQuery(query)
        
        // Simple parsing to detect if the response contains an action
        val action = parseAction(aiResponse)
        
        return Pair(aiResponse, action)
    }

    private fun parseAction(response: String): Action? {
        val lowerResponse = response.lowercase()
        
        // Detect if the user wants to open an app
        if (lowerResponse.contains("open") || lowerResponse.contains("launch")) {
            // Look for app names in the response
            val apps = listOf("youtube", "gmail", "maps", "camera", "settings", "calculator", "calendar")
            for (app in apps) {
                if (lowerResponse.contains(app)) {
                    return OpenAppAction(app)
                }
            }
        }
        
        // Detect if the user wants to search
        if (lowerResponse.contains("search") || lowerResponse.contains("find")) {
            // Extract search query
            val searchMatch = Regex("for\\s+(.+?)(?:\\s+on|\\s+in|$)").find(lowerResponse)
            if (searchMatch != null) {
                val searchTerm = searchMatch.groupValues[1]
                return SearchAction(searchTerm.trim())
            }
        }
        
        // Detect navigation commands
        if (lowerResponse.contains("go back") || lowerResponse.contains("back")) {
            return NavigationAction("back")
        }
        
        if (lowerResponse.contains("go home") || lowerResponse.contains("home")) {
            return NavigationAction("home")
        }
        
        if (lowerResponse.contains("recent") || lowerResponse.contains("switch")) {
            return NavigationAction("recent")
        }
        
        return null
    }
}

// Data classes for structured responses
sealed class Action
data class OpenAppAction(val appName: String) : Action()
data class SearchAction(val query: String) : Action()
data class NavigationAction(val action: String) : Action()