package com.jetpackcomposemvi.ui.usecase

import com.jetpackcomposemvi.domain.usecase.MovieListResult

interface GetMovieListUseCase {
    suspend fun getTopRatedMovieList(): MovieListResult
}