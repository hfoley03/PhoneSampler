import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
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
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

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

    fun startBoolean(audioViewModel: AudioViewModel)
    fun stopBoolean(audioViewModel: AudioViewModel)

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
    }

    override fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        TODO("Not yet implemented")
    }

    override fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        TODO("Not yet implemented")
    }

    override fun startBoolean(audioViewModel: AudioViewModel){
        audioViewModel.timerRunning.value = true
    }

    override fun stopBoolean(audioViewModel: AudioViewModel){
        audioViewModel.timerRunning.value = false
    }
}



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
                    start()
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
            mediaPlayer?.seekTo(position, mode)
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

    override fun setPlaybackSpeed(speed: Float) {
        mediaPlayer?.let {
            it.playbackParams = it.playbackParams.setSpeed(speed)
            Log.d("AndroidMediaPlayerWrapper", "setPlaybackSpeed() - speed: $speed")
        }
    }

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
            mediaPlayer?.reset()
        }

        mediaPlayer?.apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                it.start()
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
                start()
            }
            setOnCompletionListener {
                Log.d("AndroidMediaPlayerScript", "Playback completed")
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

    override fun startBoolean(audioViewModel: AudioViewModel) {
        //do nothing
    }

    override fun stopBoolean(audioViewModel: AudioViewModel) {
        //do nothing
    }


}

