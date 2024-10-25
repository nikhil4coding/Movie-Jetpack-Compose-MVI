package com.jetpackcomposemvi.data

import com.google.gson.annotations.SerializedName

data class MovieListResponseDTO(@SerializedName("results") val results: List<MovieDetailDTO>)

data class MovieDetailDTO(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String
)
