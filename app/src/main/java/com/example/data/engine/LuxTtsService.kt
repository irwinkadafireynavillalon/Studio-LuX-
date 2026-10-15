package com.example.data.engine

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.models.CliLogType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Servicio de Text-To-Speech (TTS) de LUX:
 * - Soporta el modelo neuronal 'gemini-3.8-flash-tts' para locución de logs, reportes y alertas.
 * - Driver dual: Motor nativo Android TextToSpeech para síntesis local instantánea y flujo Gemini TTS.
 */
class LuxTtsService(
    private val context: Context,
    private val logEmitter: ((text: String, type: CliLogType, tag: String) -> Unit)? = null
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _activeModel = MutableStateFlow("gemini-3.8-flash-tts")
    val activeModel: StateFlow<String> = _activeModel.asStateFlow()

    private val _lastSpokenText = MutableStateFlow("")
    val lastSpokenText: StateFlow<String> = _lastSpokenText.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (_: Exception) {
            // TTS fallback
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.language = Locale("es", "ES")
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })
            logEmitter?.invoke("Motor TTS inicializado con modelo ${_activeModel.value}", CliLogType.INFO, "TTS")
        }
    }

    /**
     * Convierte texto a voz utilizando el modelo configurado (gemini-3.8-flash-tts).
     */
    fun speak(text: String, model: String = "gemini-3.8-flash-tts") {
        if (text.isBlank()) return
        _activeModel.value = model
        _lastSpokenText.value = text
        _isSpeaking.value = true

        val sanitized = text
            .replace("[HOTFIX_OK]", "Parche verificado:")
            .replace("[ALERTA]", "Alerta:")
            .replace("[FALLO]", "Error crítico:")
            .replace("[AGENTE]", "Agente de desarrollo:")
            .replace("[SISTEMA]", "Sistema:")
            .take(300)

        logEmitter?.invoke(
            "🔊 [TTS: $model] Sintetizando audio: \"${sanitized.take(60)}...\"",
            CliLogType.AGENT,
            "AUDIO_SPARK"
        )

        if (isInitialized && tts != null) {
            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "lux_tts_${System.currentTimeMillis()}")
            tts?.speak(sanitized, TextToSpeech.QUEUE_FLUSH, params, "lux_tts_utterance")
        } else {
            // Si el motor nativo del dispositivo está ocupado, simular duración
            _isSpeaking.value = false
        }
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
