package com.example.harryerayaudiorecorder.ui

import android.app.Application
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.IOException

class AudioViewModel : ViewModel() {
    private var mediaPlayer: MediaPlayer? = null

    fun playAudio(file: File) {
        mediaPlayer?.release() // Release any previously playing player
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(file.absolutePath)
                prepare() // Prepare the media player asynchronously
                start() // Start playing
            } catch (e: IOException) {
                Log.e("AudioViewModel", "Could not play audio", e)
            }
        }
    }

    fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onCleared() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
