package com.example.harryerayaudiorecorder

import AudioViewModel
import MockMediaPlayerWrapper
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
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
        //composeTestRule.onNodeWithTag("SoundCard").assertExists()
        composeTestRule.onAllNodesWithTag("SoundCard")[0].assertExists()
    }



    @Test
    fun testClickingPencilIconShowsRenameDialog() {
//        composeTestRule.onNodeWithContentDescription("Edit Title").performClick()
//        composeTestRule.onNodeWithTag("FileNameEditDialog").assertExists()

        composeTestRule.onAllNodesWithContentDescription("Edit Title")[0].performClick()
        composeTestRule.onNodeWithTag("FileNameEditDialog").assertExists()

    }

    @Test
    fun testClickingUploadIconShowsUploadDialog() {
//        composeTestRule.onNodeWithContentDescription("Upload").performClick()
//        composeTestRule.onNodeWithTag("UploadDialog").assertExists()

        composeTestRule.onAllNodesWithContentDescription("Upload")[0].performClick()
        composeTestRule.onNodeWithTag("UploadDialog").assertExists()


    }

    @Test
    fun testSearchFunctionality() {
        composeTestRule.onNodeWithText("Local Search").performTextInput("trim")
        composeTestRule.onAllNodesWithTag("SoundCard")[0].assertExists()
        composeTestRule.onAllNodesWithText("trimmed_tomatoes.wav")[0].assertIsDisplayed()

    }



    fun navigateToRecordingListScreen(){
        composeTestRule.onNodeWithContentDescription("Menu Icon")
            .performClick()
    }
}
