package com.example.harryerayaudiorecorder

//import com.example.harryerayaudiorecorder.ui.AndroidAudioPlayer
import AudioViewModel
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.harryerayaudiorecorder.data.AudioRecordDatabase
import com.example.harryerayaudiorecorder.ui.PlaybackScreen
import com.example.harryerayaudiorecorder.ui.RecordScreen
import com.example.harryerayaudiorecorder.ui.RecordingsListScreen
import com.example.harryerayaudiorecorder.ui.SamplerViewModel

enum class PhoneSamplerScreen(@StringRes val title: Int) {
    Record(title = R.string.record),
    RecordingsList(title = R.string.recordings_list),
    Playback(title = R.string.playback)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneSamplerAppBar(
    currentScreen: PhoneSamplerScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(
            text = stringResource(currentScreen.title),
            color = MaterialTheme.colorScheme.onBackground
        )},
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    )
}

@Composable
fun PhoneSamplerApp(
    viewModel: SamplerViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navController: NavHostController = rememberNavController(),
    audioViewModel: AudioViewModel,
    db : AudioRecordDatabase
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = PhoneSamplerScreen.valueOf(
        backStackEntry?.destination?.route ?: PhoneSamplerScreen.Record.name
    )

        Scaffold(
            topBar = {
                PhoneSamplerAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() }
                )
            }
            ) { innerPadding ->
            val uiState by viewModel.uiState.collectAsState()

            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.bez1),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

            NavHost(
                navController = navController,
                startDestination = PhoneSamplerScreen.Record.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(route = PhoneSamplerScreen.Record.name) {
                    RecordScreen(
                        audioViewModel,
                        onListButtonClicked = {
                            navController.navigate(PhoneSamplerScreen.RecordingsList.name)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(dimensionResource(R.dimen.padding_medium))
                    )
                }
                composable(route = PhoneSamplerScreen.RecordingsList.name) {
                    RecordingsListScreen(
                        audioViewModel,
                        onSongButtonClicked = {
                            viewModel.setSoundCard(it)
                            navController.navigate(PhoneSamplerScreen.Playback.name)
                        },
                        onThreeDotsClicked = {},
                        modifier = Modifier.fillMaxHeight()
                    )
                }
                composable(route = PhoneSamplerScreen.Playback.name) {
                    PlaybackScreen(
                        audioViewModel,
                        durationSample = uiState.duration,
                        fileName = uiState.fileName,
                        fileSize = uiState.fileSize,
                    )
                }
            }
        }
    }
}