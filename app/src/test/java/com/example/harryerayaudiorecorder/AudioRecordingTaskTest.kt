package com.example.harryerayaudiorecorder

import android.media.projection.MediaProjection
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Config.OLDEST_SDK], shadows = [
    ShadowAudioFormatBuilder::class,
    ShadowAudioPlaybackCaptureConfigurationBuilder::class,
    ShadowAudioRecordBuilder::class
])
class AudioRecordingTaskTest {

    @Test(expected = Exception::class)
    fun testPermissionDenied() {
        val context = RuntimeEnvironment.getApplication()
        val mediaProjection = mock(MediaProjection::class.java)
        Shadows.shadowOf(context).denyPermissions(android.Manifest.permission.RECORD_AUDIO)
        AudioRecordingTask(context, mediaProjection)
    }

//    @Test
//    fun testPermissionGrantedAndAudioSetup() {
//        val context = RuntimeEnvironment.getApplication()
//        val mediaProjection = mock(MediaProjection::class.java)
//        Shadows.shadowOf(context).grantPermissions(android.Manifest.permission.RECORD_AUDIO)
//        val task = AudioRecordingTask(context, mediaProjection)
//        assertNotNull(task)
//    }
//
//    @Test
//    fun testRecordingExecution() {
//        val context = RuntimeEnvironment.getApplication()
//        val mediaProjection = mock(MediaProjection::class.java)
//        val fileOutputStream = mock(FileOutputStream::class.java)
//        val byteArray = ByteArray(1024)
//
//        Shadows.shadowOf(context).grantPermissions(android.Manifest.permission.RECORD_AUDIO)
//        val task = AudioRecordingTask(context, mediaProjection)
//        task.execute(fileOutputStream)
//
//        // Assuming the loop runs at least once for simplicity
//        // mock additional methods as needed
//    }
}

