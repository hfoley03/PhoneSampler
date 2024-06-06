package com.example.harryerayaudiorecorder

import AudioViewModel
import MockMediaPlayerWrapper
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.harryerayaudiorecorder.data.MockAudioRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlaybackScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var audioViewModel: AudioViewModel
    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        val mockAudioRepository = MockAudioRepository()
        val mockMediaPlayerWrapper = MockMediaPlayerWrapper()
        val mockRecorderControl = MockRecorderControl()

        audioViewModel = AudioViewModel(
            mediaPlayerWrapper = mockMediaPlayerWrapper,
            recorderControl = mockRecorderControl,
            audioRepository = mockAudioRepository
        )

        navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            PhoneSamplerApp(navController = navController, audioViewModel = audioViewModel)
        }

        navigateToPlaybackScreen()

    }

    @Test
    fun testPlayButton() {
        composeTestRule.onNodeWithContentDescription("Play").assertExists()
        composeTestRule.onNodeWithContentDescription("Play").performClick()
        composeTestRule.onNodeWithContentDescription("Stop").assertExists()
    }

    @Test
    fun testPauseButton() {
        composeTestRule.onNodeWithContentDescription("Play").assertExists()
        composeTestRule.onNodeWithContentDescription("Play").performClick()
        composeTestRule.onNodeWithContentDescription("Stop").performClick()
        composeTestRule.onNodeWithContentDescription("Play").assertExists()
    }

    @Test
    fun testRepeatButton() {
        composeTestRule.onNodeWithContentDescription("Repeat").assertExists()
        composeTestRule.onNodeWithContentDescription("Repeat").performClick()
        composeTestRule.onNodeWithContentDescription("Repeat").assertExists()
    }

    @Test
    fun testSpeedButton() {
        composeTestRule.onNodeWithContentDescription("Speed").performClick()
        composeTestRule.onNodeWithTag("SpeedSlider").assertExists()
    }

    fun navigateToPlaybackScreen(){
        composeTestRule.onNodeWithContentDescription("Menu Icon")
            .performClick()
        composeTestRule.onAllNodesWithTag("SoundCard")[0].performClick()

    }
}