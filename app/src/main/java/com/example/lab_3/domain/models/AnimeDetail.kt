package com.example.lab_3.domain.models

data class AnimeDetail(
    val id: Int,
    val title: String,
    val titleEnglish: String?,
    val imageUrl: String = "",
    val synopsis: String?,
    val episodes: Int?,
    val status: String,
    val rating: Double?,
    val year: Int?,
    val genres: List<String>,
    val studios: List<String>
)
