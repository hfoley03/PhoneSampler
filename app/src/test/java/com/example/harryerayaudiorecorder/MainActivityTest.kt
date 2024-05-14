package com.example.harryerayaudiorecorder

import AudioViewModel
import android.Manifest
import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33], manifest = Config.NONE)
class MainActivityTest {

    private lateinit var activity: MainActivity
    @Mock
    private lateinit var audioViewModel: AudioViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        activity = Robolectric.buildActivity(MainActivity::class.java).create().get()
        activity.audioViewModel = audioViewModel
    }

    @Test
    fun `verify permissions are requested if not already granted`() {
        Shadows.shadowOf(activity).denyPermissions(Manifest.permission.RECORD_AUDIO)
        activity.requestAudioPermissions()
        verify(activity).requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), MainActivity.RECORD_AUDIO_REQUEST_CODE)
    }

    @Test
    fun `startRecorder launches AudioRecordService if permissions granted`() {
        // Assume permissions are already granted
        Shadows.shadowOf(activity).grantPermissions(Manifest.permission.RECORD_AUDIO)
        activity.startRecorder()
        verify(audioViewModel).startRecording()  // Assuming this call interacts with ViewModel
    }

    @Test
    fun `stopRecorder stops the AudioRecordService`() {
        activity.stopRecorder()
        val expectedIntent = Intent(activity, AudioRecordService::class.java)
        verify(activity).stopService(expectedIntent)
    }
}