open class AudioViewModel(
    private val mediaPlayerWrapper: MediaPlayerWrapper,
    private val recorderControl: RecorderControl,
    private val audioRepository: AudioRepository,
) : ViewModel() {

    val _recorderRunning = mutableStateOf(false)
    val recorderRunning: State<Boolean> = _recorderRunning
    private val _currentFileName = mutableStateOf<String?>(null)
    var currentFileName: State<String?> = _currentFileName
    var currentPosition: Long = 0
    private val _playingStates = mutableMapOf<Int, MutableState<Boolean>>()
    val searchText = mutableStateOf("")
    val timerRunning: MutableState<Boolean> = mutableStateOf(false)
    val showUploadDialog = mutableStateOf(false)
    private val _downloadStatusMessage = mutableStateOf<String?>(null)
    val downloadStatusMessage: State<String?> = _downloadStatusMessage


    fun playAudio(file: File, startPosition: Long = 0) {
        stopAudio()

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
        mediaPlayerWrapper.pause()
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

    fun setLooping(state: Boolean){
        mediaPlayerWrapper.setLooping(state)
    }
    // Format mm:ss:cs
    fun formatDurationCantiSec(milliseconds: Int): String {
        val minutes = (milliseconds / 1000) / 60
        val seconds = (milliseconds / 1000) % 60
        val centiseconds = (milliseconds / 10) % 100
        return String.format("%02d:%02d.%02d", minutes, seconds, centiseconds)
    }

    fun reformatDateString(originalString: String): String {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val dateTime = LocalDateTime.parse(originalString, formatter)
        val targetFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return dateTime.format(targetFormatter)
    }
    // used from dd-mm-yyyy to reverse
    fun convertDateFormatYearFirst(dateStr: String): String {
        val originalFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ITALIAN)
        val newFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN)
        val date = originalFormat.parse(dateStr)
        return newFormat.format(date ?: return "")
    }


    fun seekTo(position: Long) {
        mediaPlayerWrapper.seekTo(position, MediaPlayer.SEEK_CLOSEST)
        _recorderRunning.value = true
    }

    override fun onCleared() {
        Log.d("AndroidMediaPlayerWrapper", "oncleared()")
        mediaPlayerWrapper.release()
    }

    fun fastForward(skipMillis: Int) {
        val newPosition = (getCurrentPosition() + skipMillis)
        seekTo(newPosition.toLong())
        _recorderRunning.value = true
    }

    fun fastRewind(skipMillis: Int) {
        val newPosition = (getCurrentPosition() - skipMillis).coerceAtLeast(0)
        seekTo(newPosition.toLong())
        _recorderRunning.value = true
    }

    fun setRecorderRunningBool(valu : Boolean){
        _recorderRunning.value = valu
    }
    fun startRecording() {
        recorderControl.startRecorder()
        _recorderRunning.value = true
        mediaPlayerWrapper.startBoolean(this)
    }

    fun stopRecording(defaultFileName: String) {
        recorderControl.stopRecorder()
        _recorderRunning.value = false
        _currentFileName.value = "$defaultFileName.wav"
        mediaPlayerWrapper.stopBoolean(this)
    }

    fun stopWithoutSavingRecording(){
        recorderControl.stopRecorder()
        _recorderRunning.value = false
        mediaPlayerWrapper.stopBoolean(this)
    }

    fun setTemporaryFileName(tempFileName: String) {
        _currentFileName.value = tempFileName
    }

    fun save(name: String) {
        audioRepository.save(name)
    }

    fun saveFromFile(file: File) {
        audioRepository.saveFromFile(file)
    }

    fun deleteSoundCard(soundCard: SoundCard, soundCardList: SnapshotStateList<MutableState<SoundCard>>) {
        audioRepository.deleteSoundCard(soundCard, soundCardList)
    }

    fun renameSoundCard(soundCard: SoundCard, newFileName: String, soundCardList: SnapshotStateList<MutableState<SoundCard>>) {
        audioRepository.renameSoundCard(soundCard, newFileName, soundCardList)
    }

    fun trimAudio(file: File, startMillis: Int, endMillis: Int, onTrimmed: (File) -> Unit) {
        audioRepository.trimAudio(file, startMillis, endMillis, onTrimmed)
    }

    fun renameFile(newName: String) {
        audioRepository.renameFile(newName)
        _currentFileName.value = newName
    }

    fun renameFileFromList(oldName: String, newName: String) {
        audioRepository.renameFileFromList(oldName, newName)
    }


    fun getAudioDuration(file: File): Int {
        return audioRepository.getAudioDuration(file)
    }

    fun setPlaybackSpeed(speed: Float) {
        if (mediaPlayerWrapper.isPlaying()) {
            mediaPlayerWrapper.setPlaybackSpeed(speed)
        }
    }

    fun adjustPlaybackSpeed(speed: Float) {
        mediaPlayerWrapper.setPlaybackSpeed(speed)
    }

    suspend fun getAllAudioRecords(): List<AudioRecordEntity> {
        return audioRepository.getAllAudioRecords()
    }
    fun downloadSound(soundId: String, accessToken: String, fileName:String, audioCapturesDirectory: File, downloadTrigger:Boolean ,setDownloadTrigger: (Boolean) -> Unit,context: Context) {
        val freesoundService = ApiService.retrofit.create(FreesoundService::class.java)
        val call = freesoundService.downloadSound(soundId, "Bearer $accessToken")
        _downloadStatusMessage.value = "Download Started: $fileName"

        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        saveSoundToFile(responseBody,fileName,audioCapturesDirectory,context)
                        setDownloadTrigger(!downloadTrigger)
                        _downloadStatusMessage.value = "Download complete: $fileName"
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

    private fun saveSoundToFile(body: ResponseBody,fileName:String, audioCapturesDirectory:File, context: Context) {
        var fileNameWav = fileName

        if (!audioCapturesDirectory.exists()) {
            if (!audioCapturesDirectory.mkdirs()) {
                Log.e("Directory Creation", "Failed to create directory: ${audioCapturesDirectory.absolutePath}")
                return
            }
        }
        try{
            if (!fileName.endsWith(".wav", ignoreCase = true)) {
                fileNameWav = "$fileName.wav"
            }
            val soundFile = File(audioCapturesDirectory.absolutePath + "/" + "$fileNameWav")
            soundFile.outputStream().use {
                it.write(body.bytes())
            }
            // save to db here
            saveFromFile(soundFile)
            Log.d("Freesound file saved",soundFile.path)
        }catch (e: IOException) {
            Log.e("File Writing Error", "Error saving sound file: ${e.message}")
        }

    }

    fun exchangeCode(clientId: String, clientSecret: String, code: String, redirectUri: String, callback: Callback<TokenResponse>) {
        val freesoundService = ApiService.retrofit.create(FreesoundService::class.java)
        val call = freesoundService.exchangeCode(clientId, clientSecret, "authorization_code", code, redirectUri)
        call.enqueue(callback)
    }

    fun uploadSound(accessToken: String, file: File, name: String, tags: String, description: String, license: String) {
        val requestFile = file.asRequestBody("audio/wav".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("audiofile", file.name, requestFile)
        val license = license.toRequestBody("text/plain".toMediaTypeOrNull())
        val name = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val description = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val tags = tags.toRequestBody("text/plain".toMediaTypeOrNull())



        val freesoundService = ApiService.retrofit.create(FreesoundService::class.java)
        val call = freesoundService.uploadSound(
            accessToken = "Bearer $accessToken",
            audiofile = body,
            name = name,
            tags = tags,
            description = description,
            license = license
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

    fun setAccessToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentTime = System.currentTimeMillis()
        with(sharedPreferences.edit()) {
            putString(KEY_ACCESS_TOKEN, token)
            putLong(KEY_TIMESTAMP, currentTime)
            apply()
        }
    }


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

    fun showApiResponseMessage(message: String, isSuccess: Boolean) {
        _apiResponse.value = Pair(message, isSuccess)
    }

    fun clearApiResponseMessage() {
        _apiResponse.value = null
    }


    fun performSearch(clientSecret: String, query: String, setFreesoundSoundCards: (Collection<MutableState<FreesoundSoundCard>>) -> Unit) {
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

                        val mutableStateCollection: Collection<MutableState<FreesoundSoundCard>> = soundCards.map {
                            mutableStateOf(it)
                        }
                        setFreesoundSoundCards(mutableStateCollection)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("SearchFailure", "Network error: ${t.message}")
            }
        })
    }

    fun playPreview(sound: FreesoundSoundCard) {
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
            }
        } else {
            Log.e("AudioViewModel", "No valid preview URL found")
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

    fun performSearchWithCoroutines(clientSecret: String, searchText: String, updateUI: (
        Collection<MutableState<FreesoundSoundCard>>) -> Unit) {
        viewModelScope.launch {
            performSearch(
                clientSecret = clientSecret,
                query = searchText,
                setFreesoundSoundCards = { newSounds ->
                    updateUI(newSounds)
                }
            )
        }
    }

    fun syncFiles() {
        Log.d("AudioViewModel", "syncingfiles")
        viewModelScope.launch {
            audioRepository.syncFolderandDatabase()
        }
    }


    fun clearDownloadStatusMessage() {
        _downloadStatusMessage.value = null
    }

    suspend fun doesFileExist(fileName: String): Boolean {
        val exists = audioRepository.doesFileExist(fileName)
        return exists
    }

    fun sortSoundCards(cards: List<MutableState<SoundCard>>, sortBy: String,sortDirection: Float): List<MutableState<SoundCard>> {
        return when (sortBy) {
            "Name" -> {
                if (sortDirection == 1.0f) cards.sortedBy { it.value.fileName.substringAfterLast('/').lowercase(Locale.getDefault()) }
                else cards.sortedByDescending { it.value.fileName.substringAfterLast('/').lowercase(Locale.getDefault()) }
            }
            "Duration" -> {
                if (sortDirection == 1.0f) cards.sortedBy { it.value.duration }
                else cards.sortedByDescending { it.value.duration }
            }
            "Size" -> {
                if (sortDirection == 1.0f) cards.sortedBy { it.value.fileSize }
                else cards.sortedByDescending { it.value.fileSize }
            }
            "Date" -> {
                if (sortDirection == 1.0f) cards.sortedBy { convertDateFormatYearFirst(it.value.date) }
                else cards.sortedByDescending { convertDateFormatYearFirst(it.value.date) }
            }
            else -> cards
        }
    }

    fun sortFsSoundCards(cards: List<MutableState<FreesoundSoundCard>>, sortBy: String, sortDirection: Float): List<MutableState<FreesoundSoundCard>> {
        return when (sortBy) {
            "Name" -> {
                if (sortDirection == 1.0f) cards.sortedBy { it.value.name.substringAfterLast('/').lowercase(Locale.getDefault()) }
                else cards.sortedByDescending { it.value.name.substringAfterLast('/').lowercase(Locale.getDefault()) }
            }
            "Duration" -> {
                if (sortDirection == 1.0f) cards.sortedBy { it.value.duration }
                else cards.sortedByDescending { it.value.duration }
            }
            "Size" -> {
                if (sortDirection == 1.0f) cards.sortedBy { it.value.filesize }
                else cards.sortedByDescending { it.value.filesize }
            }
            "Date" -> {
                if (sortDirection == 1.0f) cards.sortedBy { it.value.created }
                else cards.sortedByDescending { it.value.created }
            }
            else -> cards
        }
    }


}
