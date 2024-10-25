package com.jetpackcomposemvi.ui

const val MOVIE_LIST = "movie_list"
const val MOVIE_DETAILS = "movie_detail"


sealed class RouteName{
    data object MovieList: RouteName()
    data class MovieDetails(val movieId: Long): RouteName()
}