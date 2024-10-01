package com.jetpackcomposemvi.data.api

import com.jetpackcomposemvi.data.MovieDetailDTO
import com.jetpackcomposemvi.data.MovieListResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MovieService {

    @GET("movie/top_rated")
    suspend fun topRated(): Response<MovieListResponseDTO>

    @GET("movie/{movie_id}")
    suspend fun movieDetails(@Path("movie_id") movieId: Long): Response<MovieDetailDTO>
}