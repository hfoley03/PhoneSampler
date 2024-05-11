package com.example.harryerayaudiorecorder.ui

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class AudioViewModel : ViewModel() {
    var mediaPlayer: MediaPlayer? = null

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

    public override fun onCleared() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // Method to get the current playback position
    fun getCurrentPosition(): Int {
        // Return the current position if the media player is not null and is playing, else return 0
        return mediaPlayer?.let {
            if (it.isPlaying) it.currentPosition else 0
        } ?: 0
    }

    fun getAudioDuration(file: File): Int {
        var duration = 0
        val tempPlayer = MediaPlayer()
        try {
            tempPlayer.setDataSource(file.absolutePath)
            tempPlayer.prepare()  // Synchronously prepare the media player
            duration = tempPlayer.duration  // Get the duration in milliseconds
        } catch (e: IOException) {
            Log.e("AudioViewModel", "Error getting audio duration", e)
        } finally {
            tempPlayer.release()  // Ensure the temporary player is released after use
        }
        return duration
    }

    fun formatDuration(millis: Long): String {
        //hh:mm:ss
        return String.format("%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
    }

    fun seekTo(position: Long) {
        mediaPlayer?.seekTo(position, MediaPlayer.SEEK_CLOSEST)
    }

    fun renameFile(audioCapturesDirectory: File, oldName: String, newName: String) {

        val file = File(audioCapturesDirectory, oldName)
        if (file.exists()) {
            val newFile = File(audioCapturesDirectory, newName)
            if (!newFile.exists()) {
                file.renameTo(newFile)
            } else {
                // Handle the case where a file with the new name already exists
            }
        }
    }

}
