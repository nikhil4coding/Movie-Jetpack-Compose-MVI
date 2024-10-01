package com.jetpackcomposemvi.domain.usecase

import com.jetpackcomposemvi.data.MovieListResponse
import com.jetpackcomposemvi.domain.MovieRepository
import com.jetpackcomposemvi.domain.model.MovieDetail
import com.jetpackcomposemvi.ui.usecase.GetMovieListUseCase
import javax.inject.Inject

internal class GetMovieListUseCaseImpl @Inject constructor(
    private val movieRepository: MovieRepository
) : GetMovieListUseCase {

    //Business logic comes here
    override suspend fun getTopRatedMovieList(): MovieListResult {
        return when (val response = movieRepository.fetchTopRatedMovies()) {
            is MovieListResponse.Error -> MovieListResult.Error(response.errorCode)
            is MovieListResponse.Success -> {
                MovieListResult.Success(
                    response.data.results.map {
                        MovieDetail(
                            id = it.id,
                            title = it.title,
                            overview = it.overview,
                            posterPath = it.posterPath
                        )
                    }
                )
            }
        }
    }
}

sealed interface MovieListResult {
    data class Success(val movieDetail: List<MovieDetail>) : MovieListResult
    data class Error(val errorCode: String) : MovieListResult
}