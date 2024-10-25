package com.jetpackcomposemvi.domain.usecase

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