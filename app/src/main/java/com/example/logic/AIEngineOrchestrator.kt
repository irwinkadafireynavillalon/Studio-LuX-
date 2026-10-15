package com.example.logic

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.logic.AiEngineState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AIEngineOrchestrator(private val context: Context) {
    private val _engineState = MutableStateFlow(AiEngineState.CLOUD)
    val engineState: StateFlow<AiEngineState> = _engineState

    init {
        checkConnectivity()
    }

    private fun checkConnectivity() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        
        // Simplified logic: If WiFi/Data, prefer Cloud, otherwise local
        _engineState.value = if (capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))) {
            AiEngineState.CLOUD
        } else {
            AiEngineState.LOCAL
        }
    }

    fun getAiResponse(prompt: String): String {
        return if (_engineState.value == AiEngineState.CLOUD) {
            // Call Gemini API (using placeholder logic)
            "Cloud Response for: $prompt"
        } else {
            // Call Localhost (127.0.0.1:4250)
            "Localhost Response for: $prompt"
        }
    }
}
