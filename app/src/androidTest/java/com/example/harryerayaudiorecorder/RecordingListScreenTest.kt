package com.example.harryerayaudiorecorder

import AudioViewModel
import MockMediaPlayerWrapper
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
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
        composeTestRule.onAllNodesWithTag("SoundCard")[0].assertExists()
    }

    @Test
     fun testThreeDotsShowsDialogAndFourOptions(){
        composeTestRule.onAllNodesWithContentDescription("More Options")[0].performClick()
        composeTestRule.onAllNodesWithTag("DropdownMenu")[0].assertExists()
        composeTestRule.onNodeWithText("Edit Title").assertExists()
        composeTestRule.onNodeWithText("Delete").assertExists()
        composeTestRule.onNodeWithText("Share").assertExists()
        composeTestRule.onNodeWithText("Upload to Freesound").assertExists()
    }

    @Test
    fun testDeleteRemovesSoundCard(){
        val initialCount = composeTestRule.onAllNodesWithTag("SoundCard").fetchSemanticsNodes().size
        composeTestRule.onAllNodesWithContentDescription("More Options")[0].performClick()
        composeTestRule.onNodeWithText("Delete").performClick()
        val newCount = composeTestRule.onAllNodesWithTag("SoundCard").fetchSemanticsNodes().size
        assert(initialCount - 4 == newCount)
    }

    @Test
    fun testRenameDialog() {
        composeTestRule.onAllNodesWithContentDescription("More Options")[0].performClick()
        composeTestRule.onNodeWithText("Edit Title").performClick()
        composeTestRule.onNodeWithTag("FileNameEditDialog").assertExists()
    }

    @Test
    fun testClickingUploadIconShowsUploadDialog() {
        composeTestRule.onAllNodesWithContentDescription("More Options")[0].performClick()
        composeTestRule.onNodeWithText("Upload to Freesound").performClick()
        composeTestRule.onNodeWithTag("authenticate").assertExists()
    }

    fun navigateToRecordingListScreen(){
        composeTestRule.onNodeWithContentDescription("Menu Icon")
            .performClick()
    }
}
