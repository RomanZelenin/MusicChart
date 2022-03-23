package com.romazelenin.musicchart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.romazelenin.musicchart.screen.StartScreen
import com.romazelenin.musicchart.ui.theme.MusicChartTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MusicChartTheme {
                StartScreen(viewModel = viewModel)
            }
        }
    }
}