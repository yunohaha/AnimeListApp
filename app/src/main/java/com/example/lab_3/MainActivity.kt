package com.example.lab_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.lab_3.data.network.NetworkModule
import com.example.lab_3.data.repository.AnimeRepository
import com.example.lab_3.ui.navigation.NavGraph
import com.example.lab_3.ui.theme.Lab_3Theme
import com.example.lab_3.ui.viewmodels.AnimeDetailViewModel
import com.example.lab_3.ui.viewmodels.AnimeListViewModel
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Lab_3Theme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val repository = remember { AnimeRepository(NetworkModule.api) }
                    val animeListViewModel = remember { AnimeListViewModel(repository) }
                    val animeDetailViewModel = remember { AnimeDetailViewModel(repository) }
                    NavGraph(
                        animeListViewModel = animeListViewModel,
                        animeDetailViewModel = animeDetailViewModel
                    )
                }
            }
        }
    }
}