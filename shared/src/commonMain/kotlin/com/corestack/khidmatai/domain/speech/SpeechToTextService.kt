package com.corestack.khidmatai.domain.speech

import kotlinx.coroutines.flow.StateFlow

sealed interface SpeechState {
    data object Idle : SpeechState
    data object Listening : SpeechState
    data class Result(val text: String, val isFinal: Boolean) : SpeechState
    data class Error(val message: String) : SpeechState
}

interface SpeechToTextService {
    val isSupported: Boolean
    val state: StateFlow<SpeechState>
    val rmsdB: StateFlow<Float> // Real-time decibels for dynamic waveform visualization

    fun startListening()
    fun stopListening()
    fun cancel()
}
