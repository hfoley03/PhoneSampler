package com.example.harryerayaudiorecorder

import android.Manifest
import android.content.pm.PackageManager
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
import com.example.harryerayaudiorecorder.ui.BottomSheet
import com.example.harryerayaudiorecorder.ui.theme.HarryErayAudioRecorderTheme


@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity(), Timer.OnTimerTickListener {
    companion object {
        const val TAG = "MainActivity"
    }

    var showSheet by mutableStateOf(false)

    var recorderRunning by mutableStateOf(false)

    private lateinit var timer: Timer
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        requestAudioPermissions()
        timer = Timer(this)



        setContent {

            if (showSheet) {
                BottomSheet() {
                    showSheet = false
                }
            }

            HarryErayAudioRecorderTheme {
                //RecordSwitchButton(applicationContext)
//                SimpleFrontPage(applicationContext)
//                PhoneSamplerApp(SamplerViewModel(), context = this)
                PhoneSamplerApp(this)
            }
        }

    }

//    @Composable
//    fun RecordSwitchButton(context: Context) {
//        val mycontext = context
//        Button(
//            onClick = {
//                if (recorderRunning) stopRecorder() else startRecorder()
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Text(text = if (recorderRunning) "Stop Recording" else "Start Recording")
//        }
//    }

    @Preview
    @Composable
    private fun SimpleFrontPagePreview(){
//        PhoneSamplerApp(SamplerViewModel(), applicationContext = applicationContext)
        PhoneSamplerApp(this)

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
        //switchButtonStyle(false)
        showSheet = true
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

    private fun requestAudioPermissions() {
        Log.d(TAG, "requestAudioPermissions")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }
    }

    override fun onTimerTick(duration: String) {
        Log.d(TAG, duration)
    }


}
