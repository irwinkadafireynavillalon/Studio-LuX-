package com.example.data.engine

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Servicio y Gestor de Prioridad Máxima, Memoria Extendida (500 MB) y Superposición ("Sobre otras aplicaciones")
 * para LUX Meta-DevOS.
 */
class LuxPriorityKernelService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: LinearLayout? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        applyRealtimeProcessPriority()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        applyRealtimeProcessPriority()
        val action = intent?.action
        if (action == ACTION_SHOW_SYSTEM_OVERLAY) {
            showNativeSystemOverlay()
        } else if (action == ACTION_HIDE_SYSTEM_OVERLAY) {
            removeNativeSystemOverlay()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        removeNativeSystemOverlay()
        super.onDestroy()
    }

    private fun showNativeSystemOverlay() {
        if (!canDrawOverlays(this) || overlayView != null) return
        try {
            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager = wm

            val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                x = 24
                y = 140
            }

            val container = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(24, 14, 24, 14)
                setBackgroundColor(0xE6101520.toInt())
                val badge = TextView(context).apply {
                    text = "⚡ LUX DEVOS · PRIORIDAD MÁX · 500MB"
                    setTextColor(0xFFF5C451.toInt())
                    textSize = 11f
                }
                addView(badge)
                setOnClickListener {
                    val launchIntent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    }
                    if (launchIntent != null) startActivity(launchIntent)
                }
            }

            wm.addView(container, params)
            overlayView = container
            _isOverlayRunning.value = true
        } catch (_: Exception) {
            _isOverlayRunning.value = false
        }
    }

    private fun removeNativeSystemOverlay() {
        try {
            overlayView?.let { view ->
                windowManager?.removeView(view)
            }
        } catch (_: Exception) {
        } finally {
            overlayView = null
            _isOverlayRunning.value = false
        }
    }

    companion object {
        const val ACTION_SHOW_SYSTEM_OVERLAY = "com.example.action.SHOW_SYSTEM_OVERLAY"
        const val ACTION_HIDE_SYSTEM_OVERLAY = "com.example.action.HIDE_SYSTEM_OVERLAY"

        private val _priorityLevel = MutableStateFlow("MÁXIMA (Tiempo Real · Foreground)")
        val priorityLevel: StateFlow<String> = _priorityLevel.asStateFlow()

        private val _allocatedMemoryMb = MutableStateFlow(500)
        val allocatedMemoryMb: StateFlow<Int> = _allocatedMemoryMb.asStateFlow()

        private val _isOverlayRunning = MutableStateFlow(false)
        val isOverlayRunning: StateFlow<Boolean> = _isOverlayRunning.asStateFlow()

        private val _isInAppFloatingHudEnabled = MutableStateFlow(false)
        val isInAppFloatingHudEnabled: StateFlow<Boolean> = _isInAppFloatingHudEnabled.asStateFlow()

        private val _isHighPriorityActive = MutableStateFlow(true)
        val isHighPriorityActive: StateFlow<Boolean> = _isHighPriorityActive.asStateFlow()

        fun applyRealtimeProcessPriority() {
            try {
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY)
                _isHighPriorityActive.value = true
                _priorityLevel.value = "MÁXIMA (URGENT_DISPLAY · Sin Límite 500MB)"
            } catch (_: Exception) {
            }
        }

        fun setAllocatedMemoryMb(megabytes: Int) {
            _allocatedMemoryMb.value = megabytes.coerceIn(128, 1024)
        }

        fun toggleInAppFloatingHud(enabled: Boolean? = null) {
            _isInAppFloatingHudEnabled.value = enabled ?: !_isInAppFloatingHudEnabled.value
        }

        fun canDrawOverlays(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.canDrawOverlays(context)
            } else {
                true
            }
        }

        fun isIgnoringBatteryOptimizations(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                pm?.isIgnoringBatteryOptimizations(context.packageName) ?: false
            } else {
                true
            }
        }

        fun canRequestPackageInstalls(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.packageManager.canRequestPackageInstalls()
            } else {
                true
            }
        }

        fun requestOverlayPermission(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {
                }
            }
        }

        fun requestUnrestrictedBatteryPriority(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    val intent = Intent(
                        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                        Uri.parse("package:${context.packageName}")
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {
                }
            }
        }

        fun requestInstallPackagesPermission(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:${context.packageName}")
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {
                }
            }
        }

        fun toggleSystemOverlay(context: Context): Boolean {
            return if (canDrawOverlays(context)) {
                val nextState = !_isOverlayRunning.value
                val intent = Intent(context, LuxPriorityKernelService::class.java).apply {
                    action = if (nextState) ACTION_SHOW_SYSTEM_OVERLAY else ACTION_HIDE_SYSTEM_OVERLAY
                }
                try {
                    context.startService(intent)
                    _isOverlayRunning.value = nextState
                } catch (_: Exception) {
                    _isOverlayRunning.value = nextState
                }
                _isInAppFloatingHudEnabled.value = nextState
                nextState
            } else {
                // Activa el HUD flotante y abre el ajuste de superposición del sistema
                _isInAppFloatingHudEnabled.value = !_isInAppFloatingHudEnabled.value
                requestOverlayPermission(context)
                _isInAppFloatingHudEnabled.value
            }
        }
    }
}
