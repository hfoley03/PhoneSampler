package com.example.harryerayaudiorecorder

import AndroidMediaPlayerWrapper
import AudioViewModel
import android.Manifest
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.example.harryerayaudiorecorder.data.AudioRecordDatabase
import com.example.harryerayaudiorecorder.data.AudioRepository
import com.example.harryerayaudiorecorder.data.MyAudioRepository
import com.example.harryerayaudiorecorder.ui.theme.AppTheme
import java.io.File

interface RecorderControl {
    fun startRecorder()
    fun stopRecorder()
}

class MainActivity : ComponentActivity(), RecorderControl {
    companion object {
        const val TAG = "MainActivity"
        const val RECORD_AUDIO_REQUEST_CODE = 1
    }

    lateinit var audioViewModel: AudioViewModel
    lateinit var audioRepository: AudioRepository

    lateinit var db : AudioRecordDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        requestAudioPermissions()

        db = Room.databaseBuilder(
            this,
            AudioRecordDatabase::class.java,
            "audioRecordsDatabase"
        ).fallbackToDestructiveMigration() // Add this line
            .build()

        val audioCapturesDirectory = File(this.getExternalFilesDir(null), "/AudioCaptures")
        audioRepository = MyAudioRepository(db, audioCapturesDirectory)
        audioViewModel = AudioViewModel(
            mediaPlayerWrapper = AndroidMediaPlayerWrapper(),
            recorderControl = this,
            audioRepository = audioRepository
        )

//        audioViewModel.syncFiles()

        setContent {
            AppTheme {
                Surface(tonalElevation = 25.dp) {
                    PhoneSamplerApp(audioViewModel = audioViewModel)
                }

            }
        }
    }

    override fun startRecorder(){
        Log.d(TAG, "startRecorder")
        val mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        resultLauncher.launch(mediaProjectionManager.createScreenCaptureIntent()) //prompt to be approved by user to allow capture
    }

    override fun stopRecorder() {
        Log.d(TAG, "stopRecorder")
        val intent = Intent(this, AudioRecordService::class.java)
        stopService(intent)
        audioViewModel.timerRunning.value = false
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d(TAG, "Media projection permission granted")
            audioViewModel.timerRunning.value = true
            AudioRecordService.start(this.applicationContext, it, "dummt")
        }

    fun requestAudioPermissions() {
        Log.d(TAG, "requestAudioPermissions")
        if (!AudioUtils.hasRecordAudioPermission(this)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_REQUEST_CODE
            )
            //audioViewModel.setRecorderRunningBool(true)
        }
    }
}
