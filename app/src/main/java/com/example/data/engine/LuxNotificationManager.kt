package com.example.data.engine

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity

class LuxNotificationManager(
    private val context: Context,
    var soundService: com.example.data.repository.SoundHapticService? = null
) {

    companion object {
        const val CHANNEL_BUILD = "lux_channel_build"
        const val CHANNEL_MODELS = "lux_channel_models"
        const val CHANNEL_SYSTEM = "lux_channel_system"

        const val NOTIF_ID_BUILD = 1001
        const val NOTIF_ID_MODEL = 1002
        const val NOTIF_ID_SYSTEM = 1003
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val buildChannel = NotificationChannel(
                CHANNEL_BUILD,
                "Compilador APK & Build Engine",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones sobre compilación y firma de APKs"
                enableVibration(true)
            }

            val modelsChannel = NotificationChannel(
                CHANNEL_MODELS,
                "Modelos de IA Gemma",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de descarga y verificación SHA-256 de pesos"
            }

            val systemChannel = NotificationChannel(
                CHANNEL_SYSTEM,
                "Sistema LUX Kernel & CLI",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alertas del kernel de auto-reparación y telemetría"
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(buildChannel)
            notificationManager.createNotificationChannel(modelsChannel)
            notificationManager.createNotificationChannel(systemChannel)
        }
    }

    fun canPostNotifications(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    fun notifyBuildComplete(appName: String, apkSize: String, sha256Short: String) {
        if (!canPostNotifications()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_BUILD)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("APK Compilado con Éxito: $appName")
            .setContentText("Tamaño: $apkSize | Checksum: $sha256Short")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("El artefacto binario para '$appName' fue empaquetado, verificado por Zipalign y firmado con clave debug.\nTamaño final: $apkSize\nSHA-256: $sha256Short")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_ID_BUILD, notification)
        } catch (_: SecurityException) {
            // Permission not granted or revoked
        }
    }

    fun notifyModelVerified(modelName: String, sha256: String) {
        if (!canPostNotifications()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_MODELS)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle("Modelo Gemma Verificado: $modelName")
            .setContentText("Integridad SHA-256 confirmada para CLI")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Los pesos del modelo $modelName han sido descargados en almacenamiento seguro y validados.\nChecksum: ${sha256.take(24)}...")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_ID_MODEL, notification)
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }

    fun notifyKernelAlert(title: String, message: String) {
        if (!canPostNotifications()) return

        val notification = NotificationCompat.Builder(context, CHANNEL_SYSTEM)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_ID_SYSTEM, notification)
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }

    fun sendTestNotification(title: String = "LUX Meta-DevOS Notificación", message: String = "El subsistema de notificaciones y permisos está activo y funcionando.") {
        if (!canPostNotifications()) return

        val notification = NotificationCompat.Builder(context, CHANNEL_SYSTEM)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_ID_SYSTEM, notification)
            soundService?.playNotificationSound()
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }
}
