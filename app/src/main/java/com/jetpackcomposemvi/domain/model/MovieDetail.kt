package com.jetpackcomposemvi.domain.model

data class MovieDetail(
    val id: Long,
    val title: String,
    val overview: String,
    val posterPath: String
)