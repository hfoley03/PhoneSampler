package com.example.harryerayaudiorecorder

import AudioViewModel
import MockMediaPlayerWrapper
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.example.harryerayaudiorecorder.data.MockAudioRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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

    @Test
    fun clickSoundCard_navigatestoplaybackscreen() {
        navigateToRecordingListScreen()

        composeTestRule.onNodeWithTag("SoundCard")
            .performClick()

        navController.assertCurrentRouteName(PhoneSamplerScreen.Playback.name)
    }

    @Test
    fun clickBackOnPlaybackScreen_goesToRecordListScreen() {
        navigateToPlayBackScreen()
        performNavigateUp()
        navController.assertCurrentRouteName(PhoneSamplerScreen.RecordingsList.name)
    }

    @Test
    fun clickEditSampleOnPlaybackScreen_goesToEditRecordingScreen() {
        navigateToPlayBackScreen()
        val editText = composeTestRule.activity.getString(R.string.edit_recording)
        composeTestRule.onNodeWithContentDescription(editText).performClick()
        navController.assertCurrentRouteName(PhoneSamplerScreen.EditRecord.name)
    }

    @Test
    fun clickBackOnEditRecordingScreen_goesToPlaybackScreen() {
        navigateToEditRecordingScreen()
        performNavigateUp()
        navController.assertCurrentRouteName(PhoneSamplerScreen.Playback.name)
    }


    fun navigateToRecordingListScreen(){
        composeTestRule.onNodeWithContentDescription("Menu Icon")
            .performClick()
    }
    fun performNavigateUp() {
        val backText = composeTestRule.activity.getString(R.string.back_button)
        composeTestRule.onNodeWithContentDescription(backText).performClick()
    }

    fun navigateToPlayBackScreen(){
        navigateToRecordingListScreen()
        composeTestRule.onNodeWithTag("SoundCard")
            .performClick()
    }

    fun navigateToEditRecordingScreen(){
        navigateToPlayBackScreen()
        val editText = composeTestRule.activity.getString(R.string.edit_recording)
        composeTestRule.onNodeWithContentDescription(editText).performClick()
    }
}




class MockRecorderControl : RecorderControl {
    override fun startRecorder() {}
    override fun stopRecorder() {}
}


fun copyFileFromTestResourcesToExternalFilesDir(context: Context, fileName: String, subDir: String) {
    val inputStream: InputStream = context.resources.assets.open(fileName)
    val outputDir = File(context.getExternalFilesDir(null), subDir)
    if (!outputDir.exists()) {
        outputDir.mkdirs()
    }
    val outputFile = File(outputDir, fileName)
    FileOutputStream(outputFile).use { output ->
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            output.write(buffer, 0, length)
        }
        output.flush()
    }
    inputStream.close()
}

