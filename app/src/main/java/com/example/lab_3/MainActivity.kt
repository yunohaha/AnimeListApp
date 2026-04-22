package com.example.lab_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.lab_3.ui.navigation.NavGraph
import com.example.lab_3.ui.theme.Lab_3Theme
import com.example.lab_3.ui.viewmodels.AnimeDetailViewModel
import com.example.lab_3.ui.viewmodels.AnimeListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Lab_3Theme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val animeListViewModel: AnimeListViewModel = hiltViewModel()
                    val animeDetailViewModel: AnimeDetailViewModel = hiltViewModel()
                    NavGraph(
                        animeListViewModel = animeListViewModel,
                        animeDetailViewModel = animeDetailViewModel
                    )
                }
            }
        }
    }
}