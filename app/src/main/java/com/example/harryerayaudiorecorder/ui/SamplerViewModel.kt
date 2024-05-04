package com.example.harryerayaudiorecorder.ui

import androidx.lifecycle.ViewModel
import com.example.harryerayaudiorecorder.data.SoundCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class SamplerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SoundCard())
    val uiState: StateFlow<SoundCard> = _uiState.asStateFlow()
    var testOnly: Int = 0

//    fun setSoundCardTitle(fileName: String){
//        _uiState.update { currentState -> currentState.copy(
//            title = fileName
//        ) }
//    }
    fun setSoundCard(soundCard: SoundCard){
        _uiState.update { currentState -> currentState.copy(
            duration = soundCard.duration,
            fileName = soundCard.fileName,
            fileSize = soundCard.fileSize
        ) }
    }

    //TODO : Implement this functions (first calculate or save)
//    fun setDuration(someNumber: Double) {
//        _uiState.update { currentState ->
//            currentState.copy(
//                duration = someNumber,
//            )
//        }
//    }
//    fun setFileSize(someNumber: Double) {}


}

