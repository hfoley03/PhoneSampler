package com.example.harryerayaudiorecorder

import AudioViewModel
import MockMediaPlayerWrapper
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertRangeInfoEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
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
class EditRecordingScreenTest {

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

        navigateToEditScreen()

    }

    @Test
    fun testPlayButton() {
        composeTestRule.onNodeWithContentDescription("Play").assertExists()
        composeTestRule.onNodeWithContentDescription("Play").performClick()
        composeTestRule.onNodeWithContentDescription("Pause").assertExists()
    }

    @Test
    fun testTrimButtonAndSnackBarConfirmation() {
        composeTestRule.onNodeWithContentDescription("trim").assertExists()
        composeTestRule.onNodeWithContentDescription("trim").performClick()
        composeTestRule.onNodeWithText("trimmed_trimmed_tomatoes.wav").assertIsDisplayed()
    }

    @Test
    fun testDoubleSlider(){
        val initRange = ProgressBarRangeInfo(0.0f, 0.0f..1.0f)
        composeTestRule.onNodeWithTag("doubleSlider").assertExists()
        composeTestRule.onNodeWithTag("doubleSlider").assertRangeInfoEquals(initRange)
    }
    fun navigateToEditScreen(){
        composeTestRule.onNodeWithContentDescription("Menu Icon").performClick()
        composeTestRule.onAllNodesWithTag("SoundCard")[0].performClick()
        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(R.string.edit_recording)).performClick()
    }
}