package com.jetpackcomposemvi.domain.usecase

import com.jetpackcomposemvi.data.ErrorResponse
import com.jetpackcomposemvi.data.MovieListResponse
import com.jetpackcomposemvi.domain.MovieRepository
import com.jetpackcomposemvi.domain.model.MovieDetail
import javax.inject.Inject

class GetMovieListUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {

    //Business logic comes here
    suspend fun getTopRatedMovieList(pageNumber: Int): MovieListResult {
        return when (val response = movieRepository.fetchTopRatedMovies(pageNumber)) {
            is MovieListResponse.Error ->
                MovieListResult.Error(
                    when (response.errorCode) {
                        ErrorResponse.NULL_RESPONSE -> ErrorResult.NULL_RESPONSE
                        ErrorResponse.RESPONSE_FAILURE -> ErrorResult.RESPONSE_FAILURE
                    }
                )

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

enum class ErrorResult {
    NULL_RESPONSE,
    RESPONSE_FAILURE
}

sealed interface MovieListResult {
    data class Success(val movieDetail: List<MovieDetail>) : MovieListResult
    data class Error(val errorCode: ErrorResult) : MovieListResult
}