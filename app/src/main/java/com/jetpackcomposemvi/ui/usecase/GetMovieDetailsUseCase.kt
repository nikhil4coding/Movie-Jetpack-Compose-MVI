package com.jetpackcomposemvi.ui.usecase

import com.jetpackcomposemvi.domain.usecase.MovieDetailResult

interface GetMovieDetailsUseCase {
    suspend fun getMovieDetails(movieId: Long): MovieDetailResult
}