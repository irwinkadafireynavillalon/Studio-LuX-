package com.example.data.engine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "ACTION_PLAY" -> {
                Log.d("NotificationReceiver", "Play")
                // Implement play logic
            }
            "ACTION_PAUSE" -> {
                Log.d("NotificationReceiver", "Pause")
                // Implement pause logic
            }
        }
    }
}
