package com.example.harryerayaudiorecorder

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import androidx.activity.result.ActivityResult
import androidx.test.core.app.ApplicationProvider
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import java.io.FileOutputStream

@Config(sdk = [29])
class AudioRecordServiceTest2 {

    private lateinit var context: Context
    private lateinit var service: AudioRecordService

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        service = Robolectric.buildService(AudioRecordService::class.java).create().get()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testStartRecording() {
        val mockMediaProjectionManager = mockk<MediaProjectionManager>()
        val mockMediaProjection = mockk<MediaProjectionManager>()
        val mockActivityResult = mockk<ActivityResult>()
        val mockFileOutputStream = mockk<FileOutputStream>(relaxed = true)

        every { mockActivityResult.resultCode } returns 0
        every { mockActivityResult.data } returns Intent()

        mockkObject(AudioRecordService.Companion)
        every { AudioRecordService.activityResult } returns mockActivityResult

        mockkConstructor(AudioRecordingTask::class)
        every { anyConstructed<AudioRecordingTask>().execute(any()) } just Runs

        service.startRecording()

        verify { anyConstructed<AudioRecordingTask>().execute(any()) }
    }
}
