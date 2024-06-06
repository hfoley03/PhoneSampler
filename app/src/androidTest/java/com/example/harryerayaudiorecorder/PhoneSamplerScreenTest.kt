package com.example.harryerayaudiorecorder

import AudioViewModel
import MockMediaPlayerWrapper
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
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


//    @get:Rule
//    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
//
//    private lateinit var audioViewModel: AudioViewModel
//    private lateinit var navController: TestNavHostController
//    lateinit var db : AudioRecordDatabase
//
//    @Before
//    fun setUp() {
        //val mockAudioRepository = MyAudioRepository()
        val mockMediaPlayerWrapper = MockMediaPlayerWrapper()
        val mockRecorderControl = MockRecorderControl()


//        audioViewModel = AudioViewModel(
//            mediaPlayerWrapper = mockMediaPlayerWrapper,
//            recorderControl = mockRecorderControl,
//            audioRepository = mockAudioRepository
//        )

//        composeTestRule.setContent {
//
//            navController = TestNavHostController(LocalContext.current).apply {
//
//                db = Room.databaseBuilder(
//                    LocalContext.current,
//                    AudioRecordDatabase::class.java,
//                    "audioRecordsDatabase"
//                ).fallbackToDestructiveMigration() // Add this line
//                    .build()
//
//                val audioCapturesDirectory = File("/storage/emulated/0/Music/testSong")
//                val audioRepository = MyAudioRepository(db, audioCapturesDirectory)
//
//                audioViewModel = AudioViewModel(
//                    mediaPlayerWrapper = mockMediaPlayerWrapper,
//                    recorderControl = mockRecorderControl,
//                    audioRepository = audioRepository
//                )
//                navigatorProvider.addNavigator(ComposeNavigator())
//            }
//            PhoneSamplerApp(navController = navController, audioViewModel = audioViewModel)
//        }
//    }

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

//        composeTestRule.onNodeWithTag("SoundCard")
//            .performClick()

        composeTestRule.onAllNodesWithTag("SoundCard")[0].performClick()


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
//        composeTestRule.onNodeWithTag("SoundCard")
//            .performClick()
        composeTestRule.onAllNodesWithTag("SoundCard")[0].performClick()

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



