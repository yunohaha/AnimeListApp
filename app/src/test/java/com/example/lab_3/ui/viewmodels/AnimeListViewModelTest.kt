package com.example.lab_3.ui.viewmodels

import com.example.lab_3.data.repository.AnimeRepository
import com.example.lab_3.domain.models.Anime
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import io.mockk.coVerify
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy

@OptIn(ExperimentalCoroutinesApi::class)
class AnimeListViewModelTest {

    private lateinit var repository: AnimeRepository
    private lateinit var viewModel: AnimeListViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()

        coEvery { repository.getFavourites() } returns emptyList()
        viewModel = AnimeListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isLoadingFalseAndListEmpty() = runTest {
        assertThat(viewModel.uiState.isLoading).isFalse()
        assertThat(viewModel.uiState.animeList).isEmpty()
        assertThat(viewModel.uiState.errorMessage).isNull()
    }

    @Test
    fun loadAnimeList_success_updatesUiState() = runTest {
        val fakeAnimeList = listOf(
            Anime(1, "Anime 1", "url1", 12, 8.5, 2024, false),
            Anime(2, "Anime 2", "url2", 24, 9.0, 2023, false)
        )
        coEvery { repository.getAnimeList(page = 1) } returns fakeAnimeList

        viewModel.loadAnimeList()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.isLoading).isFalse()
        assertThat(viewModel.uiState.animeList).hasSize(2)
        assertThat(viewModel.uiState.animeList[0].title).isEqualTo("Anime 1")
        assertThat(viewModel.uiState.errorMessage).isNull()
    }

    @Test
    fun loadAnimeList_error_updatesErrorMessage() = runTest {
        coEvery { repository.getAnimeList(page = 1) } throws Exception("Network error")

        viewModel.loadAnimeList()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.isLoading).isFalse()
        assertThat(viewModel.uiState.errorMessage).isNotNull()
        assertThat(viewModel.uiState.errorMessage).contains("Network error")
    }


    @Test
    fun retry_afterError_loadsDataAgain() = runTest {

        coEvery { repository.getAnimeList(page = 1) }.throws(Exception("Network error"))

        val fakeAnimeList = listOf(
            Anime(1, "Anime 1", "url1", 12, 8.5, 2024, false)
        )
        coEvery { repository.getFavourites() }.returns(fakeAnimeList)
        viewModel.loadAnimeList()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.errorMessage).contains("Network error")

        assertThat(viewModel.uiState.animeList).isEmpty()

        viewModel.onRetry()
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.uiState.errorMessage).isNull()
        assertThat(viewModel.uiState.favouriteList).hasSize(1)
        assertThat(viewModel.uiState.favouriteList[0].title).isEqualTo("Anime 1")
        coVerify(atLeast = 1) {
            repository.getFavourites()
        }
    }



    @Test
    fun searchAnime_emptyResult_setsEmptyState() = runTest {
        coEvery { repository.searchAnime("xyz", 1) } returns emptyList()
        viewModel.onSearchQueryChange("xyz")
        advanceTimeBy(500)
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(viewModel.uiState.isEmpty).isTrue()
        assertThat(viewModel.uiState.animeList).isEmpty()
        assertThat(viewModel.uiState.errorMessage).isNull()
    }
}