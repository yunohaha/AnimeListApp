package com.example.lab_3.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_3.data.repository.AnimeRepository
import com.example.lab_3.ui.states.AnimeListUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AnimeListViewModel(
    private val repository: AnimeRepository
): ViewModel(){
    var uiState by mutableStateOf(AnimeListUiState(isLoading = true))
        private set

    private var searchJob: Job? = null
    private var isLoadingMore = false

    init {
        loadAnimeList()
    }

    fun loadMore() {
        if (uiState.isLoadingMore || !uiState.hasMorePages) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoadingMore = true)
            try {
                val nextPage = uiState.currentPage + 1
                val newList = if (uiState.searchQuery.isBlank()) {
                    repository.getAnimeList(page = nextPage)
                } else {
                    repository.searchAnime(uiState.searchQuery, page = nextPage)
                }
                if (newList.isEmpty()) {
                    uiState = uiState.copy(
                        isLoadingMore = false,
                        hasMorePages = false
                    )
                    return@launch
                }
                val uniqueInNewPage = newList.distinctBy { it.id }
                val existingIds = uiState.animeList.map { it.id }.toSet()
                val uniqueNewList = uniqueInNewPage.filter { it.id !in existingIds }

                android.util.Log.d("Pagination", "Page $nextPage: got ${newList.size}, unique: ${uniqueNewList.size}, existing: ${existingIds.size}")

                if (uniqueNewList.isNotEmpty()) {
                    uiState = uiState.copy(
                        animeList = uiState.animeList + uniqueNewList,
                        currentPage = nextPage,
                        isLoadingMore = false
                    )
                } else {
                    uiState = uiState.copy(
                        isLoadingMore = false,
                        hasMorePages = false
                    )
                }
                isLoadingMore = false

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoadingMore = false,
                    errorMessage = e.message ?: "Load more error"
                )
                isLoadingMore = false
            }
        }
    }

    private fun loadAnimeList() {
        viewModelScope.launch {

            uiState = uiState.copy(
                isLoading = true,
                errorMessage = null,
                currentPage = 1,
                hasMorePages = true,
                animeList = emptyList(),
                isEmpty = false
            )
            try {
                val list = repository.getAnimeList(page = 1)
                val uniqueList = list.distinctBy { it.id }
                uiState = uiState.copy(
                    animeList = uniqueList,
                    isLoading = false,
                    hasSearched = true,
                    isEmpty = uniqueList.isEmpty()
                )

            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Download error",
                    hasSearched = true,
                    isEmpty = false
                )
            }
        }
    }

    fun onSearchQueryChange(newValue: String) {
        searchJob?.cancel()
        isLoadingMore = false
        uiState = uiState.copy(searchQuery = newValue)

        if (newValue.isBlank()) {
            loadAnimeList()
            return
        }

        searchJob = viewModelScope.launch {

            try {
                delay(500)
                uiState = uiState.copy(isLoading = true, errorMessage = null, currentPage = 1,hasMorePages = true,
                    animeList = emptyList())
                val list = repository.searchAnime(newValue,  page = 1)
                val uniqueList = list.distinctBy { it.id }
                uiState = uiState.copy(
                    animeList = list,
                    isLoading = false,
                    hasSearched = true,
                    isEmpty = uniqueList.isEmpty()
                )
            }  catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Search error",
                    hasSearched = true,
                    isEmpty = false
                )
            }
        }
    }

    fun onRetry() {
        isLoadingMore = false
        if (uiState.searchQuery.isBlank()) {
            loadAnimeList()
        } else {
            onSearchQueryChange(uiState.searchQuery)
        }
    }
}