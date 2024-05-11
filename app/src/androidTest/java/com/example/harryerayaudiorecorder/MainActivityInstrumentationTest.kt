package com.example.harryerayaudiorecorder

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testStartStopRecorder() {
        composeTestRule.onNodeWithText("Start").performClick()
        composeTestRule.waitForIdle()

        // Assert state changes or other interactions
        composeTestRule.onNodeWithText("Stop").performClick()
        composeTestRule.waitForIdle()
    }
}
