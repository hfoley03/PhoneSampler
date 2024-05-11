package com.example.harryerayaudiorecorder

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.Q], manifest = "src/main/AndroidManifest.xml") // Adjust as necessary for your target SDK
class AudioRecordServiceTest {

    private lateinit var service: AudioRecordService
    private lateinit var mockContext: Context
    private lateinit var mockIntent: Intent
    private lateinit var mockMediaProjection: MediaProjection
    private lateinit var mockMediaProjectionManager: MediaProjectionManager

    @Before
    fun setup() {
        mockContext = ApplicationProvider.getApplicationContext()
        service = AudioRecordService()
        mockIntent = mockk(relaxed = true)
        mockMediaProjection = mockk(relaxed = true)
        mockMediaProjectionManager = mockk(relaxed = true)

        every { mockContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) } returns mockMediaProjectionManager
        every { mockMediaProjectionManager.getMediaProjection(any(), any()) } returns mockMediaProjection

        // Setup required to handle the static component
        AudioRecordService.activityResult = mockk(relaxed = true)
    }

    @Test
    fun onStartCommand_CreatesNotificationAndStartsRecording() = runTest {
        // Given
        every { mockContext.startForegroundService(any()) } returns null

        // When
        val result = service.onStartCommand(mockIntent, 0, 1)

        // Then
        verify(exactly = 1) { mockContext.getSystemService(Context.NOTIFICATION_SERVICE) }
        verify { mockMediaProjectionManager.getMediaProjection(any(), any()) } // Ensures MediaProjection is fetched
    }

    @Test
    fun onDestroy_StopsRecordingAndHandlesResources() = runTest {
        // Setup
        service.onCreate() // To initialize the lazy components
        service.startRecording() // Start recording to set things up for onDestroy

        // Act
        service.onDestroy()

        // Assert
        verify { service.audioRecordingTask.cancel() } // Verifies if the recording task was canceled
    }
}
