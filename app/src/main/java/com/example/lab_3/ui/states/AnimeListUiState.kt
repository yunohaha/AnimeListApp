package com.example.lab_3.ui.states

import com.example.lab_3.domain.models.Anime

data class AnimeListUiState(
    val searchQuery: String = "",
    val animeList: List<Anime> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val errorMessage: String? = null,
    val hasSearched: Boolean = false,
)