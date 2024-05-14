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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.harryerayaudiorecorder.ui.theme.HarryErayAudioRecorderTheme

interface RecorderControl {
    fun startRecorder()
    fun stopRecorder()
}

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity(), RecorderControl {
    companion object {
        const val TAG = "MainActivity"
        const val RECORD_AUDIO_REQUEST_CODE = 1
    }

    lateinit var audioViewModel: AudioViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        requestAudioPermissions()
        audioViewModel = AudioViewModel(AndroidMediaPlayerWrapper(), recorderControl = this)

        setContent {
            HarryErayAudioRecorderTheme {
                PhoneSamplerApp(audioViewModel = audioViewModel)
            }
        }
    }


    @Preview
    @Composable
    private fun SimpleFrontPagePreview(){
        audioViewModel = AudioViewModel(AndroidMediaPlayerWrapper(), recorderControl = this)
        PhoneSamplerApp( audioViewModel = audioViewModel)
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
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d(TAG, "Media projection permission granted")
            AudioRecordService.start(this.applicationContext, it)
        }

    fun requestAudioPermissions() {
        Log.d(TAG, "requestAudioPermissions")
        if (!AudioUtils.hasRecordAudioPermission(this)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_REQUEST_CODE
            )
        }
    }
}
