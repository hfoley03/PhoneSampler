// Import necessary packages and libraries
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.harryerayaudiorecorder.ApiService
import com.example.harryerayaudiorecorder.FreesoundService
import com.example.harryerayaudiorecorder.RecorderControl
import com.example.harryerayaudiorecorder.TokenResponse
import com.example.harryerayaudiorecorder.data.AudioRecordEntity
import com.example.harryerayaudiorecorder.data.AudioRepository
import com.example.harryerayaudiorecorder.data.FreesoundSoundCard
import com.example.harryerayaudiorecorder.data.SearchResponse
import com.example.harryerayaudiorecorder.data.SoundCard
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    fun setDataSourceFromUrl(url: String)
    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener)
    fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener)

}

class MockMediaPlayerWrapper : MediaPlayerWrapper {
    override fun setDataSource(path: String) {}
    override fun prepare() {}
    override fun start() {}
    override fun stop() {}
    override fun release() {}
    override fun isPlaying(): Boolean = false
    override fun getCurrentPosition(): Int = 0
    override fun getDuration(): Int = 0
    override fun seekTo(position: Long, mode: Int) {}
    override fun pause() {}
    override fun isMediaPlayer(): Any = this
    override fun setLooping(state: Boolean) {}
    override fun setPlaybackSpeed(speed: Float) {}
    override fun onCleared() {}
    override fun reset() {}
    override fun setDataSourceFromUrl(url: String) {
        TODO("Not yet implemented")
    }

    override fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        TODO("Not yet implemented")
    }

    override fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        TODO("Not yet implemented")
    }
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

    override fun setDataSourceFromUrl(url: String) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            setupMediaPlayerListeners()
        } else {
            mediaPlayer?.reset() // Reset the player to ensure it's in a clean state
        }

        mediaPlayer?.apply {
            setDataSource(url)
            prepareAsync() // Use prepareAsync for network sources
            setOnPreparedListener {
                it.start() // Start playback automatically once prepared
            }
            setOnErrorListener { mp, what, extra ->
                Log.e("MediaPlayer Error", "What: $what, Extra: $extra")
                true
            }
            setOnInfoListener { mediaPlayer, what, extra ->
                Log.i("MediaPlayer Info", "Info: What $what, Extra $extra")
                true
            }
        }
        Log.d("AndroidMediaPlayerWrapper", "setDataSourceFromUrl() - url: $url")
    }

    private fun setupMediaPlayerListeners() {
        mediaPlayer?.apply {
            setOnPreparedListener {
                Log.d("AndroidMediaPlayerWrapper", "MediaPlayer prepared")
                start() // Optionally start playback immediately upon preparing
            }
            setOnCompletionListener {
                Log.d("AndroidMediaPlayerScript", "Playback completed")
                // Handle completion of playback
            }
            setOnErrorListener { _, what, extra ->
                Log.e("AndroidMediaPlayer Error", "MediaPlayer error occurred: What $what, Extra $extra")
                true
            }
        }
    }
    override fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        mediaPlayer?.setOnCompletionListener(listener)
    }

    override fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        mediaPlayer?.setOnPreparedListener(listener)
    }



}

