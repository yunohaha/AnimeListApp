package com.example.lab_3.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_3.data.repository.AnimeRepository
import com.example.lab_3.domain.models.Anime
import com.example.lab_3.ui.states.AnimeListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class AnimeListViewModel @Inject constructor(
    private val repository: AnimeRepository
): ViewModel(){
    var uiState by mutableStateOf(AnimeListUiState())
        private set

    private var searchJob: Job? = null

    private fun loadFavourites() {
        viewModelScope.launch {
            try {
                val favourites = repository.getFavourites()
                val favouritesIds = favourites.map { it.id }.toSet()

                uiState = uiState.copy(
                    favouriteList = favourites,
                    animeList = uiState.animeList.map { anime ->
                        anime.copy(isFavourite = anime.id in favouritesIds)
                    }
                )
            } catch (e: CancellationException) {
                throw e
            }catch ( e: Exception){
                uiState = uiState.copy(
                    errorMessage = "Failed to load favorites"
                )
            }
        }
    }

    init {
        loadFavourites()
    }
    fun onSearchQueryChange(newValue: String) {
        searchJob?.cancel()
        uiState = uiState.copy(
            searchQuery = newValue,
            errorMessage = null
        )
        if (newValue.isBlank()) {
            uiState = uiState.copy(
                animeList = emptyList(),
                hasSearched = false,
                isLoading = false,
                errorMessage = null
            )
            return
        }

        searchJob = viewModelScope.launch {
            try {
                if (newValue == uiState.searchQuery) {
                    uiState = uiState.copy(isLoading = true)
                }
                delay(500)
                if (newValue != uiState.searchQuery) return@launch

                val results = repository.searchAnime(newValue)

                if (newValue == uiState.searchQuery) {
                    uiState = uiState.copy(
                        animeList = results.distinctBy { it.id },
                        isLoading = false,
                        hasSearched = true,
                        errorMessage = null,
                        isEmpty = results.isEmpty()
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (newValue == uiState.searchQuery) {
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Search error",
                        hasSearched = true
                    )
                }
            }
        }
    }

    fun onFavouriteClick(anime: Anime) {
        viewModelScope.launch {
            try {
                repository.setFavourite(anime, !anime.isFavourite)
                val updatedAnimeList = uiState.animeList.map { current ->
                    if (current.id == anime.id) {
                        current.copy(isFavourite = !anime.isFavourite)
                    } else {
                        current
                    }
                }

                val updatedFavouriteList = if (anime.isFavourite) {
                    uiState.favouriteList.filter { it.id != anime.id }
                } else {
                    uiState.favouriteList + anime.copy(isFavourite = true)
                }

                uiState = uiState.copy(
                    animeList = updatedAnimeList,
                    favouriteList = updatedFavouriteList
                )
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Failed to save favorite")
            }
        }
    }
    fun onRetry() {
        if (uiState.searchQuery.isBlank()) {
            loadFavourites()
        } else {
            onSearchQueryChange(uiState.searchQuery)
        }
    }
}