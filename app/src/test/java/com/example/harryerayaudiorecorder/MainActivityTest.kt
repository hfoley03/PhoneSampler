package com.example.harryerayaudiorecorder.com.example.harryerayaudiorecorder
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.harryerayaudiorecorder.MainActivity
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class MainActivityTest {

    @MockK
    lateinit var activity: MainActivity

    @BeforeEach
    fun setup() {
        mockkStatic(ActivityCompat::class)
        every { ActivityCompat.checkSelfPermission(any(), any()) } returns PackageManager.PERMISSION_DENIED
    }

    @Test
    fun requestAudioPermissions() {
        activity.requestAudioPermissions()
        verify { ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.RECORD_AUDIO), 1) }
    }
}
