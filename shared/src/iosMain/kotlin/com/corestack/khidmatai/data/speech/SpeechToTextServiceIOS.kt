package com.corestack.khidmatai.data.speech

import com.corestack.khidmatai.domain.speech.SpeechState
import com.corestack.khidmatai.domain.speech.SpeechToTextService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Single

@Single(binds = [SpeechToTextService::class])
class SpeechToTextServiceIOS : SpeechToTextService {
    override val isSupported: Boolean = false
    
    private val _state = MutableStateFlow<SpeechState>(SpeechState.Idle)
    override val state: StateFlow<SpeechState> = _state.asStateFlow()
    
    private val _rmsdB = MutableStateFlow(0f)
    override val rmsdB: StateFlow<Float> = _rmsdB.asStateFlow()
    
    override fun startListening() {}
    override fun stopListening() {}
    override fun cancel() {}
}
