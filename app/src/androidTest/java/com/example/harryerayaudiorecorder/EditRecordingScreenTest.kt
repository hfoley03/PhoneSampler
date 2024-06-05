package com.example.harryerayaudiorecorder

import AudioViewModel
import MockMediaPlayerWrapper
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
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
        //composeTestRule.onNodeWithContentDescription("Play").performClick()
        //composeTestRule.onNodeWithContentDescription("Pause").assertExists()
    }

    @Test
    fun testTrimButton() {
        composeTestRule.onNodeWithText("Trim").assertExists()
//        composeTestRule.onNodeWithText("Start Position: 00:00:00").performTouchInput { swipeRight() }
//        composeTestRule.onNodeWithText("End Position: 00:01:00").performTouchInput { swipeLeft() }
//
//        composeTestRule.onNodeWithText("Trim").performClick()

        // Verify the trim functionality
    }

    @Test
    fun testStartSliderPosition(){
        composeTestRule.onAllNodesWithTag("StartSlider")[0].performSemanticsAction(SemanticsActions.SetProgress) { it(0.5f) }
        composeTestRule.onNodeWithText("Start Position: 00:01:00").assertExists()
    }
    @Test
    fun testEndSliderPosition(){
        composeTestRule.onAllNodesWithTag("EndSlider")[0].performSemanticsAction(SemanticsActions.SetProgress) { it(0.5f) }
        composeTestRule.onNodeWithText("End Position: 00:01:00").assertExists()
    }
    fun navigateToEditScreen(){
        composeTestRule.onNodeWithContentDescription("Menu Icon")
            .performClick()
        composeTestRule.onNodeWithTag("SoundCard")
            .performClick()
        val editText = composeTestRule.activity.getString(R.string.edit_recording)
        composeTestRule.onNodeWithContentDescription(editText).performClick()
    }
}