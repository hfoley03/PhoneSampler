package com.example.harryerayaudiorecorder

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@Config(sdk = [30])
class MainActivityTest {


    @Test
    fun testStartRecorder() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            activity.startRecorder()
            // Verify that the startRecorder method logs the correct message
            // and the MediaProjectionManager is called
        }
    }




}
