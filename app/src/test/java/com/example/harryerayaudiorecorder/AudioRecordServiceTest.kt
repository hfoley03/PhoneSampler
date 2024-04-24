package com.example.harryerayaudiorecorder

import android.app.Instrumentation
import android.content.Intent
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.Context
import org.mockito.Mock

class AudioRecordServiceTest {

    // Mocked objects
    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockMediaProjectionManager: MediaProjectionManager

    @Mock
    private lateinit var mockMediaProjection: MediaProjection

    private val activityResult = Instrumentation.ActivityResult(0, Intent())

//    @Before
//    fun setup() {
//        MockitoAnnotations.initMocks(this)
//        `when`(mockContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE))
//            .thenReturn(mockMediaProjectionManager)
//
//        `when`(mockMediaProjectionManager.getMediaProjection(activityResult.resultCode, activityResult.data!!))
//            .thenReturn(mockMediaProjection)
//    }
//
//    @Test
//    fun testAudioRecordingTaskInitialization() {
//        val audioRecordingTask = AudioRecordingTask(mockContext, activityResult.resultCode, activityResult.data!!)
//        assertNotNull(audioRecordingTask)
//        // Add more assertions if needed
//    }
}