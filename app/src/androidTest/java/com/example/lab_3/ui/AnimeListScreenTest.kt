package com.example.lab_3.ui

import com.google.common.truth.Truth.assertThat
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lab_3.ui.screens.AnimeListScreen
import com.example.lab_3.ui.states.AnimeListUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnimeListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun errorState_clickRetry_callsOnRetry() {
        var retryCalled = false

        val uiState = AnimeListUiState(
            errorMessage = "Network error"
        )

        composeTestRule.setContent {
            AnimeListScreen(
                uiState = uiState,
                onSearchChange = {},
                onAnimeClick = {},
                onFavouriteClick = {},
                onRetry = { retryCalled = true }
            )
        }

        composeTestRule.onNodeWithText("Retry").performClick()
        assertThat(retryCalled).isTrue()
    }
}