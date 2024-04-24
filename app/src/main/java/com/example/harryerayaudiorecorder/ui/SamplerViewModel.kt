package com.example.harryerayaudiorecorder.ui

import androidx.lifecycle.ViewModel
import com.example.harryerayaudiorecorder.data.SamplerUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class SamplerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SamplerUiState())
    val uiState: StateFlow<SamplerUiState> = _uiState.asStateFlow()

    fun setRecordingFileName(fileName: String){
        _uiState.update { currentState -> currentState.copy(
            recordingFileName = fileName
        ) }
    }

    fun setQuantity(someNumber: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                quantity = someNumber,
            )
        }
    }
}

