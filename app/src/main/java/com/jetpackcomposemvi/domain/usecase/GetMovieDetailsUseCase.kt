package com.jetpackcomposemvi.domain.usecase

import com.jetpackcomposemvi.data.MovieDetailResponse
import com.jetpackcomposemvi.domain.MovieRepository
import com.jetpackcomposemvi.domain.model.MovieDetail
import javax.inject.Inject

class GetMovieDetailsUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {

    //Business logic comes here
    suspend fun getMovieDetails(movieId: String): MovieDetailResult {
        return when (val response = movieRepository.fetchMovieDetails(movieId)) {
            is MovieDetailResponse.Error -> MovieDetailResult.Error(response.errorCode)
            is MovieDetailResponse.Success -> MovieDetailResult.Success(
                movieDetail = MovieDetail(
                    id = response.data.id,
                    title = response.data.title,
                    overview = response.data.overview,
                    posterPath = response.data.posterPath
                )
            )
        }
    }
}

sealed interface MovieDetailResult {
    data class Success(val movieDetail: MovieDetail) : MovieDetailResult
    data class Error(val errorCode: String) : MovieDetailResult
}