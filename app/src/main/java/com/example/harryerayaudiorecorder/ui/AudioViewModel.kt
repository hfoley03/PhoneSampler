// Import necessary packages and libraries

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.harryerayaudiorecorder.RecorderControl
import com.example.harryerayaudiorecorder.data.AudioRecordEntity
import com.example.harryerayaudiorecorder.data.AudioRepository
import com.example.harryerayaudiorecorder.data.SoundCard
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

// Interface defining methods for MediaPlayer abstraction
interface MediaPlayerWrapper {
    fun setDataSource(path: String)
    fun prepare()
    fun start()
    fun stop()
    fun release()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
    fun getDuration(): Int
    fun seekTo(position: Long, mode: Int)
    fun pause()
    fun isMediaPlayer(): Any
    fun setLooping(state: Boolean)
    fun setPlaybackSpeed(speed: Float)
    fun onCleared()
    fun reset()
}

// Real implementation of MediaPlayer using Android's MediaPlayer
class AndroidMediaPlayerWrapper : MediaPlayerWrapper {
    private var mediaPlayer: MediaPlayer? = null

    // Set the data source for the media player
    override fun setDataSource(path: String) {
        mediaPlayer?.reset()
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setOnPreparedListener {
                    Log.d("AndroidMediaPlayerWrapper", "MediaPlayer prepared")
                }
                setOnSeekCompleteListener {
                    Log.d("AndroidMediaPlayerWrapper", "Seek completed")
                    start()  // Start playback after seek completes
                }
            }
        }
        mediaPlayer?.setDataSource(path)
        Log.d("AndroidMediaPlayerWrapper", "setDataSource() - path: $path")
    }

    // Prepare the media player for playback
    override fun prepare() {
        mediaPlayer?.prepare()
        Log.d("AndroidMediaPlayerWrapper", "prepare() - MediaPlayer prepared")
    }

    // Start media playback
    override fun start() {
        mediaPlayer?.start()
        Log.d("AndroidMediaPlayerWrapper", "start() - MediaPlayer started")
    }

    // Seek to a specific position in the media
    override fun seekTo(position: Long, mode: Int) {
        Log.d("AndroidMediaPlayerWrapper", "seekTo() - position: $position, mode: $mode")
        if (mediaPlayer != null) {
            mediaPlayer?.seekTo(position, mode) // Ensure position is converted to Int
            Log.d("AndroidMediaPlayerWrapper", "seekTo() - Seek operation called")
        } else {
            Log.e("AndroidMediaPlayerWrapper", "seekTo() - MediaPlayer is null")
        }
    }

    // Pause media playback
    override fun pause() {
        mediaPlayer?.pause()
        Log.d("AndroidMediaPlayerWrapper", "pause() - MediaPlayer paused")
    }

    // Release the media player resources
    override fun release() {
        mediaPlayer?.let {
            it.release()
            mediaPlayer = null
            Log.d("AndroidMediaPlayerWrapper", "release() - MediaPlayer released")
        }
    }

    // Stop media playback and release resources
    override fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d("AndroidMediaPlayerWrapper", "stop() - MediaPlayer stopped and released")
    }

    // Check if media is currently playing
    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    // Get the current position of the media playback
    override fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    // Get the duration of the media
    override fun getDuration(): Int = mediaPlayer?.duration ?: 0

    // Reset the media player
    override fun reset() {
        mediaPlayer?.reset()
        Log.d("AndroidMediaPlayerWrapper", "reset() - MediaPlayer reset")
    }

    // Check if the media player is initialized
    override fun isMediaPlayer(): Boolean = mediaPlayer != null

    // Set the looping state for media playback
    override fun setLooping(state: Boolean) {
        mediaPlayer?.isLooping = state
        Log.d("looping", mediaPlayer?.isLooping.toString())
    }

    // Set the playback speed for the media
    override fun setPlaybackSpeed(speed: Float) {
        mediaPlayer?.let {
            it.playbackParams = it.playbackParams.setSpeed(speed)
            Log.d("AndroidMediaPlayerWrapper", "setPlaybackSpeed() - speed: $speed")
        }
    }

    // Release resources when the ViewModel is cleared
    override fun onCleared() {
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d("AndroidMediaPlayerWrapper", "onCleared() - MediaPlayer cleared")
    }
}

