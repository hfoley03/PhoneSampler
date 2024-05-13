import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
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
    fun seekTo(position: Int, mode: Int)
}

// Real implementation of MediaPlayer
class AndroidMediaPlayerWrapper : MediaPlayerWrapper {
    private var mediaPlayer: MediaPlayer? = null

    override fun setDataSource(path: String) {
        mediaPlayer?.release()
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

    override fun stop() {
        mediaPlayer?.stop()
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    override fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    override fun getDuration(): Int = mediaPlayer?.duration ?: 0

    override fun seekTo(position: Int, mode: Int) {
        mediaPlayer?.seekTo(position.toLong(), mode)
    }
}

// ViewModel using the wrapper
class AudioViewModel(private val mediaPlayerWrapper: MediaPlayerWrapper) : ViewModel() {
    fun playAudio(file: File) {
        mediaPlayerWrapper.setDataSource(file.absolutePath)
        try {
            mediaPlayerWrapper.prepare()
            mediaPlayerWrapper.start()
        } catch (e: IOException) {
            Log.e("AudioViewModel", "Could not play audio", e)
        }
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
        mediaPlayerWrapper.seekTo(position.toInt(), MediaPlayer.SEEK_CLOSEST)
    }

    override fun onCleared() {
        mediaPlayerWrapper.release()
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
