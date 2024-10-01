package com.jetpackcomposemvi.domain

import com.jetpackcomposemvi.data.MovieDetailResponse
import com.jetpackcomposemvi.data.MovieListResponse

interface MovieRepository {
    suspend fun fetchTopRatedMovies(): MovieListResponse
    suspend fun fetchMovieDetails(id: Long): MovieDetailResponse
}