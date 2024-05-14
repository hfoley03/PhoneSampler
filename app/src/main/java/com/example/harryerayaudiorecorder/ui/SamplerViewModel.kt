package com.example.harryerayaudiorecorder.ui

import androidx.lifecycle.ViewModel
import com.example.harryerayaudiorecorder.data.SoundCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


interface ISamplerViewModel {
    val uiState: StateFlow<SoundCard>
    fun setSoundCard(soundCard: SoundCard)
}

class SamplerViewModel : ViewModel(), ISamplerViewModel {

    private val _uiState = MutableStateFlow(SoundCard())
    override val uiState: StateFlow<SoundCard> = _uiState.asStateFlow()

    override fun setSoundCard(soundCard: SoundCard) {
        _uiState.update { currentState ->
            currentState.copy(
                duration = soundCard.duration,
                fileName = soundCard.fileName,
                fileSize = soundCard.fileSize
            )
        }
    }
}

