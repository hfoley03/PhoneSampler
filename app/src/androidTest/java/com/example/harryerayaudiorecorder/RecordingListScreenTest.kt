package com.example.harryerayaudiorecorder

import AudioViewModel
import MockMediaPlayerWrapper
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
class RecordingListScreenTest {

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

        navigateToRecordingListScreen()

    }

    @Test
    fun testSoundCardDisplayed() {
        composeTestRule.onNodeWithTag("SoundCard").assertExists()
    }

    @Test
    fun testSearchFunctionality() {
        // Set the search text
        composeTestRule.onNodeWithText("Search Recordings").performTextInput("dummy_sound")

        // Verify the search result
        composeTestRule.onNodeWithTag("SoundCard").assertExists()
        composeTestRule.onNodeWithText("dummy_sound.wav").assertIsDisplayed()
    }

    @Test
    fun testClickingPencilIconShowsRenameDialog() {
        composeTestRule.onNodeWithContentDescription("Edit Title").performClick()
        composeTestRule.onNodeWithTag("FileNameEditDialog").assertExists()
    }

    @Test
    fun testClickingUploadIconShowsUploadDialog() {
        composeTestRule.onNodeWithContentDescription("Upload").performClick()
        composeTestRule.onNodeWithTag("UploadDialog").assertExists()
    }

//    @Test
//    fun testInitialUIState() {
//        composeTestRule.onNodeWithText("ready").assertIsDisplayed()
//        composeTestRule.onNodeWithContentDescription("Menu Icon").assertIsDisplayed()
//        composeTestRule.onNodeWithContentDescription("Record Icon").assertIsDisplayed()
//    }
//
//    @Test
//    fun testStartRecording() {
//        composeTestRule.onNodeWithContentDescription("Record Icon").performClick()
//        composeTestRule.onNodeWithText("recording").assertIsDisplayed()
//        composeTestRule.onNodeWithContentDescription("Stop Icon").assertIsDisplayed()
//        composeTestRule.onNodeWithContentDescription("Delete Icon").assertIsDisplayed()
//    }
//
//    @Test
//    fun testStopRecording() {
//        composeTestRule.onNodeWithContentDescription("Record Icon").performClick()
//        composeTestRule.onNodeWithContentDescription("Stop Icon").performClick()
//        composeTestRule.onNodeWithText("ready").assertIsDisplayed()
//        composeTestRule.onNodeWithContentDescription("Menu Icon").assertIsDisplayed()
//    }

    fun navigateToRecordingListScreen(){
        composeTestRule.onNodeWithContentDescription("Menu Icon")
            .performClick()
    }
}
