package com.example.lab_3.data.models

import com.example.lab_3.domain.models.Anime

data class AnimeResponse(
    val data: List<AnimeDto>,
    val pagination: Pagination
)
data class AnimeDto(
    val mal_id: Int,
    val title: String,
    val images: Images,
    val episodes: Int?,
    val score: Double?,
    val year: Int?
) {
    val imageUrl: String?
        get() = images.webp?.image_url
            ?: images.jpg?.image_url
    fun toDomainOrNull(): Anime?{
       val safeTitle = title.takeIf {it.isNotBlank() } ?: return null

        android.util.Log.d("AnimeImage", "Title: $safeTitle, Image URL: $imageUrl")

        return Anime(
            id = mal_id,
            title = safeTitle,
            imageUrl = imageUrl,
            episodes = episodes ?: 0,
            rating = score ?: 0.0,
            year = year ?: 0
        )
    }
}

data class Images(
    val jpg: JpgImage?,
    val webp: WebpImage?
)

data class JpgImage(
    val image_url: String?,
    val small_image_url: String?,
    val large_image_url: String?
)

data class WebpImage(
    val image_url: String?,
    val small_image_url: String?
)

data class Genre(
    val mal_id: Int,
    val name: String,
    val url: String
)

data class Studio(
    val mal_id: Int,
    val name: String,
    val url: String
)

data class Pagination(
    val last_visible_page: Int,
    val has_next_page: Boolean,
    val current_page: Int,
    val items: Items?
)

data class Items(
    val count: Int,
    val total: Int,
    val per_page: Int
)


