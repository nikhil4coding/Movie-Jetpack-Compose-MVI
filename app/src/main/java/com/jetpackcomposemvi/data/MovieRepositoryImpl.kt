package com.jetpackcomposemvi.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jetpackcomposemvi.data.api.MovieService
import com.jetpackcomposemvi.data.cache.MovieSharedPref
import com.jetpackcomposemvi.domain.MovieRepository
import javax.inject.Inject

internal class MovieRepositoryImpl @Inject constructor(
    private val movieService: MovieService,
    private val movieSharedPref: MovieSharedPref
) : MovieRepository {
    override suspend fun fetchTopRatedMovies(pageNumber: Int): MovieListResponse {
        val cacheMovieList = movieSharedPref.getMovieList(pageNumber.toString())
        if (!cacheMovieList.isNullOrEmpty()) {
            val movieList = Gson().fromJson<MovieListResponseDTO>(cacheMovieList,
                object : TypeToken<MovieListResponseDTO>() {}.type
            )
            return MovieListResponse.Success(movieList)
        } else {
            val response = movieService.topRated(pageNumber)
            return if (response.isSuccessful) {
                response.body()?.let {
                    val movieListStr = Gson().toJson(it)
                    movieSharedPref.updateMovieList(pageNumber.toString(), movieListStr)
                    MovieListResponse.Success(it)
                } ?: MovieListResponse.Error(ErrorResponse.NULL_RESPONSE)
            } else {
                MovieListResponse.Error(ErrorResponse.RESPONSE_FAILURE)
            }
        }
    }

    override suspend fun fetchMovieDetails(id: String): MovieDetailResponse {
        val response = movieService.movieDetails(id)
        return if (response.isSuccessful) {
            response.body()?.let {
                MovieDetailResponse.Success(it)
            } ?: MovieDetailResponse.Error(ErrorResponse.NULL_RESPONSE)
        } else {
            MovieDetailResponse.Error(ErrorResponse.RESPONSE_FAILURE)
        }
    }
}


enum class ErrorResponse{
    NULL_RESPONSE,
    RESPONSE_FAILURE
}
sealed interface MovieListResponse {
    data class Success(val data: MovieListResponseDTO) : MovieListResponse
    data class Error(val errorCode: ErrorResponse) : MovieListResponse
}

sealed interface MovieDetailResponse {
    data class Success(val data: MovieDetailDTO) : MovieDetailResponse
    data class Error(val errorCode: ErrorResponse) : MovieDetailResponse
}
