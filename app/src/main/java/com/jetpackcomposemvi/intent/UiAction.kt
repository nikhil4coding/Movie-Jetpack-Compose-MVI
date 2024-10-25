package com.jetpackcomposemvi.intent

sealed class UiAction {
    data object FetchTopRatedMovies : UiAction()
    data class FetchMovieDetails(val movieId: String) : UiAction()
}