// ViewModel managing audio playback and recording using the media player wrapper
open class AudioViewModel(
    private val mediaPlayerWrapper: MediaPlayerWrapper,
    private val recorderControl: RecorderControl,
    private val audioRepository: AudioRepository,
) : ViewModel() {

    val _recorderRunning = mutableStateOf(false)
    val recorderRunning: State<Boolean> = _recorderRunning
    private val _currentFileName = mutableStateOf<String?>(null)
    val currentFileName: State<String?> = _currentFileName
    var currentPosition: Long = 0
    private val _playingStates = mutableMapOf<Int, MutableState<Boolean>>()
    val searchText = mutableStateOf("")
    val timerRunning: MutableState<Boolean> = mutableStateOf(false)
    var filteredSoundCardList = listOf<MutableState<SoundCard>>()

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

    fun setRecorderRunningBool(valu : Boolean){
        _recorderRunning.value = valu
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

    fun stopWithoutSavingRecording(){
        recorderControl.stopRecorder()
        _recorderRunning.value = false
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
    fun downloadSound(soundId: String, accessToken: String, context: Context) {
        val freesoundService = ApiService.retrofit.create(FreesoundService::class.java)
        val call = freesoundService.downloadSound(soundId, "Bearer $accessToken")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Handle the binary data of the sound file
                    response.body()?.let { responseBody ->
                        // Save the file or process it as needed
                        saveSoundToFile(responseBody,context)
                    }
                } else {

                    Log.e("API Error", "Failed with HTTP status ${response.code()} and message ${response.message()}")
                    response.errorBody()?.let {
                        Log.e("API Error Details", "Error body: ${it.string()}")
                    }

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API Error", "Network error: ${t.message}")
            }
        })
    }

    private fun saveSoundToFile(body: ResponseBody,context: Context) {

        val soundFile = File(context.filesDir, "downloaded_sound.mp3")
        soundFile.outputStream().use {
            it.write(body.bytes())
        }
        Log.d("Freesound file saved",soundFile.path)
    }

    fun exchangeCode(clientId: String, clientSecret: String, code: String, redirectUri: String, callback: Callback<TokenResponse>) {
        val freesoundService = ApiService.retrofit.create(FreesoundService::class.java)
        val call = freesoundService.exchangeCode(clientId, clientSecret, "authorization_code", code, redirectUri)
        call.enqueue(callback)
    }

    fun uploadSound(accessToken: String, file: File, name: String, tags: String, description: String, license: String,
                    pack: String, geotag: String) {
        val requestFile = file.asRequestBody("audio/wav".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("audiofile", file.name, requestFile)
        val license = license.toRequestBody("text/plain".toMediaTypeOrNull())
        val name = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val description = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val tags = tags.toRequestBody("text/plain".toMediaTypeOrNull())
        val pack = pack.toRequestBody("text/plain".toMediaTypeOrNull())
        val geotag = geotag.toRequestBody("text/plain".toMediaTypeOrNull())


        Log.d("UploadSound", "Access Token: $accessToken")
        Log.d("UploadSound", "File: ${file.absolutePath}")

        val freesoundService = ApiService.retrofit.create(FreesoundService::class.java)
        val call = freesoundService.uploadSound(
            accessToken = "Bearer $accessToken",
            audiofile = body,
            name = name,
            tags = tags,
            description = description,
            license = license,
//            pack = pack,
//            geotag = geotag
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("Upload Success", "File uploaded successfully!")
                    showApiResponseMessage("File uploaded successfully to FreeSound, it awaits for manual moderation confirmation!", true)
                } else {
                    val error = response.errorBody()?.string() ?: "Unknown error"
                    showApiResponseMessage("Failed: $error", false)
                    Log.e("API Error", "Failed with HTTP status ${response.code()} and message ${response.message()}")

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API Error", "Network error: ${t.message}")
                showApiResponseMessage("Network error: ${t.message}", false)
            }
        })
    }


    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_TIMESTAMP = "timestamp"
        private const val EXPIRATION_TIME = 24 * 60 * 60 * 1000 // 24 hours in ms
    }


    // Function to get the authentication token
    fun getAccessToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentTime = System.currentTimeMillis()
        val savedTime = sharedPreferences.getLong(KEY_TIMESTAMP, 0)

        return if ((currentTime - savedTime) < EXPIRATION_TIME) {
            sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        } else {
            clearAccessToken(context)
            null
        }
    }

    // Function to set the authentication token
    fun setAccessToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentTime = System.currentTimeMillis()
        with(sharedPreferences.edit()) {
            putString(KEY_ACCESS_TOKEN, token)
            putLong(KEY_TIMESTAMP, currentTime)
            apply()
        }
    }


    // Function to clear the authentication token
    fun clearAccessToken(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_TIMESTAMP)
            apply()
        }
    }


    private val _apiResponse = mutableStateOf<Pair<String, Boolean>?>(null)
    val apiResponse: State<Pair<String, Boolean>?> = _apiResponse

    // Call this method to show an API response message
    fun showApiResponseMessage(message: String, isSuccess: Boolean) {
        _apiResponse.value = Pair(message, isSuccess)
    }

    fun clearApiResponseMessage() {
        _apiResponse.value = null
    }


    fun performSearch(clientSecret: String, query: String, setFreesoundSoundCards: (MutableList<FreesoundSoundCard>) -> Unit) {
        val freesoundService = ApiService.retrofit.create(FreesoundService::class.java)
        freesoundService.searchSounds(clientSecret = clientSecret, query = query).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        val gson = Gson()
                        val responseBodyString = responseBody.string()
                        val type = object : TypeToken<SearchResponse<FreesoundSoundCard>>() {}.type
                        val searchResponse = gson.fromJson<SearchResponse<FreesoundSoundCard>>(
                            responseBodyString,
                            type
                        )
                        val soundCards: MutableList<FreesoundSoundCard> = searchResponse.results.toMutableList()
                        Log.d("soundCards", soundCards.toString())
                        setFreesoundSoundCards(soundCards)
                        searchResponse.results.forEach {
                            Log.d(
                                "SearchResult",
                                "Sound: ${it.name}, Tags: ${it.tags.joinToString()}, ID: ${it.id}"
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("SearchFailure", "Network error: ${t.message}")
            }
        })
    }

    fun playPreview(sound: FreesoundSoundCard) {
        // Prioritize preview links in the order of preference
        val url = sound.previews["preview-hq-mp3"]
            ?: sound.previews["preview-lq-mp3"]
            ?: sound.previews["preview-hq-ogg"]
            ?: sound.previews["preview-lq-ogg"]
            ?: ""

        if (url.isNotEmpty()) {
            try {
                mediaPlayerWrapper.setDataSourceFromUrl(url)
            } catch (e: IOException) {
                Log.e("AudioViewModel", "Error playing preview: ${e.message}")
                // Handle errors such as network issues or corrupted audio paths
            }
        } else {
            Log.e("AudioViewModel", "No valid preview URL found")
            // Notify user or handle the absence of a preview URL
        }
        mediaPlayerWrapper.setOnCompletionListener {
            togglePlayPause(sound)
        }

    }

    fun stopPreview() {
        try {
            if (mediaPlayerWrapper.isPlaying()) {
                mediaPlayerWrapper.stop()
                mediaPlayerWrapper.reset()
                Log.d("AudioViewModel", "Preview stopped and reset")
            }
        } catch (e: Exception) {
            Log.e("AudioViewModel", "Error stopping preview: ${e.message}")
        }
    }


    fun getPlayingState(id: Int): MutableState<Boolean> {
        return _playingStates.getOrPut(id) { mutableStateOf(false) }
    }

    fun togglePlayPause(sound: FreesoundSoundCard) {
        val isPlaying = getPlayingState(sound.id)
        if (isPlaying.value) {
            stopPreview()
        } else {
            playPreview(sound)
        }
        isPlaying.value = !isPlaying.value
    }


    fun updateSearchText(newText: String) {
        searchText.value = newText
    }

    fun updateFilteredSCardList(filteredSCList: List<MutableState<SoundCard>>){
        filteredSoundCardList = filteredSCList
    }


}
