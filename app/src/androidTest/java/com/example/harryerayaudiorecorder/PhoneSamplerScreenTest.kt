package com.example.harryerayaudiorecorder

import AudioViewModel
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.harryerayaudiorecorder.data.AudioRecordDatabase
import com.example.harryerayaudiorecorder.ui.SamplerViewModel
import com.example.harryerayaudiorecorder.ui.theme.AppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PhoneSamplerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var viewModel: SamplerViewModel
    private  lateinit var db : AudioRecordDatabase
    private lateinit var audioViewModel: AudioViewModel

    @Test
    fun testRecordScreenDisplayed() {
        composeTestRule.setContent {
            AppTheme {
                val navController = rememberNavController()
                PhoneSamplerApp(navController = navController, db = db, audioViewModel = audioViewModel)
            }
        }
        // Verify that the RecordScreen is displayed
        composeTestRule.onNodeWithText("Record").assertIsDisplayed()
    }

    @Test
    fun testNavigateToRecordingsListScreen() {
        composeTestRule.setContent {
            AppTheme {
                val navController = rememberNavController()
                PhoneSamplerApp(navController = navController, db = db, audioViewModel = audioViewModel)
            }
        }
        // Navigate to RecordingsListScreen
        composeTestRule.onNodeWithText("Recordings List").performClick()
        // Verify that the RecordingsListScreen is displayed
        composeTestRule.onNodeWithText("Recordings List").assertIsDisplayed()
    }

    @Test
    fun testNavigateToPlaybackScreen() {
        composeTestRule.setContent {
            AppTheme {
                val navController = rememberNavController()
                PhoneSamplerApp(navController = navController, db = db, audioViewModel = audioViewModel)
            }
        }
        // Navigate to RecordingsListScreen
        composeTestRule.onNodeWithText("Recordings List").performClick()
        // Navigate to PlaybackScreen
        composeTestRule.onNodeWithText("Playback").performClick()
        // Verify that the PlaybackScreen is displayed
        composeTestRule.onNodeWithText("Playback").assertIsDisplayed()
    }
}

