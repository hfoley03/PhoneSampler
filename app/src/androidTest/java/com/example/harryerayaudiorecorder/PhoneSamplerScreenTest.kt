package com.example.harryerayaudiorecorder

import AudioViewModel
import MockMediaPlayerWrapper
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.example.harryerayaudiorecorder.data.MockAudioRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PhoneSamplerScreenTest {


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

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            PhoneSamplerApp(navController = navController, audioViewModel = audioViewModel)
        }
    }

    @Test
    fun verifyStartDestination() {
        navController.assertCurrentRouteName("Record")
    }

    @Test
    fun verifyBackNavigationNotShownOnStartOrderScreen() {
        val backText = composeTestRule.activity.getString(R.string.back_button)
        composeTestRule.onNodeWithContentDescription(backText).assertDoesNotExist()
    }

    @Test
    fun clickList_navigatesToSelectRecordingList() {
        composeTestRule.onNodeWithContentDescription("Menu Icon")
            .performClick()
        navController.assertCurrentRouteName(PhoneSamplerScreen.RecordingsList.name)
    }

    @Test
    fun clickBackOnRecordingListScreen_goesToRecordScreen() {
        navigateToRecordingListScreen()
        performNavigateUp()
        navController.assertCurrentRouteName("Record")
    }

    fun navigateToRecordingListScreen(){
        composeTestRule.onNodeWithContentDescription("Menu Icon")
            .performClick()
    }
    private fun performNavigateUp() {
        val backText = composeTestRule.activity.getString(R.string.back_button)
        composeTestRule.onNodeWithContentDescription(backText).performClick()
    }
}




class MockRecorderControl : RecorderControl {
    override fun startRecorder() {}
    override fun stopRecorder() {}
}




