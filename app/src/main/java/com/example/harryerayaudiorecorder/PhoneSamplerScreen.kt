package com.example.harryerayaudiorecorder

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneSamplerApp(
    viewModel: SamplerViewModel,
    navController: NavHostController = rememberNavController()
){
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

        NavHost(
            navController = navController,
            startDestination = PhoneSamplerScreen.Record.name,
            modifier = Modifier
                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = PhoneSamplerScreen.Record.name) {
                RecordScreen(
                    onListButtonClicked = {
                        navController.navigate(PhoneSamplerScreen.RecordingsList.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.padding_medium))

//                    uiState
                )
            }
            composable(route = PhoneSamplerScreen.RecordingsList.name) {
                val context = LocalContext.current
                RecordingsListScreen(
                    onSongButtonClicked = {
                        viewModel.setSoundCard(it)
                        navController.navigate(PhoneSamplerScreen.Playback.name)
                    },

                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = PhoneSamplerScreen.Playback.name) {
                PlaybackScreen(
                    title = uiState.title,
                    duration = uiState.duration,
                    filePath = uiState.filePath,
                    fileSize = uiState.fileSize
                )
            }
        }
    }
}