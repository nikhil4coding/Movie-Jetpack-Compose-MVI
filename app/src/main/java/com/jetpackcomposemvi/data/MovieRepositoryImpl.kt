package com.jetpackcomposemvi.data

import com.jetpackcomposemvi.data.api.MovieService
import com.jetpackcomposemvi.domain.MovieRepository
import javax.inject.Inject

internal class MovieRepositoryImpl @Inject constructor(
    private val movieService: MovieService
) : MovieRepository {
    override suspend fun fetchTopRatedMovies(): MovieListResponse {
        val response = movieService.topRated()
        return if (response.isSuccessful) {
            response.body()?.let {
                MovieListResponse.Success(it)
            } ?: MovieListResponse.Error("Null List")
        } else {
            MovieListResponse.Error("Something went Wrong")
        }
    }

    override suspend fun fetchMovieDetails(id: Long): MovieDetailResponse {
        val response = movieService.movieDetails(id)
        return if (response.isSuccessful) {
            response.body()?.let {
                MovieDetailResponse.Success(it)
            } ?: MovieDetailResponse.Error("No details found")
        } else {
            MovieDetailResponse.Error("Something went Wrong")
        }
    }
}

sealed interface MovieListResponse {
    data class Success(val data: MovieListResponseDTO) : MovieListResponse
    data class Error(val errorCode: String) : MovieListResponse
}

sealed interface MovieDetailResponse {
    data class Success(val data: MovieDetailDTO) : MovieDetailResponse
    data class Error(val errorCode: String) : MovieDetailResponse
}
