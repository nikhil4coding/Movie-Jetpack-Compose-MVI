package com.movies.ui.model

import java.io.Serializable

data class MovieDetailUI(
    val id: Long = 0L,
    val title: String = "",
    val overview: String = "",
    val posterPath: String = ""
) : Serializable