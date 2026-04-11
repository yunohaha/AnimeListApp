package com.example.lab_3.data.repository

object RepositoryProvider {
    val instance by lazy {
        AnimeRepository()
    }
}