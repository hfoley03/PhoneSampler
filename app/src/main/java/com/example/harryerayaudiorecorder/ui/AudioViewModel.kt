
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.arthenica.ffmpegkit.FFmpegKit
import com.example.harryerayaudiorecorder.RecorderControl
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

// Media player interface for abstraction
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
}

// Real implementation of MediaPlayer
class AndroidMediaPlayerWrapper : MediaPlayerWrapper {
    private var mediaPlayer: MediaPlayer? = null

    override fun setDataSource(path: String) {
        Log.d("AndroidMediaPlayerWrapper", "setdatasource()")
        //mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(path)
        }
    }

    override fun prepare() {
        mediaPlayer?.prepare()
    }

    override fun start() {
        mediaPlayer?.start()
    }

    override fun isMediaPlayer(): Boolean = mediaPlayer != null

    override fun stop() {
        mediaPlayer?.stop()
    }

    override fun release() {
        Log.d("AndroidMediaPlayerWrapper", "rlease 51()")

        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    override fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    override fun getDuration(): Int = mediaPlayer?.duration ?: 0

    override fun seekTo(position: Long, mode: Int) {
        Log.d("AudioViewModel", "seekto 61")
        mediaPlayer?.seekTo(position, mode)
    }

    override fun pause() {
        mediaPlayer?.pause();
    }
}

// ViewModel using the wrapper
open class AudioViewModel(private val mediaPlayerWrapper: MediaPlayerWrapper,
                     private val recorderControl: RecorderControl,
                     val audioCapturesDirectory: File
) : ViewModel() {

    val _recorderRunning = mutableStateOf(false)
    val recorderRunning: State<Boolean> = _recorderRunning

    private val _currentFileName = mutableStateOf<String?>(null)
    val currentFileName: State<String?> = _currentFileName


    fun playAudio(file: File) {
        Log.d("AudioViewModel", "exists: " + mediaPlayerWrapper.isMediaPlayer().toString())
        mediaPlayerWrapper.setDataSource(file.absolutePath)
        try {
            mediaPlayerWrapper.prepare()
            mediaPlayerWrapper.start()
        } catch (e: IOException) {
            Log.e("AudioViewModel", "Could not play audio", e)
        }
    }

    fun pauseAudio(){
        mediaPlayerWrapper.pause();
    }

    fun stopAudio() {
        mediaPlayerWrapper.stop()
        mediaPlayerWrapper.release()
    }

    fun getCurrentPosition(): Int = mediaPlayerWrapper.getCurrentPosition()

    fun getAudioDuration(file: File): Int {
        mediaPlayerWrapper.setDataSource(file.absolutePath)
        mediaPlayerWrapper.prepare()
        return mediaPlayerWrapper.getDuration()
    }

    fun formatDuration(millis: Long): String {
        return String.format("%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
    }

    fun seekTo(position: Long) {
        Log.d("AudioViewModel", position.toString())
        mediaPlayerWrapper.seekTo(position, MediaPlayer.SEEK_PREVIOUS_SYNC)
    }

    override fun onCleared() {
        Log.d("AndroidMediaPlayerWrapper", "oncleared()")

        mediaPlayerWrapper.release()
    }

    fun renameFile(oldName: String, newName: String) {


        val file =  getLastCreatedFile(audioCapturesDirectory)//File(audioCapturesDirectory, oldName)

        if (file != null) {
            if (file.exists()) {
                val newFile = File(audioCapturesDirectory, newName)
                if (!newFile.exists()) {
                    file.renameTo(newFile)
                    _currentFileName.value = newName
                } else {
                    // Handle the case where a file with the new name already exists
                }
            }
            else {
                Log.d("AudioViewModel", "no such file")
            }
        }
    }

    fun renameFileFromList(oldName: String, newName: String) {
        val file =  File(audioCapturesDirectory, oldName)
            if (file.exists()) {
                val newFile = File(audioCapturesDirectory, newName)
                if (!newFile.exists()) {
                    file.renameTo(newFile)
                    _currentFileName.value = newName
                } else {
                    // Handle the case where a file with the new name already exists
                }
            }
            else {
                Log.d("AudioViewModel", "no such file")
            }

    }
    fun startRecording() {
        recorderControl.startRecorder()
        _recorderRunning.value = true

    }

    fun stopRecording(defaultFileName: String) {
        recorderControl.stopRecorder()
        _recorderRunning.value = false
        _currentFileName.value = "$defaultFileName.wav"
    }

    fun setTemporaryFileName(tempFileName: String) {
        _currentFileName.value = tempFileName
    }

    fun getLastCreatedFile(directory: File): File? {
        return directory.listFiles()?.sortedByDescending { it.lastModified() }?.firstOrNull()
    }

    fun trimAudio(file: File, startMillis: Int, endMillis: Int) {
        val outputTrimmedFile = File(audioCapturesDirectory, "trimmed_${file.name}")
        val startSeconds = startMillis / 1000
        val durationSeconds = (endMillis - startMillis) / 1000
        val command = "-i ${file.absolutePath} -ss $startSeconds -t $durationSeconds -c copy ${outputTrimmedFile.absolutePath}"

        FFmpegKit.execute(command).apply {
            if (returnCode.isSuccess) {
                Log.d("AudioViewModel", "Trimming successful: ${outputTrimmedFile.absolutePath}")
            } else {
                Log.e("AudioViewModel", "Trimming failed")
            }
        }
    }

}
