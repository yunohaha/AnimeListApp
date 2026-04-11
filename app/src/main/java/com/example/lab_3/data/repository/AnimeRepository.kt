package com.example.lab_3.data.repository

import com.example.lab_3.data.network.JikanApi
import com.example.lab_3.data.network.NetworkModule
import com.example.lab_3.domain.models.Anime
import com.example.lab_3.domain.models.AnimeDetail

class AnimeRepository(
    private val api: JikanApi = NetworkModule.api
) {
    suspend fun getAnimeList(page: Int = 1): List<Anime> {
        repeat(2) { attempt ->
            try {
                val response = api.getAnimeList(page = page)

                if (!response.isSuccessful) {
                    if (response.code() == 429 && attempt == 0) {
                        kotlinx.coroutines.delay(1000)
                        return@repeat
                    }
                    throw Exception("HTTP ${response.code()}")
                }
                val body = response.body() ?: throw Exception("Empty body")
                return body.data.mapNotNull { it.toDomainOrNull() }
            } catch (e: Exception) {
                if (attempt == 1) throw e
            }
        }
        return emptyList()
    }

    suspend fun searchAnime(query: String, page: Int = 1): List<Anime> {
        if (query.isBlank()) return emptyList()
        val response = api.searchAnime(query = query, page = page)

        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code()}")
        }
        val body = response.body() ?: throw Exception("Empty body")
        return body.data.mapNotNull { it.toDomainOrNull() }
    }

    suspend fun getAnimeDetail(id: Int): AnimeDetail {
        repeat(2) {
            try {
                val response = api.getAnimeDetail(id)

                if (!response.isSuccessful) {
                    throw Exception("HTTP ${response.code()}")
                }
                return response.body()?.data?.toDomainOrNull()
                    ?: throw Exception("Empty body")
            } catch (e: Exception) {
                if (it == 1) throw e
                kotlinx.coroutines.delay(800)
            }
        }
        error("unreachable")
    }
}