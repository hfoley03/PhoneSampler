package com.example.harryerayaudiorecorder

import AudioViewModel
import MockMediaPlayerWrapper
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
class RecordScreenTest {

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

    }

    @Test
    fun testInitialUIState() {
        composeTestRule.onNodeWithText("ready").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Menu Icon").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Record Icon").assertIsDisplayed()
    }

    @Test
    fun testStartRecording() {
        composeTestRule.onNodeWithContentDescription("Record Icon").performClick()
        composeTestRule.onNodeWithText("recording").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Stop Icon").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Delete Icon").assertIsDisplayed()
    }

    @Test
    fun testStopRecording() {
        composeTestRule.onNodeWithContentDescription("Record Icon").performClick()
        composeTestRule.onNodeWithContentDescription("Stop Icon").performClick()
        composeTestRule.onNodeWithText("ready").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Menu Icon").assertIsDisplayed()
    }

    @Test
    fun testExitDeleteRecording() {
        composeTestRule.onNodeWithContentDescription("Record Icon").performClick()
        composeTestRule.onNodeWithContentDescription("Delete Icon").performClick()
        composeTestRule.onNodeWithText("ready").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Menu Icon").assertIsDisplayed()
    }

    @Test
    fun testNamingDialogPopUp() {
        composeTestRule.onNodeWithContentDescription("Record Icon").performClick()
        composeTestRule.onNodeWithContentDescription("Stop Icon").performClick()
        composeTestRule.onNodeWithText("File Name").assertIsDisplayed()
    }

    @Test
    fun testNoNamingDialogPopUpForDeleteRecording() {
        composeTestRule.onNodeWithContentDescription("Record Icon").performClick()
        composeTestRule.onNodeWithContentDescription("Delete Icon").performClick()
        composeTestRule.onNodeWithText("File Name").assertIsNotDisplayed()
    }

}
