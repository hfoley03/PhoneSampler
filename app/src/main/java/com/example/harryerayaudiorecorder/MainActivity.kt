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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.harryerayaudiorecorder.data.Timer
import com.example.harryerayaudiorecorder.ui.theme.HarryErayAudioRecorderTheme


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity(), Timer.OnTimerTickListener {
    companion object {
        const val TAG = "MainActivity"
        const val RECORD_AUDIO_REQUEST_CODE = 1
    }

    var recorderRunning by mutableStateOf(false)

    private lateinit var audioViewModel: AudioViewModel

    private lateinit var timer: Timer
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        requestAudioPermissions()
        timer = Timer(this)
        audioViewModel = AudioViewModel(AndroidMediaPlayerWrapper())

        setContent {
            HarryErayAudioRecorderTheme {
                PhoneSamplerApp(this, audioViewModel = audioViewModel)
            }
        }
    }



    @Preview
    @Composable
    private fun SimpleFrontPagePreview(){
        PhoneSamplerApp(this, audioViewModel = audioViewModel)
    }

    fun startRecorder(){
        Log.d(TAG, "startRecorder")
        val mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        timer.start()
        recorderRunning = true;
        resultLauncher.launch(mediaProjectionManager.createScreenCaptureIntent()) //prompt to be approved by user to allow capture
    }

    fun stopRecorder() {
        Log.d(TAG, "stopRecorder")
        timer.stop()
        recorderRunning = false;
        val intent = Intent(this, AudioRecordService::class.java)
        stopService(intent)
    }


    private fun switchButtonStyle(boolean: Boolean){
        Log.d(TAG, "switchButtonStyle")
        recorderRunning = boolean
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d(TAG, "Media projection permission granted")
            switchButtonStyle(true)
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

    override fun onTimerTick(duration: String) {
        Log.d(TAG, duration)
    }
}
