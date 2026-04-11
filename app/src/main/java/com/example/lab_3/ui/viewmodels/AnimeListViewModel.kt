package com.example.lab_3.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_3.data.repository.RepositoryProvider
import com.example.lab_3.ui.states.AnimeListUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AnimeListViewModel : ViewModel() {
    private val repository = RepositoryProvider.instance
    var uiState by mutableStateOf(AnimeListUiState(isLoading = true))
        private set

    private var searchJob: Job? = null

    init {
        loadAnimeList()
    }

    fun loadMore() {
        if (uiState.isLoadingMore || !uiState.hasMorePages) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingMore = true)
            try {
                val nextPage = uiState.currentPage + 1
                val newList = repository.getAnimeList(page = nextPage)

                if (newList.isEmpty()) {
                    uiState = uiState.copy(
                        isLoadingMore = false,
                        hasMorePages = false
                    )
                    return@launch
                }
                uiState = uiState.copy(
                    animeList = uiState.animeList + newList,
                    currentPage = nextPage,
                    isLoadingMore = false
                )

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoadingMore = false,
                    errorMessage = e.message ?: "Load more error"
                )
            }
        }
    }

    private fun loadAnimeList() {
        viewModelScope.launch {

            uiState = uiState.copy(
                isLoading = true,
                errorMessage = null,
                currentPage = 1,
                hasMorePages = true
            )
            try {
                val list = repository.getAnimeList(page = 1)

                uiState = uiState.copy(
                    animeList = list,
                    isLoading = false,
                    hasSearched = true
                )

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Download error",
                    hasSearched = true
                )
            }
        }
    }

    fun onSearchQueryChange(newValue: String) {
        searchJob?.cancel()
        uiState = uiState.copy(searchQuery = newValue)

        if (newValue.isBlank()) {
            loadAnimeList()
            return
        }

        searchJob = viewModelScope.launch {

            try {
                delay(500)
                uiState = uiState.copy(isLoading = true, errorMessage = null)
                val list = repository.searchAnime(newValue)
                uiState = uiState.copy(
                    animeList = list,
                    isLoading = false,
                    hasSearched = true,
                    errorMessage = if (list.isEmpty()) "No results found" else null
                )
            }  catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Search error",
                    hasSearched = true
                )
            }
        }
    }

    fun onRetry() {
        if (uiState.searchQuery.isBlank()) {
            loadAnimeList()
        } else {
            onSearchQueryChange(uiState.searchQuery)
        }
    }
}