package com.example.harryerayaudiorecorder

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import com.example.harryerayaudiorecorder.ui.AudioViewModel
import com.example.harryerayaudiorecorder.ui.PlaybackScreen
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class PlaybackScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun playbackScreen_playButtonTriggersPlayingState() {
        val mockViewModel = mockk<AudioViewModel>(relaxed = true)

        composeTestRule.setContent {
            PlaybackScreen(
                audioViewModel = mockViewModel,
                durationSample = 100,
                fileName = "testFile.mp3",
                fileSize = 1.0
            )
        }

        // Assuming the button content description is set to "Play"
        composeTestRule.onNodeWithContentDescription("Play").performClick()

        // Using coVerify if playAudio is a suspend function
        coVerify(exactly = 1) { mockViewModel.playAudio(any()) }
    }
}