// ViewModel managing audio playback and recording using the media player wrapper
open class AudioViewModel(
    private val mediaPlayerWrapper: MediaPlayerWrapper,
    private val recorderControl: RecorderControl,
    private val audioRepository: AudioRepository
) : ViewModel() {

    val _recorderRunning = mutableStateOf(false)
    val recorderRunning: State<Boolean> = _recorderRunning

    private val _currentFileName = mutableStateOf<String?>(null)
    val currentFileName: State<String?> = _currentFileName

    var currentPosition: Long = 0

    // Play an audio file from a specified position
    fun playAudio(file: File, startPosition: Long = 0) {
        stopAudio() // Ensure the previous instance is stopped and released

        mediaPlayerWrapper.setDataSource(file.absolutePath)
        try {
            mediaPlayerWrapper.prepare()
            mediaPlayerWrapper.seekTo(startPosition, MediaPlayer.SEEK_CLOSEST)
            mediaPlayerWrapper.start()
        } catch (e: IOException) {
            Log.e("AudioViewModel", "Could not play audio", e)
        }
    }

    // Pause audio playback
    fun pauseAudio(){
        mediaPlayerWrapper.pause()
        currentPosition = mediaPlayerWrapper.getCurrentPosition().toLong()
    }

    // Stop audio playback
    fun stopAudio() {
        if (mediaPlayerWrapper.isPlaying()) {
            currentPosition = mediaPlayerWrapper.getCurrentPosition().toLong()
            mediaPlayerWrapper.stop()
        }
        mediaPlayerWrapper.onCleared()
    }

    // Get the current position of audio playback
    fun getCurrentPosition(): Int = mediaPlayerWrapper.getCurrentPosition()

    // Set the looping state for audio playback
    fun setLooping(state: Boolean){
        mediaPlayerWrapper.setLooping(state)
    }

    // Format a duration in milliseconds to a string in HH:mm:ss format
    fun formatDuration(millis: Long): String {
        return String.format("%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
    }

    // Seek to a specific position in the audio
    fun seekTo(position: Long) {
        mediaPlayerWrapper.seekTo(position, MediaPlayer.SEEK_CLOSEST)
        _recorderRunning.value = true
    }

    // Clear resources when the ViewModel is cleared
    override fun onCleared() {
        Log.d("AndroidMediaPlayerWrapper", "oncleared()")
        mediaPlayerWrapper.release()
    }

    // Fast forward audio playback by a specified number of milliseconds
    fun fastForward(skipMillis: Int) {
        val newPosition = (getCurrentPosition() + skipMillis)
        seekTo(newPosition.toLong())
        _recorderRunning.value = true
    }

    // Rewind audio playback by a specified number of milliseconds
    fun fastRewind(skipMillis: Int) {
        val newPosition = (getCurrentPosition() - skipMillis).coerceAtLeast(0)
        seekTo(newPosition.toLong())
        _recorderRunning.value = true
    }

    // Start recording audio
    fun startRecording() {
        recorderControl.startRecorder()
        _recorderRunning.value = true
    }

    // Stop recording audio and set the default file name
    fun stopRecording(defaultFileName: String) {
        recorderControl.stopRecorder()
        _recorderRunning.value = false
        _currentFileName.value = "$defaultFileName.wav"
    }

    // Set a temporary file name
    fun setTemporaryFileName(tempFileName: String) {
        _currentFileName.value = tempFileName
    }

    // Save audio file details to the database
    fun save(name: String) {
        audioRepository.save(name)
    }

    // Delete a sound card and its associated audio file from the database and filesystem
    fun deleteSoundCard(soundCard: SoundCard, soundCardList: SnapshotStateList<MutableState<SoundCard>>) {
        audioRepository.deleteSoundCard(soundCard, soundCardList)
    }

    // Rename a sound card in the database and update the UI
    fun renameSoundCard(soundCard: SoundCard, newFileName: String, soundCardList: SnapshotStateList<MutableState<SoundCard>>) {
        audioRepository.renameSoundCard(soundCard, newFileName, soundCardList)
    }

    // Trim an audio file and save the result
    fun trimAudio(file: File, startMillis: Int, endMillis: Int, onTrimmed: (File) -> Unit) {
        audioRepository.trimAudio(file, startMillis, endMillis, onTrimmed)
    }

    // Rename an audio file to a new name
    fun renameFile(newName: String) {
        audioRepository.renameFile(newName)
    }

    // Rename a file from a list of files
    fun renameFileFromList(oldName: String, newName: String) {
        audioRepository.renameFileFromList(oldName, newName)
    }

    // Get the duration of an audio file
//    fun getAudioDuration(file: File): Int {
//        mediaPlayerWrapper.setDataSource(file.absolutePath)
//        mediaPlayerWrapper.prepare()
//        return mediaPlayerWrapper.getDuration()
//    }

    fun getAudioDuration(file: File): Int {
        return audioRepository.getAudioDuration(file)
    }

    // Set the playback speed for audio
    fun setPlaybackSpeed(speed: Float) {
        if (mediaPlayerWrapper.isPlaying()) {
            mediaPlayerWrapper.setPlaybackSpeed(speed)
        }
    }

    // Adjust the playback speed for audio
    fun adjustPlaybackSpeed(speed: Float) {
        mediaPlayerWrapper.setPlaybackSpeed(speed)
    }

    suspend fun getAllAudioRecords(): List<AudioRecordEntity> {
        return audioRepository.getAllAudioRecords()
    }
}
