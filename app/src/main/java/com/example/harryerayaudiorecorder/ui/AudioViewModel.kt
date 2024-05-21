
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthenica.ffmpegkit.FFmpegKit
import com.example.harryerayaudiorecorder.RecorderControl
import com.example.harryerayaudiorecorder.data.AudioRecordDatabase
import com.example.harryerayaudiorecorder.data.AudioRecordEntity
import com.example.harryerayaudiorecorder.data.SoundCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
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

    fun setLooping(state: Boolean)

    fun onCleared()
    fun reset()
}

// Real implementation of MediaPlayer
class AndroidMediaPlayerWrapper : MediaPlayerWrapper {
    private var mediaPlayer: MediaPlayer? = null

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

    override fun prepare() {
        mediaPlayer?.prepare()
        Log.d("AndroidMediaPlayerWrapper", "prepare() - MediaPlayer prepared")
    }

    override fun start() {
        mediaPlayer?.start()
        Log.d("AndroidMediaPlayerWrapper", "start() - MediaPlayer started")
    }

    override fun seekTo(position: Long, mode: Int) {
        Log.d("AndroidMediaPlayerWrapper", "seekTo() - position: $position, mode: $mode")
        if (mediaPlayer != null) {
            mediaPlayer?.seekTo(position, mode) // Ensure position is converted to Int
            Log.d("AndroidMediaPlayerWrapper", "seekTo() - Seek operation called")
        } else {
            Log.e("AndroidMediaPlayerWrapper", "seekTo() - MediaPlayer is null")
        }
    }

    override fun pause() {
        mediaPlayer?.pause()
        Log.d("AndroidMediaPlayerWrapper", "pause() - MediaPlayer paused")
    }

    override fun release() {
        mediaPlayer?.let {
            it.release()
            mediaPlayer = null
            Log.d("AndroidMediaPlayerWrapper", "release() - MediaPlayer released")
        }
    }

    override fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d("AndroidMediaPlayerWrapper", "stop() - MediaPlayer stopped and released")
    }

    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    override fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    override fun getDuration(): Int = mediaPlayer?.duration ?: 0

    override fun reset() {
        mediaPlayer?.reset()
        Log.d("AndroidMediaPlayerWrapper", "reset() - MediaPlayer reset")
    }

    override fun isMediaPlayer(): Boolean = mediaPlayer != null
    override fun setLooping(state: Boolean) {
        mediaPlayer?.isLooping = state
        Log.d("looping", mediaPlayer?.isLooping.toString())
    }

    override fun onCleared() {
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d("AndroidMediaPlayerWrapper", "onCleared() - MediaPlayer cleared")
    }
}






// ViewModel using the wrapper
open class AudioViewModel(
    private val mediaPlayerWrapper: MediaPlayerWrapper,
    private val recorderControl: RecorderControl,
    val audioCapturesDirectory: File,
    val db : AudioRecordDatabase
) : ViewModel() {

    val _recorderRunning = mutableStateOf(false)
    val recorderRunning: State<Boolean> = _recorderRunning

    private val _currentFileName = mutableStateOf<String?>(null)
    val currentFileName: State<String?> = _currentFileName

    var currentPosition: Long = 0
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

    fun pauseAudio(){
        mediaPlayerWrapper.pause();
        currentPosition = mediaPlayerWrapper.getCurrentPosition().toLong()
    }

    fun stopAudio() {
        if (mediaPlayerWrapper.isPlaying()) {
            currentPosition = mediaPlayerWrapper.getCurrentPosition().toLong()
            mediaPlayerWrapper.stop()
        }
        mediaPlayerWrapper.onCleared()
    }

    fun getCurrentPosition(): Int = mediaPlayerWrapper.getCurrentPosition()

    fun getAudioDuration(file: File): Int {
        mediaPlayerWrapper.setDataSource(file.absolutePath)
        mediaPlayerWrapper.prepare()
        return mediaPlayerWrapper.getDuration()
    }

    fun setLooping(state: Boolean){
        mediaPlayerWrapper.setLooping(state)
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
        mediaPlayerWrapper.seekTo(position, MediaPlayer.SEEK_CLOSEST)
    }

    override fun onCleared() {
        Log.d("AndroidMediaPlayerWrapper", "oncleared()")
        mediaPlayerWrapper.release()
    }

    fun fastForward(skipMillis: Int) {
        val newPosition = (getCurrentPosition() + skipMillis)
        seekTo(newPosition.toLong())
    }

    fun fastRewind(skipMillis: Int) {
        val newPosition = (getCurrentPosition() - skipMillis).coerceAtLeast(0)
        seekTo(newPosition.toLong())
    }

    fun renameFile(oldName: String, newName: String) {


        val file =  getLastCreatedFile(audioCapturesDirectory) //File(audioCapturesDirectory, oldName)

        if (file != null) {
            if (file.exists()) {
                val newFile = File(audioCapturesDirectory, newName)
                if (!newFile.exists()) {
                    file.renameTo(newFile)
                    _currentFileName.value = newName
                } else {
                    // Handle the case where a file with the new name already exists
                }
//                save(file, newName)
            }
            else {
                Log.d("AudioViewModel", "no such file")
            }
        }

    }

    fun renameFileFromList(oldName: String, newName: String) {
        val file = File(audioCapturesDirectory, oldName)
            if (file.exists())
            {
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

    fun save(name: String){

        val file = File(audioCapturesDirectory, name)

        val dur =  getAudioDuration(file)
        val fSizeMB = file.length().toDouble() / (1024 * 1024)
        val lastModDate = SimpleDateFormat("dd-MM-yyyy").format(Date(file.lastModified()))
        Log.d("AUDIOVIEWMODEL", lastModDate)

        var record = AudioRecordEntity(name, file.absolutePath, dur, fSizeMB, lastModDate)

        Log.d("AudioViewModel", record.filename)
        Log.d("AudioViewModel", record.filePath)
        Log.d("AudioViewModel", record.fileSize.toString())
        Log.d("AudioViewModel", record.duration.toString())


        GlobalScope.launch {
            db.audioRecordDoa().insert(record)
        }
    }

    fun deleteSoundCard(soundCard: SoundCard, soundCardList: SnapshotStateList<MutableState<SoundCard>>) {
        viewModelScope.launch(Dispatchers.IO) {
            val audioRecordEntity = db.audioRecordDoa().getAll()
                .find { it.filename == soundCard.fileName } // Assuming filename is unique
            audioRecordEntity?.let {
                db.audioRecordDoa().delete(it)
                withContext(Dispatchers.Main) {
                    soundCardList.removeIf { it.value.fileName == soundCard.fileName }
                }
            }
        }
    }

}
