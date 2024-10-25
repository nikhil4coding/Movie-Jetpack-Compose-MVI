package com.jetpackcomposemvi.domain

import com.jetpackcomposemvi.data.MovieDetailResponse
import com.jetpackcomposemvi.data.MovieListResponse

interface MovieRepository {
    suspend fun fetchTopRatedMovies(pageNumber: Int): MovieListResponse
    suspend fun fetchMovieDetails(id: String): MovieDetailResponse
}