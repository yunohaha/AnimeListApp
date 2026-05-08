package com.example.lab_3.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lab_3.data.local.AnimeDao
import com.example.lab_3.data.local.AnimeDatabase
import com.example.lab_3.data.network.JikanApi
import com.example.lab_3.data.repository.AnimeRepositoryImpl
import com.example.lab_3.domain.models.Anime
import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class AnimeRepositoryIntegrationTest {

    private lateinit var database: AnimeDatabase
    private lateinit var dao: AnimeDao
    private lateinit var repository: AnimeRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, AnimeDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.AnimeDao()
        repository = AnimeRepositoryImpl(api = mockApi(), animeDao = dao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun setFavourite_savesToRoom() = runTest {
        val anime = Anime(1, "Test Anime", "url", 12, 8.5, 2024, false)

        repository.setFavourite(anime, isFavourite = true)
        val favourites = repository.getFavourites()

        assertThat(favourites.size).isEqualTo(1)
        assertThat(favourites[0].id).isEqualTo(1)
    }

    @Test
    fun addFavouriteTwice_noDuplicate() = runTest {
        val anime = Anime(1, "Test Anime", "url", 12, 8.5, 2024, false)

        repository.setFavourite(anime, isFavourite = true)
        repository.setFavourite(anime, isFavourite = true)
        val favourites = repository.getFavourites()

        assertThat(favourites.size).isEqualTo(1)
    }

    @Test
    fun removeFavourite_deletesFromRoom() = runTest {
        val anime = Anime(1, "Test Anime", "url", 12, 8.5, 2024, false)

        repository.setFavourite(anime, isFavourite = true)
        repository.setFavourite(anime, isFavourite = false)
        val favourites = repository.getFavourites()

        assertThat(favourites.size).isEqualTo(0)
    }

    private fun mockApi(): JikanApi = mockk(relaxed = true)
}