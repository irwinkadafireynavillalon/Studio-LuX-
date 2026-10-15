package com.example.data.util

import android.content.Context
import com.example.data.db.MessageDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExportManager(private val context: Context, private val messageDao: MessageDao) {

    suspend fun exportData(): String? = withContext(Dispatchers.IO) {
        try {
            val messages = messageDao.getAllMessagesRaw()
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val filename = "lux_export_$timestamp.txt"
            
            val content = StringBuilder()
            content.append("--- Studio LuX Export ---\n")
            content.append("Date: $timestamp\n\n")
            content.append("--- Chat History ---\n")
            messages.forEach { msg ->
                content.append("[${msg.sender}] ${msg.text}\n")
            }
            
            val file = File(context.getExternalFilesDir(null), filename)
            file.writeText(content.toString())
            
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
