package com.example.data.repository

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class SoundHapticService(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 75)
        } catch (_: Exception) {
            // ToneGenerator might fail in restricted sandboxes
        }
    }

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            null
        }
    }

    fun playClick() {
        vibrateShort(25)
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
        } catch (_: Exception) {}
    }

    fun playSuccess() {
        vibratePattern(longArrayOf(0, 30, 40, 60))
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
        } catch (_: Exception) {}
    }

    fun playCliExecute() {
        vibrateShort(45)
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 70)
        } catch (_: Exception) {}
    }

    fun playError() {
        vibratePattern(longArrayOf(0, 50, 40, 90))
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 180)
        } catch (_: Exception) {}
    }

    fun playStartupChime() {
        vibratePattern(longArrayOf(0, 60, 40, 100, 30, 140))
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 250)
            Thread {
                synthesizeCustomChime(frequencies = doubleArrayOf(370.0, 466.0, 554.0, 740.0), durationMs = 600)
            }.start()
        } catch (_: Exception) {}
    }

    fun playNotificationSound() {
        vibratePattern(longArrayOf(0, 40, 60, 80))
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 180)
            Thread {
                synthesizeCustomChime(frequencies = doubleArrayOf(587.33, 880.0, 1174.66), durationMs = 380)
            }.start()
        } catch (_: Exception) {}
    }

    private fun synthesizeCustomChime(frequencies: DoubleArray, durationMs: Int) {
        try {
            val sampleRate = 44100
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = Math.exp(-3.5 * (t / (durationMs / 1000.0)))
                var sample = 0.0
                for (f in frequencies) {
                    sample += Math.sin(2.0 * Math.PI * f * t)
                }
                sample /= frequencies.size
                buffer[i] = (sample * envelope * Short.MAX_VALUE * 0.75).toInt().toShort()
            }
            @Suppress("DEPRECATION")
            val audioTrack = android.media.AudioTrack(
                android.media.AudioManager.STREAM_NOTIFICATION,
                sampleRate,
                android.media.AudioFormat.CHANNEL_OUT_MONO,
                android.media.AudioFormat.ENCODING_PCM_16BIT,
                buffer.size * 2,
                android.media.AudioTrack.MODE_STATIC
            )
            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 50)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {}
    }

    private fun vibrateShort(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }

    private fun vibratePattern(pattern: LongArray) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, -1)
            }
        } catch (_: Exception) {}
    }
}
